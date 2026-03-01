package net.agusdropout.bloodyhell.entity.client.base;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.client.layer.InsightGlowingLayer;
import net.agusdropout.bloodyhell.util.visuals.InsightDistortingVertexConsumer;
import net.agusdropout.bloodyhell.util.visuals.ModRenderTypes;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class InsightCreatureRenderer<T extends LivingEntity & GeoAnimatable> extends GeoEntityRenderer<T> {

    public InsightCreatureRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.shadowRadius = 0.0001f;
        addRenderLayer(new InsightGlowingLayer<>(this, entity -> {
            ResourceLocation baseTexture = this.getTextureLocation(entity);
            return new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath().replace(".png", "_glowmask.png"));
        }));
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (ShaderUtils.areShadersActive()) {
            return RenderType.entityTranslucent(texture);
        }
        return ModRenderTypes.getInsightDistortion(texture);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        float playerInsight = 80;
        float calculatedAlpha = 0.07f;

        if (calculatedAlpha <= 0.01f) {
            return;
        }

        boolean shadersActive = ShaderUtils.areShadersActive();

        if (!shadersActive) {
            if (ModShaders.INSIGHT_DISTORTION_SHADER != null) {
                Uniform timeUniform = ModShaders.INSIGHT_DISTORTION_SHADER.getUniform("GameTime");
                if (timeUniform != null) timeUniform.set(entity.tickCount + partialTick);

                Uniform alphaUniform = ModShaders.INSIGHT_DISTORTION_SHADER.getUniform("InsightAlpha");
                if (alphaUniform != null) alphaUniform.set(calculatedAlpha);
            }

            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        } else {
            float renderTime = entity.tickCount + partialTick;

            MultiBufferSource wrappedBufferSource = renderType -> {
                VertexConsumer originalBuffer = bufferSource.getBuffer(renderType);
                return new InsightDistortingVertexConsumer(originalBuffer, renderTime, calculatedAlpha);
            };

            super.render(entity, entityYaw, partialTick, poseStack, wrappedBufferSource, packedLight);
        }
    }
}