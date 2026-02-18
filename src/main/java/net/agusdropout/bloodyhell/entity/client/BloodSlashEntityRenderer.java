package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodSlashEntity;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BloodSlashEntityRenderer extends EntityRenderer<BloodSlashEntity> {

    // CONSTANTS ---

    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/misc/white.png");

    public static float GLOBAL_YAW_OFFSET = 180.0F;
    public static float MESH_ROTATION_Z = 90.0F;
    public static float MESH_ROTATION_X = 270.0F;
    public static float MESH_ROTATION_Y = 180.0F;

    public BloodSlashEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    // --- MAIN RENDER ---

    @Override
    public void render(BloodSlashEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTicks;

        // Use Synced Rotation for smooth interpolation
        float lerpYaw = Mth.lerp(partialTicks, entity.getYawSynced(), entity.getYawSynced());
        float lerpPitch = Mth.lerp(partialTicks, entity.getPitchSynced(), entity.getPitchSynced());

        if (entity.tickCount > 1) {
            lerpYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
            lerpPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        }

        // Get the synchronized scale (Gem Upgrade)
        float entityScale = entity.getScale();

        // Setup Render State
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // A. AIR SLASH RENDERING
        poseStack.pushPose();

        // Orient to entity rotation
        poseStack.mulPose(Axis.YP.rotationDegrees(GLOBAL_YAW_OFFSET - lerpYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(lerpPitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(MESH_ROTATION_Z));
        poseStack.mulPose(Axis.XP.rotationDegrees(MESH_ROTATION_X));
        poseStack.mulPose(Axis.YP.rotationDegrees(MESH_ROTATION_Y));

        // Render Core
        renderAirSlash(poseStack, buffer, age, 1.0f, entityScale);

        // Render Side Echoes
        for (int i = 1; i <= 2; i++) {
            float offset = i * 0.25f * entityScale; // Scale the offset too
            float fade = 0.5f / (i + 1);

            poseStack.pushPose(); poseStack.translate(0, 0, offset);
            renderAirSlash(poseStack, buffer, age, fade, entityScale);
            poseStack.popPose();

            poseStack.pushPose(); poseStack.translate(0, 0, -offset);
            renderAirSlash(poseStack, buffer, age, fade, entityScale);
            poseStack.popPose();
        }

        // Render Back Trails
        for (int i = 1; i <= 3; i++) {
            float backOffset = i * 0.6f * entityScale;
            float fade = 0.6f / i;
            poseStack.pushPose();
            poseStack.translate(0, backOffset, 0);
            float trailScale = 1.0f - (i * 0.1f);
            poseStack.scale(trailScale, trailScale, 1.0f);
            renderAirSlash(poseStack, buffer, age, fade, entityScale);
            poseStack.popPose();
        }
        poseStack.popPose();

        // B. FLOOR TRAIL RENDERING
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Cleanup
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    // --- HELPERS ---

    private void renderAirSlash(PoseStack poseStack, BufferBuilder buffer, float age, float alphaMult, float entityScale) {
        // Base animation growth * Entity Scale
        float animationScale = 1.0f + (age * 0.15f);
        float finalScale = animationScale * entityScale;

        poseStack.pushPose();
        poseStack.scale(finalScale, finalScale, finalScale);

        float alpha = Math.max(0, 1.0f - (age / 25.0f)) * alphaMult;

        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        // Render Core Crescent
        RenderHelper.renderCrescent(buffer, poseStack.last().pose(), null,
                1.5f, 0.5f, (float)Math.PI / 1.5f, 1.0f, 0.2f, 0.0f, alpha, 15728880);

        // Render Edge Glow
        RenderHelper.renderCrescent(buffer, poseStack.last().pose(), null,
                1.6f, 0.7f, (float)Math.PI / 1.5f, 0.0f, 0.0f, 0.0f, alpha * 0.6f, 15728880);

        Tesselator.getInstance().end();
        poseStack.popPose();
    }



    @Override
    public ResourceLocation getTextureLocation(BloodSlashEntity entity) {
        return BLANK_TEXTURE;
    }
}