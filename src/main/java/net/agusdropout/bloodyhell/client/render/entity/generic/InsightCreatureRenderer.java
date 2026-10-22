package net.agusdropout.bloodyhell.client.render.entity.generic;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.client.render.entity.generic.GenericGeckoEntityRenderer;
import net.agusdropout.bloodyhell.client.render.entity.layer.BloodFireGeoLayer;
import net.agusdropout.bloodyhell.entity.base.InsightEntity;
import net.agusdropout.bloodyhell.client.render.entity.layer.InsightGlowingLayer;
import net.agusdropout.bloodyhell.client.render.ModRenderTypes;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredTaskQueues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public abstract class InsightCreatureRenderer<T extends LivingEntity & GeoAnimatable & InsightEntity>
        extends GenericGeckoEntityRenderer<T> {

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
        this.addRenderLayer(new BloodFireGeoLayer<>(this));
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (Minecraft.getInstance().screen != null || ClientInsightData.canSee(animatable)) {
            return super.getRenderType(animatable, texture, bufferSource, partialTick);
        }

        return ModRenderTypes.getInsightDistortion(texture);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (Minecraft.getInstance().screen != null || ClientInsightData.canSee(entity)) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            return;
        }



        PoseStack copiedPose = new PoseStack();
        copiedPose.last().pose().set(poseStack.last().pose());
        copiedPose.last().normal().set(poseStack.last().normal());


        DeferredTaskQueues.INSIGHT.queue(() -> {
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