package net.agusdropout.bloodyhell.entity.minions.custom;

import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.minions.ai.BastionShieldGoal;
import net.agusdropout.bloodyhell.entity.minions.ai.LungeAttackGoal;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ShockwaveParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class BastionOfTheUnknownEntity extends AbstractMinionEntity {

    private static final EntityDataAccessor<Boolean> IS_LUNGING = SynchedEntityData.defineId(BastionOfTheUnknownEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_BLOCKING = SynchedEntityData.defineId(BastionOfTheUnknownEntity.class, EntityDataSerializers.BOOLEAN);

    public boolean triggerStepParticles;
    public boolean triggerShieldParticles;

    private int shieldCooldown = 0;
    private float shieldedDamage = 0.0F;

    public BastionOfTheUnknownEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_LUNGING, false);
        this.entityData.define(IS_BLOCKING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LungeAttackGoal(this, 15, 60));
        this.goalSelector.addGoal(3, new BastionShieldGoal(this, 40));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, false, entity -> entity instanceof Enemy));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        AnimationController<BastionOfTheUnknownEntity> movementController = new AnimationController<>(this, "movement_controller", 5, state -> {
            if (this.isLunging()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("lunge"));
            }
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        });

        movementController.setCustomInstructionKeyframeHandler(event -> {
            if (this.level().isClientSide) {
                String instruction = event.getKeyframeData().getInstructions();
                if (instruction.equals("step;")) {
                    this.triggerStepParticles = true;
                    triggerStepClientEffects();
                }
            }
        });


        AnimationController<BastionOfTheUnknownEntity> actionController = new AnimationController<>(this, "action_controller", 5, state -> {
            if (this.isBlocking()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("shield"));
            }
            return PlayState.STOP;
        });

        actionController.triggerableAnim("lunge", RawAnimation.begin().thenPlay("lunge"))
                .triggerableAnim("reposition", RawAnimation.begin().thenPlay("repositionAfterLunge"));

        actionController.setCustomInstructionKeyframeHandler(event -> {
            if (this.level().isClientSide) {
                String instruction = event.getKeyframeData().getInstructions();
                if (instruction.equals("shieldHit;")) {
                    this.triggerShieldParticles = true;
                    triggerShieldClientEffects();
                }
            }
        });

        controllers.add(movementController);
        controllers.add(actionController);
    }

    private void triggerStepClientEffects(){
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                SoundEvents.WARDEN_STEP, this.getSoundSource(), 1.2F, 0.6F, false);
        EntityCameraShake.clientCameraShake(this.level(), this.position(), 5.0f, 0.3f, 10, 3);
    }

    private void triggerShieldClientEffects(){
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                SoundEvents.ANVIL_LAND, this.getSoundSource(), 1.0F, 0.8F, false);
        EntityCameraShake.clientCameraShake(this.level(), this.position(), 3.0f, 0.2f, 5, 2);
    }

    @Override
    public void travel(Vec3 movement) {
        if (this.isLunging()) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(movement);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && !source.is(DamageTypes.MAGIC) && !source.is(DamageTypes.STARVE)) {
           if(this.isBlocking()) {
               this.shieldedDamage += amount;
               this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.5F);
               return false;
           }

        }
        return super.hurt(source, amount);
    }

    public boolean isLunging() {
        return this.entityData.get(IS_LUNGING);
    }

    public void setLunging(boolean lunging) {
        this.entityData.set(IS_LUNGING, lunging);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShieldCooldown", this.shieldCooldown);
        tag.putFloat("ShieldedDamage", this.shieldedDamage);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.shieldCooldown = tag.getInt("ShieldCooldown");
        this.shieldedDamage = tag.getFloat("ShieldedDamage");
    }

    public boolean isBlocking() {
        return this.entityData.get(IS_BLOCKING);
    }

    public void setBlocking(boolean blocking) {
        this.entityData.set(IS_BLOCKING, blocking);
    }

    public int getShieldCooldown() {
        return this.shieldCooldown;
    }

    public void setShieldCooldown(int cooldown) {
        this.shieldCooldown = cooldown;
    }

    public float getShieldedDamage() {
        return this.shieldedDamage;
    }

    public void setShieldedDamage(float damage) {
        this.shieldedDamage = damage;
    }

    public float getMaxShieldCapacity() {
        return 30.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.shieldCooldown > 0) {
            this.shieldCooldown--;
        }

        if(this.level().isClientSide){
            handleClientEffects();
        }

    }

    private void handleClientEffects(){
        if (this.random.nextFloat() < 0.7f && this.isLunging()) {
            Vector3f color = ColorHelper.hexToVector3f( this.getStripeColor());
            Vec3 motion = this.getDeltaMovement();
            ShockwaveParticleOptions shockWaveParticle = new ShockwaveParticleOptions(color, 0.5f, 2.5f);
            ParticleHelper.spawn(this.level(), shockWaveParticle, this.getX(), this.getY()+2, this.getZ(), motion.x, motion.y, motion.z);
        }
    }

    @Override
    public String getMinionId() {
        return "bastion_of_the_unknown";
    }

    @Override
    public float getMinimumInsight() {
        return 10;
    }
}