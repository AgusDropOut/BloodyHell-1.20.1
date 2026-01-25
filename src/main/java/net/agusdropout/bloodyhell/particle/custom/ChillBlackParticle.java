package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChillBlackParticle extends TextureSheetParticle {

    private final float targetSize;
    private final int growTime;
    private double targetvx;
    private double targetvy;
    private double targetvz;

    protected ChillBlackParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);

        // 1. Visual Config (Dark & Chill)
        this.rCol = 0.05f; // Almost Black
        this.gCol = 0.05f;
        this.bCol = 0.05f;
        this.alpha = 0.9f; // Slightly opaque

        // 2. Size Config
        this.quadSize = 0.0f; // Starts invisible
        this.targetSize = 0.016f ; // Random final size

        // 3. Timing
        this.lifetime = 60 + this.random.nextInt(40); // Lasts 3-5 seconds
        this.growTime = 15; // Grows/Hangs for 0.75 seconds

        // 4. Initial Physics (Stopped)
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.targetvx = vx ;
        this.targetvy = vy ;
        this.targetvz = vz ;
        this.gravity = 0.0f; // We handle gravity manually
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

        // --- PHASE 1: GROW & HOVER (Immobile) ---
        if (this.age <= this.growTime) {
            // Smooth growth (Lerp)
            float progress = (float) this.age / (float) this.growTime;
            this.quadSize = this.targetSize * Mth.sin(progress * (float)Math.PI / 2f); // Ease Out

            // Keep completely still
            this.xd = 0;
            this.yd = 0;
            this.zd = 0;
        }
        // --- PHASE 2: FALL (Gravity kicks in) ---
        else {
            // Apply slow, heavy gravity
            this.yd -= 0.005D;
            this.xd = targetvx;
            this.zd = targetvz;

            // Optional: Very slight horizontal drift
            // this.xd += (random.nextFloat() - 0.5) * 0.001;
            // this.zd += (random.nextFloat() - 0.5) * 0.001;
        }

        // --- FADE OUT ---
        // Start fading in the last 20 ticks
        if (this.age > this.lifetime - 20) {
            this.alpha = 0.9f * ((float) (this.lifetime - this.age) / 20f);
        }

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    public ParticleRenderType getRenderType() {
        // PARTICLE_SHEET_TRANSLUCENT allows alpha transparency
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ChillBlackParticle particle = new ChillBlackParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprite); // Uses the texture from the JSON
            return particle;
        }
    }
}