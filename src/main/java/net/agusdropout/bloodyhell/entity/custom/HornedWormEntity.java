package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
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
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class HornedWormEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public int maxBurrowingTicks = 80;
    public int burrowingTicks = maxBurrowingTicks;

    public int maxRisingTicks = 80;
    public int risingTicks = maxRisingTicks;

    public int maxAttackDuration = 35;
    public int attackDuration = maxAttackDuration;

    public int maxBurrowSoundEffectDuration = 120;
    public int burrowSoundEffectDuration = 0;



    private static final EntityDataAccessor<Integer> ATTACK_COOLDOWN = SynchedEntityData.defineId(HornedWormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(HornedWormEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> IS_BURROWED = SynchedEntityData.defineId(HornedWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_BURROWING = SynchedEntityData.defineId(HornedWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RISING = SynchedEntityData.defineId(HornedWormEntity.class, EntityDataSerializers.BOOLEAN);



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(IS_BURROWED, false);
        this.entityData.define(IS_BURROWING, false);
        this.entityData.define(IS_RISING, false);
        this.entityData.define(ATTACK_COOLDOWN, 50);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }


    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    public HornedWormEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35)
                .add(Attributes.ATTACK_DAMAGE, 7.0f)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.20f)
                .add(Attributes.FOLLOW_RANGE,20).build();


    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
                if(this.isBurrowing()) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("burrow", Animation.LoopType.PLAY_ONCE));
                    return PlayState.CONTINUE;
                } else
                if(this.isRising()) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("rise", Animation.LoopType.PLAY_ONCE));
                    return PlayState.CONTINUE;
                } else
                if (animationState.isMoving()) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("moving", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                } else if(this.getIsAttacking()) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("attack_while_burrowed", Animation.LoopType.PLAY_ONCE));
                    return PlayState.CONTINUE;
                } else {
                    animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }

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

    @Override
    protected float tickHeadTurn(float v, float v1) {
        return super.tickHeadTurn(v, v1);
    }
    @Override
    public void aiStep() {
        super.aiStep();
            if(this.level().isClientSide()) return;


            if (this.getTarget() != null && !this.getIsAttacking()) {
                this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
            }

            updateCooldowns();
            if (this.getTarget() != null && !this.getIsAttacking() && !this.isBurrowed() && !this.isBurrowing() && !this.isRising()) {
                this.setBurrowing(true);
            } if(this.getTarget() == null && !this.isBurrowing() && this.isBurrowed() && !this.isRising()) {
                this.setRising(true);
                this.setBurrowed(false);
            }

            if(this.isBurrowing()) {
                this.burrowingTicks--;
                burrowingAndRisingEffects();
                this.setDeltaMovement(0,0,0);
                if(this.burrowingTicks <= 0) {
                    this.setBurrowing(false);
                    this.setBurrowed(true);
                    this.burrowingTicks = maxBurrowingTicks;
                }
            }

            if(this.isRising()) {
                this.risingTicks--;
                burrowingAndRisingEffects();
                this.setDeltaMovement(0,0,0);
                if(this.risingTicks <= 0) {
                    this.setRising(false);
                    this.risingTicks = maxRisingTicks;
                }
            }

            if(this.isBurrowed() && this.getTarget() != null && !this.getIsAttacking() && this.distanceTo(this.getTarget()) > 1) {
                this.getNavigation().moveTo(getTarget(), 1.5);
                spawnMovingWhileBurrowedParticles();
                if(burrowSoundEffectDuration > 0) {
                    burrowSoundEffectDuration--;
                } else {
                    this.level().playSound(null, this.blockPosition(), ModSounds.HORNED_WORM_BURROWED.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                    burrowSoundEffectDuration = maxBurrowSoundEffectDuration;
                }
            }

            if(this.isBurrowed() && this.getTarget() != null && this.distanceTo(this.getTarget()) <= 1 && this.getAttackCooldown() <= 0 && !this.getIsAttacking()) {
                this.setIsAttacking(true);
                this.setAttackCooldown(100);
                this.level().playSound(null, this.blockPosition(), ModSounds.OFFSPRING_ATTACK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            }

            if(this.getIsAttacking()) {
                if(attackDuration == maxAttackDuration-5) {
                    this.setBurrowed(false);
                }
                this.attackDuration--;
                this.setDeltaMovement(0,0,0);
                if(this.attackDuration == 15) {
                    if(this.getTarget() != null && this.distanceTo(this.getTarget()) <= 2) {
                        this.getTarget().hurt(this.damageSources().mobAttack(getTarget()), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        this.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.HOSTILE, 1.0F, 1.0F);
                    }
                }
                burrowingAndRisingEffects();
                if(this.attackDuration <= 0) {
                    this.setBurrowed(true);
                    this.setIsAttacking(false);
                    this.attackDuration = maxAttackDuration;
                }
            }




    }


    public void updateCooldowns(){
        if(this.getAttackCooldown() > 0 && !this.getIsAttacking()){
            this.setAttackCooldown(this.getAttackCooldown() - 1);
        }

    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if(this.isBurrowed()) {
            return false;
        } else {
            return super.hurt(p_21016_, p_21017_);
        }
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        //if(this.isBurrowed() || this.getIsAttacking()) {
        //    return false;
        //} else return super.canCollideWith(p_20303_);
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {

    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if(this.isBurrowed() || this.getIsAttacking()) {
            return;
        } else super.knockback(p_147241_, p_147242_, p_147243_);
    }



    public void burrowingAndRisingEffects() {

        BlockPos belowPos = this.blockPosition().below();
        BlockState belowBlock = level().getBlockState(belowPos);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), belowBlock.getSoundType().getHitSound(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // Use Explosion helper for block debris
            ParticleHelper.spawnExplosion(level(), new BlockParticleOption(ParticleTypes.BLOCK, belowBlock),
                    position(), 10, 0.1, 0.5);
        }
    }

    public void spawnMovingWhileBurrowedParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        // Bloque directamente debajo del gusano
        BlockPos belowPos = this.blockPosition().below();
        BlockState belowBlock = serverLevel.getBlockState(belowPos);

        // Solo spawnear partículas si el bloque no es aire ni líquido
        if (belowBlock.isAir() || belowBlock.getFluidState().isSource()) return;

        // Obtenemos el movimiento actual de la entidad
        Vec3 motion = this.getDeltaMovement();

        // Invertimos el movimiento (para que las partículas vayan hacia atrás)
        double vx = -motion.x * 0.5; // factor de dispersión
        double vy = Math.abs(motion.y) * 0.3 + 0.05; // un poquito hacia arriba
        double vz = -motion.z * 0.5;

        // Spawnear las partículas del bloque
        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, belowBlock),
                this.getX(), this.getY() + 0.1, this.getZ(),
                8,                     // cantidad
                0.25, 0.1, 0.25,       // dispersión inicial
                0                      // velocidad base (usamos la nuestra)
        );

        // Opcional: emitir una ráfaga de partículas direccionales (más dinámico)
        for (int i = 0; i < 5; i++) {
            double spreadX = vx + (serverLevel.random.nextDouble() - 0.5) * 0.1;
            double spreadY = vy + (serverLevel.random.nextDouble() - 0.5) * 0.05;
            double spreadZ = vz + (serverLevel.random.nextDouble() - 0.5) * 0.1;
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, belowBlock),
                    this.getX(), this.getY() + 0.2, this.getZ(),
                    1, 0, 0, 0, 0.0D
            );
        }
    }


    //Getters & Setters

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
    public boolean isBurrowed() {
        return this.entityData.get(IS_BURROWED);
    }
    public void setBurrowed(boolean burrowed) {
        this.entityData.set(IS_BURROWED, burrowed);
    }
    public boolean isBurrowing() {
        return this.entityData.get(IS_BURROWING);
    }
    public void setBurrowing(boolean burrowing) {
        this.entityData.set(IS_BURROWING, burrowing);
    }
    public boolean isRising() {
        return this.entityData.get(IS_RISING);
    }
    public void setRising(boolean rising) {
        this.entityData.set(IS_RISING, rising);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.OFFSPRING_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.OFFSPRING_HURT.get();

    }
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if(!this.isBurrowed()) {
            this.playSound(ModSounds.OFFSPRING_STEP.get(), 0.8F, 0.2F);
        }
    }


    protected float getSoundVolume() {
        return 1F;
    }



    


}
