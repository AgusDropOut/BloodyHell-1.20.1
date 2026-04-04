package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.agusdropout.bloodyhell.particle.ParticleOptions.FrenziedFlameParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.manager.FrenziedFlameRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FrenziedFlameParticle extends Particle {

    private final float scale;

    protected FrenziedFlameParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, int lifetime) {
        super(level, x, y, z);
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.scale = 1.5F + this.random.nextFloat() * 0.5F;
        this.lifetime = lifetime;
        this.hasPhysics = false;

        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float renderX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float renderY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float renderZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.translate(renderX, renderY, renderZ);

        /* Full spherical billboarding matches the camera's exact rotation */
        poseStack.mulPose(camera.rotation());

        /* Rotates 180 degrees so the front face points towards the camera */
        //poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees());

        float time = (System.currentTimeMillis() % 100000L) / 1000.0F;

        /* Back Layer - Pushed into the screen (positive Z after rotation) */
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.3D);
        FrenziedFlameRenderManager.addFlame(poseStack.last().pose(), this.scale * 1.15F, this.rCol, this.gCol, this.bCol, this.alpha * 0.6F, time + 25.0F);
        poseStack.popPose();

        /* Core Layer - Center */
        FrenziedFlameRenderManager.addFlame(poseStack.last().pose(), this.scale, this.rCol, this.gCol, this.bCol, this.alpha, time);

        /* Front Layer - Pulled toward the camera (negative Z after rotation) */
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, -0.3D);
        FrenziedFlameRenderManager.addFlame(poseStack.last().pose(), this.scale * 0.85F, this.rCol, this.gCol, this.bCol, this.alpha * 0.8F, time - 15.0F);
        poseStack.popPose();
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
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<FrenziedFlameParticleOptions> {
        @Override
        public Particle createParticle(FrenziedFlameParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            return new FrenziedFlameParticle(level, x, y, z, vx, vy, vz, options.getR(), options.getG(), options.getB(), options.getLifetime());
        }
    }
}