package net.agusdropout.bloodyhell.block.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GenericEmissiveLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

    public GenericEmissiveLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {


        ResourceLocation baseTexture = getTextureResource(animatable);


        String glowPath = baseTexture.getPath().replace(".png", "_glowmask.png");
        ResourceLocation glowTexture = new ResourceLocation(baseTexture.getNamespace(), glowPath);

        RenderType emissiveRenderType = RenderType.entityTranslucentEmissive(glowTexture);

        VertexConsumer glowingBuffer = bufferSource.getBuffer(emissiveRenderType);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, emissiveRenderType,
                glowingBuffer, partialTick,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                1f, 1f, 1f, 1f);
    }
}