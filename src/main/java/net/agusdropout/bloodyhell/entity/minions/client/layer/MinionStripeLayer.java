package net.agusdropout.bloodyhell.entity.minions.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.entity.minions.client.base.AbstractMinionRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class MinionStripeLayer<T extends AbstractMinionEntity> extends GeoRenderLayer<T> {

    public MinionStripeLayer(AbstractMinionRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }


    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (ClientInsightData.getPlayerInsight() < animatable.getMinimumInsight()) {
            return;
        }
        ResourceLocation baseTexture = this.getRenderer().getTextureLocation(animatable);
        ResourceLocation stripeTexture = new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath().replace(".png", "_stripes.png"));
        RenderType emissiveRenderType = RenderType.eyes(stripeTexture);
        int color = animatable.getStripeColor();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, emissiveRenderType,
                bufferSource.getBuffer(emissiveRenderType), partialTick, 15728880, packedOverlay,
                r, g, b, 1.0f);
    }


}