package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class BloodFlameParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private final float initialSize;

    protected BloodFlameParticle(ClientLevel level, double x, double y, double z,
                                 double xSpeed, double ySpeed, double zSpeed,
                                 SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.friction = 0.96F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.sprites = spriteSet;

        this.quadSize *= 1.2F + (this.random.nextFloat() * 0.6F - 0.3F);


        this.initialSize = this.quadSize;

        this.lifetime = 20 + this.random.nextInt(10);
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {

        this.setSpriteFromAge(this.sprites);


        updateVisuals();
    }

    private void updateVisuals() {

        float lifeCoeff = (float)this.age / (float)this.lifetime;


        this.quadSize = this.initialSize * (1.0F - lifeCoeff);


        this.alpha = 1.0F - (lifeCoeff * lifeCoeff);

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
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
            BloodFlameParticle particle = new BloodFlameParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.pickSprite(this.spriteSet);
            particle.setSpriteFromAge(this.spriteSet);
            return particle;
        }
    }
}