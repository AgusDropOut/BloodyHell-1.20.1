package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.FrenziedExplosionParticleOptions;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.client.render.effect.Effects;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FrenziedExplosionParticle extends Particle {
    private final float baseScale;

    protected FrenziedExplosionParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, FrenziedExplosionParticleOptions options) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.hasPhysics = false;
        this.baseScale = options.getSize();
        this.lifetime = 30;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.85D;
            this.yd *= 0.85D;
            this.zd *= 0.85D;
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float renderX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float renderY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float renderZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.translate(renderX, renderY, renderZ);
        poseStack.mulPose(camera.rotation());

        float time = ShaderTime.seconds();
        float lifeProgress = 1.0F - ((float)this.age / (float)this.lifetime);

        Effects.FRENZIED_EXPLOSION.submitWhite(
                poseStack.last().pose(),
                this.baseScale,
                lifeProgress,
                time
        );
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<FrenziedExplosionParticleOptions> {
        @Override
        public Particle createParticle(FrenziedExplosionParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FrenziedExplosionParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type);
        }
    }
}