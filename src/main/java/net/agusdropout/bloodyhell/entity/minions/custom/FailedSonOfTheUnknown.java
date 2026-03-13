package net.agusdropout.bloodyhell.entity.minions.custom;

import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;
import net.agusdropout.bloodyhell.entity.minions.ai.FollowSummonerGoal;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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

    public static AttributeSupplier.Builder createAttributes() {
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
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(3, new FollowSummonerGoal(this, 0.95D, 7,15));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.95D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, target -> target instanceof Enemy && !this.isAlliedTo(target)
        ));
    }

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
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(3.0D),
                            entity -> !this.isAlliedTo(entity) && entity.isAlive())
                    .forEach(enemy -> enemy.hurt(this.damageSources().magic(), 4.0F));

            ((ServerLevel) this.level()).sendParticles(ParticleTypes.SQUID_INK,
                    this.getX(), this.getY() + 0.5D, this.getZ(),
                    20, 0.5D, 0.5D, 0.5D, 0.1D);
        }
        super.die(cause);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 0, this::predicate));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getIsSummoning() ? null : ModSounds.FAILED_SON_OF_THE_UNKNOWN_AMBIENCE.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 300 + this.random.nextInt(200);
    }

    @Override
    protected float getSoundVolume() {
        return 0.3F;
    }

    @Override
    public float getVoicePitch() {
        return 0.8F + this.random.nextFloat() * 0.4F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ModSounds.FAILED_SON_OF_THE_UNKNOWN_STEP.get(), 0.8F, 0.2F);
    }

    @Override
    public int getStripeColor() {
        return 0xffbf00;
    }

    @Override
    public float getMinimumInsight() {
        return PlayerInsight.INSIGHT_FOR_LEVEL_1;
    }
}