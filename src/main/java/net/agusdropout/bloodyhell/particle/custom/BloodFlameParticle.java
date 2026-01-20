package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class BloodFlameParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    // 1. New field to store the starting size so we can shrink relative to it
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

        // 2. VARYING INITIAL SIZE
        // Base boost of 1.2F, plus or minus a random amount so not every flame is identical
        this.quadSize *= 1.2F + (this.random.nextFloat() * 0.6F - 0.3F);

        // Save this specific particle's size
        this.initialSize = this.quadSize;

        this.lifetime = 20 + this.random.nextInt(10);
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {

        this.setSpriteFromAge(this.sprites);

        // Update visual properties (Size and Alpha)
        updateVisuals();
    }

    private void updateVisuals() {
        // Calculate how far along the particle is in its life (0.0 = birth, 1.0 = death)
        float lifeCoeff = (float)this.age / (float)this.lifetime;

        // 3. DECREASE SIZE
        // Shrink from initialSize down to 0 as it ages
        // You can adjust the logic (e.g., " * (1.0F - lifeCoeff * 0.5F)" to only shrink to half size)
        this.quadSize = this.initialSize * (1.0F - lifeCoeff);

        // Alpha Fade (Quadratic curve)
        this.alpha = 1.0F - (lifeCoeff * lifeCoeff);

        // Kill particle if too old
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