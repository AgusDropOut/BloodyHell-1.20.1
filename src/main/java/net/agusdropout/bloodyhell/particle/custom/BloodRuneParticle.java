package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BloodRuneParticle extends Particle {

    private final float baseRadius;
    private final float cylinderHeight;

    protected BloodRuneParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 80;
        this.gravity = 0;
        this.hasPhysics = false;
        this.baseRadius = 4.5f;
        this.cylinderHeight = 8.0f; // Height of the shining wave cylinder
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) {
            remove();
        }
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        double px = Mth.lerp(partialTicks, xo, x) - camPos.x;
        double py = Mth.lerp(partialTicks, yo, y) - camPos.y;
        double pz = Mth.lerp(partialTicks, zo, z) - camPos.z;

        // --- RENDER SETUP ---
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Additive blending (Glowing)
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float time = age + partialTicks;

        // Master Fade
        float alphaFade = 1.0f;
        if (age < 10) alphaFade = age / 10f;
        else if (age > lifetime - 20) alphaFade = (lifetime - age) / 20f;

        // 1. RENDER THE FLOOR WAVES ("Sea Shore")
        renderFloorRune(buffer, px, py + 0.05, pz, time, alphaFade);

        // 2. RENDER THE SHINING CYLINDER
        renderCylinder(buffer, px, py, pz, time, alphaFade);

        tess.end();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    private void renderFloorRune(BufferBuilder buffer, double px, double py, double pz, float time, float alphaFade) {
        int segments = 64;
        float rotation = time * 0.02f;

        // We draw 3 layers of "Waves" to look like a shore
        for (int layer = 0; layer < 3; layer++) {

            // Each layer has a slightly different radius pulsing
            float waveOffset = (float) Math.sin((time * 0.1f) + (layer * 1.5f));
            float currentRadius = baseRadius + (waveOffset * 0.3f);
            float thickness = 0.8f + (layer * 0.2f);

            // Layer Colors (Bi-luminescent Red/Pink mix)
            float r = 1.0f;
            float g = layer * 0.1f; // Slight orange/pink tint for inner layers
            float b = 0.1f + (layer * 0.1f);
            float layerAlpha = 0.4f * alphaFade; // More transparent than the hard border

            for (int i = 0; i < segments; i++) {
                double angle1 = (2 * Math.PI * i) / segments + rotation;
                double angle2 = (2 * Math.PI * (i + 1)) / segments + rotation;

                // Perlin Noise for jagged "water" edges
                float noise = (float) Perlin.noise(i * 0.15, time * 0.05 + layer);

                float rInner = currentRadius - thickness + (noise * 0.3f);
                float rOuter = currentRadius + (noise * 0.1f);

                // Vertices
                float x1_in = (float) (px + Math.cos(angle1) * rInner);
                float z1_in = (float) (pz + Math.sin(angle1) * rInner);
                float x1_out = (float) (px + Math.cos(angle1) * rOuter);
                float z1_out = (float) (pz + Math.sin(angle1) * rOuter);

                float x2_in = (float) (px + Math.cos(angle2) * rInner);
                float z2_in = (float) (pz + Math.sin(angle2) * rInner);
                float x2_out = (float) (px + Math.cos(angle2) * rOuter);
                float z2_out = (float) (pz + Math.sin(angle2) * rOuter);

                // Inner = Transparent, Outer = Colored
                buffer.vertex(x1_out, py, z1_out).color(r, g, b, layerAlpha).endVertex();
                buffer.vertex(x1_in, py, z1_in).color(r, g, b, 0.0f).endVertex(); // Fade to center
                buffer.vertex(x2_in, py, z2_in).color(r, g, b, 0.0f).endVertex();
                buffer.vertex(x2_out, py, z2_out).color(r, g, b, layerAlpha).endVertex();
            }
        }
    }

    private void renderCylinder(BufferBuilder buffer, double px, double py, double pz, float time, float alphaFade) {
        int segments = 32;
        float radius = baseRadius * 0.95f; // Slightly smaller than the floor ring

        float r = 1.0f;
        float g = 0.0f;
        float b = 0.1f;
        float baseAlpha = 0.15f * alphaFade; // Very transparent

        for (int i = 0; i < segments; i++) {
            double angle1 = (2 * Math.PI * i) / segments;
            double angle2 = (2 * Math.PI * (i + 1)) / segments;

            float x1 = (float) (px + Math.cos(angle1) * radius);
            float z1 = (float) (pz + Math.sin(angle1) * radius);
            float x2 = (float) (px + Math.cos(angle2) * radius);
            float z2 = (float) (pz + Math.sin(angle2) * radius);

            // Bottom Vertices (On ground, Alpha = baseAlpha)
            // Top Vertices (High up, Alpha = 0)

            // We draw the walls of the cylinder
            // Vertex 1: Bottom Right
            buffer.vertex(x1, py, z1).color(r, g, b, baseAlpha).endVertex();
            // Vertex 2: Top Right (Fade out)
            buffer.vertex(x1, py + cylinderHeight, z1).color(r, g, b, 0.0f).endVertex();
            // Vertex 3: Top Left (Fade out)
            buffer.vertex(x2, py + cylinderHeight, z2).color(r, g, b, 0.0f).endVertex();
            // Vertex 4: Bottom Left
            buffer.vertex(x2, py, z2).color(r, g, b, baseAlpha).endVertex();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new BloodRuneParticle(world, x, y, z);
        }
    }

    public static class Perlin {
        private static final int[] perm = new int[512];
        private static final int[] p = new int[256];
        static {
            for (int i = 0; i < 256; i++) p[i] = i;
            java.util.Random rand = new java.util.Random(1234);
            for (int i = 0; i < 256; i++) {
                int j = rand.nextInt(256 - i) + i;
                int tmp = p[i]; p[i] = p[j]; p[j] = tmp;
                perm[i] = perm[i + 256] = p[i];
            }
        }
        private static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
        private static double lerp(double t, double a, double b) { return a + t * (b - a); }
        private static double grad(int hash, double x, double y) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
        public static double noise(double x, double y) {
            int X = (int)Math.floor(x) & 255;
            int Y = (int)Math.floor(y) & 255;
            x -= Math.floor(x);
            y -= Math.floor(y);
            double u = fade(x);
            double v = fade(y);
            int A = perm[X] + Y, B = perm[X + 1] + Y;
            return lerp(v, lerp(u, grad(perm[A], x, y), grad(perm[B], x - 1, y)),
                    lerp(u, grad(perm[A + 1], x, y - 1), grad(perm[B + 1], x - 1, y - 1)));
        }
    }
}