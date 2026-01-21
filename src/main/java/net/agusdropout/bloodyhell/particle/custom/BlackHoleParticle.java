package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.RenderHelper;
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
    // COLORS
    // ==========================================
    private static final Vector3f COLOR_RIM = rgb(255, 110, 10);
    private static final Vector3f COLOR_DISK = rgb(182, 139, 30);
    private static final Vector3f COLOR_SPARKLES = rgb(255, 220, 150);
    private static final Vector3f COLOR_LENS = rgb(255, 250, 240);

    private final float baseCoreSize = 2f;
    private final float baseRingSize = 4f;
    private final float baseLensSize = 2.5f;
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
        float scale = 1.0f;

        if (lifeRatio < 0.1f) scale = (float) Math.sin((lifeRatio / 0.1f) * Math.PI / 2);
        else if (lifeRatio > 0.9f) scale = 1.0f - (lifeRatio - 0.9f) / 0.1f;

        if (scale <= 0.01f) return;

        float currentCoreSize = baseCoreSize * scale;
        float currentRingSize = baseRingSize * scale;
        float currentLensSize = baseLensSize * scale;
        float rotation = time * 0.1f;

        // Setup Render
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // We create a PoseStack to handle local transformations relative to the camera
        PoseStack poseStack = new PoseStack();
        poseStack.translate(px, py, pz);

        // ==========================================
        // 1. LENTE GRAVITACIONAL (Procedural Distortion)
        // ==========================================
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Use the new renderProceduralSphere to handle the noise logic
        RenderHelper.renderProceduralSphere(buffer, poseStack.last().pose(), null,
                24, 32,
                (theta, phi) -> {
                    // Replicated Noise Logic
                    double p = phi + (-rotation * 0.5f);
                    float noise = (float) Math.sin(p * 5 + (-rotation * 0.5f)) * 0.15f;
                    return currentLensSize + noise;
                },
                COLOR_LENS.x(), COLOR_LENS.y(), COLOR_LENS.z(), 0.15f, 15728880);

        tess.end();

        // ==========================================
        // 2. DISK & CORE
        // ==========================================
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Additive for energy
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // --- DISCO DE ACRECIÓN ---
        poseStack.pushPose();
        // The disk is tilted 0.5 rads (~28 deg) on X axis
        poseStack.mulPose(Axis.XP.rotation(0.5f));

        float ringInner = currentCoreSize * 1.1f;
        float diskAlpha = 0.8f * Math.min(1.0f, scale * 2.0f);
        float[] cDiskIn = {COLOR_DISK.x(), COLOR_DISK.y(), COLOR_DISK.z(), diskAlpha};
        float[] cDiskOut = {COLOR_DISK.x(), COLOR_DISK.y(), COLOR_DISK.z(), 0.0f};

        RenderHelper.renderDisk(buffer, poseStack.last().pose(), null,
                ringInner, currentRingSize, 48, rotation,
                cDiskIn, cDiskOut, 15728880);
        poseStack.popPose();

        // --- BORDE BRILLANTE ---
        RenderHelper.renderSphere(buffer, poseStack.last().pose(), null,
                currentCoreSize * 1.15f, 16, 24,
                COLOR_RIM.x(), COLOR_RIM.y(), COLOR_RIM.z(), 0.65f * scale, 15728880);

        tess.end();

        // ==========================================
        // 3. CORE & STARS
        // ==========================================

        // --- NÚCLEO NEGRO ---
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float coreFlicker = (float) (Math.sin(time * 45.0f) * 0.015f + Math.sin(time * 27.0f) * 0.005f);
        float flickerCoreSize = currentCoreSize * (1.0f + coreFlicker);

        RenderHelper.renderSphere(buffer, poseStack.last().pose(), null,
                flickerCoreSize, 16, 24,
                0f, 0f, 0f, 1f, 15728880);
        tess.end();

        // --- ESTRELLAS (Orbiting Sparkles) ---
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        renderSparkles(buffer, poseStack, flickerCoreSize * 0.98f, scale, rotation, camera.rotation());

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    private void renderSparkles(BufferBuilder buffer, PoseStack stack, float radius, float scale, float globalRotation, Quaternionf camRot) {
        RandomSource seededRandom = RandomSource.create(this.starSeed);
        int starCount = 200;
        float starSizeBase = 0.04f * scale;

        for (int i = 0; i < starCount; i++) {
            double theta = seededRandom.nextDouble() * Math.PI * 2;
            double phi = Math.acos(2.0 * seededRandom.nextDouble() - 1.0);

            double animTheta = theta + globalRotation * (0.5 + seededRandom.nextDouble() * 0.5);
            double currentRadius = radius * (0.3 + seededRandom.nextDouble() * 0.7);

            // Math to determine local position of the sparkle
            float dx = (float) (currentRadius * Math.sin(phi) * Math.cos(animTheta));
            float dy = (float) (currentRadius * Math.sin(phi) * Math.sin(animTheta));
            float dz = (float) (currentRadius * Math.cos(phi));

            float blink = 0.5f + 0.5f * (float) Math.sin(globalRotation * 5.0 + i);
            float alpha = 0.8f * blink;

            // RenderHelper does the quad creation logic
            RenderHelper.renderBillboardQuad(buffer, stack.last().pose(),
                    dx, dy, dz, starSizeBase,
                    COLOR_SPARKLES.x(), COLOR_SPARKLES.y(), COLOR_SPARKLES.z(), alpha,
                    camRot, 15728880);
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
            return new BlackHoleParticle(world, x, y, z);
        }
    }
}