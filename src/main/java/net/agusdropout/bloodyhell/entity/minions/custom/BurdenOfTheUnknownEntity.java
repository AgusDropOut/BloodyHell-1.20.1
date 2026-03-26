package net.agusdropout.bloodyhell.entity.minions.custom;

import net.agusdropout.bloodyhell.entity.minions.ai.BurdenOfTheUnknownAttackGoal;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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

    public BurdenOfTheUnknownEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BurdenOfTheUnknownAttackGoal(this, 1.0D, 60, 15.0F));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true,
                entity -> entity != this.getOwner() && !(entity instanceof AbstractMinionEntity)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CANNON_PITCH, 0.0F);
    }

    public float getCannonPitch() {
        return this.entityData.get(CANNON_PITCH);
    }

    public void setCannonPitch(float pitch) {
        this.entityData.set(CANNON_PITCH, pitch);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
           // if (this.getIsSummoning()) {
           //    // return state.setAndContinue(RawAnimation.begin().thenLoop("summon"));
           // }
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }).triggerableAnim("shoot", RawAnimation.begin().thenPlay("shoot")));
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