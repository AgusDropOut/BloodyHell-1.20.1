package net.agusdropout.bloodyhell.entity.unknown.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EchoGlowLayer extends GeoRenderLayer<EchoOfTheNamelessEntity> {

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/echo_of_the_nameless_glowmask.png");

    public EchoGlowLayer(GeoEntityRenderer<EchoOfTheNamelessEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, EchoOfTheNamelessEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        float energyRatio = animatable.getEnergy() / 100.0F;

        float r = 1.0F;
        float g = energyRatio;
        float b = energyRatio;

        RenderType glowRenderType = RenderType.eyes(GLOW_TEXTURE);

        getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                glowRenderType,
                bufferSource.getBuffer(glowRenderType),
                partialTick,
                15728880,
                OverlayTexture.NO_OVERLAY,
                r, g, b, 1.0F
        );
    }
}