package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.agusdropout.bloodyhell.entity.projectile.ViscousProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class ViscousProjectileRenderer extends EntityRenderer<ViscousProjectileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("bloodyhell", "textures/entity/viscous_projectile.png");
    private final ViscousProjectileModel<ViscousProjectileEntity> model;

    public ViscousProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ViscousProjectileModel<>(context.bakeLayer(ViscousProjectileModel.LAYER_LOCATION));
    }

    @Override
    public void render(ViscousProjectileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        Vector3f color = entity.getBaseColor();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color.x(), color.y(), color.z(), 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ViscousProjectileEntity entity) {
        return TEXTURE;
    }
}