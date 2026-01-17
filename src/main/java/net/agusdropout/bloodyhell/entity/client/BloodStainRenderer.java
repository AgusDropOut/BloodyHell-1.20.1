package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.BloodStainEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BloodStainRenderer extends EntityRenderer<BloodStainEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_stain.png");
    private static final ResourceLocation GLOWMASK = new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_stain_glowmask.png");

    public BloodStainRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodStainEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        Direction face = entity.getAttachFace();

        // --- 1. ALIGN TO SURFACE ---
        // Rotates the quad to lie flat against the attached block face
        switch (face) {
            case UP:    poseStack.translate(0, 0.01, 0); break;
            case DOWN:  poseStack.translate(0, 0.99, 0); poseStack.mulPose(Axis.XP.rotationDegrees(180)); break;
            case NORTH: poseStack.translate(0, 0.5, 0.99); poseStack.mulPose(Axis.XP.rotationDegrees(90)); break;
            case SOUTH: poseStack.translate(0, 0.5, 0.01); poseStack.mulPose(Axis.XP.rotationDegrees(-90)); break;
            case WEST:  poseStack.translate(0.99, 0.5, 0); poseStack.mulPose(Axis.ZP.rotationDegrees(90)); break;
            case EAST:  poseStack.translate(0.01, 0.5, 0); poseStack.mulPose(Axis.ZP.rotationDegrees(-90)); break;
        }

        // Random rotation (0-360) so stains don't look identical
        float randomRot = (entity.getId() * 1337) % 360;
        poseStack.mulPose(Axis.YP.rotationDegrees(randomRot));

        float size = 1.0f;
        poseStack.scale(size, size, size);

        // --- 2. RENDER LAYERS ---
        float alpha = entity.getAlpha();

        // A. Base Texture (Standard lighting)
        VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        renderQuad(poseStack, baseConsumer, packedLight, alpha);

        // B. Glowmask (Pulsating)
        // Calculate heartbeat pulse (0.5 to 1.0) based on time
        float time = entity.tickCount + partialTicks;
        float heartbeat = (float) Math.sin(time * 0.15f) * 0.5f + 0.5f;

        // Combine base fade alpha with heartbeat pulse
        float glowAlpha = alpha * (0.5f + (heartbeat * 0.5f));

        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucent(GLOWMASK));
        // Use full brightness (15728880) so it glows in the dark
        renderQuad(poseStack, glowConsumer, 15728880, glowAlpha);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderQuad(PoseStack poseStack, VertexConsumer consumer, int light, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f p = pose.pose();
        Matrix3f n = pose.normal();
        float s = 0.5f;

        consumer.vertex(p, -s, 0, -s).color(1f, 1f, 1f, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 1, 0).endVertex();
        consumer.vertex(p, -s, 0, s).color(1f, 1f, 1f, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 1, 0).endVertex();
        consumer.vertex(p, s, 0, s).color(1f, 1f, 1f, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 1, 0).endVertex();
        consumer.vertex(p, s, 0, -s).color(1f, 1f, 1f, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BloodStainEntity entity) {
        return TEXTURE;
    }
}