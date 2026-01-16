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
public class EnergyVortexParticle extends Particle {

    private final int lifetime = 60; // Dura menos que el agujero negro

    protected EnergyVortexParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 0;
        this.hasPhysics = false;
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

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Luz pura

        float time = age + partialTicks;
        float rotation = time * 0.2f; // Gira más rápido
        float scale = 1.0f;

        // Animación simple de entrada y salida
        if (age < 10) scale = age / 10f;
        if (age > lifetime - 10) scale = (lifetime - age) / 10f;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Renderizar dos jets (arriba y abajo)
        renderJet(buffer, px, py, pz, scale, rotation, 1);
        renderJet(buffer, px, py, pz, scale, rotation, -1);

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    private void renderJet(BufferBuilder buffer, double px, double py, double pz, float scale, float rotation, int dir) {
        float height = 5.0f * scale;
        float maxRadius = 1.5f * scale;
        float minRadius = 0.1f * scale;

        int layers = 8;
        int segments = 8;

        // Color Personalizable (Cyan)
        float r = 0.2f;
        float g = 0.8f;
        float b = 1.0f;

        for (int i = 0; i < layers; i++) {
            float h1 = (float) i / layers * height;
            float h2 = (float) (i + 1) / layers * height;

            // Forma de trompeta
            float rad1 = Mth.lerp((float) Math.pow((float)i/layers, 2), minRadius, maxRadius);
            float rad2 = Mth.lerp((float) Math.pow((float)(i+1)/layers, 2), minRadius, maxRadius);

            float twist1 = rotation * 2.0f + (h1 * 0.5f);
            float twist2 = rotation * 2.0f + (h2 * 0.5f);

            float alpha1 = (1.0f - (float)i/layers) * 0.8f;
            float alpha2 = (1.0f - (float)(i+1)/layers) * 0.8f;

            for (int j = 0; j < segments; j++) {
                float ang1 = (float) j / segments * Mth.TWO_PI;
                float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

                float x1a = Mth.cos(ang1 + twist1) * rad1; float z1a = Mth.sin(ang1 + twist1) * rad1;
                float x1b = Mth.cos(ang2 + twist1) * rad1; float z1b = Mth.sin(ang2 + twist1) * rad1;
                float x2a = Mth.cos(ang1 + twist2) * rad2; float z2a = Mth.sin(ang1 + twist2) * rad2;
                float x2b = Mth.cos(ang2 + twist2) * rad2; float z2b = Mth.sin(ang2 + twist2) * rad2;

                buffer.vertex(px + x1a, py + h1 * dir, pz + z1a).color(r, g, b, alpha1).endVertex();
                buffer.vertex(px + x1b, py + h1 * dir, pz + z1b).color(r, g, b, alpha1).endVertex();
                buffer.vertex(px + x2b, py + h2 * dir, pz + z2b).color(r, g, b, alpha2).endVertex();
                buffer.vertex(px + x2a, py + h2 * dir, pz + z2a).color(r, g, b, alpha2).endVertex();
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.CUSTOM; }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new EnergyVortexParticle(world, x, y, z);
        }
    }
}