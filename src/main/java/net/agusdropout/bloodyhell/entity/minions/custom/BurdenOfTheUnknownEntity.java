package net.agusdropout.bloodyhell.entity.minions.custom;

import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.minions.ai.BurdenOfTheUnknownAttackGoal;
import net.agusdropout.bloodyhell.entity.minions.ai.FollowSummonerGoal;
import net.agusdropout.bloodyhell.entity.minions.ai.RefugeUnderBastionShieldGoal;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class BurdenOfTheUnknownEntity extends AbstractMinionEntity {

    private static final EntityDataAccessor<Float> CANNON_PITCH = SynchedEntityData.defineId(BurdenOfTheUnknownEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BurdenOfTheUnknownEntity.class, EntityDataSerializers.BOOLEAN);
    public boolean triggerLeftStepParticles = false;
    public boolean triggerRightStepParticles = false;
    public BurdenOfTheUnknownEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 120.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new BurdenOfTheUnknownAttackGoal(this, 1.0D, 80, 120.0F));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(1, new RefugeUnderBastionShieldGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true,
                entity -> entity != this.getOwner() && !(entity instanceof AbstractMinionEntity)));
        this.goalSelector.addGoal(4, new FollowSummonerGoal(this, 1.1F, 7, 15));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CANNON_PITCH, 0.0F);
        this.entityData.define(IS_ATTACKING, false);
    }

    public float getCannonPitch() {
        return this.entityData.get(CANNON_PITCH);
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setIsAttacking(boolean attacking) {
        this.entityData.set(IS_ATTACKING, attacking);
    }

    @Override
    public void setStripeColor(int color) {
        super.setStripeColor(color);
    }

    public void setCannonPitch(float pitch) {
        this.entityData.set(CANNON_PITCH, pitch);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<BurdenOfTheUnknownEntity> controller = new AnimationController<>(this, "controller", 5, state -> {
           // if (this.getIsSummoning()) {
           // }
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        });


        controller.setCustomInstructionKeyframeHandler(event -> {
            if (this.level().isClientSide) {
                String instruction = event.getKeyframeData().getInstructions();
                if (instruction.equals("frontLeftStep;")) {
                    this.triggerLeftStepParticles = true;
                    triggerClientEffects();
                } else if (instruction.equals("frontRightStep;")) {
                    this.triggerRightStepParticles = true;
                    triggerClientEffects();
                }
            }
        });

        controller.triggerableAnim("shoot", RawAnimation.begin().thenPlay("shoot"));
        controllers.add(controller);
    }

    private void triggerClientEffects(){
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                SoundEvents.WARDEN_STEP, this.getSoundSource(), 1.2F, 0.6F, false);
        EntityCameraShake.clientCameraShake(this.level(), this.position(), 5.0f, 0.3f, 10, 3);
    }


    public void triggerShootAnimation() {
        this.triggerAnim("controller", "shoot");
    }


    @Override
    public String getMinionId() {
        return "burden_of_the_unknown";
    }

    @Override
    public float getMinimumInsight() {
        return 10;
    }
}