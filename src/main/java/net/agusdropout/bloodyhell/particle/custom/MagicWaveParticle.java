package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MagicWaveParticle extends Particle {
    private final ResourceLocation texture = new ResourceLocation(BloodyHell.MODID, "textures/particle/yellow.png");
    private final float baseRadius = 4f;
    private final float growthRate = 0.1f; // cuanto crece por tick
    private final int latSegments = 16; // "paralelos"
    private final int lonSegments = 32; // "meridianos"

    protected MagicWaveParticle(ClientLevel level, double x, double y, double z, int maxLifeTime) {
        super(level, x, y, z);
        this.lifetime = maxLifeTime;
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

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float time = age + partialTicks;
        float alpha = Mth.clamp(1.0f - (time / lifetime), 0f, 1f);
        float radius = baseRadius + growthRate * time;

        double minTheta = -Math.PI / 32;
        double maxTheta = Math.PI / 32;

        float r = 1f, g = 0.9f, b = 0.3f;

        for (int i = 0; i < latSegments; i++) {
            double theta1 = Math.PI * i / latSegments - Math.PI / 2;
            double theta2 = Math.PI * (i + 1) / latSegments - Math.PI / 2;
            if (theta2 < minTheta || theta1 > maxTheta) continue;

            for (int j = 0; j < lonSegments; j++) {
                double phi1 = 2 * Math.PI * j / lonSegments;
                double phi2 = 2 * Math.PI * (j + 1) / lonSegments;

                float x1 = (float) (px + radius * Math.cos(theta1) * Math.cos(phi1));
                float y1 = (float) (py + radius * Math.sin(theta1));
                float z1 = (float) (pz + radius * Math.cos(theta1) * Math.sin(phi1));

                float x2 = (float) (px + radius * Math.cos(theta2) * Math.cos(phi1));
                float y2 = (float) (py + radius * Math.sin(theta2));
                float z2 = (float) (pz + radius * Math.cos(theta2) * Math.sin(phi1));

                float x3 = (float) (px + radius * Math.cos(theta2) * Math.cos(phi2));
                float y3 = (float) (py + radius * Math.sin(theta2));
                float z3 = (float) (pz + radius * Math.cos(theta2) * Math.sin(phi2));

                float x4 = (float) (px + radius * Math.cos(theta1) * Math.cos(phi2));
                float y4 = (float) (py + radius * Math.sin(theta1));
                float z4 = (float) (pz + radius * Math.cos(theta1) * Math.sin(phi2));

                buffer.vertex(x1, y1, z1).color(r, g, b, alpha).endVertex();
                buffer.vertex(x2, y2, z2).color(r, g, b, alpha).endVertex();
                buffer.vertex(x3, y3, z3).color(r, g, b, alpha).endVertex();
                buffer.vertex(x4, y4, z4).color(r, g, b, alpha).endVertex();
            }
        }

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new MagicWaveParticle(world, x, y, z, (int) vx);
        }
    }
}

