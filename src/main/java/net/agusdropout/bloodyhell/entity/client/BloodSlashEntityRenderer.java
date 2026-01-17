package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BloodSlashEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class BloodSlashEntityRenderer extends EntityRenderer<BloodSlashEntity> {

    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/misc/white.png");

    public static float GLOBAL_YAW_OFFSET = 180.0F;
    public static float MESH_ROTATION_Z = 90.0F;
    public static float MESH_ROTATION_X = 270.0F;
    public static float MESH_ROTATION_Y = 180.0F;

    public BloodSlashEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodSlashEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTicks;

        // 1. Get Rotation
        float lerpYaw = Mth.lerp(partialTicks, entity.getYawSynced(), entity.getYawSynced());
        float lerpPitch = Mth.lerp(partialTicks, entity.getPitchSynced(), entity.getPitchSynced());

        if (entity.tickCount > 1) {
            lerpYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
            lerpPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        }

        // --- CONFIG ---
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        RenderSystem.enableBlend();
        // Additive blending creates a "glowing" effect where trails overlap
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // =============================================================
        // 1. RENDER AIR SLASH & GHOSTS
        // =============================================================
        poseStack.pushPose();

        // A. Global Rotation (Trajectory)
        poseStack.mulPose(Axis.YP.rotationDegrees(GLOBAL_YAW_OFFSET - lerpYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(lerpPitch));

        // B. Local Mesh Orientation (Your fix)
        poseStack.mulPose(Axis.ZP.rotationDegrees(MESH_ROTATION_Z));
        poseStack.mulPose(Axis.XP.rotationDegrees(MESH_ROTATION_X));
        poseStack.mulPose(Axis.YP.rotationDegrees(MESH_ROTATION_Y));

        // 1. Main Body
        renderAirSlash(poseStack, buffer, age, 1.0f);

        // 2. Side Echoes (The ones you liked - Duplicates to the side)
        // Offset on Z axis (which acts as side/width in this rotation config)
        for (int i = 1; i <= 2; i++) {
            float offset = i * 0.25f;
            float fade = 0.5f / (i + 1);

            // Right Side
            poseStack.pushPose();
            poseStack.translate(0, 0, offset);
            renderAirSlash(poseStack, buffer, age, fade);
            poseStack.popPose();

            // Left Side
            poseStack.pushPose();
            poseStack.translate(0, 0, -offset);
            renderAirSlash(poseStack, buffer, age, fade);
            poseStack.popPose();
        }

        // 3. Back Trail (The "Leaving Behind" effect)
        // Offset on Y axis (which acts as Forward/Back in this rotation config)
        for (int i = 1; i <= 3; i++) {
            float backOffset = i * 0.6f;
            float fade = 0.6f / i;

            poseStack.pushPose();
            // Translate +Y to move "Backwards" relative to the slash face
            poseStack.translate(0, backOffset, 0);
            // Shrink slightly as it trails
            float trailScale = 1.0f - (i * 0.1f);
            poseStack.scale(trailScale, trailScale, 1.0f);

            renderAirSlash(poseStack, buffer, age, fade);
            poseStack.popPose();
        }

        poseStack.popPose(); // End Air Matrix

        // =============================================================
        // 2. RENDER FLOOR TRAIL
        // =============================================================
        // Restoring Standard Blending for floor to look more like a stain
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        renderFloorTrail(entity, partialTicks, poseStack, buffer, age, lerpYaw);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderAirSlash(PoseStack poseStack, BufferBuilder buffer, float age, float alphaMult) {
        float scale = 1.0f + (age * 0.15f);
        poseStack.scale(scale, scale, scale);
        float alpha = Math.max(0, 1.0f - (age / 25.0f)) * alphaMult;

        Matrix4f matrix = poseStack.last().pose();

        // Core
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        buildCrescentMesh(buffer, matrix, 1.5f, 0.5f, 1.0f, 0.2f, 0.0f, alpha);
        Tesselator.getInstance().end();

        // Edge
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        buildCrescentMesh(buffer, matrix, 1.6f, 0.7f, 0.5f, 0.0f, 0.0f, alpha * 0.6f);
        Tesselator.getInstance().end();
    }

    private void renderFloorTrail(BloodSlashEntity entity, float partialTick, PoseStack poseStack, BufferBuilder buffer, float age, float lerpYaw) {
        double entityY = Mth.lerp(partialTick, entity.yo, entity.getY());
        double groundY = -999;
        boolean foundGround = false;

        BlockPos.MutableBlockPos checkPos = entity.blockPosition().mutable();
        for(int i = 0; i < 5; i++) {
            if(!entity.level().isEmptyBlock(checkPos)) {
                groundY = checkPos.getY() + 1.02;
                foundGround = true;
                break;
            }
            checkPos.move(0, -1, 0);
        }

        if(foundGround) {
            poseStack.pushPose();

            double yOffset = groundY - entityY;
            poseStack.translate(0, yOffset, 0);

            // Rotate Y (Face direction)
            poseStack.mulPose(Axis.YP.rotationDegrees(GLOBAL_YAW_OFFSET - lerpYaw));

            // Rotate X (Lay flat)
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            double speed = entity.getDeltaMovement().horizontalDistance();
            float stretch = 1.0f + (float)(speed * 10.0f);
            float alpha = Math.max(0, 0.9f - (age / 18.0f));
            float scaleWidth = 1.0f + (age * 0.05f);

            // Scale (X=Width, Y=Length/Forward)
            poseStack.scale(scaleWidth, stretch, 1.0f);

            Matrix4f matrix = poseStack.last().pose();
            buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
            buildCrescentMesh(buffer, matrix, 1.8f, 1.5f, 0.7f, 0.0f, 0.0f, alpha);
            Tesselator.getInstance().end();
            poseStack.popPose();
        }
    }

    private void buildCrescentMesh(BufferBuilder buffer, Matrix4f matrix, float radius, float maxThickness, float r, float g, float b, float maxAlpha) {
        int segments = 12;
        float arcAngle = (float) Math.PI / 1.5f;

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            float angle = -arcAngle / 2.0f + t * arcAngle;

            float shapeFactor = Mth.sin(t * (float) Math.PI);
            float currentThickness = maxThickness * shapeFactor;
            float alpha = maxAlpha * shapeFactor;

            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            // Mesh on XY Plane
            float xInner = (radius - currentThickness / 2) * sin;
            float yInner = (radius - currentThickness / 2) * cos;
            float xOuter = (radius + currentThickness / 2) * sin;
            float yOuter = (radius + currentThickness / 2) * cos;

            buffer.vertex(matrix, xInner, yInner, 0).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, xOuter, yOuter, 0).color(r, g, b, alpha).endVertex();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BloodSlashEntity entity) {
        return BLANK_TEXTURE;
    }
}