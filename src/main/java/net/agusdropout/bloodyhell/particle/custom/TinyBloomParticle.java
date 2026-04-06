package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.manager.TinyBloomRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class TinyBloomParticle extends Particle {
    private final float baseScale;

    protected TinyBloomParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TinyBloomParticleOptions options) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.gravity = 0F;
        this.hasPhysics = false;

        this.baseScale = options.getSize() * (0.85F + this.random.nextFloat() * 0.3F);
        this.lifetime = 20 + this.random.nextInt(15);
        this.alpha = 1.0F;

        this.rCol = options.getColor().x();
        this.gCol = options.getColor().y();
        this.bCol = options.getColor().z();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.98D;
            this.yd *= 0.98D;
            this.zd *= 0.98D;

            if (this.onGround) {
                this.xd *= 0.7D;
                this.zd *= 0.7D;
                this.yd *= -0.5D;
            }
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

        float lifeRatio = (float)this.age / (float)this.lifetime;
        float currentScale = this.baseScale * (1.0F - (lifeRatio * lifeRatio));

        TinyBloomRenderManager.addBloom(
                poseStack.last().pose(),
                currentScale,
                this.rCol, this.gCol, this.bCol,
                this.alpha
        );
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<TinyBloomParticleOptions> {
        @Override
        public Particle createParticle(TinyBloomParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new TinyBloomParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type);
        }
    }
}