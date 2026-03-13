package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ai.goals.*;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;


public class VeinraverEntity extends Monster implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Boolean> isSlamAttack = SynchedEntityData.defineId(VeinraverEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> isThrowing = SynchedEntityData.defineId(VeinraverEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> isSlashAttack = SynchedEntityData.defineId(VeinraverEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SlamAttackCooldown = SynchedEntityData.defineId(VeinraverEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ThrowCooldown = SynchedEntityData.defineId(VeinraverEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SlashAttackCooldown = SynchedEntityData.defineId(VeinraverEntity.class, EntityDataSerializers.INT);

    private int DeathCooldown = 50;
    private boolean isAlreadyDead = false;

    @Override
    protected void tickDeath() {
        isAlreadyDead = true;
        DeathCooldown--;
        if(DeathCooldown <= 0){
            level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.GRAWL_DEATH.get(), this.getSoundSource(), 1.0F, 1.0F, false);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(isSlamAttack, false);
        this.entityData.define(isThrowing, false);
        this.entityData.define(isSlashAttack, false);
        this.entityData.define(SlamAttackCooldown, 120);
        this.entityData.define(ThrowCooldown, 120);
        this.entityData.define(SlashAttackCooldown, 120);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }


    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    public VeinraverEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80)
                .add(Attributes.ATTACK_DAMAGE, 7.0f)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE,20);


    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new VeinRaverAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(30, new VeinRaverSlamAttackGoal(this));
        this.goalSelector.addGoal(31, new VeinRaverSlashAttackGoal(this));
        this.goalSelector.addGoal(32, new VeinRaverThrowAttackGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
            if(!this.isAlreadyDead) {

                if (animationState.isMoving()) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("walking", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
                animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else {
                animationState.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
    }

    private PlayState attackPredicate(AnimationState state) {
        if(state.getController().getAnimationState().equals(AnimationController.State.STOPPED)){
            if(this.isSlamAttack()){
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().then("slam_attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            } else if(this.isThrowing()){
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().then("visceral_attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            } else if(this.isSlashAttack()){
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().then("slash_attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            } else if(this.swinging) {
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().then("basic_attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
        }

        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",
                0, this::predicate));
        controllers.add(new AnimationController(this, "attackController",
                0, this::attackPredicate));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.8F, 0.2F);
    }
    @Override
    protected float tickHeadTurn(float v, float v1) {
        return super.tickHeadTurn(v, v1);
    }
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getTarget() != null) {
            this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
        }
        if (isAlreadyDead) {
            DeathCooldown--;
            if (DeathCooldown <= 0) {
                this.discard();
            }
        } else {
            updateCooldowns();
        }
        if (this.getTarget() != null && !isThrowing() && !isSlashAttack() && !isSlamAttack()) {
            this.getNavigation().moveTo(getTarget(), 1.2);
        }



    }


    public void updateCooldowns(){
        if(this.getThrowCooldown() > 0 && !this.isThrowing()){
            this.setThrowCooldown(this.getThrowCooldown() - 1);
        }
        if(this.getSlamAttackCooldown() > 0 && !this.isSlamAttack()){
            this.setSlamAttackCooldown(this.getSlamAttackCooldown() - 1);
        }
        if(this.getSlashAttackCooldown() > 0 && !this.isSlashAttack()) {
            this.setSlashAttackCooldown(this.getSlashAttackCooldown() - 1);
        }
    }


    //Getters & Setters

    public boolean isSlamAttack() {
        return this.entityData.get(isSlamAttack);
    }
    public void setSlamAttack(boolean slamAttack) {
        this.entityData.set(isSlamAttack, slamAttack);
    }
    public boolean isThrowing() {
        return this.entityData.get(isThrowing);
    }
    public void setThrowing(boolean throwing) {
        this.entityData.set(isThrowing, throwing);
    }
    public boolean isSlashAttack() {
        return this.entityData.get(isSlashAttack);
    }
    public void setSlashAttack(boolean slashAttack) {
        this.entityData.set(isSlashAttack, slashAttack);
    }
    public int getSlamAttackCooldown() {
        return this.entityData.get(SlamAttackCooldown);
    }
    public void setSlamAttackCooldown(int slamAttackCooldown) {
        this.entityData.set(SlamAttackCooldown, slamAttackCooldown);
    }
    public int getThrowCooldown() {
        return this.entityData.get(ThrowCooldown);
    }
    public void setThrowCooldown(int throwCooldown) {
        this.entityData.set(ThrowCooldown, throwCooldown);
    }
    public int getSlashAttackCooldown() {
        return this.entityData.get(SlashAttackCooldown);
    }
    public void setSlashAttackCooldown(int slashAttackCooldown) {
        this.entityData.set(SlashAttackCooldown, slashAttackCooldown);
    }
    protected SoundEvent getAmbientSound() {
        return ModSounds.VEINRAVER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.VEINRAVER_HURT.get();

    }

    protected SoundEvent getDeathSound() {
        return ModSounds.VEINRAVER_DEATH.get();
    }

    protected float getSoundVolume() {
        return 1F;
    }



    


}
