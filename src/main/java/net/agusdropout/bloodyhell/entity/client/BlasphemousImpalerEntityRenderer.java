package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousImpalerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BlasphemousImpalerEntityRenderer extends EntityRenderer<BlasphemousImpalerEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_impaler_entity.png");


    private final BlasphemousImpalerEntityModel model;

    public BlasphemousImpalerEntityRenderer(EntityRendererProvider.Context context) {
        super(context);

        this.model = new BlasphemousImpalerEntityModel(context.bakeLayer(BlasphemousImpalerEntityModel.LAYER_LOCATION));
    }

    @Override
    public void render(BlasphemousImpalerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());


        poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        // 2. SHAKE EFFECT
        float shake = (float)entity.shakeTime - partialTick;
        if (shake > 0.0F) {
            float angle = -Mth.sin(shake * 3.0F) * shake;
            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        }


        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BlasphemousImpalerEntity entity) {
        return TEXTURE;
    }
}