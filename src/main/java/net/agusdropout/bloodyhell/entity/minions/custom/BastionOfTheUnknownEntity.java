package net.agusdropout.bloodyhell.entity.minions.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.entity.minions.ai.BastionShieldGoal;
import net.agusdropout.bloodyhell.entity.minions.ai.BastionSpecialShieldGoal;
import net.agusdropout.bloodyhell.entity.minions.ai.LungeAttackGoal;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ShockwaveParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class BastionOfTheUnknownEntity extends AbstractMinionEntity {

    private static final EntityDataAccessor<Boolean> IS_LUNGING = SynchedEntityData.defineId(BastionOfTheUnknownEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_BLOCKING = SynchedEntityData.defineId(BastionOfTheUnknownEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SPECIAL_SHIELDING = SynchedEntityData.defineId(BastionOfTheUnknownEntity.class, EntityDataSerializers.BOOLEAN);

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
        this.entityData.define(IS_SPECIAL_SHIELDING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
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
        this.goalSelector.addGoal(2, new BastionSpecialShieldGoal(this, 6.0F, 300, 400));
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

    private void triggerStepClientEffects() {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                SoundEvents.WARDEN_STEP, this.getSoundSource(), 1.2F, 0.6F, false);
        EntityCameraShake.clientCameraShake(this.level(), this.position(), 5.0f, 0.3f, 10, 3);
    }

    private void triggerShieldClientEffects() {
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

    public boolean isSpecialShielding() {
        return this.entityData.get(IS_SPECIAL_SHIELDING);
    }

    public void setSpecialShielding(boolean shielding) {
        this.entityData.set(IS_SPECIAL_SHIELDING, shielding);
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

        if (this.isLunging()) {
            handleLungeEffects();
        }

        if (this.isSpecialShielding()) {
            handleSpecialShieldEffects();
        }

        if(this.level().isClientSide) {
            handleClientEffects();
        }
    }

    private void handleLungeEffects() {
        if (this.getDeltaMovement().lengthSqr() < 0.01) return;

        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = this.level().getBlockState(posBelow);

        if (stateBelow.isAir()) return;

        if (this.level().isClientSide) {
            ParticleHelper.spawn(this.level(), new BlockParticleOption(ParticleTypes.BLOCK, stateBelow),
                    this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                    this.getY() + 0.1D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                    (this.random.nextDouble() - 0.5D) * 0.5D,
                    this.random.nextDouble() * 0.5D,
                    (this.random.nextDouble() - 0.5D) * 0.5D);
        } else {
            if (this.tickCount % 3 == 0 && stateBelow.isCollisionShapeFullBlock(this.level(), posBelow)) {
                float burstVelocity = 0.3F + this.random.nextFloat() * 0.3F;
                EntityFallingBlock fallingBlock = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), stateBelow, burstVelocity);
                fallingBlock.setPos(this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        this.getY() + 0.1D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth());

                this.level().addFreshEntity(fallingBlock);
            }
        }
    }

    private void handleSpecialShieldEffects() {
        if (!this.level().isClientSide) return;

        double heightOffset = this.getBbHeight() / 2.0D;
        float radius = 6.0F;

        double r = radius * Math.cbrt(this.random.nextDouble());
        double theta = this.random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(this.random.nextDouble());

        double dx = r * Math.sin(phi) * Math.cos(theta);
        double dy = r * Math.cos(phi);
        double dz = r * Math.sin(phi) * Math.sin(theta);

        MagicParticleOptions magicParticle = new MagicParticleOptions(ColorHelper.hexToVector3f(this.getStripeColor()), 0.5F,false, 30,true);

        ParticleHelper.spawn(this.level(), magicParticle,
                this.getX() + dx, this.getY() + heightOffset + dy, this.getZ() + dz,
                0.0D, 0.01D, 0.0D);

        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = this.level().getBlockState(posBelow);

        if (!stateBelow.isAir()) {
            double chargeRadius = 4.0D + this.random.nextDouble() * 3.0D;
            double chargeAngle = this.random.nextDouble() * 2 * Math.PI;

            double offsetX = Math.cos(chargeAngle) * chargeRadius;
            double offsetZ = Math.sin(chargeAngle) * chargeRadius;
            double offsetY = this.random.nextDouble() * 1.5D;

            double vx = -offsetX * 0.15D;
            double vy = (heightOffset - offsetY) * 0.15D + 0.1D;
            double vz = -offsetZ * 0.15D;

            ParticleHelper.spawn(this.level(), new BlockParticleOption(ParticleTypes.BLOCK, stateBelow),
                    this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                    vx, vy, vz);
        }
    }

    private void handleClientEffects() {
        if (this.random.nextFloat() < 0.7F && this.isLunging() && this.getDeltaMovement().lengthSqr() > 0.01) {
            Vector3f color = ColorHelper.hexToVector3f(this.getStripeColor());
            Vec3 motion = this.getDeltaMovement();
            ShockwaveParticleOptions shockWaveParticle = new ShockwaveParticleOptions(color, 0.5F, 2.5F);
            ParticleHelper.spawn(this.level(), shockWaveParticle, this.getX(), this.getY() + 2, this.getZ(), motion.x, motion.y, motion.z);
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