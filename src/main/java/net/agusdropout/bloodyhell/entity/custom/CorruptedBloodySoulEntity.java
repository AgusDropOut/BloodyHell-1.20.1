package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class CorruptedBloodySoulEntity extends FlyingMob implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    // Config: How long before it gets aggressive? (e.g. 5 seconds of confusion, then attack)
    private int aggressionDelay = 100;

    public CorruptedBloodySoulEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        // Better flying control
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D) // Lower health since it explodes
                .add(Attributes.FLYING_SPEED, 0.6F)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D).build(); // Sees you from far away
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // Custom Attack Goal: Fly to target -> Explode
        this.goalSelector.addGoal(2, new KamikazeFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Target Players
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (aggressionDelay > 0) aggressionDelay--;

        // Visuals: Magic Particles under the soul
        if (this.level().isClientSide) {
            Vector3f darkRed = new Vector3f(0.6f, 0.0f, 0.0f);
            // Spawn slightly below the center
            ParticleHelper.spawn(level(), new MagicParticleOptions(darkRed, 0.4F, false, 20),
                    this.getX() + (random.nextDouble() - 0.5) * 0.5,
                    this.getY() + 0.8, // Just under the body
                    this.getZ() + (random.nextDouble() - 0.5) * 0.5,
                    0, -0.05, 0); // Drift down slightly
        }
    }

    // --- CUSTOM GOAL: Explode on Contact ---
    static class KamikazeFlyGoal extends Goal {
        private final CorruptedBloodySoulEntity mob;
        private LivingEntity target;

        public KamikazeFlyGoal(CorruptedBloodySoulEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            // Only attack after delay is over
            if (mob.aggressionDelay > 0) return false;

            this.target = mob.getTarget();
            return this.target != null && mob.distanceToSqr(this.target) < 400; // 20 blocks
        }

        @Override
        public void tick() {
            if (target == null) return;

            // Fly towards target
            mob.getNavigation().moveTo(target, 1.5D);

            // If close enough -> BOOM
            if (mob.distanceToSqr(target) < 3.0D) {
                mob.level().explode(mob, mob.getX(), mob.getY(), mob.getZ(), 2.0F, Level.ExplosionInteraction.NONE); // NONE = No block damage
                mob.discard(); // Die after exploding
            }
        }
    }

    // --- INTERACTION (Capture in bottle) ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(player.getMainHandItem().getItem() == ModItems.BLOOD_FLASK.get()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CORRUPTED_BLOOD_FLASK.get()));
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.BOTTLE_FILL, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F, false);
            this.discard();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    // --- GECKOLIB ---
    private PlayState predicate(AnimationState<CorruptedBloodySoulEntity> state) {

        if(state.isMoving()) {
            // If moving, play flying animation
            return state.setAndContinue(RawAnimation.begin().thenLoop("moving"));
        }

        // Simple fly animation logic
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    // --- SOUNDS ---
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SLIME_HURT; }
    protected SoundEvent getDeathSound() { return SoundEvents.SLIME_DEATH; }
}