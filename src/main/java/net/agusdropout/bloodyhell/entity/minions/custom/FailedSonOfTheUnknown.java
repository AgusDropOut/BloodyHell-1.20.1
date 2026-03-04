package net.agusdropout.bloodyhell.entity.minions.custom;



import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;
import net.agusdropout.bloodyhell.entity.minions.ai.FollowSummonerGoal;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class FailedSonOfTheUnknown extends AbstractMinionEntity {

    public FailedSonOfTheUnknown(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public String getMinionId() {
        return "failed_son_of_the_unknown";
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ATTACK_SPEED, 1.2D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true)); // 1.5x speed triggers running
        this.goalSelector.addGoal(3, new FollowSummonerGoal(this, 0.95D, 7,15)); // 1.5x speed triggers running
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.95D)); // 1.0x speed triggers walking
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, target -> target instanceof Enemy && !this.isAlliedTo(target)
        ));
    }


  // @Override
  // protected int getSummonDuration() {
  //     return 50;
  // }

  // // Adjusts duration to match the specific unsummon/death animation length
  // @Override
  // protected int getUnsummonDuration() {
  //     return 45;
  // }

    private PlayState predicate(AnimationState<FailedSonOfTheUnknown> state) {
        if (this.isDeadOrDying()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("unsummon"));
            return PlayState.CONTINUE;
        }

        if (this.getIsSummoning()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("summon"));
            return PlayState.CONTINUE;
        }

        if (state.isMoving()) {
            // Evaluates horizontal velocity to differentiate between strolling and chasing/running
            double velocitySq = this.getDeltaMovement().horizontalDistanceSqr();
            if (velocitySq > 0.005D) {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("running"));
            } else {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("walking"));
            }
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 0, this::predicate));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getIsSummoning() ? null : ModSounds.OFFSPRING_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.OFFSPRING_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ModSounds.OFFSPRING_STEP.get(), 0.8F, 0.2F);
    }

    @Override
    public int getStripeColor() {
        return 0xffc400;
    }

    @Override
    public float getMinimumInsight() {
        return PlayerInsight.INSIGHT_FOR_LEVEL_1;
    }
}