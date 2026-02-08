package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.RenderHelper;
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
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BloodRuneParticle extends Particle {

    private final float baseRadius;
    private final float cylinderHeight;

    protected BloodRuneParticle(ClientLevel level, double x, double y, double z, double radius) {
        super(level, x, y, z);
        this.lifetime = 80;
        this.gravity = 0;
        this.hasPhysics = false;


        this.baseRadius = (radius > 0) ? (float) radius : 4.5f;
        this.cylinderHeight = 8.0f;
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
        float px = (float)(Mth.lerp(partialTicks, xo, x) - camPos.x);
        float py = (float)(Mth.lerp(partialTicks, yo, y) - camPos.y);
        float pz = (float)(Mth.lerp(partialTicks, zo, z) - camPos.z);

        PoseStack poseStack = new PoseStack();
        poseStack.translate(px, py, pz);

        // --- RENDER SETUP ---
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float time = age + partialTicks;
        float alphaFade = 1.0f;
        if (age < 10) alphaFade = age / 10f;
        else if (age > lifetime - 20) alphaFade = (lifetime - age) / 20f;

        Matrix4f pose = poseStack.last().pose();
        float rotation = time * 0.02f;

        // 1. RENDER RINGS
        for (int layer = 0; layer < 3; layer++) {
            float waveOffset = (float) Math.sin((time * 0.1f) + (layer * 1.5f));
            float currentRadius = baseRadius + (waveOffset * 0.3f);
            float thickness = 0.8f + (layer * 0.2f);

            float r = 1.0f;
            float g = layer * 0.1f;
            float b = 0.1f + (layer * 0.1f);
            float alpha = 0.4f * alphaFade;

            float finalLayer = layer;
            RenderHelper.renderSimpleProceduralRing(buffer, pose,
                    currentRadius, thickness, 64, rotation,
                    r, g, b, 0.0f, alpha,
                    (angle) -> (float) RenderHelper.Perlin.noise(angle * 0.15 * (64.0 / (2*Math.PI)), time * 0.05 + finalLayer),
                    0.3f, 0.1f
            );
        }

        // 2. RENDER CYLINDER
        float cylRadius = baseRadius * 0.95f;
        float baseAlpha = 0.15f * alphaFade;

        RenderHelper.renderSimpleGradientCylinder(buffer, pose,
                cylinderHeight,
                cylRadius, cylRadius,
                32, 0,
                1.0f, 0.0f, 0.1f, baseAlpha, 0.0f
        );

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
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            // VX is hijacked to pass the Radius
            return new BloodRuneParticle(world, x, y, z, vx);
        }
    }
}