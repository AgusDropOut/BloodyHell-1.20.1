package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.interfaces.BloodFlammable;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncBloodFireEffectPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ImpactParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class BloodFireColumnProjectile extends Projectile implements BloodFlammable {

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
            if (this.getOwner() instanceof LivingEntity owner) {
                stain.setOwner(owner);
            }
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
            setOnBloodFire(target,200, 0);
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



        // Pulse Ring
        if (this.tickCount % 5 == 0) {
            double r = COLUMN_RADIUS * (1.0f - progress);
            ParticleHelper.spawnRing(level(), ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    position().add(0, 0.1, 0), r, 8, 0);
        }
    }

    private void spawnEruptionParticles() {
        Vec3 pos = position();

        // COLORS
        Vector3f core = new Vector3f(1.0f, 0.9f, 0.9f);
        Vector3f mid = new Vector3f(1.0f, 0.1f, 0.0f);
        Vector3f edge = new Vector3f(0.05f, 0.0f, 0.0f);

        // GRADIENT PILLAR
        // Density: 30 particles per tick
        ParticleHelper.spawnCylinderGradient(level(), pos, COLUMN_RADIUS * 1.2, COLUMN_HEIGHT, 30, 0.4, (ratio) -> {

            // Color Gradient
            Vector3f color = ParticleHelper.gradient3(ratio, core, mid, mid);

            // Size Gradient (Center = small/tight, Edge = large/wispy)
            float pSize = 0.5f + (ratio * 0.8f);

            return new MagicParticleOptions(color, pSize, false, 40);
        });

        // Impact Ring (Standard)
        if (this.tickCount == CHARGE_TIME) {
            ParticleHelper.spawnRing(level(), ImpactParticleOptions.create(255, 50, 0, 4.0f, 40, false, 0.2f),
                    pos.add(0, 0.1, 0), COLUMN_RADIUS * 2.0, 40, 0);
        }
    }

    @Override
    public Level getLevel() {
        return this.level();
    }
}