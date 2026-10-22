package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.BloodyHell;
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
public class StarExplosionParticle extends Particle {
        private final ResourceLocation texture = new ResourceLocation(BloodyHell.MODID, "textures/particle/yellow.png");
        private final float baseRadius = 0.5f;
        private final float growthRate = 0.2f;
        private final int latSegments = 16; // "paralelos"
        private final int lonSegments = 32;// "meridianos"
        private final int lifetime = 100;

        protected StarExplosionParticle(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
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
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 0f, 1f);
        RenderSystem.setShaderTexture(0, texture);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float time = age + partialTicks;
        float baseR = baseRadius + growthRate * time;
        float alpha = 1.0f - (time / lifetime);
        alpha = Math.max(alpha, 0.05f);

        int blurLayers = 10;
        for (int b = 0; b < blurLayers; b++) {
            float blurFactor = 1f + b * 0.25f;
            float alphaFactor = 1f / (b + 2);
            float radius = baseR * blurFactor;

            float layerLifetime = lifetime * (1f + b * 0.15f);
            float lifeDecay = 1f - (time / layerLifetime);
            float depthFactor = (1f - (float)b / blurLayers);

            float layerAlpha = alpha * alphaFactor * lifeDecay * depthFactor;
            if (layerAlpha <= 0f) continue;

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            for (int i = 0; i < latSegments; i++) {
                double theta1 = Math.PI * i / latSegments - Math.PI / 2;
                double theta2 = Math.PI * (i + 1) / latSegments - Math.PI / 2;

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

                    buffer.vertex(x1, y1, z1).color(1f, 1f, 0f, layerAlpha).endVertex();
                    buffer.vertex(x2, y2, z2).color(1f, 1f, 0f, layerAlpha).endVertex();
                    buffer.vertex(x3, y3, z3).color(1f, 1f, 0f, layerAlpha).endVertex();
                    buffer.vertex(x4, y4, z4).color(1f, 1f, 0f, layerAlpha).endVertex();
                }
            }

            tess.end();
        }

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
                return new StarExplosionParticle(world, x, y, z);
            }
        }
}

