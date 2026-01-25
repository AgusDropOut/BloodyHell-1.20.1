package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.BloodFireBlockEntity;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ImpactParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class BloodFireMeteorProjectile extends Projectile {

    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(BloodFireMeteorProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(BloodFireMeteorProjectile.class, EntityDataSerializers.FLOAT);

    // Default Stats
    private float damage = 20.0f;
    private float speed = 1.5f;
    private float explosionRadius = 4.0f;
    private int growTime = 40;

    public BloodFireMeteorProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public BloodFireMeteorProjectile(EntityType<? extends Projectile> type, Level level, LivingEntity owner, float damage, float speed, float size) {
        super(type, level);
        this.setOwner(owner);
        this.damage = damage;
        this.speed = speed;
        this.entityData.set(SCALE, size);
        this.setPos(owner.getX(), owner.getY() + 5.0, owner.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(SCALE, 1.0f);
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }

    @Override
    public void tick() {
        super.tick();

        boolean launched = this.entityData.get(LAUNCHED);
        float currentScale = getScale();

        if (!launched) {
            if (this.tickCount >= growTime) {
                if (!this.level().isClientSide) {
                    launch();
                }
            } else {
                if (this.tickCount % 5 == 0) {
                    this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.HOSTILE, 1.0f + (tickCount/(float)growTime), 0.5f);
                }
            }
        }
        else {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }
            Vec3 motion = this.getDeltaMovement();
            this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        }

        if (this.level().isClientSide) {
            spawnParticles(currentScale);
        }

        if (this.tickCount > 200) discard();
    }

    private void launch() {
        this.entityData.set(LAUNCHED, true);

        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 dir;

            // 1. ROBUST AI TARGETING (Fix for Mobs/Bosses)
            if (owner instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() != null) {
                LivingEntity target = mob.getTarget();

                // Calculate vector from Meteor -> Target Prediction
                // We aim at the target's center, not feet.
                Vec3 targetPos = target.getBoundingBox().getCenter();

                // Optional: Predictive aiming (Aim slightly ahead based on target movement)
                // targetPos = targetPos.add(target.getDeltaMovement().scale(10));

                dir = targetPos.subtract(this.position()).normalize();
            }
            // 2. PLAYER / NO TARGET FALLBACK
            else {
                Vec3 look = owner.getLookAngle();
                // Project a point far ahead to get a vector
                Vec3 targetPos = owner.getEyePosition().add(look.scale(20.0));
                dir = targetPos.subtract(this.position()).normalize();
            }

            // 3. Apply Velocity
            this.setDeltaMovement(dir.scale(this.speed));

            // 4. Sound
            this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 2.0f, 0.5f);

        } else {
            // Fallback if owner is null/dead: Just drop down
            this.setDeltaMovement(new Vec3(0, -1, 0).normalize().scale(this.speed));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) return;

        float size = getScale();

        EntityCameraShake.cameraShake(this.level(), this.position(), 20.0f * size, 1.5f, 15, 10);
        this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.0f, 0.6f);

        spawnFallingBlocks(size);

        BloodStainEntity stain = new BloodStainEntity(this.level(), getX(), getY(), getZ(), Direction.UP, size * 2.5f);
        this.level().addFreshEntity(stain);
        stain.setOwner((LivingEntity) this.getOwner());

        placeFire(size);
        damageArea(size);
        this.level().broadcastEntityEvent(this, (byte) 3);

        this.discard();
    }

    private void spawnFallingBlocks(float size) {
        // Increased Debris Count (5 -> 12 per size unit)
        int count = (int)(12 * size);
        BlockPos below = this.blockPosition().below();
        BlockState state = this.level().getBlockState(below);

        if(state.getRenderShape() != RenderShape.INVISIBLE) {
            for(int i=0; i<count; i++) {
                // Wider spread for debris
                double offsetX = (random.nextDouble() - 0.5) * size * 3.0;
                double offsetZ = (random.nextDouble() - 0.5) * size * 3.0;

                EntityFallingBlock falling = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), 40, state);
                falling.setPos(this.getX() + offsetX, this.getY() + 1, this.getZ() + offsetZ);

                // Explosive outward velocity
                falling.setDeltaMovement(
                        (random.nextDouble()-0.5)*1.2,
                        0.6 + random.nextDouble()*0.8,
                        (random.nextDouble()-0.5)*1.2
                );

                // IMPORTANT: Actually spawn the entity!
                this.level().addFreshEntity(falling);
            }
        }
    }

    private void placeFire(float size) {
        BlockPos center = this.blockPosition();
        int radius = (int) size;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (random.nextBoolean()) {
                    BlockPos target = center.offset(x, 0, z);
                    if (this.level().getBlockState(target).isAir() && !this.level().getBlockState(target.below()).isAir()) {
                        this.level().setBlockAndUpdate(target, ModBlocks.BLOOD_FIRE.get().defaultBlockState());
                        // 2. Get the Block Entity
                        BlockEntity be = this.level().getBlockEntity(target);

                        // 3. Set the Owner
                        if (be instanceof BloodFireBlockEntity fireBe) {
                            fireBe.setOwner((LivingEntity) this.getOwner()); // Pass the LivingEntity owner
                        }
                    }
                }
            }
        }
    }

    private void damageArea(float size) {
        AABB area = this.getBoundingBox().inflate(explosionRadius * size);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for(LivingEntity e : list) {
            if (e != this.getOwner()) {
                e.hurt(this.damageSources().explosion(this, this.getOwner()), this.damage);
                e.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 100, 1, false, false, true));
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            spawnImpactParticles();
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnParticles(float size) {
        boolean launched = this.entityData.get(LAUNCHED);
        if (!launched) return;

        Vec3 motion = this.getDeltaMovement().normalize().scale(-1); // Trail moves opposite to flight

        // COLORS
        Vector3f white = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f brightRed = new Vector3f(1.0f, 0.1f, 0.0f);
        Vector3f darkRed = new Vector3f(0.1f, 0.0f, 0.0f);

        // ONE CALL - Handles Core, Mid, and Outer layers smoothly
        // Count: 25 particles per tick (Very Dense)
        ParticleHelper.spawnSphereGradient(level(), position().add(0, size * 0.5, 0), size * 1.5, 25, motion.scale(0.1), (ratio) -> {

            // 1. Calculate Color based on ratio (0.0 = center, 1.0 = edge)
            Vector3f color = ParticleHelper.gradient3(ratio, white, brightRed, brightRed);

            // 2. Calculate Size (Core is smaller, Edge is smoke-like and huge)
            float pSize = (size * 0.4f) + (ratio * size * 0.5f);

            // 3. Calculate Life (Core burns fast, Edge lingers)
            int life = 10 + (int)(ratio * 20);

            return new MagicParticleOptions(color, pSize, false, life);
        });

        // Add separate fire layer for variety
        ParticleHelper.spawnSphereGradient(level(), position().add(0, size * 0.5, 0), size * 0.8, 5, motion.scale(0.2), (r) ->
                ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get()
        );
    }
    private void spawnImpactParticles() {
        float size = getScale();
        Vec3 pos = position();

        // 1. IMPACT RING (Fixed Alignment)
        // We spawn ONE particle exactly at the center (pos).
        // The particle itself handles the expansion logic to 'size * 5.0'.
        ParticleHelper.spawn(level(),
                ImpactParticleOptions.create(255, 50, 0, size * 5.0f, 40, false, 0.2f),
                pos.x, pos.y + 0.1, pos.z,
                0, 0, 0
        );

        // 2. RISING FLOOR COLUMNS (Restored Logic)
        // We keep the loop because we need random rising speeds (vy) for each particle
        // to create that "uneven ground eruption" feel.
        for(int i = 0; i < 15; i++) {
            double r = random.nextDouble() * size * 2.0; // Random distance
            double a = random.nextDouble() * Math.PI * 2; // Random angle

            double x = pos.x + Math.cos(a) * r;
            double z = pos.z + Math.sin(a) * r;

            // Random upward velocity (0.2 to 0.5)
            double vy = 0.2 + random.nextDouble() * 0.3;

            ParticleHelper.spawn(level(),
                    new MagicFloorParticleOptions(new Vector3f(0.8f, 0, 0), 0.5f, false, 40),
                    x, pos.y + 0.1, z,
                    0, vy, 0
            );
        }

        // 3. DENSE MAGIC EXPLOSION (Existing)
        Vector3f color = random.nextBoolean() ? new Vector3f(0.8f, 0f, 0f) : new Vector3f(0.3f, 0f, 0f);

        ParticleHelper.spawnHollowSphere(level(),
                new MagicParticleOptions(color, 0.8f, false, 50),
                pos.add(0, 0.5, 0),
                size * 3.0,
                30,
                0.1
        );
    }
}