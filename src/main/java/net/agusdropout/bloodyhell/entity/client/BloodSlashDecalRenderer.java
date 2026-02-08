package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.effects.BloodSlashDecalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class BloodSlashDecalRenderer extends EntityRenderer<BloodSlashDecalEntity> {

    // --- SECT 1: TEXTURES ---

    private static final ResourceLocation DECAL_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_slash_decal.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_slash_decal_glowmask.png");

    public BloodSlashDecalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    // --- SECT 2: MAIN RENDER ---

    @Override
    public void render(BloodSlashDecalEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        Direction face = entity.getFace();

        // 1. Orientation Logic (Align to surface)
        if (face == Direction.UP) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0, 0, -0.02);
        } else if (face == Direction.DOWN) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.translate(0, 0, -0.02);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(face.getOpposite().toYRot()));
            poseStack.translate(0, 0, -0.02);
        }

        // 2. Fading Logic
        float age = entity.getAge(partialTicks);
        float maxAge = entity.getMaxAge();
        float alpha = 1.0F;

        float fadeStart = maxAge * 0.6f; // Start fading at 60% life

        if (age > fadeStart) {
            float t = (age - fadeStart) / (maxAge - fadeStart);
            t = Mth.clamp(t, 0.0F, 1.0F);
            alpha = 1.0F - (t * t * t);
        }

        // 3. Scaling Logic
        float scale = 3.0f * entity.getSize();
        poseStack.scale(scale, scale, 1.0f);

        Matrix4f matrix = poseStack.last().pose();

        // --- LAYER 1: Base (Dark/Shadowed) ---
        VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityTranslucent(DECAL_TEXTURE));
        drawQuad(baseConsumer, matrix, alpha, packedLight);

        // --- LAYER 2: Glow (Full Bright) ---
        if (alpha > 0.05f) {
            VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLOW_TEXTURE));
            drawQuad(glowConsumer, matrix, alpha, 15728880);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    // --- SECT 3: HELPERS ---

    private void drawQuad(VertexConsumer consumer, Matrix4f matrix, float alpha, int light) {
        consumer.vertex(matrix, -0.5f, -0.5f, 0).color(1.0f, 1.0f, 1.0f, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, -0.5f, 0.5f, 0).color(1.0f, 1.0f, 1.0f, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, 0.5f, 0.5f, 0).color(1.0f, 1.0f, 1.0f, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, 0.5f, -0.5f, 0).color(1.0f, 1.0f, 1.0f, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BloodSlashDecalEntity entity) {
        return DECAL_TEXTURE;
    }
}