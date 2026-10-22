package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class LightParticle extends TextureSheetParticle {
    private float rotSpeed;
    private final float spinAcceleration;

    protected LightParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.friction = 1.0F;
        double diagonalFactor = 0.01D;
        this.xd += xSpeed * 0.0075D +   diagonalFactor;
        this.yd=0.0D;
        this.zd = 0.0D;
        this.gravity = 0.003F;
        this.lifetime = 80;
        this.quadSize *= 0.2F;
        this.hasPhysics = true;
        this.rotSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -60.0D : 60.0D);
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -25.0D : 25.0D);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        if (!this.removed) {
            this.yd -= this.gravity;
            this.rotSpeed += this.spinAcceleration / 20.0F;
            this.oRoll = this.roll;
            this.roll += this.rotSpeed / 10.0F;
            if (!this.onGround) {
                this.move(this.xd, this.yd, this.zd);
            } else {
                this.age = Math.max(this.lifetime - 10, this.age);
            }

            if (this.age > this.lifetime - 10) {
                this.scale(Mth.abs(this.age - this.lifetime) * 0.1F);
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            LightParticle snow = new LightParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            snow.pickSprite(this.spriteSet);
            return snow;
        }
    }
}

