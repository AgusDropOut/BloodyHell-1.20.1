package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BloodNovaEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BloodNovaRenderer extends EntityRenderer<BloodNovaEntity> {

    private static final ResourceLocation BLANK = new ResourceLocation(BloodyHell.MODID, "textures/misc/white.png");

    private static final Vector3f COL_CORE = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Vector3f COL_RIM = new Vector3f(0.5f, 0.0f, 0.05f);
    private static final Vector3f COL_DISK_IN = new Vector3f(0.9f, 0.05f, 0.1f);
    private static final Vector3f COL_DISK_OUT = new Vector3f(0.1f, 0.0f, 0.0f);
    private static final Vector3f COL_SPARKLES = new Vector3f(1.0f, 0.2f, 0.2f);

    public BloodNovaRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodNovaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTicks;
        float scale = 2.5f + 0.1f * Mth.sin(age * 0.1f);
        if (entity.tickCount > 150) scale *= Math.max(0, (180 - age) / 30.0f);

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        poseStack.mulPose(Axis.YP.rotationDegrees(age * 1.5f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(age * 0.03f) * 15f));

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = poseStack.last().pose(); // CAPTURE THE MATRIX

        // 1. JETS
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float jetRot = age * 0.2f;
        renderJet(buffer, matrix, 1.0f, jetRot, 1);
        renderJet(buffer, matrix, 1.0f, jetRot, -1);
        tess.end();

        // 2. CORE
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderSphere(buffer, matrix, 0.4f, COL_CORE, 1.0f);
        renderSphere(buffer, matrix, 0.45f, COL_RIM, 0.6f);
        tess.end();

        // 3. SPARKLES (FIXED)
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Quaternionf camRot = camera.rotation();

        // Pass the Matrix here!
        renderProceduralSparkles(buffer, matrix, 0.38f, 1.0f, age * 0.1f, camRot, entity.getUUID().getLeastSignificantBits());

        tess.end();

        // 4. DISK
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderDisk(buffer, matrix, 0.5f, 1.3f, age * 0.05f, COL_DISK_IN, COL_DISK_OUT);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(65));
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * 2.0f));
        renderDisk(buffer, poseStack.last().pose(), 0.6f, 1.1f, 0, COL_DISK_IN, COL_DISK_OUT);
        poseStack.popPose();

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    // FIXED: Now accepts Matrix4f and uses it in renderBillboardQuad
    private void renderProceduralSparkles(BufferBuilder buffer, Matrix4f matrix, float radius, float scale, float globalRotation, Quaternionf camRot, long seed) {
        RandomSource seededRandom = RandomSource.create(seed);
        int starCount = 80;
        float starSizeBase = 0.03f * scale;

        for (int i = 0; i < starCount; i++) {
            double theta = seededRandom.nextDouble() * Math.PI * 2;
            double phi = Math.acos(2.0 * seededRandom.nextDouble() - 1.0);

            double animTheta = theta + globalRotation * (0.5 + seededRandom.nextDouble() * 0.5);
            double currentRadius = radius * (seededRandom.nextDouble() * 0.9);

            float dx = (float) (currentRadius * Math.sin(phi) * Math.cos(animTheta));
            float dy = (float) (currentRadius * Math.sin(phi) * Math.sin(animTheta));
            float dz = (float) (currentRadius * Math.cos(phi));

            float blink = 0.5f + 0.5f * (float) Math.sin(globalRotation * 8.0 + i);
            float alpha = 1.0f * blink;

            renderBillboardQuad(buffer, matrix, dx, dy, dz, starSizeBase, COL_SPARKLES.x, COL_SPARKLES.y, COL_SPARKLES.z, alpha, camRot);
        }
    }

    // FIXED: Multiplies vertex position by the Matrix
    private void renderBillboardQuad(BufferBuilder buffer, Matrix4f matrix, float x, float y, float z, float size, float r, float g, float b, float a, Quaternionf camRot) {
        Vector3f[] vertices = {
                new Vector3f(-size, -size, 0),
                new Vector3f(-size, size, 0),
                new Vector3f(size, size, 0),
                new Vector3f(size, -size, 0)
        };

        for (Vector3f vertex : vertices) {
            // 1. Rotate to face camera
            vertex.rotate(camRot);

            // 2. Translate to sparkle position
            vertex.add(x, y, z);

            // 3. Apply the Entity's PoseStack Matrix (Scaling, Rotation, World Pos)
            buffer.vertex(matrix, vertex.x(), vertex.y(), vertex.z())
                    .color(r, g, b, a).endVertex();
        }
    }

    private void renderJet(BufferBuilder buffer, Matrix4f matrix, float scale, float rotation, int dir) {
        float height = 4.0f * scale;
        float maxRadius = 1.2f * scale;
        float minRadius = 0.2f * scale;
        int layers = 10; int segments = 12;
        float r = 0.8f; float g = 0.0f; float b = 0.1f;
        for (int i = 0; i < layers; i++) {
            float h1 = (float) i / layers * height; float h2 = (float) (i + 1) / layers * height;
            float rad1 = Mth.lerp((float) Math.pow((float)i/layers, 2), minRadius, maxRadius);
            float rad2 = Mth.lerp((float) Math.pow((float)(i+1)/layers, 2), minRadius, maxRadius);
            float twist1 = rotation * 2.0f + (h1 * 0.5f); float twist2 = rotation * 2.0f + (h2 * 0.5f);
            float alpha1 = (1.0f - (float)i/layers) * 0.7f; float alpha2 = (1.0f - (float)(i+1)/layers) * 0.7f;
            for (int j = 0; j < segments; j++) {
                float ang1 = (float) j / segments * Mth.TWO_PI; float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;
                float x1a = Mth.cos(ang1 + twist1) * rad1; float z1a = Mth.sin(ang1 + twist1) * rad1;
                float x1b = Mth.cos(ang2 + twist1) * rad1; float z1b = Mth.sin(ang2 + twist1) * rad1;
                float x2a = Mth.cos(ang1 + twist2) * rad2; float z2a = Mth.sin(ang1 + twist2) * rad2;
                float x2b = Mth.cos(ang2 + twist2) * rad2; float z2b = Mth.sin(ang2 + twist2) * rad2;
                buffer.vertex(matrix, x1a, h1 * dir, z1a).color(r, g, b, alpha1).endVertex();
                buffer.vertex(matrix, x1b, h1 * dir, z1b).color(r, g, b, alpha1).endVertex();
                buffer.vertex(matrix, x2b, h2 * dir, z2b).color(r, g, b, alpha2).endVertex();
                buffer.vertex(matrix, x2a, h2 * dir, z2a).color(r, g, b, alpha2).endVertex();
            }
        }
    }

    private void renderDisk(BufferBuilder buffer, Matrix4f matrix, float innerRadius, float outerRadius, float rotation, Vector3f cIn, Vector3f cOut) {
        int segments = 24;
        for (int i = 0; i < segments; i++) {
            float a1 = (float) (Math.PI * 2 * i / segments) + rotation; float a2 = (float) (Math.PI * 2 * (i + 1) / segments) + rotation;
            float n1 = 1.0f + 0.15f * Mth.sin(a1 * 6); float n2 = 1.0f + 0.15f * Mth.sin(a2 * 6);
            float x1_in = Mth.cos(a1) * innerRadius; float z1_in = Mth.sin(a1) * innerRadius;
            float x2_in = Mth.cos(a2) * innerRadius; float z2_in = Mth.sin(a2) * innerRadius;
            float x1_out = Mth.cos(a1) * outerRadius * n1; float z1_out = Mth.sin(a1) * outerRadius * n1;
            float x2_out = Mth.cos(a2) * outerRadius * n2; float z2_out = Mth.sin(a2) * outerRadius * n2;
            buffer.vertex(matrix, x1_in, 0, z1_in).color(cIn.x, cIn.y, cIn.z, 0.9f).endVertex();
            buffer.vertex(matrix, x1_out, 0, z1_out).color(cOut.x, cOut.y, cOut.z, 0.0f).endVertex();
            buffer.vertex(matrix, x2_out, 0, z2_out).color(cOut.x, cOut.y, cOut.z, 0.0f).endVertex();
            buffer.vertex(matrix, x2_in, 0, z2_in).color(cIn.x, cIn.y, cIn.z, 0.9f).endVertex();
        }
    }

    private void renderSphere(BufferBuilder buffer, Matrix4f matrix, float r, Vector3f c, float a) {
        int lat = 8; int lon = 12;
        for (int i = 0; i < lat; i++) {
            float theta1 = (float) (Math.PI * i / lat); float theta2 = (float) (Math.PI * (i + 1) / lat);
            for (int j = 0; j < lon; j++) {
                float phi1 = (float) (Math.PI * 2 * j / lon); float phi2 = (float) (Math.PI * 2 * (j + 1) / lon);
                float x1 = r * Mth.sin(theta1) * Mth.cos(phi1); float y1 = r * Mth.cos(theta1); float z1 = r * Mth.sin(theta1) * Mth.sin(phi1);
                float x2 = r * Mth.sin(theta2) * Mth.cos(phi1); float y2 = r * Mth.cos(theta2); float z2 = r * Mth.sin(theta2) * Mth.sin(phi1);
                float x3 = r * Mth.sin(theta2) * Mth.cos(phi2); float y3 = r * Mth.cos(theta2); float z3 = r * Mth.sin(theta2) * Mth.sin(phi2);
                float x4 = r * Mth.sin(theta1) * Mth.cos(phi2); float y4 = r * Mth.cos(theta1); float z4 = r * Mth.sin(theta1) * Mth.sin(phi2);
                buffer.vertex(matrix, x1, y1, z1).color(c.x, c.y, c.z, a).endVertex();
                buffer.vertex(matrix, x2, y2, z2).color(c.x, c.y, c.z, a).endVertex();
                buffer.vertex(matrix, x3, y3, z3).color(c.x, c.y, c.z, a).endVertex();
                buffer.vertex(matrix, x4, y4, z4).color(c.x, c.y, c.z, a).endVertex();
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BloodNovaEntity entity) {
        return BLANK;
    }
}