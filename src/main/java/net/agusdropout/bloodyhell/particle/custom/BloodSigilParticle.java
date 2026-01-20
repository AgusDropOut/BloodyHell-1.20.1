package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class BloodSigilParticle extends TextureSheetParticle {
    private float rotSpeed;

    protected BloodSigilParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z);
        this.lifetime = 20 + this.random.nextInt(10); // duración 20-30 ticks

        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.setAlpha(0.7f);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f - ((float)this.age / this.lifetime);
    }
    @Override
    protected int getLightColor(float tint) {
        int baseLight = super.getLightColor(tint);
        int fullBright = 0xF000F0;
        return Math.max(baseLight, fullBright);
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

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            BloodSigilParticle particle = new BloodSigilParticle(level, x, y, z , xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}

