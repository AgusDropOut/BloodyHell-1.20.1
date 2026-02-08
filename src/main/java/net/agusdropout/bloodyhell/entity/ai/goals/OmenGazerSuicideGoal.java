package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.custom.OmenGazerEntity;
import net.agusdropout.bloodyhell.entity.projectile.VisceralProjectile;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class OmenGazerSuicideGoal extends Goal {
    private final OmenGazerEntity omenGazer;
    private static final double SUICIDE_SPEED = 1.5; // Aumenta la velocidad
    private static final double EXPLOSION_RADIUS = 2.5; // Distancia para explotar

    public OmenGazerSuicideGoal(OmenGazerEntity omenGazer) {
        this.omenGazer = omenGazer;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.omenGazer.getTarget();
        return target != null && this.omenGazer.isAboutToExplode();
    }

    @Override
    public void tick() {
        LivingEntity target = this.omenGazer.getTarget();
        if (target != null) {
            // Moverse rápidamente hacia el objetivo
            this.omenGazer.getNavigation().moveTo(target, SUICIDE_SPEED);
            this.omenGazer.getLookControl().setLookAt(target, 30.0F, 30.0F);
            // Verificar si está lo suficientemente cerca para explotar
            if (this.omenGazer.distanceTo(target) < EXPLOSION_RADIUS) {
                explode();
            }
        }
    }

    private void explode() {
        if (this.omenGazer.level() instanceof ServerLevel level) {
            double x = this.omenGazer.getX();
            double y = this.omenGazer.getY();
            double z = this.omenGazer.getZ();
            float explosionRadius = 4.0F; // Radio de daño
            int particleCount = 100; // Cantidad de partículas

            // 🔥 Generar partículas con diferentes direcciones y velocidades
            for (int i = 0; i < particleCount; i++) {
                double velX = (level.random.nextDouble() - 0.5) * 1.5; // Velocidad aleatoria en X
                double velY = level.random.nextDouble() * 1.2 + 0.2;   // Impulso hacia arriba
                double velZ = (level.random.nextDouble() - 0.5) * 1.5; // Velocidad aleatoria en Z
                double speed = 0.2 + level.random.nextDouble() * 0.3;  // Variación de velocidad

                level.sendParticles(
                        ModParticles.VICERAL_PARTICLE.get(), // Partícula personalizada
                        x, y, z, // Posición de origen
                        1, // Generamos una partícula a la vez
                        velX, velY, velZ, // Dirección de la partícula
                        speed // Velocidad
                );
            }

            // 💥 Hacer daño en el radio de la explosión
            AABB damageArea = new AABB(x - explosionRadius, y - explosionRadius, z - explosionRadius,
                    x + explosionRadius, y + explosionRadius, z + explosionRadius);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageArea);
            for (LivingEntity entity : entities) {
                if (entity != this.omenGazer) { // No dañarse a sí mismo
                    double distance = this.omenGazer.distanceTo(entity);
                    float damage = Math.max(2.0F, 10.0F - (float) distance * 2.0F); // Menos daño si está más lejos
                    entity.hurt(omenGazer.damageSources().explosion(omenGazer, omenGazer.getTarget()), damage);
                    entity.addEffect(new MobEffectInstance(ModEffects.VISCERAL_EFFECT.get(), 80, 0));
                }
            }
        }
            // 💥 Spawnear projectiles viceralales con velocidad aleatoria
            for (int i = 0; i < 5; i++) { // Puedes ajustar la cantidad de projectiles
                double velX = (omenGazer.level().random.nextDouble() - 0.5) * 1.2; // Velocidad aleatoria en X
                double velY = omenGazer.level().random.nextDouble() * 2.0 + 0.5; // Impulso aleatorio hacia arriba
                double velZ = (omenGazer.level().random.nextDouble() - 0.5) * 1.2; // Velocidad aleatoria en Z
                float damage = 5.0F; // Daño de los projectiles

                VisceralProjectile projectile = new VisceralProjectile(omenGazer.level(), omenGazer.getX(), omenGazer.getY()+1, omenGazer.getZ(), velX, velY, velZ, damage, omenGazer);
                omenGazer.level().addFreshEntity(projectile);
            }

            omenGazer.level().playSound(omenGazer, omenGazer.getOnPos(), ModSounds.VISCERAL_EXPLOSION.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            this.omenGazer.discard(); // Eliminar la entidad tras explotar

    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        this.omenGazer.setAboutToExplode(false);
    }
}