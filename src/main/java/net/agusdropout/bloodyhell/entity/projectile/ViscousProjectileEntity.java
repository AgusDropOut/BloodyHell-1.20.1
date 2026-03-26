package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.projectile.base.AbstractColoredProjectile;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class ViscousProjectileEntity extends AbstractColoredProjectile {

    public final AnimationState idleAnimationState = new AnimationState();
    private final float explosionRadius = 3.0f;

    public ViscousProjectileEntity(EntityType<? extends AbstractColoredProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        }

        if (!this.isNoGravity()) {
            Vec3 currentMovement = this.getDeltaMovement();
            this.setDeltaMovement(currentMovement.x, currentMovement.y - 0.05D, currentMovement.z);
        }
    }

    @Override
    protected void handleClientEffects() {
        if (this.level().getRandom().nextFloat() < 0.5f) {
            Vector3f baseColor = this.getBaseColor();

            ChillFallingParticleOptions trailParticle = new ChillFallingParticleOptions(
                    baseColor,
                    0.05f,
                    20,
                    5
            );

            for( int i = 0; i < 10; i++) {

                double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetY = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

                ParticleHelper.spawn(
                        this.level(),
                        trailParticle,
                        this.getX() + offsetX,
                        this.getY() + 0.25 + offsetY,
                        this.getZ() + offsetZ,
                        0, 0, 0
                );
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            this.detonate();
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            this.detonate();
            this.discard();
        }
    }

    private void detonate() {
        Vec3 impactPos = this.position();
        Vector3f baseColor = this.getBaseColor();

        ChillFallingParticleOptions explosionParticle = new ChillFallingParticleOptions(
                baseColor,
                0.04f,
                80,
                0
        );

        ParticleHelper.spawnHemisphereExplosion(
                this.level(),
                explosionParticle,
                impactPos,
                80,
                this.explosionRadius,
                0.8
        );

        ParticleHelper.spawnCrownSplash(
                this.level(),
                explosionParticle,
                impactPos,
                80,
                1.5,
                0.5,
                0.4
        );

        AABB damageArea = new AABB(
                impactPos.x - this.explosionRadius,
                impactPos.y - this.explosionRadius,
                impactPos.z - this.explosionRadius,
                impactPos.x + this.explosionRadius,
                impactPos.y + this.explosionRadius,
                impactPos.z + this.explosionRadius
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, damageArea);

        for (LivingEntity target : targets) {
            if (this.distanceToSqr(target) <= (this.explosionRadius * this.explosionRadius)) {

                target.hurt(this.damageSources().magic(), this.damage);
                target.invulnerableTime = 0;
            }
        }
    }
}