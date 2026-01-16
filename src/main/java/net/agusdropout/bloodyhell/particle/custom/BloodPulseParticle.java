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

        // --- FÍSICA PERSONALIZADA ---

        // 1. FRICCIÓN (Resistencia al aire)
        // 0.98 = Casi nada de fricción (vuela lejos)
        // 0.91 = Aire denso (se frena rápido) <- LO QUE QUIERES
        this.friction = 0.91F;

        // 2. GRAVEDAD
        // 1.0 = Cae como bloque
        // 0.04 = Cae suavemente (efecto pluma/ceniza) <- LO QUE QUIERES
        this.gravity = 0.04F;

        // Velocidad inicial
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        // Tamaño un poco más grande
        this.quadSize *= 1.2F;

        // Duración: 20 ticks (1 segundo) para que de tiempo a ver la animación
        this.lifetime = 20;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);

        // Opcional: Hacer que se achiquen al morir
        if (this.age > this.lifetime - 5) {
            this.quadSize *= 0.9f;
        }
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