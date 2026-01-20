package net.agusdropout.bloodyhell.entity.custom;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.ai.goals.CrystalAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.OniAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.SummonGoal;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.UUID;

public class OniEntity extends Monster implements GeoEntity {

    public OniEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    private static final int MAX_HEALTH = 200;
    private Phase PHASE = Phase.MELEE_ATTACK;
    private int counter = 0;

    private int meleeCounter = 0;
    private int crystalCounter = 0;
    private int summonCounter = 0;
    private boolean canCrystalAttack = false;
    private boolean canSummonAttack = false;
    private boolean canMeleeAttack = false;


    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);


    private static final EntityDataAccessor<Byte> DATA_EYE_ID = SynchedEntityData.defineId(OniEntity.class, EntityDataSerializers.BYTE);

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_SPEED, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 7.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.23).build();
    }

    public void aiStep() {
        counter++;
        if(!this.canCrystalAttack){
            crystalCounter++;
        }
        if(!this.canSummonAttack){
            summonCounter++;
        }

        if(this.counter == 1000){
            this.counter = 0;
        } else if(this.counter == 1) {
            this.PHASE = Phase.MELEE_ATTACK;
        } else if(this.counter == 500) {
            this.PHASE = Phase.CRYSTAL_ATTACK;
        } else if(this.counter == 900) {
            this.PHASE = Phase.SUMMONING_ATTACK;
        }
        if(this.crystalCounter == 100 ) {
            this.canCrystalAttack = true;
            this.crystalCounter = 0;
        }
        if(this.summonCounter == 50) {
            this.canSummonAttack = true;
            this.summonCounter = 0;
        }

        super.aiStep();
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CrystalAttackGoal(this));
        this.goalSelector.addGoal(4, new OniAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new SummonGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    private PlayState predicate(AnimationState animationState) {
        if ((animationState.isMoving())) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.oni.walking", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("animation.oni.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        Phase actualPhase = this.getPhase();
        state.getController().forceAnimationReset();
        if ((this.swinging) && (state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) && (this.getPhase() == Phase.MELEE_ATTACK)){
               BlockPos blockPos = this.getOnPos();
               state.getController().forceAnimationReset();
               state.getController().setAnimation(RawAnimation.begin().then("animation.oni.baseattack", Animation.LoopType.PLAY_ONCE));
               this.swinging = false;
        } else if(this.swinging && this.getPhase() == Phase.CRYSTAL_ATTACK && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.oni.crystalsmash", Animation.LoopType.PLAY_ONCE));
            this.swinging = false;
        } else if(this.swinging && this.getPhase() == Phase.SUMMONING_ATTACK && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.oni.summoning", Animation.LoopType.PLAY_ONCE));
            addParticles();
            this.level().playLocalSound(this.getX(),this.getY(),this.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1, 1, true);
            this.swinging = false;
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
        this.playSound(SoundEvents.AXE_SCRAPE, 0.15F, 1.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_EYE_ID, (byte) 0);
    }



        @Override
        protected float tickHeadTurn ( float v, float v1){
            return super.tickHeadTurn(v, v1);
        }


        protected SoundEvent getAmbientSound () {
            return SoundEvents.WARDEN_AMBIENT;
        }

        protected SoundEvent getHurtSound (DamageSource damageSourceIn){
            return SoundEvents.IRON_GOLEM_HURT;
        }

        protected SoundEvent getDeathSound () {
            return SoundEvents.WARDEN_DEATH;
        }

        protected float getSoundVolume () {
            return 0.2F;
        }


        public boolean doHurtTarget (Entity p_28837_){
            this.level().broadcastEntityEvent(this, (byte) 4);

            float f = this.getAttackDamage();
            float f1 = (int) f > 0 ? f / 2.0F + (float) this.random.nextInt((int) f) : f;
            boolean flag = p_28837_.hurt(this.damageSources().mobAttack(this), f1);
            if (flag) {
                double d2;
                if (p_28837_ instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity) p_28837_;
                    d2 = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                } else {
                    d2 = 0.0;
                }

                double d0 = d2;
                double d1 = Math.max(0.0, 1.0 - d0);
                p_28837_.setDeltaMovement(p_28837_.getDeltaMovement().add(0.0, 0.4000000059604645 * d1, 0.0));
                this.doEnchantDamageEffects(this, p_28837_);
            }

            this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 1.0F, 1.0F);
            return flag;
        }
        private float getAttackDamage () {
            return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }
        protected boolean shouldDespawnInPeaceful () {
            return false;
        }

        public enum Phase {
            MELEE_ATTACK,
            CRYSTAL_ATTACK,
            SUMMONING_ATTACK
        }
    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Phase getPhase() {
        return this.PHASE;
    }
    public boolean isCanSummonAttack() {
        return canSummonAttack;
    }

    public void setCanSummonAttack(boolean canSummonAttack) {
        this.canSummonAttack = canSummonAttack;
    }

    public boolean isCanCrystalAttack() {
        return canCrystalAttack;
    }

    public void setCanCrystalAttack(boolean canCrystalAttack) {
        this.canCrystalAttack = canCrystalAttack;
    }
    public boolean isCanMeleeAttack() {
        return canMeleeAttack;
    }

    public void setCanMeleeAttack(boolean canMeleeAttack) {
        this.canMeleeAttack = canMeleeAttack;
    }

    public void addParticles(){
        for (int i = 0; i < 4; ++i) {
            double x = (double) this.getX() + random.nextDouble();
            double y = (double) this.getY() + random.nextDouble();
            double z = (double) this.getZ() + random.nextDouble();
            double xSpeed = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double ySpeed = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double zSpeed = ((double) random.nextFloat() - 0.5D) * 0.5D;
            int j = random.nextInt(2) * 2 - 1;

                x = (double) this.getX() + 0.5D + 0.25D * (double) j;
                xSpeed = random.nextFloat() * 2.0F * (float) j;

                z = (double) this.getZ() + 0.5D + 0.25D * (double) j;
                zSpeed = random.nextFloat() * 2.0F * (float) j;

            level().addParticle(ModParticles.BLOOD_PARTICLES.get(), x, y, z, xSpeed, ySpeed, zSpeed);

        }
    }


}

