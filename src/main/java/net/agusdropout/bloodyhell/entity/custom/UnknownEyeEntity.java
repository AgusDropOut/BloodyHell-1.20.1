package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.ai.goals.CrystalAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.OniAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.SummonGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.UUID;

public class UnknownEyeEntity extends Entity implements TraceableEntity, GeoEntity {
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(UnknownEyeEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(UnknownEyeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> LOOK_YAW = SynchedEntityData.defineId(UnknownEyeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LOOK_PITCH = SynchedEntityData.defineId(UnknownEyeEntity.class, EntityDataSerializers.FLOAT);
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private int warmupDelayTicks;
    private boolean emergePhase = true;
    private boolean retractPhase = false;
    private boolean idlePhase = false;
    private int lifeTicks = 200;
    public static final float DEFAULT_DAMAGE = 3.0F;
    private float damage = DEFAULT_DAMAGE;
    public AnimationState emergeAnimationState = new AnimationState();
    public AnimationState retractAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();

    public UnknownEyeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public UnknownEyeEntity(Level level, double d, double e, double f, float g, int warmupDelayTicks, float damage, LivingEntity livingEntity) {
        this(ModEntityTypes.UNKNOWN_EYE_ENTITY.get(), level);
        this.warmupDelayTicks = warmupDelayTicks;
        this.damage = damage;
        this.setOwner(livingEntity);
        this.setYRot(g * 57.295776f);
        this.setPos(d, e, f);
        level().playLocalSound(d, e, f, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.2f + 0.85f, false);
    }
    public UnknownEyeEntity(Level level, double d, double e, double f, float g, int warmupDelayTicks, float damage, LivingEntity livingEntity, ItemStack itemStack) {
        this(ModEntityTypes.UNKNOWN_EYE_ENTITY.get(), level);
        this.warmupDelayTicks = warmupDelayTicks;
        this.damage = damage;
        this.setOwner(livingEntity);
        this.setYRot(g * 57.295776f);
        this.setPos(d, e, f);
        this.entityData.set(ITEM, itemStack.copy());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ACTIVE, false);
        this.entityData.define(ITEM, ItemStack.EMPTY);
        this.entityData.define(LOOK_YAW, 0f);
        this.entityData.define(LOOK_PITCH, 0f);
    }
    public float getLookYaw() {
        return this.entityData.get(LOOK_YAW);
    }
    public float getLookPitch() {
        return this.entityData.get(LOOK_PITCH);
    }

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
        this.warmupDelayTicks = compoundTag.getInt("Warmup");
        this.lifeTicks = compoundTag.getInt("LifeTicks");
        this.damage = compoundTag.getFloat("Damage");
        this.setActive(compoundTag.getBoolean("Active"));
        ItemStack itemStack = ItemStack.of(compoundTag.getCompound("TabletItem"));
        if (!itemStack.isEmpty()) {
            this.entityData.set(ITEM, itemStack);
        }
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Warmup", this.warmupDelayTicks);
        compoundTag.putInt("LifeTicks", this.lifeTicks);
        compoundTag.putFloat("Damage", this.damage);
        compoundTag.putBoolean("Active", this.isActive());
        ItemStack itemStack = this.entityData.get(ITEM);
        if (!itemStack.isEmpty()) {
            compoundTag.put("TabletItem", itemStack.save(new CompoundTag()));
        }
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
    }

    private void setActive(boolean active) {
        this.entityData.set(ACTIVE, active);
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    @Override
    public void tick() {
        super.tick();
        Player closestPlayer = this.level().getNearestPlayer(this, 10);
        if (closestPlayer != null) {

            Vec3 direction = closestPlayer.position().subtract(this.position()).normalize();


            double yaw = Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0;
            double pitch = Math.toDegrees(-Math.asin(direction.y));


            this.entityData.set(LOOK_YAW, (float) yaw);
            this.entityData.set(LOOK_PITCH, (float) pitch);
        }

        List<Arrow> arrows = this.level().getEntitiesOfClass(Arrow.class, this.getBoundingBox().inflate(1.2D));
        for (Arrow arrow : arrows) {
            Vec3 selfPos = this.position().add(0, 1.6f, 0);
            Vec3 enemyPos = arrow.getEyePosition().subtract(selfPos);
            Vec3 normalizedDirection = enemyPos.normalize();
            double knockbackX = 0.15;
            double knockbackY = 0.15;
            arrow.setDeltaMovement(arrow.getDeltaMovement().add(normalizedDirection.x() * knockbackY, normalizedDirection.y() * knockbackX, normalizedDirection.z() * knockbackY));
        }
        if (--this.warmupDelayTicks < 0) {
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
            for (LivingEntity livingEntity : list) {
                this.dealDamageTo(livingEntity);
            }
            if(lifeTicks == 199){

            }
            if(emergePhase && lifeTicks < 150){
                idlePhase = true;
                emergePhase = false;
            }
            if(idlePhase && lifeTicks < 20){
                retractPhase = true;
                idlePhase = false;
            }
            if(lifeTicks == 5){
                if (this.level().isClientSide) {
                    spawnExpandingCircleParticles(this.level(), this, 1.5, 25, 0.1);
                }
                level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.2f + 0.85f, false);
            }
            if(retractPhase && lifeTicks < 0){
                this.discard();
            }


        }
        lifeTicks--;

    }

    private void dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (livingEntity instanceof Player player && livingEntity2 != null && player.getUUID().equals(livingEntity2.getUUID())) {
            return;
        } else if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return;
        }
        if (livingEntity2 == null) {
            livingEntity.hurt(this.damageSources().magic(), this.damage);
        } else {
            if (livingEntity2.isAlliedTo(livingEntity)) return;
            livingEntity.hurt(this.damageSources().indirectMagic(this, livingEntity2), this.damage);
            ItemStack stack = this.entityData.get(ITEM);

        }
    }


    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.getEntity() instanceof Arrow) return false;
        return super.hurt(damageSource, f);
    }
    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState animationState) {



        if(idlePhase){
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if(emergePhase){
            animationState.getController().setAnimation(RawAnimation.begin().then("emerge", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if(retractPhase){
            animationState.getController().setAnimation(RawAnimation.begin().then("retract", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",
                0, this::predicate));

    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    public void spawnExpandingCircleParticles(Level level, Entity entity, double radius, int particleCount, double speed) {
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double offsetX = radius * Math.cos(angle);
            double offsetZ = radius * Math.sin(angle);

            double particleX = entity.getX() + offsetX;
            double particleY = entity.getY() + 1.0;
            double particleZ = entity.getZ() + offsetZ;

            double velocityX = speed * Math.cos(angle);
            double velocityZ = speed * Math.sin(angle);

            level.addParticle(ParticleTypes.FIREWORK, particleX, particleY-1, particleZ, velocityX, 0, velocityZ);
        }
    }


}

