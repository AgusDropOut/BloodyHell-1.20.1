package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class CyclopsHaloParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;
    private float rotSpeed;

    protected CyclopsHaloParticle(ClientLevel level, double x, double y, double z,
                                  SpriteSet spriteSet, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);


        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.spriteSet = spriteSet;
        this.lifetime = 2000;
        this.gravity = 0.0F;


        this.quadSize = 0.6F;



        this.rotSpeed = 1.0F; // Velocidad inicial de rotación

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
        this.oRoll = this.roll;
        this.roll += this.rotSpeed / 10.0F;

        float lifeRatio = (float)this.age / (float)this.lifetime;


        this.alpha = 1F;
    }

    @Override
    protected int getLightColor(float tint) {
        return 0xF000F0;
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

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new CyclopsHaloParticle(level, x, y, z, this.spriteSet, xSpeed, ySpeed, zSpeed);
        }
    }
}
