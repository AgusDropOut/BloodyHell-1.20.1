package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class BloodySoulEntity extends Animal implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private int lifeTicks = 600;

    public BloodySoulEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    private PlayState predicate(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("moving", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {}

    protected SoundEvent getAmbientSound() { return SoundEvents.ALLAY_AMBIENT_WITH_ITEM; }
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ALLAY_HURT; }
    protected SoundEvent getDeathSound() { return SoundEvents.ALLAY_DEATH; }
    protected float getSoundVolume() { return 0.2F; }

    @Override
    public void tick() {
        super.tick();
        if (lifeTicks < 0) {
            spawnDeathParticles();
            this.discard();
        }
        lifeTicks--;
    }

    private void spawnDeathParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.5, this.getZ(),
                    20, 0.3, 0.3, 0.3, 0.1);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if(itemInHand.getItem() == ModItems.BLOOD_FLASK.get()) {
            if (!this.level().isClientSide) {

                if (this.getPersistentData().contains("FallenAllyType")) {
                    ItemStack soulFlask = new ItemStack(ModItems.BOUND_BLOOD_FLASK.get());
                    CompoundTag nbt = soulFlask.getOrCreateTag();


                    nbt.putString("FallenAllyType", this.getPersistentData().getString("FallenAllyType"));
                    nbt.put("FallenAllyData", this.getPersistentData().getCompound("FallenAllyData"));

                    if (this.getPersistentData().contains("FallenAllyName")) {
                        nbt.putString("FallenAllyName", this.getPersistentData().getString("FallenAllyName"));
                    }

                    itemInHand.shrink(1);
                    if (!player.getInventory().add(soulFlask)) {
                        player.drop(soulFlask, false);
                    }
                } else {
                    // Si era un alma genérica sin datos de aliado, te da sangre normal
                    itemInHand.shrink(1);
                    ItemStack genericFlask = new ItemStack(ModItems.FILLED_BLOOD_FLASK.get());
                    if (!player.getInventory().add(genericFlask)) {
                        player.drop(genericFlask, false);
                    }
                }

                this.level().playSound(null, this.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                this.discard();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("LifeTicks")) {
            this.lifeTicks = tag.getInt("LifeTicks");
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}