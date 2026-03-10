package net.agusdropout.bloodyhell.entity.client.base;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.entity.base.InsightEntity;
import net.agusdropout.bloodyhell.entity.client.layer.InsightGlowingLayer;
import net.agusdropout.bloodyhell.util.visuals.ModRenderTypes;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.manager.InsightRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class InsightCreatureRenderer<T extends LivingEntity & GeoAnimatable & InsightEntity> extends GeoEntityRenderer<T> {

    public InsightCreatureRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        this(renderManager, model, true);
    }

    public InsightCreatureRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, boolean applyDefaultGlow) {
        super(renderManager, model);
        this.shadowRadius = 0.0001f;

        if (applyDefaultGlow) {
            this.addRenderLayer(new InsightGlowingLayer<>(this, entity -> {
                ResourceLocation baseTexture = this.getTextureLocation(entity);
                return new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath().replace(".png", "_glowmask.png"));
            }));
        }
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (ClientInsightData.getPlayerInsight() >= animatable.getMinimumInsight()) {
            return super.getRenderType(animatable, texture, bufferSource, partialTick);
        }

        // Fallback deleted: Will always natively output the Insight Distortion shader mapping
        return ModRenderTypes.getInsightDistortion(texture);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float playerInsight = ClientInsightData.getPlayerInsight();

        if (playerInsight >= entity.getMinimumInsight()) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            return;
        }


        PoseStack copiedPose = new PoseStack();
        copiedPose.last().pose().set(poseStack.last().pose());
        copiedPose.last().normal().set(poseStack.last().normal());


        InsightRenderManager.queueInsightRender(() -> {
            if (ModShaders.INSIGHT_DISTORTION_SHADER != null) {
                Uniform timeUniform = ModShaders.INSIGHT_DISTORTION_SHADER.getUniform("GameTime");
                if (timeUniform != null) timeUniform.set(entity.tickCount + partialTick);

                Uniform alphaUniform = ModShaders.INSIGHT_DISTORTION_SHADER.getUniform("InsightAlpha");
                if (alphaUniform != null) alphaUniform.set(0.07f);
            }


            MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
            super.render(entity, entityYaw, partialTick, copiedPose, immediateBuffer, packedLight);
            immediateBuffer.endBatch();
        });
    }
}