package net.agusdropout.bloodyhell.entity.minions.custom;

import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;
import net.agusdropout.bloodyhell.entity.minions.ai.FollowSummonerGoal;
import net.agusdropout.bloodyhell.entity.minions.ai.OcularFlightControl;
import net.agusdropout.bloodyhell.entity.minions.ai.OcularShootGoal;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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

public class WeepingOcularEntity extends AbstractMinionEntity {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SUMMON_ANIM = RawAnimation.begin().thenPlayAndHold("summon");
    private static final RawAnimation UNSUMMON_ANIM = RawAnimation.begin().thenPlayAndHold("unsummon");
    private static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenPlay("shoot");

    public WeepingOcularEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new OcularFlightControl(this);
        this.setNoGravity(true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowSummonerGoal(this, 1.0D, 7, 15));
        this.goalSelector.addGoal(3, new OcularShootGoal(this,0.95D, 20,15));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, target -> target instanceof Enemy && !this.isAlliedTo(target)
        ));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    private PlayState movementPredicate(AnimationState<WeepingOcularEntity> state) {
        if (this.isDeadOrDying()) {
            state.getController().setAnimation(UNSUMMON_ANIM);
            return PlayState.CONTINUE;
        }

        if (this.getIsSummoning()) {
            state.getController().setAnimation(SUMMON_ANIM);
            return PlayState.CONTINUE;
        }

        if (state.isMoving() || !this.onGround()) {
            state.getController().setAnimation(IDLE_ANIM);
            state.getController().setAnimationSpeed(1.5D);
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(IDLE_ANIM);
        state.getController().setAnimationSpeed(1.0D);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 0, this::movementPredicate));

        controllers.add(new AnimationController<>(this, "action_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("shoot", SHOOT_ANIM));
    }

    @Override
    public String getMinionId() {
        return "the_weeping_ocular";
    }

    @Override
    public int getStripeColor() {
        //return 0xffc400;
        return 0xfc0303;
    }

    @Override
    public float getMinimumInsight() {
        return PlayerInsight.INSIGHT_FOR_LEVEL_1;
    }

    public void triggerShootAnimation() {
        this.triggerAnim("action_controller", "shoot");
    }
}