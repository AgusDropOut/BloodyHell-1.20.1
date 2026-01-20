package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SmallBloodFlameParticle extends TextureSheetParticle {

    protected SmallBloodFlameParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        // 1. Setup Size & Life
        this.quadSize = 0.08F; // Small size
        this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4; // Random life (like vanilla flame)

        // 2. Setup Velocity (Rise Up)
        this.xd = xSpeed;
        this.yd = ySpeed; // Slight upward drift by default
        this.zd = zSpeed;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // 1. Move
        this.move(this.xd, this.yd, this.zd);

        // 2. Physics: Add drag and slight lift
        this.xd *= 0.96F;
        this.zd *= 0.96F;
        this.yd *= 0.96F;

        if (this.onGround) {
            this.xd *= 0.7F;
            this.zd *= 0.7F;
        }

        // 3. Visuals: Shrink slightly as it dies (Flame behavior)
        if (this.age > this.lifetime / 2) {
            this.quadSize *= 0.96F;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    // --- MAX BRIGHTNESS (Glows in dark) ---
    @Override
    protected int getLightColor(float partialTick) {
        return 15728880; // Full Brightness (240)
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallBloodFlameParticle particle = new SmallBloodFlameParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}