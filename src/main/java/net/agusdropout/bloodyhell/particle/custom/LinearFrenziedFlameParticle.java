package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.LinearFrenziedFlameParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.manager.LinearFrenziedFlameRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class LinearFrenziedFlameParticle extends Particle {

    private final float scale;

    protected LinearFrenziedFlameParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, LinearFrenziedFlameParticleOptions options) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.scale = options.getScale();
        this.lifetime = options.getLifetime();
        this.alpha = 1.0F;

        this.rCol = options.getR();
        this.gCol = options.getG();
        this.bCol = options.getB();
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
            this.xd *= 0.95;
            this.yd *= 0.95;
            this.zd *= 0.95;
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

        float time = (System.currentTimeMillis() % 100000L) / 1000.0F;

        LinearFrenziedFlameRenderManager.addFlame(
                poseStack.last().pose(),
                this.scale,
                this.rCol,
                this.gCol,
                this.bCol,
                this.alpha,
                time
        );
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<LinearFrenziedFlameParticleOptions> {
        @Override
        public Particle createParticle(LinearFrenziedFlameParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LinearFrenziedFlameParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type);
        }
    }
}