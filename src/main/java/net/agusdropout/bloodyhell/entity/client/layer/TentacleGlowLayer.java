package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.client.TentacleEntityModel;
import net.agusdropout.bloodyhell.entity.custom.TentacleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;


public class TentacleGlowLayer extends RenderLayer<TentacleEntity, TentacleEntityModel> {


    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/entity_tentacles_glowmask.png");

    public TentacleGlowLayer(RenderLayerParent<TentacleEntity, TentacleEntityModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TentacleEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!entity.isInvisible()) {


            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(GLOW_TEXTURE));



            this.getParentModel().renderToBuffer(
                    poseStack,
                    vertexConsumer,
                    15728880,
                    OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F
            );
        }
    }
}