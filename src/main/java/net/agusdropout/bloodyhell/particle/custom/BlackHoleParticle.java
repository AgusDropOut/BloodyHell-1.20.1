package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BlackHoleParticle extends Particle {

    // ==========================================
    // COLORES
    // ==========================================
    private static final Vector3f COLOR_RIM = rgb(255, 110, 10);
    private static final Vector3f COLOR_DISK = rgb(182, 139, 30);
    private static final Vector3f COLOR_SPARKLES = rgb(255, 220, 150);

    // LENTE: Blanco Crema (o un poco azulado si prefieres sci-fi)
    // Al ser procedural, este color teñirá la distorsión.
    private static final Vector3f COLOR_LENS = rgb(255, 250, 240);

    private static final Vector3f COLOR_ELDRITCH = rgb(100, 50, 10);

    // ==========================================

    // YA NO USAMOS TEXTURA. TODO ES CÓDIGO.

    private final float baseCoreSize = 2f;
    private final float baseRingSize = 4f;
    private final float baseLensSize = 2.5f; // Un poco más grande para envolver todo
    private final int lifetime = 250;
    private final long starSeed;

    protected BlackHoleParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 0;
        this.hasPhysics = false;
        this.starSeed = random.nextLong();
    }

    private static Vector3f rgb(int r, int g, int b) {
        return new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) {
            remove();
        }
        if (age > 20 && age < lifetime - 20) {
            if (random.nextInt(3) == 0) {
                spawnInfallingParticles();
            }
        }
    }

    private void spawnInfallingParticles() {
        double radius = 5.0 + random.nextDouble() * 3.0;
        double theta = random.nextDouble() * Math.PI * 2;
        double phi = random.nextDouble() * Math.PI - Math.PI / 2;
        double spawnX = this.x + radius * Math.cos(theta) * Math.cos(phi);
        double spawnY = this.y + radius * Math.sin(phi);
        double spawnZ = this.z + radius * Math.sin(theta) * Math.cos(phi);
        double speed = 0.4;
        SimpleParticleType type = random.nextBoolean() ? ModParticles.MAGIC_LINE_PARTICLE.get() : ParticleTypes.END_ROD;
        this.level.addParticle(type, spawnX, spawnY, spawnZ, (this.x - spawnX)*speed/radius, (this.y - spawnY)*speed/radius, (this.z - spawnZ)*speed/radius);
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        double px = Mth.lerp(partialTicks, xo, x) - camPos.x;
        double py = Mth.lerp(partialTicks, yo, y) - camPos.y;
        double pz = Mth.lerp(partialTicks, zo, z) - camPos.z;

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        float time = age + partialTicks;
        float lifeRatio = time / (float) lifetime;
        float scale = 1.0f;

        if (lifeRatio < 0.1f) {
            scale = (float) Math.sin((lifeRatio / 0.1f) * Math.PI / 2);
        } else if (lifeRatio > 0.9f) {
            scale = 1.0f - (lifeRatio - 0.9f) / 0.1f;
        }

        if (scale <= 0.01f) return;

        float currentCoreSize = baseCoreSize * scale;
        float currentRingSize = baseRingSize * scale;
        float currentLensSize = baseLensSize * scale;
        float rotation = time * 0.1f;

        // ==========================================
        // 1. LENTE GRAVITACIONAL (PROCEDURAL)
        // ==========================================
        // Usamos POSITION_COLOR (sin textura)
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Blend: Transparencia suave
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false); // No escribir en el Z-Buffer para que sea translúcido

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // ALPHA: 0.15f. Suficiente para verse como una "burbuja" pero transparente.
        renderDistortedSphere(buffer, px, py, pz, currentLensSize, -rotation * 0.5f,
                COLOR_LENS.x(), COLOR_LENS.y(), COLOR_LENS.z(), 0.15f);

        tess.end();

        // ==========================================
        // 2. RESTO DE ELEMENTOS (GEOMETRÍA)
        // ==========================================
        // El shader ya es PositionColor, no hace falta setearlo de nuevo.

        // --- DISCO DE ACRECIÓN ---
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Aditivo para luz
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int segments = 48;
        float ringInner = currentCoreSize * 1.1f;
        float ringOuter = currentRingSize;
        float tilt = 0.5f;
        float diskAlpha = 0.8f * Math.min(1.0f, scale * 2.0f);

        for (int i = 0; i < segments; i++) {
            double angle1 = (Math.PI * 2 * i) / segments + rotation;
            double angle2 = (Math.PI * 2 * (i + 1)) / segments + rotation;
            float x1_in = (float) (Math.cos(angle1) * ringInner); float z1_in = (float) (Math.sin(angle1) * ringInner);
            float x1_out = (float) (Math.cos(angle1) * ringOuter); float z1_out = (float) (Math.sin(angle1) * ringOuter);
            float x2_in = (float) (Math.cos(angle2) * ringInner); float z2_in = (float) (Math.sin(angle2) * ringInner);
            float x2_out = (float) (Math.cos(angle2) * ringOuter); float z2_out = (float) (Math.sin(angle2) * ringOuter);

            vertexRotated(buffer, px, py, pz, x1_out, 0, z1_out, tilt, rotation, COLOR_DISK.x(), COLOR_DISK.y(), COLOR_DISK.z(), 0.0f);
            vertexRotated(buffer, px, py, pz, x1_in, 0, z1_in, tilt, rotation, COLOR_DISK.x(), COLOR_DISK.y(), COLOR_DISK.z(), diskAlpha);
            vertexRotated(buffer, px, py, pz, x2_in, 0, z2_in, tilt, rotation, COLOR_DISK.x(), COLOR_DISK.y(), COLOR_DISK.z(), diskAlpha);
            vertexRotated(buffer, px, py, pz, x2_out, 0, z2_out, tilt, rotation, COLOR_DISK.x(), COLOR_DISK.y(), COLOR_DISK.z(), 0.0f);
        }

        // --- BORDE BRILLANTE ---
        renderSphere(buffer, px, py, pz, currentCoreSize * 1.15f,
                COLOR_RIM.x(), COLOR_RIM.y(), COLOR_RIM.z(), 0.65f * scale);

        tess.end();

        // --- NÚCLEO NEGRO ---
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float coreFlicker = (float) (Math.sin(time * 45.0f) * 0.015f + Math.sin(time * 27.0f) * 0.005f);
        float flickerCoreSize = currentCoreSize * (1.0f + coreFlicker);
        renderSphere(buffer, px, py, pz, flickerCoreSize, 0f, 0f, 0f, 1f);
        tess.end();

        // --- ESTRELLAS ---
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderProceduralSparkles(buffer, px, py, pz, flickerCoreSize * 0.98f, scale, rotation, camera.rotation());
        tess.end();

        // --- ELDRITCH SPIKES (Tentáculos) ---
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    // --- RENDERIZADO PROCEDURAL DEL LENTE (Sin texturas ni UVs) ---
    private void renderDistortedSphere(BufferBuilder buffer, double px, double py, double pz, float radius, float rotationOffset, float r, float g, float b, float a) {
        int latSegments = 24; // Más segmentos para que la "malla" de energía se vea suave
        int lonSegments = 32;

        for (int i = 0; i < latSegments; i++) {
            double theta1 = Math.PI * i / latSegments - Math.PI / 2;
            double theta2 = Math.PI * (i + 1) / latSegments - Math.PI / 2;

            for (int j = 0; j < lonSegments; j++) {
                double phi1 = 2 * Math.PI * j / lonSegments + rotationOffset;
                double phi2 = 2 * Math.PI * (j + 1) / lonSegments + rotationOffset;

                // Ruido matemático para simular distorsión gravitacional
                float noise = (float) Math.sin(phi1 * 5 + rotationOffset) * 0.15f;
                float finalRadius = radius + noise;

                // Renderizamos QUADS de puro color transparente
                // Al no usar texturas, esto crea una "cáscara" translúcida deformada.
                addSphereVertex(buffer, px, py, pz, finalRadius, theta1, phi1, r, g, b, a);
                addSphereVertex(buffer, px, py, pz, finalRadius, theta2, phi1, r, g, b, a);
                addSphereVertex(buffer, px, py, pz, finalRadius, theta2, phi2, r, g, b, a);
                addSphereVertex(buffer, px, py, pz, finalRadius, theta1, phi2, r, g, b, a);
            }
        }
    }

    // --- EL RESTO DE HELPERS (Igual que antes) ---



    private void renderProceduralSparkles(BufferBuilder buffer, double px, double py, double pz, float radius, float scale, float globalRotation, Quaternionf camRot) {
        RandomSource seededRandom = RandomSource.create(this.starSeed);
        int starCount = 200;
        float starSizeBase = 0.04f * scale;
        for (int i = 0; i < starCount; i++) {
            double theta = seededRandom.nextDouble() * Math.PI * 2;
            double phi = Math.acos(2.0 * seededRandom.nextDouble() - 1.0);
            double animTheta = theta + globalRotation * (0.5 + seededRandom.nextDouble() * 0.5);
            double currentRadius = radius * (0.3 + seededRandom.nextDouble() * 0.7);
            float dx = (float) (currentRadius * Math.sin(phi) * Math.cos(animTheta));
            float dy = (float) (currentRadius * Math.sin(phi) * Math.sin(animTheta));
            float dz = (float) (currentRadius * Math.cos(phi));
            float blink = 0.5f + 0.5f * (float) Math.sin(globalRotation * 5.0 + i);
            float alpha = 0.8f * blink;
            renderBillboardQuad(buffer, px + dx, py + dy, pz + dz, starSizeBase, COLOR_SPARKLES.x(), COLOR_SPARKLES.y(), COLOR_SPARKLES.z(), alpha, camRot);
        }
    }

    private void renderBillboardQuad(BufferBuilder buffer, double x, double y, double z, float size, float r, float g, float b, float a, Quaternionf camRot) {
        Vector3f[] vertices = {new Vector3f(-size, -size, 0), new Vector3f(-size, size, 0), new Vector3f(size, size, 0), new Vector3f(size, -size, 0)};
        for (Vector3f vertex : vertices) {
            vertex.rotate(camRot);
            buffer.vertex(x + vertex.x(), y + vertex.y(), z + vertex.z()).color(r, g, b, a).endVertex();
        }
    }

    private void vertexRotated(BufferBuilder b, double px, double py, double pz, float x, float y, float z, float pitch, float yaw, float red, float green, float blue, float alpha) {
        float y1 = (float) (y * Math.cos(pitch) - z * Math.sin(pitch));
        float z1 = (float) (y * Math.sin(pitch) + z * Math.cos(pitch));
        b.vertex(px + x, py + y1, pz + z1).color(red, green, blue, alpha).endVertex();
    }

    private void renderSphere(BufferBuilder buffer, double px, double py, double pz, float radius, float r, float g, float b, float a) {
        int latSegments = 16; int lonSegments = 24;
        for (int i = 0; i < latSegments; i++) {
            double theta1 = Math.PI * i / latSegments - Math.PI / 2; double theta2 = Math.PI * (i + 1) / latSegments - Math.PI / 2;
            for (int j = 0; j < lonSegments; j++) {
                double phi1 = 2 * Math.PI * j / lonSegments; double phi2 = 2 * Math.PI * (j + 1) / lonSegments;
                addSphereVertex(buffer, px, py, pz, radius, theta1, phi1, r, g, b, a);
                addSphereVertex(buffer, px, py, pz, radius, theta2, phi1, r, g, b, a);
                addSphereVertex(buffer, px, py, pz, radius, theta2, phi2, r, g, b, a);
                addSphereVertex(buffer, px, py, pz, radius, theta1, phi2, r, g, b, a);
            }
        }
    }

    private void addSphereVertex(BufferBuilder buffer, double px, double py, double pz, float r, double theta, double phi, float red, float green, float blue, float alpha) {
        float x = (float) (px + r * Math.cos(theta) * Math.cos(phi));
        float y = (float) (py + r * Math.sin(theta));
        float z = (float) (pz + r * Math.cos(theta) * Math.sin(phi));
        buffer.vertex(x, y, z).color(red, green, blue, alpha).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.CUSTOM; }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new BlackHoleParticle(world, x, y, z);
        }
    }
}