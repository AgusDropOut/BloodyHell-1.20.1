package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.UUID;


public class VisceralProjectile extends Entity implements GeoEntity, TraceableEntity {
    private int lifeTicks = 80;
    private float damage;
    private boolean isAlternative = false;
    private final AnimatableInstanceCache factory =  new SingletonAnimatableInstanceCache(this);
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    private static final EntityDataAccessor<Float> LOOK_YAW = SynchedEntityData.defineId(VisceralProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LOOK_PITCH = SynchedEntityData.defineId(VisceralProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ON_GROUND = SynchedEntityData.defineId(VisceralProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ALTERNATIVE = SynchedEntityData.defineId(VisceralProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(VisceralProjectile.class, EntityDataSerializers.FLOAT);

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        this.setIsOnGround(compoundTag.getBoolean("OnGround"));



    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        compoundTag.putBoolean("OnGround", this.onGround());
    }

    public VisceralProjectile(EntityType<? extends VisceralProjectile> type, Level level) {
        super(type, level);
    }

    public VisceralProjectile(Level level, double x, double y, double z, double sx, double sy, double sz, float damage, LivingEntity owner) {
        this(ModEntityTypes.VISCERAL_PROJECTILE.get(), level);
        this.damage = damage;
        this.setOwner(owner);
        this.setPos(x, y, z);
        this.setDeltaMovement(new Vec3(sx, sy, sz));
        initializeRotation();
        this.entityData.set(IS_ALTERNATIVE, level.random.nextBoolean());
    }
    public VisceralProjectile(Level level, double x, double y, double z, double sx, double sy, double sz,float gravity, float damage, LivingEntity owner) {
        this(ModEntityTypes.VISCERAL_PROJECTILE.get(), level);
        this.damage = damage;
        this.setGravity(gravity);
        this.setOwner(owner);
        this.setPos(x, y, z);
        this.setDeltaMovement(new Vec3(sx, sy, sz));
        initializeRotation();
        this.entityData.set(IS_ALTERNATIVE, level.random.nextBoolean());
    }
    public float getPitch() {
        return entityData.get(LOOK_PITCH);
    }
    public float getYaw() {
        return entityData.get(LOOK_YAW);
    }
    public boolean getIsOnGround() {
        return entityData.get(ON_GROUND);
    }


    private void initializeRotation() {
        Vec3 motion = this.getDeltaMovement();
        float yaw = (float) Math.toDegrees(Math.atan2(motion.x, motion.z));
        float pitch = (float) Math.toDegrees(Math.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z)));

        pitch -= 40;
        yaw += 180;

            this.setYRot(yaw);
            this.setXRot(pitch);


    }

    @Override
    public void tick() {
        if(!this.isOnGround()){
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.spawnFlyingParticles();
            updateGravity();
        }
        if (this.onGround()) {
            this.setDeltaMovement(0,0,0);
            this.spawnImpactParticles();
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        this,
                        this.getOnPos(),
                        SoundEvents.SLIME_ATTACK,
                        SoundSource.HOSTILE,
                        1.0F,
                        1.0F // Pitch
                );
            }
            this.checkCollisions();
            this.discard();
        }

        lifeTicks--;
        if (lifeTicks <= 0) {
            this.discard();
        }

    }

    private void checkCollisions() {
        if (!this.level().isClientSide) {
            AABB boundingBox = this.getBoundingBox().inflate(4);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, boundingBox,
                    e -> e != this.getOwner() && e.isAlive());

            for (LivingEntity entity : entities) {
                entity.hurt(this.damageSources().generic(), this.damage);
                entity.addEffect(new MobEffectInstance(ModEffects.VISCERAL_EFFECT.get(), 60, 1));
                this.discard();
                return;
            }
        }

    }



    private void spawnFlyingParticles() {
        ParticleHelper.spawnSphereVolume(level(), ModParticles.VICERAL_PARTICLE.get(), position().add(0, 1, 0), 0.1, 5, getDeltaMovement().scale(-1));
    }

    private void spawnImpactParticles() {
        ParticleHelper.spawnHollowSphere(level(), ModParticles.VICERAL_PARTICLE.get(), position(), 3.0, 100, 0);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ON_GROUND, false);
        this.entityData.define(IS_ALTERNATIVE, false);
        this.entityData.define(GRAVITY, 0.3f);
    }
    public float getLookYaw() {
        return this.entityData.get(LOOK_YAW);
    }
    public float getLookPitch() {
        return this.entityData.get(LOOK_PITCH);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller",
                0, this::predicate));
    }
    public void updateGravity(){
            this.setDeltaMovement(this.getDeltaMovement().add(0, -getGravity(), 0));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    @Override
    public boolean onGround() {
        Vec3 position = this.position();
        BlockPos belowPos = new BlockPos(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ()); // Chequea justo debajo de la entidad
        BlockState blockBelow = level().getBlockState(belowPos);
        return !blockBelow.isAir();
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState animationState) {

        return PlayState.STOP;
    }

    public boolean isOnGround(){
       return entityData.get(ON_GROUND);
    }
    public void setIsOnGround(boolean onGround){
        entityData.set(ON_GROUND, onGround);
    }
    public boolean isAlternative() {
        return entityData.get(IS_ALTERNATIVE);
    }

    public float getGravity() {
        return entityData.get(GRAVITY);
    }
    public void setGravity(float gravity) {
        entityData.set(GRAVITY, gravity);
    }
}



