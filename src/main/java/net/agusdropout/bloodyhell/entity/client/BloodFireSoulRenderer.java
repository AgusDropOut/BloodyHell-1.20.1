package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodFireSoulEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class BloodFireSoulRenderer extends EntityRenderer<BloodFireSoulEntity> {

    private final RandomSource random = RandomSource.create();

    public BloodFireSoulRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    // --- SECT 1: MAIN RENDER ---

    @Override
    public void render(BloodFireSoulEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Setup Render State
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        float time = entity.tickCount + partialTick;

        // Retrieve Synchronized Scale (Gem Upgrade)
        float gemScale = entity.getScale();

        poseStack.pushPose();
        poseStack.translate(0, 0.25, 0);

        // Apply Global Scale here so it affects all layers
        poseStack.scale(gemScale, gemScale, gemScale);

        // ===  INNER CORE ===
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Additive Glow

        poseStack.pushPose();

        // Heartbeat Pulse Animation
        float pulse = (float) Math.sin(time * 0.3f);
        float baseScale = 0.7f + (pulse * 0.05f);
        poseStack.scale(baseScale, baseScale, baseScale);

        // Rotation
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 15f));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(time * 5f));

        drawNoiseSphere(tesselator, buffer, poseStack.last().pose(), time,
                0.95f, 0.0f, 0.02f,  // PURE DEEP RED
                1.0f,                // Solid Core
                3.5f,                // Frequency
                0.15f,               // Low Threshold (Mostly Solid)
                true                 // Is Core
        );
        poseStack.popPose();

        // === OUTER SMOKE ===
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Standard Alpha

        poseStack.pushPose();
        // Slightly larger than core
        poseStack.scale(0.9f, 0.9f, 0.9f);

        // Counter Rotation
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-time * 5f));

        drawNoiseSphere(tesselator, buffer, poseStack.last().pose(), time + 500,
                0.02f, 0.0f, 0.0f,   // Black/Dark Gray
                0.6f,                // Semi-Opaque
                1.8f,                // Big blobs
                0.65f,               // HIGH THRESHOLD (More holes!)
                false                // Is Shell
        );
        poseStack.scale(gemScale, gemScale, gemScale);
        poseStack.popPose();

        poseStack.popPose(); // End Global Scale

        // Cleanup
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    // --- SECT 2: NOISE SPHERE LOGIC ---

    private void drawNoiseSphere(Tesselator tess, BufferBuilder buffer, Matrix4f pose, float time,
                                 float r, float g, float b, float maxAlpha, float frequency, float threshold, boolean isCore) {

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int segments = 32; // Resolution
        float radius = 1.0f;

        for (int i = 0; i < segments; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i) / segments);
            double lat1 = Math.PI * (-0.5 + (double) (i + 1) / segments);

            float y0 = (float) Math.sin(lat0) * radius;
            float y1 = (float) Math.sin(lat1) * radius;
            float r0 = (float) Math.cos(lat0) * radius;
            float r1 = (float) Math.cos(lat1) * radius;

            for (int j = 0; j < segments; j++) {
                double lon0 = 2 * Math.PI * (double) (j) / segments;
                double lon1 = 2 * Math.PI * (double) (j + 1) / segments;

                // Define 4 corners
                float x_tl = (float) Math.cos(lon0) * r0; float z_tl = (float) Math.sin(lon0) * r0; float y_tl = y0;
                float x_tr = (float) Math.cos(lon1) * r0; float z_tr = (float) Math.sin(lon1) * r0; float y_tr = y0;
                float x_bl = (float) Math.cos(lon0) * r1; float z_bl = (float) Math.sin(lon0) * r1; float y_bl = y1;
                float x_br = (float) Math.cos(lon1) * r1; float z_br = (float) Math.sin(lon1) * r1; float y_br = y1;

                // Apply Jitter to Core only (Electric feel)
                if (isCore) {
                    float jitter = 0.02f;
                    x_tl += (random.nextFloat() - 0.5f) * jitter; y_tl += (random.nextFloat() - 0.5f) * jitter; z_tl += (random.nextFloat() - 0.5f) * jitter;
                    x_tr += (random.nextFloat() - 0.5f) * jitter; y_tr += (random.nextFloat() - 0.5f) * jitter; z_tr += (random.nextFloat() - 0.5f) * jitter;
                    x_bl += (random.nextFloat() - 0.5f) * jitter; y_bl += (random.nextFloat() - 0.5f) * jitter; z_bl += (random.nextFloat() - 0.5f) * jitter;
                    x_br += (random.nextFloat() - 0.5f) * jitter; y_br += (random.nextFloat() - 0.5f) * jitter; z_br += (random.nextFloat() - 0.5f) * jitter;
                }

                // Calculate Noise for Alpha Cutout
                float cx = (x_tl + x_br) / 2f;
                float cy = (y_tl + y_br) / 2f;
                float cz = (z_tl + z_br) / 2f;

                double rawNoise = Math.sin(cx * frequency + time * 0.1)
                        * Math.cos((cy - time * 0.2) * frequency)
                        * Math.sin(cz * frequency + time * 0.05);

                float noise01 = (float) ((rawNoise + 1.0) / 2.0);

                float finalAlpha;
                if (noise01 < threshold) {
                    finalAlpha = 0.0f; // Cutout hole
                } else {
                    float remapped = (noise01 - threshold) / (1.0f - threshold);
                    finalAlpha = remapped * maxAlpha;
                }

                if (finalAlpha <= 0.01f) continue;

                // Color Logic
                float localR = r;
                if (isCore && y_tl < -0.3f) {
                    localR *= 0.6f; // Darken bottom
                }

                addVertex(buffer, pose, x_tl, y_tl, z_tl, localR, g, b, finalAlpha);
                addVertex(buffer, pose, x_bl, y_bl, z_bl, localR, g, b, finalAlpha);
                addVertex(buffer, pose, x_br, y_br, z_br, localR, g, b, finalAlpha);
                addVertex(buffer, pose, x_tr, y_tr, z_tr, localR, g, b, finalAlpha);
            }
        }
        tess.end();
    }

    private void addVertex(BufferBuilder buffer, Matrix4f pose, float x, float y, float z, float r, float g, float b, float a) {
        Vector3f vec = new Vector3f(x, y, z);
        vec.mulPosition(pose);
        buffer.vertex(vec.x(), vec.y(), vec.z()).color(r, g, b, a).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BloodFireSoulEntity entity) {
        return null;
    }
}