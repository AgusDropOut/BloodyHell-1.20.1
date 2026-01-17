package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.entity.projectile.BloodSphereEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BloodSphereRenderer extends EntityRenderer<BloodSphereEntity> {

    // ==========================================
    // VISUAL CONFIG
    // ==========================================
    private static final float GLOBAL_SCALE = 1.8f;
    private static final float CORE_RADIUS = 0.3f;
    private static final float HALO_RADIUS = 0.85f;

    // --- COLORS ---
    private static final int COLOR_CORE = 0x440000;
    private static final int COLOR_HALO = 0xBB0000;
    private static final int COLOR_ARC = 0xFF990000;
    private static final int COLOR_SPOT = 0xFFFF4444;
    private static final int COLOR_GROUND_WAVE = 0xFF0000; // Bright pure red for the floor wave

    // Transparencies
    private static final int ALPHA_HALO = 120;
    private static final int ALPHA_SPOT = 240;

    // Arcs
    private static final int ARC_COUNT = 10;
    private static final float ARC_WIDTH = 0.04f;
    private static final float ARC_SPEED = 0.15f;
    private static final float SPOT_SIZE = 0.15f;

    // Pulse
    private static final float PULSE_SPEED = 0.3f;
    private static final float PULSE_AMOUNT = 0.05f;

    // Ground Effect Config
    private static final float MAX_GROUND_DIST = 6.0f; // Max distance to show effect
    private static final float WAVE_MAX_SIZE = 3.5f;

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/misc/white.png");

    public BloodSphereRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodSphereEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        float time = entity.tickCount + partialTick;
        float pulse = 1.0f + (float)Math.sin(time * PULSE_SPEED) * PULSE_AMOUNT;
        float currentScale = GLOBAL_SCALE * pulse;
        poseStack.scale(currentScale, currentScale, currentScale);

        // 1. SINGULARITY (Void Portal)
        VertexConsumer portalConsumer = buffer.getBuffer(RenderType.endPortal());
        renderSphere(poseStack, portalConsumer, CORE_RADIUS * 0.8f, 0xFFFFFF, 255);

        // 2. BLOODY CORE
        VertexConsumer coreConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        renderSphere(poseStack, coreConsumer, CORE_RADIUS, COLOR_CORE, 200);

        // 3. ARCS AND SPOTS
        VertexConsumer lightningConsumer = buffer.getBuffer(RenderType.lightning());
        VertexConsumer spotConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        renderHypnoticPlasma(entity, poseStack, lightningConsumer, spotConsumer, time);

        // 4. OUTER MANTLE
        VertexConsumer haloConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        renderSphere(poseStack, haloConsumer, HALO_RADIUS, COLOR_HALO, ALPHA_HALO);

        // 5. DARKNESS AURA
        renderSphere(poseStack, haloConsumer, HALO_RADIUS * 1.05f, 0x000000, 40);

        poseStack.popPose();

        // ---------------------------------------------------------
        // 6. NEW GROUND ENERGY WAVE EFFECT
        // ---------------------------------------------------------
        renderGroundEffect(entity, partialTick, poseStack, buffer);

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderGroundEffect(BloodSphereEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        Level level = entity.level();
        Vec3 pos = entity.getPosition(partialTick);
        BlockPos blockPos = BlockPos.containing(pos);

        // Raycast downwards to find ground distance
        double groundY = -999;
        // Check 6 blocks down
        for (int i = 0; i <= (int)MAX_GROUND_DIST; i++) {
            BlockPos checkPos = blockPos.below(i);
            if (!level.getBlockState(checkPos).isAir() && level.getBlockState(checkPos).isSolidRender(level, checkPos)) {
                groundY = checkPos.getY() + 1.01; // Just above the block
                break;
            }
        }

        // If ground found and within range
        if (groundY != -999) {
            double dist = pos.y - groundY;
            if (dist >= 0 && dist <= MAX_GROUND_DIST) {
                float intensity = 1.0f - ((float)dist / MAX_GROUND_DIST); // 1.0 close, 0.0 far
                float time = entity.tickCount + partialTick;

                poseStack.pushPose();
                // Move poseStack relative to entity to position it on the floor
                // Entity render translates to entity pos (0,0,0 local). We need to move down.
                poseStack.translate(0, -dist, 0);

                // Rotate to lie flat on floor (X axis 90 deg)
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

                // Rotating effect
                poseStack.mulPose(Axis.ZP.rotationDegrees(time * 5.0f));

                // Pulsating size
                float waveScale = WAVE_MAX_SIZE * (0.8f + 0.2f * (float)Math.sin(time * 0.2f));
                poseStack.scale(waveScale, waveScale, waveScale);

                // Alpha based on distance
                int alpha = (int)(150 * intensity);

                // Render the Ring/Disk
                VertexConsumer waveConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
                renderEnergyRing(poseStack, waveConsumer, COLOR_GROUND_WAVE, alpha);

                poseStack.popPose();
            }
        }
    }

    private void renderEnergyRing(PoseStack poseStack, VertexConsumer consumer, int color, int alpha) {
        Matrix4f m = poseStack.last().pose();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // Draw a flat ring/disk using a triangle fan approximation
        // Inner radius 0.5, Outer radius 1.0
        float innerR = 0.4f;
        float outerR = 1.0f;
        int segments = 16;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * 2 * Math.PI / segments);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);

            float x1_in = (float)Math.cos(angle1) * innerR;
            float y1_in = (float)Math.sin(angle1) * innerR;
            float x1_out = (float)Math.cos(angle1) * outerR;
            float y1_out = (float)Math.sin(angle1) * outerR;

            float x2_in = (float)Math.cos(angle2) * innerR;
            float y2_in = (float)Math.sin(angle2) * innerR;
            float x2_out = (float)Math.cos(angle2) * outerR;
            float y2_out = (float)Math.sin(angle2) * outerR;

            // Quad for this segment
            // Inner vertices are more opaque, outer are transparent to create a soft edge
            addVertexRaw(consumer, m, x1_in, y1_in, r, g, b, alpha);
            addVertexRaw(consumer, m, x1_out, y1_out, r, g, b, 0); // Fade out edge
            addVertexRaw(consumer, m, x2_out, y2_out, r, g, b, 0); // Fade out edge
            addVertexRaw(consumer, m, x2_in, y2_in, r, g, b, alpha);
        }
    }

    private void addVertexRaw(VertexConsumer consumer, Matrix4f m, float x, float y, int r, int g, int b, int a) {
        consumer.vertex(m, x, y, 0)
                .color(r, g, b, a)
                .uv(0, 0) // UV doesn't matter much for white texture
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880) // Max brightness
                .normal(0, 0, 1)
                .endVertex();
    }

    // --- SPHERE GEOMETRY ---
    private void renderSphere(PoseStack poseStack, VertexConsumer consumer, float radius, int color, int alpha) {
        Matrix4f m = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int latSegments = 12;
        int lonSegments = 12;

        for (int i = 0; i < latSegments; i++) {
            double theta1 = Math.PI * i / latSegments - Math.PI / 2;
            double theta2 = Math.PI * (i + 1) / latSegments - Math.PI / 2;

            for (int j = 0; j < lonSegments; j++) {
                double phi1 = 2 * Math.PI * j / lonSegments;
                double phi2 = 2 * Math.PI * (j + 1) / lonSegments;

                addVertex(consumer, m, n, radius, theta1, phi1, r, g, b, alpha);
                addVertex(consumer, m, n, radius, theta2, phi1, r, g, b, alpha);
                addVertex(consumer, m, n, radius, theta2, phi2, r, g, b, alpha);
                addVertex(consumer, m, n, radius, theta1, phi2, r, g, b, alpha);
            }
        }
    }

    private void addVertex(VertexConsumer consumer, Matrix4f m, Matrix3f n, float r, double theta, double phi, int red, int green, int blue, int alpha) {
        float x = (float) (r * Math.cos(theta) * Math.cos(phi));
        float y = (float) (r * Math.sin(theta));
        float z = (float) (r * Math.cos(theta) * Math.sin(phi));

        float nx = x / r;
        float ny = y / r;
        float nz = z / r;

        consumer.vertex(m, x, y, z)
                .color(red, green, blue, alpha)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(n, nx, ny, nz)
                .endVertex();
    }

    // --- PLASMA ---
    private void renderHypnoticPlasma(BloodSphereEntity entity, PoseStack poseStack, VertexConsumer lineConsumer, VertexConsumer spotConsumer, float time) {
        Matrix4f matrix = poseStack.last().pose();
        RandomSource random = RandomSource.create(entity.getId());

        for (int i = 0; i < ARC_COUNT; i++) {
            float offset = i * (Mth.TWO_PI / ARC_COUNT);

            float animX = (float) Math.sin((time * ARC_SPEED) + offset + random.nextFloat());
            float animY = (float) Math.cos((time * ARC_SPEED * 0.9f) + (offset * 2));
            float animZ = (float) Math.sin((time * ARC_SPEED * 0.6f) + offset);

            Vector3f dir = new Vector3f(animX, animY, animZ).normalize();
            Vector3f start = new Vector3f(dir).mul(CORE_RADIUS);
            Vector3f end = new Vector3f(dir).mul(HALO_RADIUS);

            Vector3f mid = new Vector3f(start).lerp(end, 0.5f);
            float wave = (float) Math.sin(time * 0.4f + i) * 0.1f;
            mid.add(wave, wave, wave);

            drawLine(lineConsumer, matrix, start, mid, COLOR_ARC);
            drawLine(lineConsumer, matrix, mid, end, COLOR_ARC);

            renderSurfaceGlowSpot(poseStack, spotConsumer, end, dir, SPOT_SIZE, COLOR_SPOT);
        }
    }

    private void renderSurfaceGlowSpot(PoseStack poseStack, VertexConsumer consumer, Vector3f pos, Vector3f normal, float size, int color) {
        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);

        Vector3f up = new Vector3f(0, 1, 0);
        Quaternionf rotation = new Quaternionf().rotationTo(up, normal);
        poseStack.mulPose(rotation);

        Matrix4f m = poseStack.last().pose();

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int centerAlpha = ALPHA_SPOT;
        int edgeAlpha = 0;
        int segments = 8;

        float offsetY = 0.05f;

        addSimpleVertex(consumer, m, 0, offsetY, 0, r, g, b, centerAlpha);

        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float dx = (float) Math.cos(angle) * size;
            float dz = (float) Math.sin(angle) * size;
            addSimpleVertex(consumer, m, dx, offsetY, dz, r, g, b, edgeAlpha);
        }

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * 2 * Math.PI / segments);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);

            float x1 = (float) Math.cos(angle1) * size;
            float z1 = (float) Math.sin(angle1) * size;
            float x2 = (float) Math.cos(angle2) * size;
            float z2 = (float) Math.sin(angle2) * size;

            addSimpleVertex(consumer, m, 0, offsetY, 0, r, g, b, centerAlpha);
            addSimpleVertex(consumer, m, x1, offsetY, z1, r, g, b, edgeAlpha);
            addSimpleVertex(consumer, m, x2, offsetY, z2, r, g, b, edgeAlpha);
            addSimpleVertex(consumer, m, x2, offsetY, z2, r, g, b, edgeAlpha);
        }

        poseStack.popPose();
    }

    private void drawLine(VertexConsumer consumer, Matrix4f matrix, Vector3f start, Vector3f end, int color) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        addSimpleVertex(consumer, matrix, start.x, start.y, start.z, r, g, b, a);
        addSimpleVertex(consumer, matrix, start.x + ARC_WIDTH, start.y + ARC_WIDTH, start.z, r, g, b, a);
        addSimpleVertex(consumer, matrix, end.x + ARC_WIDTH, end.y + ARC_WIDTH, end.z, r, g, b, a);
        addSimpleVertex(consumer, matrix, end.x, end.y, end.z, r, g, b, a);
    }

    private void addSimpleVertex(VertexConsumer consumer, Matrix4f m, float x, float y, float z, int r, int g, int b, int a) {
        consumer.vertex(m, x, y, z)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(0, 1, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BloodSphereEntity entity) {
        return TEXTURE;
    }
}