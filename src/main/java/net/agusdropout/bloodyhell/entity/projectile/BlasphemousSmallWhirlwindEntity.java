package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.BlasphemousArmEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;


public class BlasphemousSmallWhirlwindEntity extends AbstractHurtingProjectile implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    @Nullable private LivingEntity owner;
    @Nullable private UUID ownerUUID;

    private int lifeTicks = 200;
    public static final float DEFAULT_DAMAGE = 3.0F;
    private float damage = DEFAULT_DAMAGE;
    private boolean wasHit = false;

    private int hitsDone = 0;
    private final int maxHits = 5;

    @Nullable private BlasphemousWhirlwindEntity parentRing;


    private double xPower;
    private double yPower;
    private double zPower;


    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(BlasphemousSmallWhirlwindEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_RETURNED = SynchedEntityData.defineId(BlasphemousSmallWhirlwindEntity.class, EntityDataSerializers.BOOLEAN);

    private LivingEntity currentTarget;

    public BlasphemousSmallWhirlwindEntity(EntityType<? extends BlasphemousSmallWhirlwindEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BlasphemousSmallWhirlwindEntity(Level level, double x, double y, double z, LivingEntity owner) {
        this(ModEntityTypes.BLASPHEMOUS_SMALL_WHIRLWIND_ENTITY.get(), level);
        setOwner(owner);
        setPos(x, y, z);
    }

    public void setParentRing(BlasphemousWhirlwindEntity parent) {
        this.parentRing = parent;
    }


    public void shootPower(Vec3 direction, float velocity) {
        Vec3 norm = direction.normalize().scale(velocity);
        this.xPower = norm.x;
        this.yPower = norm.y;
        this.zPower = norm.z;
        this.setDeltaMovement(norm);
    }

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(this.getDeltaMovement().add(xPower, yPower, zPower));
        this.move(MoverType.SELF, this.getDeltaMovement());

        if(lifeTicks == 185) {
            level().playSound(null, this.blockPosition(), ModSounds.WHIRLWIND_FLYING_SOUND.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }

        if(this.level().isClientSide()) return;

        if (getOwner() == null || !getOwner().isAlive()) {
            discard();
            return;
        }

        if (lifeTicks-- <= 0) setReturningToOwner(true);


        if (!isReturningToOwner()) {
                if(!wasHit) {
                    if (currentTarget == null || !currentTarget.isAlive()) {
                        currentTarget = findNearestTarget();
                        if (currentTarget == null) {
                            setReturningToOwner(true);
                        }
                    }
                    if (currentTarget != null) moveTowards(currentTarget);
                    setSmallRingActive(true);
                } else {
                    setReturningToOwner(true);
                }
            } else {
                if (getOwner() != null) {
                    moveTowards(getOwner());
                    if (distanceTo(getOwner()) < 3f) {
                        setSmallRingActive(false);
                        setReturningToOwner(false);
                        if (parentRing != null) parentRing.onMinorRingReturned();
                        discard();
                    }
                } else discard();
            }

    }

    private LivingEntity findNearestTarget() {
        double range = 12.0;
        return level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(range))
                .stream()
                .filter(this::isValidTarget)
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    private void moveTowards(LivingEntity target) {
        if (target == null || !target.isAlive()) return;

        Vec3 dir = new Vec3(target.getX() - getX(), target.getY() - getY(), target.getZ() - getZ()).normalize();
        double speed = 0.2;
        this.setDeltaMovement(dir.scale(speed));
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.distanceTo(target) < 1f && hitsDone < maxHits) {
            target.hurt(this.damageSources().indirectMagic(this, getOwner()), damage);
            level().playSound(null, this.blockPosition(), ModSounds.WHIRLWIND_CUT_SOUND.get(), SoundSource.PLAYERS, 2.0f, 2.0f);
            hitsDone++;
            currentTarget = null;
            if (hitsDone >= maxHits) setReturningToOwner(true);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RETURNING, false);
        this.entityData.define(HAS_RETURNED, true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Owner")) ownerUUID = compoundTag.getUUID("Owner");
        lifeTicks = compoundTag.getInt("LifeTicks");
        setReturningToOwner(compoundTag.getBoolean("Returning"));
        setSmallRingActive(compoundTag.getBoolean("HasReturned"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("LifeTicks", lifeTicks);
        if (ownerUUID != null) compoundTag.putUUID("Owner", ownerUUID);
        compoundTag.putBoolean("Returning", isReturningToOwner());
        compoundTag.putBoolean("HasReturned", isSmallRingActive());
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (owner == null && ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(ownerUUID);
            if (entity instanceof LivingEntity) owner = (LivingEntity) entity;
        }
        return owner;
    }

    public boolean isReturningToOwner() {
        return entityData.get(RETURNING);
    }
    public void setReturningToOwner(boolean value) {
        entityData.set(RETURNING, value);
    }

    public boolean isSmallRingActive() {
        return entityData.get(HAS_RETURNED);
    }
    public void setSmallRingActive(boolean value) {
        entityData.set(HAS_RETURNED, value);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        level().playLocalSound(this.getOnPos(), SoundEvents.ANVIL_USE,  SoundSource.HOSTILE, 2.0f, 2.0f,false);
        if (!level().isClientSide && source.getEntity() != null) {
            Vec3 attackerPos = source.getEntity().position();
            Vec3 dir = this.position().subtract(attackerPos).normalize();

            double knockbackStrength = 2;
            this.setDeltaMovement(this.getDeltaMovement().add(dir.scale(knockbackStrength)));
            this.wasHit = true;


            this.currentTarget = null;
        }

        return true;
    }

    private boolean isValidTarget(LivingEntity candidate) {
        if (candidate == null || !candidate.isAlive()) return false;


        if (candidate == getOwner()) return false;


        if (candidate instanceof TamableAnimal tamable && tamable.getOwner() == this.getOwner()) {
            return false;
        }


        if (candidate instanceof BlasphemousArmEntity) {
            if(((BlasphemousArmEntity) candidate).getOwner() == this.getOwner()) return false;
        }



        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return factory; }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}

