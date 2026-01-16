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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BloodSphereRenderer extends EntityRenderer<BloodSphereEntity> {

    // ==========================================
    // CONFIGURACIÓN VISUAL
    // ==========================================
    private static final float GLOBAL_SCALE = 1.8f;
    private static final float CORE_RADIUS = 0.3f;
    private static final float HALO_RADIUS = 0.85f;

    // --- COLORES ---
    private static final int COLOR_CORE = 0x440000;
    private static final int COLOR_HALO = 0xBB0000;

    // Rayos: Rojo Oscuro / Carmesí Eléctrico
    private static final int COLOR_ARC = 0xFF990000;

    // Spots: AHORA MÁS BRILLANTES (Rojo claro casi naranja)
    // Antes: 0xFFCC0000 -> Ahora: 0xFFFF4444
    private static final int COLOR_SPOT = 0xFFFF4444;

    // Transparencias
    private static final int ALPHA_HALO = 120;

    // Alpha Spot: AHORA MÁS ALTO (Para que no se vea transparente/lavado)
    // Antes: 100 -> Ahora: 240 (Casi sólido en el centro)
    private static final int ALPHA_SPOT = 240;

    // Rayos
    private static final int ARC_COUNT = 10;
    private static final float ARC_WIDTH = 0.04f;
    private static final float ARC_SPEED = 0.15f;
    private static final float SPOT_SIZE = 0.15f;

    // Pulso
    private static final float PULSE_SPEED = 0.3f;
    private static final float PULSE_AMOUNT = 0.05f;

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

        // 1. SINGULARIDAD (Portal al Vacío)
        VertexConsumer portalConsumer = buffer.getBuffer(RenderType.endPortal());
        renderSphere(poseStack, portalConsumer, CORE_RADIUS * 0.8f, 0xFFFFFF, 255);

        // 2. NÚCLEO SANGRIENTO
        VertexConsumer coreConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        renderSphere(poseStack, coreConsumer, CORE_RADIUS, COLOR_CORE, 200);

        // 3. RAYOS Y SPOTS
        VertexConsumer lightningConsumer = buffer.getBuffer(RenderType.lightning());
        VertexConsumer spotConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        renderHypnoticPlasma(entity, poseStack, lightningConsumer, spotConsumer, time);

        // 4. MANTO EXTERIOR
        VertexConsumer haloConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        renderSphere(poseStack, haloConsumer, HALO_RADIUS, COLOR_HALO, ALPHA_HALO);

        // 5. AURA DE OSCURIDAD
        renderSphere(poseStack, haloConsumer, HALO_RADIUS * 1.05f, 0x000000, 40);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    // --- GEOMETRÍA ESFERA ---
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

        // Offset de altura (Y en espacio local) aumentado para evitar "borrado" visual
        // al mezclarse con la capa de la esfera.
        float offsetY = 0.05f;

        // Centro (Opaco)
        addSimpleVertex(consumer, m, 0, offsetY, 0, r, g, b, centerAlpha);

        // Borde (Transparente)
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float dx = (float) Math.cos(angle) * size;
            float dz = (float) Math.sin(angle) * size;
            addSimpleVertex(consumer, m, dx, offsetY, dz, r, g, b, edgeAlpha);
        }

        // Relleno manual
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