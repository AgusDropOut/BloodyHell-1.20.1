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

        // --- AJUSTES DE MOVIMIENTO ---
        // Forzamos la velocidad a cero para que no se mueva.
        // El constructor 'super' asigna vx, vy, vz a las variables de movimiento (xd, yd, zd),
        // así que las sobreescribimos aquí para detener cualquier movimiento no deseado.
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.spriteSet = spriteSet;
        this.lifetime = 2000;
        this.gravity = 0.0F;

        // --- AJUSTE DE TAMAÑO ---
        // Establecemos un tamaño fijo para la partícula. El valor es en bloques.
        // Un valor de 1.0F sería aproximadamente del tamaño de un bloque.
        // Tendrás que experimentar con este valor para que se ajuste al ojo de tu entidad.
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
        // --- LÓGICA DE FADING ---
        // Calculamos qué tan "avanzada" está la vida de la partícula (de 0.0 a 1.0)
        float lifeRatio = (float)this.age / (float)this.lifetime;

        // Usamos una función de seno para que el fade in y fade out sean suaves.
        // Mth.sin(x * PI) crea una curva suave que empieza en 0, sube a 1 en el medio, y vuelve a 0 al final.
        // Esto es perfecto para un efecto que aparece y desaparece.
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
