package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
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

    private final int lifetime = 60;

    protected EnergyVortexParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 0;
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) remove();
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
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        float time = age + partialTicks;
        float rotation = time * 0.2f;
        float scale = (age < 10) ? age/10f : (age > lifetime-10 ? (lifetime-age)/10f : 1.0f);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // We create a temporary Matrix stack just for positioning the helper
        PoseStack stack = new PoseStack();
        stack.translate(px, py, pz);

        // Top Jet
        RenderHelper.renderTaperedCylinder(buffer, stack.last().pose(), null,
                5.0f * scale, 0.1f * scale, 1.5f * scale,
                rotation, rotation * 2.0f, 8, 8,
                0.2f, 0.8f, 1.0f, 0.8f, 0.0f, 15728880);

        // Bottom Jet (Inverted)
        stack.scale(1, -1, 1);
        RenderHelper.renderTaperedCylinder(buffer, stack.last().pose(), null,
                5.0f * scale, 0.1f * scale, 1.5f * scale,
                rotation, rotation * 2.0f, 8, 8,
                0.2f, 0.8f, 1.0f, 0.8f, 0.0f, 15728880);

        tess.end();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.CUSTOM; }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Nullable @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new EnergyVortexParticle(world, x, y, z);
        }
    }
}