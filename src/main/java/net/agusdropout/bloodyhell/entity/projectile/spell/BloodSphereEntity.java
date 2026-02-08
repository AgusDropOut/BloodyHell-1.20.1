package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.entity.projectile.BloodClotProjectile;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
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

public class BloodSphereEntity extends Projectile implements IGemSpell {

    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(BloodSphereEntity.class, EntityDataSerializers.FLOAT);

    private static final float DEFAULT_RADIUS = 6.0f;
    private static final int DEFAULT_LIFE = 100;

    private float damage;
    private int maxLife = DEFAULT_LIFE;
    private int lifeTicks = 0;
    private int delayTicks = 0;

    public BloodSphereEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public BloodSphereEntity(Level level, LivingEntity owner, float damage) {
        this(ModEntityTypes.BLOOD_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.damage = damage;
        this.initPosition(owner);
    }

    public BloodSphereEntity(Level level, LivingEntity owner, float damage, int delayTicks, List<Gem> gems) {
        this(ModEntityTypes.BLOOD_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.damage = damage;
        this.delayTicks = delayTicks;
        this.initPosition(owner);
        configureSpell(gems);

        if (delayTicks > 0) {
            this.setInvisible(true);
        }
    }

    private void initPosition(LivingEntity owner) {
        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        Vec3 look = owner.getLookAngle();
        this.setDeltaMovement(look.scale(0.5));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, DEFAULT_RADIUS);
    }

    public float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    public void tick() {
        super.tick();

        if (delayTicks > 0) {
            delayTicks--;
            if (delayTicks == 0) {
                this.setInvisible(false);
                this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1.0f, 1.5f);
            }
            return;
        }

        if (this.lifeTicks++ >= maxLife) {
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

        if (this.level().isClientSide) {
            spawnTravelParticles();
        }
    }

    private void spawnTravelParticles() {
        float radius = getRadius();
        float scaleFactor = radius / DEFAULT_RADIUS;

        for (int i = 0; i < 2; i++) {
            this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    this.getX() + (random.nextDouble() - 0.5) * 0.3 * scaleFactor,
                    this.getY() + (random.nextDouble() - 0.5) * 0.3 * scaleFactor,
                    this.getZ() + (random.nextDouble() - 0.5) * 0.3 * scaleFactor,
                    0, 0, 0);
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
        float currentRadius = getRadius();

        AABB area = this.getBoundingBox().inflate(currentRadius);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity target : targets) {
            if (target != this.getOwner()) {
                target.hurt(this.damageSources().magic(), this.damage);
                double dx = target.getX() - this.getX();
                double dz = target.getZ() - this.getZ();
                target.knockback(1.5, -dx, -dz);
            }
        }

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0f, 1.5f);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.HOSTILE, 1.0f, 0.5f);

        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();

            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            serverLevel.sendParticles(ModParticles.BLOOD_PULSE_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), 60, 1.0, 1.0, 1.0, 0.2);

            EntityCameraShake.cameraShake(this.level(), this.position(), currentRadius * 3.5f, 2.0f, 15, 5);

            spawnDebris(serverLevel);
            spawnClots();
        }
    }

    private void spawnClots() {
        for (int i = 0; i < 8; i++) {
            BloodClotProjectile clot = new BloodClotProjectile(this.level(), this.getX(), this.getY(), this.getZ());
            if (this.getOwner() instanceof LivingEntity l) clot.setOwner(l);

            double vx = (random.nextDouble() - 0.5) * 1.5;
            double vy = random.nextDouble() * 0.8 + 0.2;
            double vz = (random.nextDouble() - 0.5) * 1.5;

            clot.setDeltaMovement(vx, vy, vz);
            this.level().addFreshEntity(clot);
        }
    }

    private void spawnDebris(ServerLevel level) {
        BlockPos impactPos = this.blockPosition().below();
        int debrisCount = 10;
        float radiusScale = getRadius() / DEFAULT_RADIUS;

        for (int i = 0; i < debrisCount; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 3.0 * radiusScale;
            double offsetZ = (random.nextDouble() - 0.5) * 3.0 * radiusScale;
            BlockPos targetPos = impactPos.offset((int) offsetX, 0, (int) offsetZ);

            BlockState state = level.getBlockState(targetPos);

            if (!state.isAir() && state.isSolidRender(level, targetPos)) {
                EntityFallingBlock debris = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), level);

                debris.setPos(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
                debris.setBlock(state);
                debris.setDuration(40);

                debris.setDeltaMovement(offsetX * 0.3, 0.5 + random.nextDouble() * 0.4, offsetZ * 0.3);
                level.addFreshEntity(debris);
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (entity != this.getOwner() || this.lifeTicks >= 5);
    }

    @Override
    public void increaseSpellDamage(double amount) {
        this.damage += (float) amount;
    }

    @Override
    public void increaseSpellSize(double amount) {
        float current = this.entityData.get(DATA_RADIUS);
        this.entityData.set(DATA_RADIUS, current + (DEFAULT_RADIUS * (float)amount));
    }

    @Override
    public void increaseSpellDuration(int amount) {
        this.maxLife += amount;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Damage", this.damage);
        tag.putFloat("Radius", getRadius());
        tag.putInt("MaxLife", this.maxLife);
        tag.putInt("Delay", this.delayTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.contains("Radius")) this.entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
        if (tag.contains("MaxLife")) this.maxLife = tag.getInt("MaxLife");
        if (tag.contains("Delay")) this.delayTicks = tag.getInt("Delay");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}