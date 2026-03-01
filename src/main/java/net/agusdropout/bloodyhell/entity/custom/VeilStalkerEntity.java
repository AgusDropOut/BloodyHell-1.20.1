package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ai.goals.VeilStalkerDiveAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.VeilStalkerHangGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.VeilStalkerPatrolGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.VeilStalkerTargetGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VeilStalkerEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // timers and control
    private int diveTimer = 0;
    private int warningTimer = 0;

    private final int maxDiveDuration = 40;
    private final int maxWarningDuration = 20;

    // === Cooldown management ===
    private int hangCooldown = 0;
    private int patrolCooldown = 0;

    private final int maxHangCooldown = 1000;   // 10 seconds (20 ticks = 1 second)
    private final int maxPatrolCooldown = 40;  // 2 seconds

    // synced data
    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(VeilStalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HANGING = SynchedEntityData.defineId(VeilStalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING = SynchedEntityData.defineId(VeilStalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STALKING = SynchedEntityData.defineId(VeilStalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DASHING = SynchedEntityData.defineId(VeilStalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SPECIAL_ATTACK = SynchedEntityData.defineId(VeilStalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACHED_ENTITY_ID = SynchedEntityData.defineId(VeilStalkerEntity.class, EntityDataSerializers.INT);

    public VeilStalkerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.05D)
                .add(Attributes.FOLLOW_RANGE, 50.0D)
                .build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLYING, true);
        this.entityData.define(DATA_HANGING, false);
        this.entityData.define(DATA_ATTACKING, false);
        this.entityData.define(DATA_SPECIAL_ATTACK, false);
        this.entityData.define(DATA_STALKING, false);
        this.entityData.define(DATA_DASHING, false);
        this.entityData.define(DATA_ATTACHED_ENTITY_ID, -1);
    }

    /* ===================================================
       ==             GOAL-ACCESSIBLE METHODS            ==
       =================================================== */



    /* ===================================================
       ==                  ANIMATION LOGIC               ==
       =================================================== */

    private PlayState predicate(AnimationState<VeilStalkerEntity> state) {
        if (this.isHanging()) {
            state.getController().setAnimation(RawAnimation.begin().then("hanging_from_wall", Animation.LoopType.LOOP));
        }
        else if (this.isDashing()) {
            state.getController().setAnimation(RawAnimation.begin().then("dash", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else if (this.isAttacking()) {
            state.getController().setAnimation(RawAnimation.begin().then("fall_attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else if (this.isStalking()) {
            state.getController().setAnimation(RawAnimation.begin().then("stalking", Animation.LoopType.LOOP));
        } else if (this.isFlyingState()) {
            state.getController().setAnimation(RawAnimation.begin().then("flying", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /* ===================================================
       ==                    LOGIC LOOP                  ==
       =================================================== */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new VeilStalkerHangGoal(this,0.5D));
        this.goalSelector.addGoal(3, new VeilStalkerDiveAttackGoal(this, 3.0D));
        this.goalSelector.addGoal(3, new VeilStalkerPatrolGoal(this, 0.5D));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.targetSelector.addGoal(10, new VeilStalkerTargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(10, new VeilStalkerTargetGoal<>(this, AbstractVillager.class));

    }

    @Override
    public void aiStep() {
        super.aiStep();


        if (this.level().isClientSide()) return;



        // update cooldowns
        if (warningTimer > 0) warningTimer--;

        // Update cooldowns each tick
        this.decrementHangCooldown();
        this.decrementPatrolCooldown();

    }


    protected PathNavigation createNavigation(Level p_29417_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_29417_);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }


    private void detachFromEntity() {
        this.entityData.set(DATA_ATTACHED_ENTITY_ID, -1);
        this.setHanging(false);
        this.setFlyingState(true);
    }

    public void startHangingServerSide() {
        this.setHanging(true);
        this.setFlyingState(false);
        this.setDeltaMovement(Vec3.ZERO);

        // Prevent immediate re-hanging and re-patrolling
        this.setHangCooldown(this.maxHangCooldown);
        this.setPatrolCooldown(this.maxPatrolCooldown);
    }

    public void stopHanging() {
        this.setHanging(false);
        this.setFlyingState(true);

        // Give a short patrol cooldown before it starts moving again
        this.setPatrolCooldown(this.maxPatrolCooldown);
    }

    /* ===================================================
       ==                 DATA ACCESSORS                 ==
       =================================================== */

    public boolean isFlyingState() { return this.entityData.get(DATA_FLYING); }
    public void setFlyingState(boolean flying) { this.entityData.set(DATA_FLYING, flying); }

    public boolean isHanging() { return this.entityData.get(DATA_HANGING); }
    public void setHanging(boolean hanging) { this.entityData.set(DATA_HANGING, hanging); }

    public boolean isAttacking() { return this.entityData.get(DATA_ATTACKING); }
    public void setAttacking(boolean attacking) { this.entityData.set(DATA_ATTACKING, attacking); }

    public boolean isSpecialAttack() { return this.entityData.get(DATA_SPECIAL_ATTACK); }
    public void setSpecialAttack(boolean special) { this.entityData.set(DATA_SPECIAL_ATTACK, special); }

    /* ===================================================
       ==                  DAMAGE LOGIC                  ==
       =================================================== */

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isHanging()) return super.hurt(source, amount * 0.6f);
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    // --- Getters and setters ---
    public int getHangCooldown() {
        return this.hangCooldown;
    }
    public void setHangCooldown(int value) {
        this.hangCooldown = value;
    }
    public void decrementHangCooldown() {
        if (this.hangCooldown > 0) this.hangCooldown--;
    }

    public int getPatrolCooldown() {
        return this.patrolCooldown;
    }
    public void setPatrolCooldown(int value) {
        this.patrolCooldown = value;
    }
    public void decrementPatrolCooldown() {
        if (this.patrolCooldown > 0) this.patrolCooldown--;
    }

    public int getMaxHangCooldown() {
        return maxHangCooldown;
    }

    public boolean isStalking() {
        return this.entityData.get(DATA_STALKING);
    }
    public void setStalking(boolean stalking) {
        this.entityData.set(DATA_STALKING, stalking);
    }
    public boolean isDashing() {
        return this.entityData.get(DATA_DASHING);
    }
    public void setDashing(boolean dashing) {
        this.entityData.set(DATA_DASHING, dashing);
    }
    public void setDiveTimer(int ticks) {
        this.diveTimer = ticks;
    }
}