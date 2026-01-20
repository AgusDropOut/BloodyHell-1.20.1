package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.entity.projectile.BloodFireSoulProjectile;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class BloodFireSoulRenderer extends EntityRenderer<BloodFireSoulProjectile> {

    private final RandomSource random = RandomSource.create();

    public BloodFireSoulRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodFireSoulProjectile entity) {
        return null;
    }

    @Override
    public void render(BloodFireSoulProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        float time = entity.tickCount + partialTick;

        poseStack.pushPose();
        poseStack.translate(0, 0.25, 0);

        // === LAYER 1: INNER CORE ===
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Additive Glow

        poseStack.pushPose();

        // Heartbeat Pulse
        float pulse = (float) Math.sin(time * 0.3f);
        float baseScale = 0.7f + (pulse * 0.05f);
        poseStack.scale(baseScale, baseScale, baseScale); // Uniform Scale (Sphere)

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

        // === LAYER 2: OUTER SMOKE ===
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Standard Alpha

        poseStack.pushPose();
        poseStack.scale(0.9f, 0.9f, 0.9f); // Slightly larger sphere

        // Counter Rotation
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-time * 5f));

        drawNoiseSphere(tesselator, buffer, poseStack.last().pose(), time + 500,
                0.02f, 0.0f, 0.0f,   // Black/Dark Gray
                0.6f,                // Semi-Opaque
                1.8f,                // Big blobs
                0.65f,               // HIGH THRESHOLD (More holes!)
                false                // Is Shell
        );
        poseStack.popPose();

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void drawNoiseSphere(Tesselator tess, BufferBuilder buffer, Matrix4f pose, float time,
                                 float r, float g, float b, float maxAlpha, float frequency, float threshold, boolean isCore) {

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // OPTIMIZATION: 16 Segments is enough for iGPUs, especially with noise
        int segments = 32;
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

                // --- SPHERE GEOMETRY ---
                // We define 4 corners explicitly to keep variables clean

                // TL: Top Left (lat0, lon0)
                float x_tl = (float) Math.cos(lon0) * r0; float z_tl = (float) Math.sin(lon0) * r0; float y_tl = y0;

                // TR: Top Right (lat0, lon1)
                float x_tr = (float) Math.cos(lon1) * r0; float z_tr = (float) Math.sin(lon1) * r0; float y_tr = y0;

                // BL: Bottom Left (lat1, lon0)
                float x_bl = (float) Math.cos(lon0) * r1; float z_bl = (float) Math.sin(lon0) * r1; float y_bl = y1;

                // BR: Bottom Right (lat1, lon1)
                float x_br = (float) Math.cos(lon1) * r1; float z_br = (float) Math.sin(lon1) * r1; float y_br = y1;

                // --- VERTEX JITTER ---
                if (isCore) {
                    float jitter = 0.02f;
                    x_tl += (random.nextFloat() - 0.5f) * jitter; y_tl += (random.nextFloat() - 0.5f) * jitter; z_tl += (random.nextFloat() - 0.5f) * jitter;
                    x_tr += (random.nextFloat() - 0.5f) * jitter; y_tr += (random.nextFloat() - 0.5f) * jitter; z_tr += (random.nextFloat() - 0.5f) * jitter;
                    x_bl += (random.nextFloat() - 0.5f) * jitter; y_bl += (random.nextFloat() - 0.5f) * jitter; z_bl += (random.nextFloat() - 0.5f) * jitter;
                    x_br += (random.nextFloat() - 0.5f) * jitter; y_br += (random.nextFloat() - 0.5f) * jitter; z_br += (random.nextFloat() - 0.5f) * jitter;
                }

                // --- NOISE ---
                // Use center point for noise calculation
                float cx = (x_tl + x_br) / 2f;
                float cy = (y_tl + y_br) / 2f;
                float cz = (z_tl + z_br) / 2f;

                double rawNoise = Math.sin(cx * frequency + time * 0.1)
                        * Math.cos((cy - time * 0.2) * frequency) // Scrolling Up
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

                // --- COLOR LOGIC ---
                float localR = r;

                // Darken bottom of core sphere for depth
                if (isCore && y_tl < -0.3f) {
                    localR *= 0.6f;
                }

                // --- DRAW QUAD (Counter-Clockwise Winding: TL -> BL -> BR -> TR) ---
                // Note: Minecraft/OpenGL winding can be tricky.
                // Standard: TL -> BL -> BR -> TR is usually correct for avoiding backface culling issues if Cull is enabled.
                // We disabled cull, so winding order affects lighting normals mostly, but here we use unlit PositionColor shader.

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
}