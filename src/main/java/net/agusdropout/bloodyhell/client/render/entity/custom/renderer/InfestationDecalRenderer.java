package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.effects.InfestationDecalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class InfestationDecalRenderer extends EntityRenderer<InfestationDecalEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/infestation_mesh.png");

    public InfestationDecalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(InfestationDecalEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        int color = entity.getColor();
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Direction face = entity.getFace();

        float offset = 0.02f;

        switch (face) {
            case UP:
                poseStack.translate(0.0D, offset, 0.0D);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                break;
            case DOWN:
                poseStack.translate(0.0D, 1.0f - offset, 0.0D);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                break;
            case NORTH:
                poseStack.translate(0.0D, 0.5D, 0.5D + offset);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                break;
            case SOUTH:
                poseStack.translate(0.0D, 0.5D, -0.5D - offset);
                break;
            case WEST:
                poseStack.translate(0.5D + offset, 0.5D, 0.0D);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                break;
            case EAST:
                poseStack.translate(-0.5D - offset, 0.5D, 0.0D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                break;
        }

        float radius = entity.getRadius();
        poseStack.scale(radius, radius, 1.0F);

        float minU = -radius;
        float maxU = radius;
        float minV = -radius;
        float maxV = radius;

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f poseMatrix = lastPose.pose();
        Matrix3f normalMatrix = lastPose.normal();

        vertex(vertexConsumer, poseMatrix, normalMatrix, -1.0F, -1.0F, r, g, b, minU, minV, packedLight);
        vertex(vertexConsumer, poseMatrix, normalMatrix, -1.0F, 1.0F, r, g, b, minU, maxV, packedLight);
        vertex(vertexConsumer, poseMatrix, normalMatrix, 1.0F, 1.0F, r, g, b, maxU, maxV, packedLight);
        vertex(vertexConsumer, poseMatrix, normalMatrix, 1.0F, -1.0F, r, g, b, maxU, minV, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float r, float g, float b, float u, float v, int light) {
        consumer.vertex(pose, x, y, 0.0F).color(r, g, b, 1.0F).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(InfestationDecalEntity entity) { return TEXTURE; }
}
