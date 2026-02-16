package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;

import net.agusdropout.bloodyhell.entity.client.RhnullImpalerModel;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RhnullImpalerGlowLayer extends RenderLayer<RhnullImpalerEntity, RhnullImpalerModel> {

    private static final ResourceLocation GLOW_TEXTURE =
            new ResourceLocation(BloodyHell.MODID, "textures/entity/projectiles/rhnull_impaler_entity_glowmask.png");

    public RhnullImpalerGlowLayer(RenderLayerParent<RhnullImpalerEntity, RhnullImpalerModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       RhnullImpalerEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(GLOW_TEXTURE));

        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728880,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}