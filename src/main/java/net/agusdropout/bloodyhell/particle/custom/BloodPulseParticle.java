package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class BloodPulseParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected BloodPulseParticle(ClientLevel level, double x, double y, double z,
                                 double xSpeed, double ySpeed, double zSpeed,
                                 SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.sprites = spriteSet;

        this.friction = 0.91F;


        this.gravity = 0.04F;


        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;


        this.quadSize *= 1.2F;


        this.lifetime = 20;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);


        if (this.age > this.lifetime - 5) {
            this.quadSize *= 0.9f;
        }
    }
    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new BloodPulseParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}