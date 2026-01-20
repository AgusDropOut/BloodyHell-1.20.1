package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ImpactParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.List;

public class BloodFireColumnProjectile extends Projectile {

    private static final EntityDataAccessor<Boolean> ERUPTED = SynchedEntityData.defineId(BloodFireColumnProjectile.class, EntityDataSerializers.BOOLEAN);

    // Configuration
    private static final int CHARGE_TIME = 40;
    private static final int LINGER_TIME = 30;
    private static final float COLUMN_RADIUS = 1.5f;
    private static final float COLUMN_HEIGHT = 7.0f;

    public BloodFireColumnProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public BloodFireColumnProjectile(EntityType<? extends Projectile> type, Level level, LivingEntity owner, double x, double y, double z) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ERUPTED, false);
    }

    @Override
    public void tick() {
        super.tick();

        // 1. CHARGING PHASE
        if (this.tickCount < CHARGE_TIME) {
            if (!this.level().isClientSide) {
                if (this.tickCount == 1) {
                    EntityCameraShake.cameraShake(this.level(), this.position(), 10.0f, 0.1f, CHARGE_TIME, 3); // Stronger shake
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.HOSTILE, 2.0f, 0.5f);
                }
                if (this.tickCount % 5 == 0) {
                    damageArea(2.0f);
                }
            }
            if (this.level().isClientSide) {
                spawnChargingParticles();
            }
        }
        // 2. ERUPTION PHASE
        else if (this.tickCount == CHARGE_TIME) {

            this.level().addParticle(
                    ImpactParticleOptions.create(
                            255, 0, 0,    // R, G, B
                            3.0f,         // Size
                            30,           // Lifetime
                            false,        // Jitter
                            0.05f          // Speed
                    ),
                    this.getX(), this.getY() + 0.05, this.getZ(), 0, 0, 0
            );
            BloodStainEntity stain = new BloodStainEntity(this.level(), this.getX(), this.getY(), this.getZ(), Direction.UP, 3.0f);
            this.level().addFreshEntity(stain);
            if (!this.level().isClientSide) {
                this.entityData.set(ERUPTED, true);
                EntityCameraShake.cameraShake(this.level(), this.position(), 20.0f, 2f, 5, 12); // Massive shake
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5f, 0.8f);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5f, 0.5f);
                explode();
            }
        }
        // 3. LINGERING VISUALS
        else if (this.tickCount < CHARGE_TIME + LINGER_TIME) {
            if (this.level().isClientSide) {
                spawnEruptionParticles();
            }
        }
        // 4. CLEANUP
        else {
            if (!this.level().isClientSide) {
                this.discard();
            }
        }
    }

    private void damageArea(float damage) {
        AABB area = this.getBoundingBox().inflate(COLUMN_RADIUS, 1.0, COLUMN_RADIUS);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this.getOwner() && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().magic(), damage);
            target.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 40, 0));
        }
    }

    private void explode() {
        AABB area = this.getBoundingBox().inflate(COLUMN_RADIUS, COLUMN_HEIGHT, COLUMN_RADIUS);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this.getOwner() && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().explosion(this, this.getOwner()), 16.0f); // Higher Damage
            target.setDeltaMovement(target.getDeltaMovement().add(0, 1.2, 0)); // Higher Launch
            target.hurtMarked = true;
        }


    }

    // --- VISUALS ---

    private void spawnChargingParticles() {
        float progress = (float) this.tickCount / (float) CHARGE_TIME;

        // 1. FLOOR MAGIC (Accelerating)
        // Frequency increases closer to explosion
        if (this.random.nextFloat() < 0.5f + (progress * 0.5f)) {
            double r = this.random.nextDouble() * COLUMN_RADIUS;
            double angle = this.random.nextDouble() * Math.PI * 2;
            double x = this.getX() + Math.cos(angle) * r;
            double z = this.getZ() + Math.sin(angle) * r;

            // ACCELERATION LOGIC:
            // Starts slow (0.05), ends fast (0.45)
            double risingSpeed = 0.05 + (progress * 0.4);

            this.level().addParticle(new MagicFloorParticleOptions(
                            new Vector3f(0.8f, 0.0f, 0.0f),
                            0.4f,
                            false,
                            20), // Shorter life so they disappear faster as they speed up
                    x, this.getY() + 0.05, z,
                    0, risingSpeed, 0);
        }

        // 2. BLOCK CRUMBS (Massive Increase)
        // Spawns debris in a wider area (vecinity)
        int debrisCount = 1 + (int)(progress * 4); // 1 to 5 particles per tick
        BlockPos belowPos = this.blockPosition().below();
        BlockState state = this.level().getBlockState(belowPos);

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < debrisCount; i++) {
                // Wider radius for debris (Vicinity)
                double r = this.random.nextDouble() * (COLUMN_RADIUS * 1.5);
                double angle = this.random.nextDouble() * Math.PI * 2;

                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        this.getX() + Math.cos(angle) * r,
                        this.getY() + 0.1,
                        this.getZ() + Math.sin(angle) * r,
                        0.0,
                        0.1 + (progress * 0.3), // Debris also accelerates upward
                        0.0);
            }
        }

        // 3. PULSE RING (Warning)
        if (this.tickCount % 5 == 0) {
            double r = COLUMN_RADIUS * (1.0f - progress); // Shrinks
            for(int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 * i) / 8;
                this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(),
                        this.getX() + Math.cos(angle) * r, this.getY() + 0.1, this.getZ() + Math.sin(angle) * r,
                        0, 0, 0);
            }
        }
    }

    private void spawnEruptionParticles() {


        // 2. MAIN COLUMN (Gradient Core)
        int density = 20;
        if (this.tickCount > CHARGE_TIME + LINGER_TIME - 10) density = 5;

        for (int i = 0; i < density; i++) {
            double h = this.random.nextDouble() * COLUMN_HEIGHT;
            double r = this.random.nextDouble() * COLUMN_RADIUS;
            double angle = this.random.nextDouble() * Math.PI * 2;

            double x = this.getX() + Math.cos(angle) * r;
            double z = this.getZ() + Math.sin(angle) * r;
            double y = this.getY() + h;

            Vector3f color;
            float ratio = (float) (r / COLUMN_RADIUS);
            if (ratio < 0.3f) color = new Vector3f(1.0f, 0.9f, 0.9f);
            else if (ratio < 0.7f) color = new Vector3f(1.0f, 0.1f, 0.0f);
            else color = new Vector3f(0.4f, 0.0f, 0.0f);

            double vy = 0.4 + (h / COLUMN_HEIGHT) * 0.5; // Faster eruption speed

            this.level().addParticle(new MagicParticleOptions(
                            color, 0.6f + this.random.nextFloat() * 0.4f, false, 30),
                    x, y, z, 0, vy, 0);
        }

        // 3. NEW LAYER: OUTER DARK MAGIC SHELL
        // This creates a dark, ominous border around the fire column
        for (int i = 0; i < 10; i++) {
            // Spawn mostly at the edge (Radius 0.8 to 1.2)
            double r = COLUMN_RADIUS * (0.8 + this.random.nextDouble() * 0.4);
            double angle = this.random.nextDouble() * Math.PI * 2;
            double h = this.random.nextDouble() * (COLUMN_HEIGHT * 0.8); // Slightly shorter

            this.level().addParticle(new MagicParticleOptions(
                            new Vector3f(0.1f, 0.0f, 0.0f), // Very Dark Red / Black
                            0.8f + this.random.nextFloat() * 0.5f, // Larger Particles
                            false,
                            40), // Lasts longer
                    this.getX() + Math.cos(angle) * r,
                    this.getY() + h,
                    this.getZ() + Math.sin(angle) * r,
                    0, 0.1, 0); // Slow rising smoke effect
        }

        // 4. FIRE (Small Blood Flames)
        for (int i = 0; i < 8; i++) {
            double r = this.random.nextDouble() * COLUMN_RADIUS;
            double angle = this.random.nextDouble() * Math.PI * 2;

            this.level().addParticle(ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                    this.getX() + Math.cos(angle) * r,
                    this.getY(),
                    this.getZ() + Math.sin(angle) * r,
                    0, 0.2 + this.random.nextDouble() * 0.3, 0);
        }

        // 5. LOOSE FRAGMENTS (Explosive Burst)
        if (this.tickCount == CHARGE_TIME) {
            for (int i = 0; i < 30; i++) { // More fragments
                double vx = (this.random.nextDouble() - 0.5) * 1.0;
                double vy = 0.6 + this.random.nextDouble() * 0.8;
                double vz = (this.random.nextDouble() - 0.5) * 1.0;

                this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(),
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        vx, vy, vz);
            }
        }
    }
}