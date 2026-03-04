package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;

import net.agusdropout.bloodyhell.entity.base.InsightEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Function;

public class InsightGlowingLayer<T extends LivingEntity & GeoAnimatable & InsightEntity> extends GeoRenderLayer<T> {

    private final Function<T, ResourceLocation> textureProvider;

    public InsightGlowingLayer(GeoRenderer<T> entityRendererIn, Function<T, ResourceLocation> textureProvider) {
        super(entityRendererIn);
        this.textureProvider = textureProvider;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {


        if (ClientInsightData.getPlayerInsight() < animatable.getMinimumInsight()) {
            return;
        }

        ResourceLocation glowTexture = this.textureProvider.apply(animatable);
        if (glowTexture == null) return;


        RenderType glowRenderType = RenderType.eyes(glowTexture);
        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowRenderType, glowBuffer, partialTick, 15728880, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}