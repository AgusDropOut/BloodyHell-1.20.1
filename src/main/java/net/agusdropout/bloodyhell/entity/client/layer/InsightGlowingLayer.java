package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.util.visuals.InsightDistortingVertexConsumer;
import net.agusdropout.bloodyhell.util.visuals.ModRenderTypes;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Function;

public class InsightGlowingLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

    private final Function<T, ResourceLocation> textureProvider;

    public InsightGlowingLayer(GeoRenderer<T> entityRendererIn, Function<T, ResourceLocation> textureProvider) {
        super(entityRendererIn);
        this.textureProvider = textureProvider;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        float playerInsight = 80;
        float calculatedAlpha = 0.3f;

        if (calculatedAlpha <= 0.05f) return;

        ResourceLocation glowTexture = this.textureProvider.apply(animatable);
        if (glowTexture == null) return;

        boolean shadersActive = ShaderUtils.areShadersActive();

        RenderType glowRenderType = shadersActive
                ? RenderType.eyes(glowTexture)
                : ModRenderTypes.getInsightDistortionGlowing(glowTexture);

        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

        if (shadersActive) {
            int tickCount = animatable instanceof Entity entity ? entity.tickCount : 0;
            float renderTime = tickCount + partialTick;
            glowBuffer = new InsightDistortingVertexConsumer(glowBuffer, renderTime, calculatedAlpha);
        }

        float alphaToPass = shadersActive ? 1.0f : calculatedAlpha;

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowRenderType, glowBuffer, partialTick, 15728880, packedOverlay, 1.0f, 1.0f, 1.0f, alphaToPass);
    }
}