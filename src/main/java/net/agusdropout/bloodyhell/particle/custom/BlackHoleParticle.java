package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.BlackHoleParticleOptions;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.agusdropout.bloodyhell.util.ShaderUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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

    private static int captureTextureId = -1;
    private static final Vector3f COLOR_RIM = new Vector3f(1.0f, 0.43f, 0.04f);
    private static final Vector3f COLOR_LENS = new Vector3f(1.0f, 0.98f, 0.94f);
    private static final Vector3f COLOR_SPARKLES_BASE = new Vector3f(1.0f, 0.86f, 0.59f);

    private final float r, g, b;
    private final float baseCoreSize;
    private final float baseRingSize;
    private final float vortexSize;
    private final float baseLensSize;
    private final long starSeed;

    protected BlackHoleParticle(ClientLevel level, double x, double y, double z, float size, float r, float g, float b) {
        super(level, x, y, z);
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = 250;
        this.starSeed = random.nextLong();

        this.baseCoreSize = size;
        this.baseRingSize = size * 2.75f;
        this.vortexSize = size * 2.0f;
        this.baseLensSize = size * 1.25f;
        this.r = r;
        this.g = g;
        this.b = b;

        if (captureTextureId == -1) {
            captureTextureId = GL11.glGenTextures();
        }
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) remove();
        if (age > 20 && age < lifetime - 20) {
            if (random.nextInt(3) == 0) spawnInfallingParticles();
        }
    }

    private void spawnInfallingParticles() {
        double radius = 5.0 + random.nextDouble() * 3.0;
        double theta = random.nextDouble() * Math.PI * 2;
        double phi = random.nextDouble() * Math.PI - Math.PI / 2;
        double sx = this.x + radius * Math.cos(theta) * Math.cos(phi);
        double sy = this.y + radius * Math.sin(phi);
        double sz = this.z + radius * Math.sin(theta) * Math.cos(phi);
        double speed = 0.4;
        SimpleParticleType type = random.nextBoolean() ? ModParticles.MAGIC_LINE_PARTICLE.get() : ParticleTypes.END_ROD;
        this.level.addParticle(type, sx, sy, sz, (this.x - sx)*speed/radius, (this.y - sy)*speed/radius, (this.z - sz)*speed/radius);
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        double px = Mth.lerp(partialTicks, xo, x) - camPos.x;
        double py = Mth.lerp(partialTicks, yo, y) - camPos.y;
        double pz = Mth.lerp(partialTicks, zo, z) - camPos.z;

        float time = age + partialTicks;
        float lifeRatio = time / (float) lifetime;
        float scale = lifeRatio < 0.1f ? (float) Math.sin((lifeRatio / 0.1f) * Math.PI / 2) :
                (lifeRatio > 0.9f ? 1.0f - (lifeRatio - 0.9f) / 0.1f : 1.0f);

        if (scale <= 0.01f) return;

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        PoseStack poseStack = new PoseStack();
        poseStack.translate(px, py, pz);

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);

        ShaderUtils.renderGravitationalLens(poseStack, captureTextureId, vortexSize * scale, new Vector3f(1, 1, 1), 1.0f, time);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderHelper.renderProceduralSphere(buffer, poseStack.last().pose(), null, 24, 32,
                (theta, phi) -> (baseLensSize * scale) + (float) Math.sin(phi + (-time * 0.1f * 0.5f) * 5) * 0.15f,
                COLOR_LENS.x(), COLOR_LENS.y(), COLOR_LENS.z(), 0.15f, 15728880);
        tess.end();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(0.5f));
        poseStack.mulPose(Axis.YP.rotation(time * 0.1f));

        float diskAlpha = 0.8f * Math.min(1.0f, scale * 2.0f);
        float[] cDiskIn = {r, g, b, diskAlpha};
        float[] cDiskOut = {r, g, b, 0.0f};

        RenderHelper.renderDisk(buffer, poseStack.last().pose(), null,
                baseCoreSize * scale * 1.1f, baseRingSize * scale, 48, 0,
                cDiskIn, cDiskOut, 15728880);
        poseStack.popPose();

        RenderHelper.renderSphere(buffer, poseStack.last().pose(), null,
                baseCoreSize * scale * 1.15f, 16, 24,
                r, g, b, 0.65f * scale, 15728880);
        tess.end();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float coreSize = (baseCoreSize * scale) * (1.0f + (float)(Math.sin(time * 45.0f) * 0.015f));
        RenderHelper.renderSphere(buffer, poseStack.last().pose(), null, coreSize, 16, 24, 0f, 0f, 0f, 1.0f, 15728880);
        tess.end();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderSparkles(buffer, poseStack, coreSize * 1.2f, scale, time * 0.1f, camera.rotation());
        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void renderSparkles(BufferBuilder buffer, PoseStack stack, float radius, float scale, float rot, Quaternionf camRot) {
        RandomSource rand = RandomSource.create(this.starSeed);

        for (int i = 0; i < 80; i++) {
            float radialFactor = (float) Math.pow(rand.nextFloat(), 0.7);
            double cRad = radius * radialFactor ;
            double wobble = Math.sin(rot * 2.0 + i) * 0.2;
            double theta = rand.nextDouble() * Math.PI * 2 + wobble;
            double phi = Math.acos(2.0 * rand.nextDouble() - 1.0);
            float speedMult = 1.0f / (radialFactor + 0.1f);
            double aTheta = theta + (rot * speedMult * (0.5 + rand.nextDouble()));

            float dx = (float) (cRad * Math.sin(phi) * Math.cos(aTheta));
            float dy = (float) (cRad * Math.sin(phi) * Math.sin(aTheta));
            float dz = (float) (cRad * Math.cos(phi));

            float flicker = (float) Math.sin(rot * 15.0 + i * 1.5);
            float alpha = (0.4f + 0.6f * Math.max(0, flicker)) * scale;
            float finalSize = (0.03f + (rand.nextFloat() * 0.04f)) * scale;

            RenderHelper.renderBillboardQuad(buffer, stack.last().pose(),
                    dx, dy, dz, finalSize,
                    r, g, b, // Personalize star color with the disk color
                    alpha, camRot, 15728880);
        }
    }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.CUSTOM; }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<BlackHoleParticleOptions> {
        @Nullable @Override
        public Particle createParticle(BlackHoleParticleOptions data, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new BlackHoleParticle(world, x, y, z, data.getSize(), data.getR(), data.getG(), data.getB());
        }
    }
}