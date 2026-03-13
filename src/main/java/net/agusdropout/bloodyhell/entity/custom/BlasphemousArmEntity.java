package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ai.goals.CloseDistanceAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.OffspringOfTheUnknownAttack;
import net.agusdropout.bloodyhell.particle.ModParticles;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.UUID;


public class BlasphemousArmEntity extends Monster implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    private int DeathCooldown = 40;
    private boolean isRising = true;
    private int risingCooldown = 40;
    private boolean isAlreadyDead = false;
    private double attackRange = 5.0D;
    private int maxLifeTicks = 200;
    private static final EntityDataAccessor<Integer> ATTACK_COOLDOWN = SynchedEntityData.defineId(BlasphemousArmEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE_TICKS = SynchedEntityData.defineId(BlasphemousArmEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BlasphemousArmEntity.class, EntityDataSerializers.BOOLEAN);


    public BlasphemousArmEntity(EntityType<? extends Monster> p_33002_, Level p_33003_,LivingEntity livingEntity) {
        super(p_33002_, p_33003_);
        this.setOwner(livingEntity);
        this.setLifeTicks(maxLifeTicks);
        risingCooldown = 40;

    }
    public BlasphemousArmEntity(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_COOLDOWN, 40);
        this.entityData.define(LIFE_TICKS, maxLifeTicks);
        this.entityData.define(IS_ATTACKING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("LifeTicks", this.getLifeTicks());
        compoundTag.putBoolean("isAttacking", this.IsAttacking());
        compoundTag.putInt("attackCooldown", this.getAttackCooldown());
    }


    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("LifeTicks")) {
            this.setLifeTicks(compoundTag.getInt("LifeTicks"));
        } else {
            this.setLifeTicks(maxLifeTicks);
        }
        this.setAttackCooldown(compoundTag.getInt("attackCooldown"));
        this.setIsAttacking(compoundTag.getBoolean("isAttacking"));

    }


    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35)
                .add(Attributes.ATTACK_DAMAGE, 7.0f)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE,20);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(5, new CloseDistanceAttackGoal(this, 1.0, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
        if(this.isRising){
            animationState.getController().setAnimation(RawAnimation.begin().then("rise", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else if(!this.isAlreadyDead){
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

    }

    private PlayState attackPredicate(AnimationState state) {
        if(this.IsAttacking() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            this.swinging = false;
            this.setIsAttacking(false);
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

    @Override
    protected float tickHeadTurn(float v, float v1) {
        return super.tickHeadTurn(v, v1);
    }
    @Override
    public void aiStep() {
        super.aiStep();
        if(this.isRising){
            if(this.risingCooldown == 39){
                spawnMagicRingParticle();
            }
            risingCooldown--;
            spawnDirtParticles();
            if(risingCooldown <= 0){
                this.isRising = false;
            }
        } else if (isAlreadyDead) {
            DeathCooldown--;
            tickDeath();
        } else {
            this.spawnBlasphemousParticles();
            if (this.getTarget() != null) {
                this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
            }
            if (this.getAttackCooldown() > 0) {
                this.setAttackCooldown(this.getAttackCooldown() - 1);
            }
            if (this.IsAttacking() && this.swinging == false) {
                this.setIsAttacking(false);
            }
            if (this.getLifeTicks() <= 0) {
                this.isAlreadyDead = true;
            }
        }
        this.setLifeTicks(this.getLifeTicks() - 1);
    }


    @Override
    protected void tickDeath() {
        isAlreadyDead = true;
        spawnDirtParticles();
        if (DeathCooldown <= 0) {
            level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.GRAWL_DEATH.get(), this.getSoundSource(), 1.0F, 1.0F, false);
            this.remove(RemovalReason.KILLED);
        }
    }

    public void spawnDirtParticles(){
        BlockPos belowPos = this.blockPosition().below();
        BlockState belowBlock = level().getBlockState(belowPos);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0F, 1.0F);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, belowBlock),
                    this.getX(), this.getY(), this.getZ(),
                    10,
                    0.5, 0.1, 0.5,
                    0.1
            );
        }
    }
    public void spawnMagicRingParticle(){

        if (this.level().isClientSide) {
            this.level().addParticle(
                    ModParticles.BLASPHEMOUS_MAGIC_RING.get(),
                    this.getX(),
                    this.getY() + 0.1,
                    this.getZ(),
                    maxLifeTicks, 0, 0
            );
            this.level().addParticle(ModParticles.CYLINDER_PARTICLE.get(), this.getX(), this.getY() , this.getZ(), maxLifeTicks, 0, 0);
        }
    }

    public void spawnBlasphemousParticles() {
        if (this.level().isClientSide) {
            double radiusXZ = 2.0;
            double height = 1.5;
            for (int i = 0; i < 3; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * radiusXZ;
                double offsetY = this.random.nextDouble() * height;
                double offsetZ = (this.random.nextDouble() - 0.5) * radiusXZ;

                this.level().addParticle(ModParticles.MAGIC_LINE_PARTICLE.get(),
                        this.getX() + offsetX,
                        this.getY() - 1.0 + offsetY,
                        this.getZ() + offsetZ,
                        0, 0.05 + this.random.nextDouble() * 0.05, 0);
            }
        }
    }


    public boolean isValidTarget() {
        LivingEntity target = this.getTarget();
        LivingEntity owner = this.getOwner();
        if (target != null) {
            if (owner != null ) {
                if(target instanceof BlasphemousArmEntity){
                    if(((BlasphemousArmEntity) target).getOwner() == owner){
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return target != owner;
                }
            } else {
                return true;
            }


        } else {
            return false;
        }
    }


    //Getters & Setters

    public void setAttackCooldown(int cooldown) {
        this.entityData.set(ATTACK_COOLDOWN, cooldown);
    }
    public int getAttackCooldown() {
        return this.entityData.get(ATTACK_COOLDOWN);
    }
    public void setLifeTicks(int cooldown) {
        this.entityData.set(LIFE_TICKS, cooldown);
    }
    public int getLifeTicks() {
        return this.entityData.get(LIFE_TICKS);
    }
    public void setIsAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }
    public boolean IsAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.VEX_HURT;

    }

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
    }


    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }


    protected float getSoundVolume() {
        return 1F;
    }


    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
    }

    public double getAttackRange() {
        return this.attackRange;
    }

    public void setAttackRange(double range) {
        this.attackRange = range;
    }

    public boolean isInAttackRange(LivingEntity target) {
        if (target == null) return false;
        double distSq = this.distanceToSqr(target);
        return distSq <= (attackRange * attackRange);
    }

}
