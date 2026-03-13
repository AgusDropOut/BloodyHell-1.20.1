package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ai.goals.OmenGazerAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.OmenGazerSuicideGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.OmenGazerThrowGoal;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;


public class OmenGazerEntity extends Monster implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Boolean> IS_CHARGING = SynchedEntityData.defineId(OmenGazerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_THROWING = SynchedEntityData.defineId(OmenGazerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> THROWING_COOLDOWN = SynchedEntityData.defineId(OmenGazerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_COOLDOWN = SynchedEntityData.defineId(OmenGazerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ABOUT_TO_EXPLODE = SynchedEntityData.defineId(OmenGazerEntity.class, EntityDataSerializers.BOOLEAN);
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
        this.entityData.define(IS_CHARGING, false);
        this.entityData.define(IS_THROWING, false);
        this.entityData.define(THROWING_COOLDOWN, 100);
        this.entityData.define(ATTACK_COOLDOWN, 0);
        this.entityData.define(IS_ABOUT_TO_EXPLODE, false);

    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_21484_) {
        super.addAdditionalSaveData(p_21484_);
        p_21484_.putBoolean("IsCharging", this.isCharging());
        p_21484_.putBoolean("IsThrowing", this.isThrowing());
        p_21484_.putInt("ThrowingCooldown", this.getThrowingCooldown());
        p_21484_.putInt("AttackCooldown", this.getAttackCooldown());
        p_21484_.putBoolean("IsAboutToExplode", this.isAboutToExplode());
    }


    @Override
    public void readAdditionalSaveData(CompoundTag p_21450_) {
        super.readAdditionalSaveData(p_21450_);
        this.setCharging(p_21450_.getBoolean("IsCharging"));
        this.setThrowing(p_21450_.getBoolean("IsThrowing"));
        this.setThrowingCooldown(p_21450_.getInt("ThrowingCooldown"));
        this.setAttackCooldown(p_21450_.getInt("AttackCooldown"));
        this.setAboutToExplode(p_21450_.getBoolean("IsAboutToExplode"));

    }

    public OmenGazerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ATTACK_DAMAGE, 7.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE,30);


    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new OmenGazerAttackGoal(this));
        this.goalSelector.addGoal(3, new OmenGazerThrowGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new OmenGazerSuicideGoal(this));

        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
        if(!this.isAlreadyDead) {
            if (animationState.isMoving()) {
                if (!this.isAboutToExplode()) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("walking", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                } else {
                    animationState.getController().setAnimation(RawAnimation.begin().then("running", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
            }

            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
    }

    private PlayState attackPredicate(AnimationState state) {
        state.getController().forceAnimationReset();
        if(this.swinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            if (this.isCharging()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.GRAWL_ATTACK.get(), this.getSoundSource(), 1.0F, 1.0F, false);
                state.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
                this.setCharging(false);
            } else if (this.isThrowing()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EGG_THROW, net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 1.0F, false);
                state.getController().setAnimation(RawAnimation.begin().then("throw", Animation.LoopType.PLAY_ONCE));
                this.setThrowing(false);
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
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float tickHeadTurn(float v, float v1) {
        return super.tickHeadTurn(v, v1);
    }
    public static boolean canSpawn(EntityType<OmenGazerEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, position, random);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.GRAWL_DROWN.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.GRAWL_HURT.get();

    }

    protected SoundEvent getDeathSound() {
        return ModSounds.GRAWL_DEATH.get();
    }

    protected float getSoundVolume() {
        return 0.2F;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Hacer que la entidad mire a su objetivo
        if (this.getTarget() != null) {
            this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
        }

        // Manejo de cooldowns y eliminación
        if (isAlreadyDead) {
            DeathCooldown--;
            if (DeathCooldown <= 0) {
                this.discard();
            }
        }

        if (this.getThrowingCooldown() > 0) {
            this.setThrowingCooldown(this.getThrowingCooldown() - 1);
        }
        if (this.getAttackCooldown() > 0) {
            this.setAttackCooldown(this.getAttackCooldown() - 1);
        }

        if (this.getTarget() != null && !isThrowing() && !isCharging() && !isAboutToExplode()) {
            this.getNavigation().moveTo(getTarget(), 1.2);
        }


    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        boolean damaged = super.hurt(source, amount);

        if (!this.level().isClientSide) { // Solo en servidor
            float healthPercentage = this.getHealth() / this.getMaxHealth();
            if (healthPercentage < 0.2f) { // Si la vida cae por debajo del 20%
                this.setAboutToExplode(true);
            }
        }

        return damaged;

    }

    public void abouttoexplodeparticles(){
        // **SPAWNEAR PARTÍCULAS ALREDEDOR DE LA ENTIDAD**
        if (this.level().isClientSide) {  // Solo en el cliente
            for (int i = 0; i < 5; i++) {  // Número de partículas por tick
                double offsetX = (this.random.nextDouble() - 0.5) * this.getBbWidth();  // Aleatorio en el ancho de la entidad
                double offsetY = this.random.nextDouble() * this.getBbHeight();  // Aleatorio en la altura de la entidad
                double offsetZ = (this.random.nextDouble() - 0.5) * this.getBbWidth();

                this.level().addParticle(
                        ModParticles.VICERAL_PARTICLE.get(),  // Tipo de partícula (puedes usar un custom si lo tienes)
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        0, 0.05, 0  // Velocidad (en este caso, flotando ligeramente)
                );
            }
        }
    }



    public boolean doHurtTarget(Entity p_28837_) {
        this.level().broadcastEntityEvent(this, (byte)4);
        float f = 10;
        float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        boolean flag = p_28837_.hurt(this.damageSources().mobAttack(this), f1);
        if (flag) {
            double d2;
            if (p_28837_ instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)p_28837_;
                d2 = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            } else {
                d2 = 0.0;
            }

            double d0 = d2;
            double d1 = Math.max(0.0, 1.0 - d0);
            p_28837_.setDeltaMovement(p_28837_.getDeltaMovement().add(0.0, 0.4000000059604645 * d1, 0.0));
            this.doEnchantDamageEffects(this, p_28837_);
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;

    }

    public boolean isCharging() {
        return entityData.get(IS_CHARGING);
    }
    public void setCharging(boolean charging) {
        entityData.set(IS_CHARGING, charging);
    }
    public boolean isThrowing() {
        return entityData.get(IS_THROWING);
    }
    public void setThrowing(boolean throwing) {
        entityData.set(IS_THROWING, throwing);
    }
    public int getThrowingCooldown() {
        return entityData.get(THROWING_COOLDOWN);
    }
    public void setThrowingCooldown(int cooldown) {
        entityData.set(THROWING_COOLDOWN, cooldown);
    }
    public int getAttackCooldown() {
        return entityData.get(ATTACK_COOLDOWN);
    }
    public void setAttackCooldown(int cooldown) {
        entityData.set(ATTACK_COOLDOWN, cooldown);
    }
    public boolean isAboutToExplode() {
        return entityData.get(IS_ABOUT_TO_EXPLODE);
    }
    public void setAboutToExplode(boolean aboutToExplode) {
        entityData.set(IS_ABOUT_TO_EXPLODE, aboutToExplode);
    }


    


}
