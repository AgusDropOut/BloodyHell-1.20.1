package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.particle.ParticleOptions.RadialDistortionParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RadialDistortionParticle extends Particle {

    private static int captureTextureId = -1;
    private final Quaternionf customRotation;

    protected RadialDistortionParticle(ClientLevel level, double x, double y, double z, float pitch, float yaw, int lifeTicks) {
        super(level, x, y, z);
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = lifeTicks;

        if (captureTextureId == -1) {
            captureTextureId = GL11.glGenTextures();
        }



        this.customRotation = new Quaternionf();
        this.customRotation.mul(Axis.YP.rotationDegrees(-yaw));
        this.customRotation.mul(Axis.XP.rotationDegrees(pitch));
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        double px = Mth.lerp(partialTicks, xo, x) - camPos.x;
        double py = Mth.lerp(partialTicks, yo, y) - camPos.y;
        double pz = Mth.lerp(partialTicks, zo, z) - camPos.z;

        float time = (age + partialTicks) / (float) lifetime;

        PoseStack poseStack = new PoseStack();
        poseStack.translate(px, py, pz);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);

        float currentSize = 0.5f + (time * 2.5f);

        ShaderUtils.renderDistortionPlane(
                poseStack,
                captureTextureId,
                currentSize,
                new Vector3f(1, 1, 1),
                1.0f,
                time,
                this.customRotation, ModShaders.RADIAL_DISTORTION_SHADER
        );

        RenderSystem.depthMask(true);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<RadialDistortionParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(RadialDistortionParticleOptions data, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new RadialDistortionParticle(world, x, y, z, data.getPitch(), data.getYaw(), data.getLifeTicks());
        }
    }
}