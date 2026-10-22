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
    private static final double SUICIDE_SPEED = 1.5;
    private static final double EXPLOSION_RADIUS = 2.5;

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

            this.omenGazer.getNavigation().moveTo(target, SUICIDE_SPEED);
            this.omenGazer.getLookControl().setLookAt(target, 30.0F, 30.0F);

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
            float explosionRadius = 4.0F;
            int particleCount = 100;


            for (int i = 0; i < particleCount; i++) {
                double velX = (level.random.nextDouble() - 0.5) * 1.5;
                double velY = level.random.nextDouble() * 1.2 + 0.2;
                double velZ = (level.random.nextDouble() - 0.5) * 1.5;
                double speed = 0.2 + level.random.nextDouble() * 0.3;

                level.sendParticles(
                        ModParticles.VICERAL_PARTICLE.get(),
                        x, y, z,
                        1,
                        velX, velY, velZ,
                        speed
                );
            }


            AABB damageArea = new AABB(x - explosionRadius, y - explosionRadius, z - explosionRadius,
                    x + explosionRadius, y + explosionRadius, z + explosionRadius);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageArea);
            for (LivingEntity entity : entities) {
                if (entity != this.omenGazer) {
                    double distance = this.omenGazer.distanceTo(entity);
                    float damage = Math.max(2.0F, 10.0F - (float) distance * 2.0F);
                    entity.hurt(omenGazer.damageSources().explosion(omenGazer, omenGazer.getTarget()), damage);
                    entity.addEffect(new MobEffectInstance(ModEffects.VISCERAL_EFFECT.get(), 80, 0));
                }
            }
        }
            for (int i = 0; i < 5; i++) {
                double velX = (omenGazer.level().random.nextDouble() - 0.5) * 1.2;
                double velY = omenGazer.level().random.nextDouble() * 2.0 + 0.5;
                double velZ = (omenGazer.level().random.nextDouble() - 0.5) * 1.2;
                float damage = 5.0F;

                VisceralProjectile projectile = new VisceralProjectile(omenGazer.level(), omenGazer.getX(), omenGazer.getY()+1, omenGazer.getZ(), velX, velY, velZ, damage, omenGazer);
                omenGazer.level().addFreshEntity(projectile);
            }

            omenGazer.level().playSound(omenGazer, omenGazer.getOnPos(), ModSounds.VISCERAL_EXPLOSION.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            this.omenGazer.discard();

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