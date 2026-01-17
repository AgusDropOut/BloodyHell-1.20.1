package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BloodSphereEntity extends Projectile {

    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(BloodSphereEntity.class, EntityDataSerializers.FLOAT);
    private int lifeTicks = 0;
    private static final int MAX_LIFE = 100;

    public BloodSphereEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public BloodSphereEntity(Level level, LivingEntity owner, float damage) {
        this(ModEntityTypes.BLOOD_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.setDamage(damage);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        Vec3 look = owner.getLookAngle();
        // Projectile speed (0.5 is slow and threatening)
        this.setDeltaMovement(look.scale(0.5));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 5.0f);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.lifeTicks++ >= MAX_LIFE) {
            this.discard();
            return;
        }

        Vec3 movement = this.getDeltaMovement();
        double nextX = this.getX() + movement.x;
        double nextY = this.getY() + movement.y;
        double nextZ = this.getZ() + movement.z;

        this.setPos(nextX, nextY, nextZ);
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);

        if (!this.level().isClientSide) {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }
        }

        // Particle trail while traveling (Client only)
        if (this.level().isClientSide) {
            for(int i=0; i<2; i++) {
                // Using animated particle for trail too, but with low speed
                this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(),
                        this.getX() + (random.nextDouble()-0.5)*0.3,
                        this.getY() + (random.nextDouble()-0.5)*0.3,
                        this.getZ() + (random.nextDouble()-0.5)*0.3,
                        0, 0, 0); // Zero velocity to float
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            explode();
            this.discard();
        }
    }

    private void explode() {
        float radius = 6.0f;
        float dmg = getDamage();

        // 1. DAMAGE
        AABB area = this.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity target : targets) {
            if (target != this.getOwner()) {
                target.hurt(this.damageSources().magic(), dmg);
                double dx = target.getX() - this.getX();
                double dz = target.getZ() - this.getZ();
                target.knockback(1.5, -dx, -dz);
            }
        }

        // 2. SOUNDS & PARTICLES
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0f, 1.5f);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.HOSTILE, 1.0f, 0.5f);

        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            serverLevel.sendParticles(ModParticles.BLOOD_PULSE_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), 60, 1.0, 1.0, 1.0, 0.2);

            EntityCameraShake.cameraShake(this.level(), this.position(), 20.0f, 2.0f, 15, 5);
            spawnDebris(serverLevel);

            // --- NEW: SPAWN VISCOUS SHRAPNEL ---
            spawnClots();
        }
    }

    private void spawnClots() {
        // Spawn 8 clots flying in random directions
        for (int i = 0; i < 8; i++) {
            BloodClotProjectile clot = new BloodClotProjectile(this.level(), this.getX(), this.getY(), this.getZ());
            if (this.getOwner() instanceof LivingEntity l) clot.setOwner(l);

            // Velocity: Random burst
            double vx = (random.nextDouble() - 0.5) * 1.5;
            double vy = random.nextDouble() * 0.8 + 0.2; // Tend upwards slightly
            double vz = (random.nextDouble() - 0.5) * 1.5;

            clot.setDeltaMovement(vx, vy, vz);
            this.level().addFreshEntity(clot);
        }
    }

    private void spawnDebris(ServerLevel level) {
        BlockPos impactPos = this.blockPosition().below(); // Check block below impact
        int debrisCount = 10; // Increased debris count

        for (int i = 0; i < debrisCount; i++) {
            // Random position near impact (radius 3)
            double offsetX = (random.nextDouble() - 0.5) * 3.0;
            double offsetZ = (random.nextDouble() - 0.5) * 3.0;
            BlockPos targetPos = impactPos.offset((int)offsetX, 0, (int)offsetZ);

            BlockState state = level.getBlockState(targetPos);

            // Only spawn debris if block is not air and is solid
            if (!state.isAir() && state.isSolidRender(level, targetPos)) {
                EntityFallingBlock debris = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), level);

                debris.setPos(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
                debris.setBlock(state); // Copy texture
                debris.setDuration(40); // Lasts 2 seconds

                // Velocity: Upwards and away from center
                debris.setDeltaMovement(offsetX * 0.3, 0.5 + random.nextDouble() * 0.4, offsetZ * 0.3);

                level.addFreshEntity(debris);
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (entity != this.getOwner() || this.lifeTicks >= 5);
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putFloat("Damage", getDamage()); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { setDamage(tag.getFloat("Damage")); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return new ClientboundAddEntityPacket(this); }
}