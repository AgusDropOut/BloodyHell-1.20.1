package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;
import net.agusdropout.bloodyhell.entity.ai.goals.OffspringOfTheUnknownAttack;
import net.agusdropout.bloodyhell.entity.base.AbstractInsightMonster;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class OffspringOfTheUnknownEntity extends AbstractInsightMonster implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> ATTACK_COOLDOWN = SynchedEntityData.defineId(OffspringOfTheUnknownEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(OffspringOfTheUnknownEntity.class, EntityDataSerializers.BOOLEAN);

    private int deathCooldown = 40;
    private boolean isAlreadyDead = false;

    public OffspringOfTheUnknownEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ATTACK_SPEED, 0.8D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_COOLDOWN, 40);
        this.entityData.define(IS_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new OffspringOfTheUnknownAttack(this));

        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getTarget() != null) {
            this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
        }

        if (isAlreadyDead) {
            deathCooldown--;
            if (deathCooldown <= 0) {
                this.discard();
            }
        } else {
            updateCooldowns();
            if (this.getTarget() != null && !this.getIsAttacking()) {
                this.getNavigation().moveTo(getTarget(), 1.5D);
            }
        }
    }

    @Override
    protected void tickDeath() {
        isAlreadyDead = true;
        deathCooldown--;
        BlockPos belowPos = this.blockPosition().below();
        BlockState belowBlock = level().getBlockState(belowPos);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0F, 1.0F);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, belowBlock),
                    this.getX(), this.getY(), this.getZ(),
                    10, 0.5D, 0.1D, 0.5D, 0.1D
            );
        }

        if (deathCooldown <= 0) {
            this.playSound(ModSounds.GRAWL_DEATH.get(), 1.0F, 1.0F);
            this.remove(RemovalReason.KILLED);
        }
    }

    public void updateCooldowns() {
        if (this.getAttackCooldown() > 0 && !this.getIsAttacking()) {
            this.setAttackCooldown(this.getAttackCooldown() - 1);
        }
    }

    @Override
    protected float tickHeadTurn(float yRot, float animStep) {
        return super.tickHeadTurn(yRot, animStep);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
    }

    private PlayState predicate(AnimationState<OffspringOfTheUnknownEntity> animationState) {
        if (!this.isAlreadyDead) {
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

    private PlayState attackPredicate(AnimationState<OffspringOfTheUnknownEntity> state) {
        if (this.swinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public float getMinimumInsight() {
        return PlayerInsight.INSIGHT_FOR_LEVEL_1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return hasSufficientClientInsight() ? ModSounds.OFFSPRING_AMBIENT.get() : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return hasSufficientClientInsight() ? ModSounds.OFFSPRING_HURT.get() : null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if (this.hasSufficientClientInsight()) {
            this.playSound(ModSounds.OFFSPRING_STEP.get(), 0.8F, 0.2F);
        }
    }

    public void setAttackCooldown(int cooldown) {
        this.entityData.set(ATTACK_COOLDOWN, cooldown);
    }

    public int getAttackCooldown() {
        return this.entityData.get(ATTACK_COOLDOWN);
    }

    public void setIsAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    public boolean getIsAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }
}