package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.particle.ParticleOptions.RadialDistortionParticleOptions;
import net.agusdropout.bloodyhell.client.render.effect.Effects;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RadialDistortionParticle extends Particle {

    private final Quaternionf customRotation;

    protected RadialDistortionParticle(ClientLevel level, double x, double y, double z, float pitch, float yaw, int lifeTicks) {
        super(level, x, y, z);
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = lifeTicks;

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
        // Access protected variables (xo, yo, zo, x, y, z) using 'this.'
        net.minecraft.world.phys.Vec3 camPos = camera.getPosition();
        double px = Mth.lerp(partialTicks, this.xo, this.x) - camPos.x;
        double py = Mth.lerp(partialTicks, this.yo, this.y) - camPos.y;
        double pz = Mth.lerp(partialTicks, this.zo, this.z) - camPos.z;

        float time = (this.age + partialTicks) / (float) this.lifetime;

        PoseStack poseStack = new PoseStack();
        poseStack.translate(px, py, pz);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        float currentSize = 0.5f + (time * 2.5f);

        // Queue the distortion quad instead of drawing immediately
        Effects.RADIAL_DISTORTION.addDistortion(
                poseStack.last().pose(),
                currentSize,
                new Vector3f(1, 1, 1),
                1.0f,
                time,
                this.customRotation
        );
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