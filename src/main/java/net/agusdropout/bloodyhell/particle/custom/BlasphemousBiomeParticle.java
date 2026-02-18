package net.agusdropout.bloodyhell.particle.custom;

import net.agusdropout.bloodyhell.util.visuals.WindController;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class BlasphemousBiomeParticle extends TextureSheetParticle {
    // Global shared wind direction (static)
    private static double windX = 0;
    private static double windZ = 0;
    private static int ticksUntilChange = 0;

    protected BlasphemousBiomeParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z);
        this.lifetime = 100;
    }

    public void tick() {
        super.tick();

        this.alpha = 1.0f - ((float) this.age / this.lifetime);

        // Aplicar viento global
        this.x += WindController.windX;
        this.z += WindController.windZ;
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            BlasphemousBiomeParticle p = new BlasphemousBiomeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            p.pickSprite(this.spriteSet);
            return p;
        }
    }
}
