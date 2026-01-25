package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ai.goals.*;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.BossSyncS2CPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

public class SelioraEntity extends Monster implements GeoEntity{
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    private int riseCooldown = 250;
    private int riseMaxCooldown = 250;
    private int starFallCooldown = 200;
    private int starFallMaxCooldown = 200;
    private int whirlWindCooldown = 300;
    private int whirlWindMaxCooldown = 300;

    private int jumpAttackCooldown = 100;
    private int jumpAttackMaxCooldown = 100;
    private int chargeAttackCooldown = 150;
    private int chargeAttackMaxCooldown = 150;

    private int teleportCooldown = 150;
    private int teleportMaxCooldown = 150;
    private int avoidDistance = 6;

    // Cooldown para esquivar
    private int dodgeCooldown = 60;

    private int riseTicksDuration = 80;
    private int starFallTicksDuration = 40;
    private int whirlWindTicksDuration = 300;
    private int secondPhaseStartTicksDuration = 100;

    private int jumpAttackChargeTicks = 20;
    private int chargeAttackChargeTicks = 10;

    private int DeathCooldown = 100;
    private boolean isAlreadyDead = false;

    // NOTA: Eliminé la variable local "isSecondPhase" porque ahora usamos SynchedEntityData

    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> STARFALL_ACTIVE = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RISE_ACTIVE = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WHIRLWIND_ACTIVE = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> JUMP_ACTIVE = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGE_ACTIVE = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> IS_STARTING_SECOND_PHASE = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);

    // Sincronización para esquivar
    private static final EntityDataAccessor<Boolean> DODGE_ACTIVE = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);

    // --- NUEVO: Sincronización de Fase 2 (Solución al Bug) ---
    private static final EntityDataAccessor<Boolean> IS_SECOND_PHASE_SYNCED = SynchedEntityData.defineId(SelioraEntity.class, EntityDataSerializers.BOOLEAN);


    public SelioraEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);

        this.entityData.define(STARFALL_ACTIVE, false);
        this.entityData.define(RISE_ACTIVE, false);
        this.entityData.define(WHIRLWIND_ACTIVE, false);

        this.entityData.define(JUMP_ACTIVE, false);
        this.entityData.define(CHARGE_ACTIVE, false);

        this.entityData.define(IS_STARTING_SECOND_PHASE, false);
        this.entityData.define(DODGE_ACTIVE, false);

        // Definimos la variable sincronizada de fase 2
        this.entityData.define(IS_SECOND_PHASE_SYNCED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        // Guardamos la fase en el NBT por si se cierra el servidor
        compoundTag.putBoolean("IsSecondPhase", this.isSecondPhase());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        // Cargamos la fase del NBT
        if (compoundTag.contains("IsSecondPhase")) {
            this.setSecondPhase(compoundTag.getBoolean("IsSecondPhase"));
        }
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500)
                .add(Attributes.ATTACK_DAMAGE, 7.0f)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 30).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SelioraDodgeGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Creeper.class, true));

        this.goalSelector.addGoal(9, new WhirlwindGoal(this));
        this.goalSelector.addGoal(9, new SummonBlasphemousArmsGoal(this, 3));
        this.goalSelector.addGoal(9, new StarFallGoal(this));
        this.goalSelector.addGoal(8, new JumpAttackGoal(this));
        this.goalSelector.addGoal(10, new ChargeAttackGoal(this));
        this.goalSelector.addGoal(7, new TeleportNearGoal(this));
        this.goalSelector.addGoal(6, new TeleportFarGoal(this));
    }

    // --- GETTERS Y SETTERS PARA FASE 2 SINCRONIZADA ---
    public boolean isSecondPhase() {
        return this.entityData.get(IS_SECOND_PHASE_SYNCED);
    }

    public void setSecondPhase(boolean isSecondPhase) {
        this.entityData.set(IS_SECOND_PHASE_SYNCED, isSecondPhase);
    }

    @Override
    protected void tickDeath() {
        if (!isAlreadyDead) isAlreadyDead = true;

        DeathCooldown--;

        if (level() instanceof ServerLevel serverLevel) {
            if (this.tickCount % 5 == 0) {
                BlockPos belowPos = this.blockPosition().below();
                BlockState belowBlock = level().getBlockState(belowPos);
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0F, 0.5F);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, belowBlock),
                        this.getX(), this.getY(), this.getZ(), 10, 0.5, 0.1, 0.5, 0.1);
            }
            if (DeathCooldown == 1) {
                spawnFinalPhaseParticles();
            }
        }

        if (DeathCooldown <= 0) {
            level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.GRAWL_DEATH.get(), this.getSoundSource(), 1.0F, 1.0F, false);

            if (!this.level().isClientSide) {
                this.level().players().forEach(player -> {
                    ModMessages.sendToPlayer(new BossSyncS2CPacket(0, (int)getMaxHealth(), true, false,2), (ServerPlayer) player);
                });
            }

            this.remove(RemovalReason.KILLED);
        }
    }

    private PlayState predicate(AnimationState state) {
        if (this.isAlreadyDead) {
            state.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        } else if (this.isSecondPhaseStarting()) {
            state.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else if (this.isDodgeActive()) {
            state.getController().setAnimation(RawAnimation.begin().then("dodge", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else if (this.isRiseActive()) {
            state.getController().setAnimation(RawAnimation.begin().then("rise", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else if (this.isWhirlWindActive()) {
            state.getController().setAnimation(RawAnimation.begin().then("whirlwind", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (this.isStarFallActive()) {
            state.getController().setAnimation(RawAnimation.begin().then("starfall", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else if (this.isSecondPhase() && this.isJumpAttackActive()) { // Usamos el getter sincronizado
            state.getController().setAnimation(RawAnimation.begin().then("jump", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        } else if (this.isSecondPhase() && this.isChargeAttackActive()) { // Usamos el getter sincronizado
            state.getController().setAnimation(RawAnimation.begin().then("charged_attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        } else if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        sendBossPacket();

        if (isAlreadyDead) {
            DeathCooldown--;
            if (DeathCooldown <= 0) {
                this.discard();
            }
        } else if (this.isSecondPhaseStarting()) {

            if (secondPhaseStartTicksDuration == 100 - 18) spawnSecondPhaseParticles();
            if (secondPhaseStartTicksDuration == 100 - 51) {
                level().playSound(null, this.getOnPos(), ModSounds.NECK_SNAP_SOUND.get(),
                        SoundSource.HOSTILE, 1.5F, 0.9F + this.level().random.nextFloat() * 0.2F);
            }
            this.setDeltaMovement(Vec3.ZERO);
            secondPhaseStartTicksDuration--;
            if (secondPhaseStartTicksDuration <= 0) {
                spawnFinalPhaseParticles();
                this.heal(this.getMaxHealth());
                this.setSecondPhaseStarting(false);

                // ACTUALIZACIÓN CLAVE: Usamos el setter sincronizado
                this.setSecondPhase(true);
            }

        } else {
            updateCooldowns();
            if (this.isDodgeActive()) {
                return;
            }
            // Verificamos usando el getter sincronizado
            if (this.getTarget() != null && !this.isDodgeActive() && !this.isStarFallActive() && !this.isWhirlWindActive() && !this.isRiseActive() && !this.isJumpAttackActive() && !this.isChargeAttackActive() && !this.isSecondPhaseStarting() && this.isSecondPhase()) {
                this.getNavigation().moveTo(getTarget(), 1.5);
            } else {
                this.getNavigation().stop();
            }
        }
    }

    public void updateCooldowns() {
        if (this.getRiseCooldown() > 0 && !this.isRiseActive()) {
            this.setRiseCooldown(this.getRiseCooldown() - 1);
        }
        if (this.getStarFallCooldown() > 0 && !this.isStarFallActive()) {
            this.setStarFallCooldown(this.getStarFallCooldown() - 1);
        }
        if (this.getWhirlWindCooldown() > 0 && !this.isWhirlWindActive()) {
            this.setWhirlWindCooldown(this.getWhirlWindCooldown() - 1);
        }
        if (this.getJumpAttackCooldown() > 0 && !this.isJumpAttackActive()) {
            this.setJumpAttackCooldown(this.getJumpAttackCooldown() - 1);
        }
        if (this.getChargeAttackCooldown() > 0 && !this.isChargeAttackActive()) {
            this.setChargeAttackCooldown(this.getChargeAttackCooldown() - 1);
        }
        if (this.getTeleportCooldown() > 0) {
            this.setTeleportCooldown(this.getTeleportCooldown() - 1);
        }
        if (this.dodgeCooldown > 0) {
            this.dodgeCooldown--;
        }
    }

    // --- MÉTODOS DE DAÑO ---

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isSecondPhaseStarting()) {
            return false;
        }

        // Usamos el getter sincronizado
        if (!this.isSecondPhase() && (this.getHealth() - amount <= 0 || this.getHealth() - amount < 10)) {
            this.setHealth(1.0F);
            this.setSecondPhaseStarting(true);
            this.setStarFallActive(false);
            this.setWhirlWindActive(false);
            this.setRiseActive(false);
            return false;
        }

        // Usamos el getter sincronizado
        if (this.isSecondPhase() && this.getHealth() - amount <= 0) {
            this.isAlreadyDead = true;
            return super.hurt(source, amount);
        }

        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {}

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    // --- SONIDOS ---

    protected SoundEvent getAmbientSound() {
        // Getter sincronizado
        if (this.isSecondPhase()) {
            return ModSounds.SELIORA_SECOND_PHASE_AMBIENT_SOUND.get();
        } else {
            return ModSounds.SELIORA_AMBIENT_SOUND.get();
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        // Getter sincronizado
        if (!this.isSecondPhase()) {
            if (this.random.nextFloat() < 0.5f) {
                return ModSounds.SELIORA_HURT2_SOUND.get();
            } else {
                return ModSounds.SELIORA_HURT1_SOUND.get();
            }
        } else {
            return ModSounds.SELIORA_JUMP_ATTACK_SOUND.get();
        }
    }

    protected float getSoundVolume() {
        return 1F;
    }

    // --- LOGICA DE PARTICULAS ---

    private void spawnSecondPhaseParticles() {
        if (!(level() instanceof ServerLevel server)) return;
        Random random = new Random();
        BlockPos belowPos = this.blockPosition().below();
        BlockState block = level().getBlockState(belowPos);
        for (int i = 0; i < 50; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.5;
            double offsetZ = (random.nextDouble() - 0.5) * 0.5;
            double offsetY = 0;
            double velocityY = 0.15 + random.nextDouble() * 0.05;
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, block),
                    this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, 1, 0, 0, 0, velocityY);
        }
    }

    private void spawnFinalPhaseParticles() {
        if (!(level() instanceof ServerLevel server)) return;
        Random random = new Random();
        int particleCount = 200;
        double speed = 0.3;
        for (int i = 0; i < particleCount; i++) {
            double dx = random.nextDouble() * 2 - 1;
            double dy = random.nextDouble() * 2 - 1;
            double dz = random.nextDouble() * 2 - 1;
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length == 0) length = 1;
            dx /= length; dy /= length; dz /= length;
            dx *= speed; dy *= speed; dz *= speed;
            server.sendParticles(ModParticles.MAGIC_LINE_PARTICLE.get(),
                    this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
        }
    }

    // --- GETTERS Y SETTERS ---

    public void setIsAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }
    public boolean getIsAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public int getRiseCooldown() { return riseCooldown; }
    public String getBossName() { return "Seliora, the first archbishop"; }
    public void setRiseCooldown(int riseCooldown) { this.riseCooldown = riseCooldown; }

    public boolean canUseDodge() {
        // Usamos getter sincronizado isSecondPhase() implícitamente en las validaciones si fuera necesario
        return this.dodgeCooldown <= 0 && this.isAlive() && !this.isSecondPhaseStarting()
                && !this.isStarFallActive() && !this.isRiseActive() && !this.isWhirlWindActive()
                && !this.isJumpAttackActive() && !this.isChargeAttackActive() && !this.isSecondPhase()  && this.getTarget() != null;
    }

    public boolean isDodgeActive() { return this.entityData.get(DODGE_ACTIVE); }
    public void setDodgeActive(boolean active) { this.entityData.set(DODGE_ACTIVE, active); }
    public void setDodgeCooldown(int ticks) { this.dodgeCooldown = ticks; }

    // Validación de Goals usando el getter sincronizado de isSecondPhase()
    public boolean canUseSummonBlasphemousArmsGoal() {
        return this.getRiseCooldown() <= 0 && this.isAlive() && this.getTarget() != null && !this.isSecondPhase() && !this.isStarFallActive() && !this.isWhirlWindActive() && !isSecondPhaseStarting();
    }
    public boolean canUseStarFallGoal() {
        return this.getStarFallCooldown() <= 0 && this.isAlive() && this.getTarget() != null && !this.isSecondPhase() && !this.isRiseActive() && !this.isWhirlWindActive() && !isSecondPhaseStarting();
    }
    public boolean canUseWhirlwind() {
        return this.getWhirlWindCooldown() <= 0 && this.isAlive() && this.getTarget() != null && !this.isSecondPhase() && !this.isStarFallActive() && !this.isRiseActive() && !isSecondPhaseStarting();
    }
    public boolean canUseJumpAttack() {
        return this.getJumpAttackCooldown() <= 0 && this.isAlive() && this.getTarget() != null && this.isSecondPhase() && !this.isChargeAttackActive() && !isSecondPhaseStarting();
    }
    public boolean canUseChargeAttack() {
        return this.getChargeAttackCooldown() <= 0 && this.isAlive() && this.getTarget() != null && this.isSecondPhase() && !this.isJumpAttackActive() && !isSecondPhaseStarting();
    }

    public int getRiseMaxCooldown() { return riseMaxCooldown; }
    public void setRiseMaxCooldown(int riseMaxCooldown) { this.riseMaxCooldown = riseMaxCooldown; }
    public int getStarFallMaxCooldown() { return starFallMaxCooldown; }
    public void setStarFallMaxCooldown(int starFallMaxCooldown) { this.starFallMaxCooldown = starFallMaxCooldown; }
    public int getStarFallCooldown() { return starFallCooldown; }
    public void setStarFallCooldown(int starFallCooldown) { this.starFallCooldown = starFallCooldown; }
    public int getWhirlWindCooldown() { return whirlWindCooldown; }
    public void setWhirlWindCooldown(int whirlWindCooldown) { this.whirlWindCooldown = whirlWindCooldown; }
    public int getWhirlWindMaxCooldown() { return whirlWindMaxCooldown; }
    public boolean isStarFallActive() { return this.entityData.get(STARFALL_ACTIVE); }
    public void setStarFallActive(boolean active) { this.entityData.set(STARFALL_ACTIVE, active); }
    public boolean isRiseActive() { return this.entityData.get(RISE_ACTIVE); }
    public void setRiseActive(boolean active) { this.entityData.set(RISE_ACTIVE, active); }
    public boolean isWhirlWindActive() { return this.entityData.get(WHIRLWIND_ACTIVE); }
    public void setWhirlWindActive(boolean active) { this.entityData.set(WHIRLWIND_ACTIVE, active); }
    public int getRiseTicksDuration() { return riseTicksDuration; }
    public void setRiseTicksDuration(int riseTicksDuration) { this.riseTicksDuration = riseTicksDuration; }
    public int getStarFallTicksDuration() { return starFallTicksDuration; }
    public void setStarFallTicksDuration(int starFallTicksDuration) { this.starFallTicksDuration = starFallTicksDuration; }
    public int getWhirlWindTicksDuration() { return whirlWindTicksDuration; }
    public void setWhirlWindTicksDuration(int whirlWindTicksDuration) { this.whirlWindTicksDuration = whirlWindTicksDuration; }
    public int getJumpAttackCooldown() { return jumpAttackCooldown; }
    public void setJumpAttackCooldown(int jumpAttackCooldown) { this.jumpAttackCooldown = jumpAttackCooldown; }
    public int getJumpAttackMaxCooldown() { return jumpAttackMaxCooldown; }
    public boolean isSecondPhaseStarting() { return this.entityData.get(IS_STARTING_SECOND_PHASE); }
    public void setSecondPhaseStarting(boolean isStarting) { this.entityData.set(IS_STARTING_SECOND_PHASE, isStarting); }
    public void setJumpAttackMaxCooldown(int jumpAttackMaxCooldown) { this.jumpAttackMaxCooldown = jumpAttackMaxCooldown; }
    public boolean isJumpAttackActive() { return entityData.get(JUMP_ACTIVE); }
    public void setJumpAttackActive(boolean jumpAttackActive) { this.entityData.set(JUMP_ACTIVE, jumpAttackActive); }
    public int getJumpAttackChargeTicks() { return jumpAttackChargeTicks; }

    public boolean canTeleportFar() {
        boolean isCorrectDistance = false;
        if (this.getTarget() != null) {
            if (this.distanceTo(this.getTarget()) < avoidDistance) isCorrectDistance = true;
        }
        return this.getTeleportCooldown() <= 0 && !this.isDodgeActive() && this.isAlive() && !this.isSecondPhase() && !this.isStarFallActive() && !this.isRiseActive() && !this.isWhirlWindActive() && !isSecondPhaseStarting() && isCorrectDistance;
    }
    public boolean canTeleportNear() {
        boolean isCorrectDistance = false;
        if (this.getTarget() != null) {
            if (this.distanceTo(this.getTarget()) > avoidDistance) isCorrectDistance = true;
        }
        return this.getTeleportCooldown() <= 0 && !this.isDodgeActive() && this.isAlive() && !this.isSecondPhase() && !this.isStarFallActive() && !this.isRiseActive() && !this.isWhirlWindActive() && !isSecondPhaseStarting() && isCorrectDistance;
    }

    public void setJumpAttackChargeTicks(int jumpAttackChargeTicks) { this.jumpAttackChargeTicks = jumpAttackChargeTicks; }
    public int getChargeAttackCooldown() { return chargeAttackCooldown; }
    public void setChargeAttackCooldown(int chargeAttackCooldown) { this.chargeAttackCooldown = chargeAttackCooldown; }
    public int getChargeAttackMaxCooldown() { return chargeAttackMaxCooldown; }
    public void setChargeAttackMaxCooldown(int chargeAttackMaxCooldown) { this.chargeAttackMaxCooldown = chargeAttackMaxCooldown; }
    public boolean isChargeAttackActive() { return entityData.get(CHARGE_ACTIVE); }
    public void setChargeAttackActive(boolean chargeAttackActive) { entityData.set(CHARGE_ACTIVE, chargeAttackActive); }
    public int getChargeAttackChargeTicks() { return chargeAttackChargeTicks; }
    public void setChargeAttackChargeTicks(int chargeAttackChargeTicks) { this.chargeAttackChargeTicks = chargeAttackChargeTicks; }
    public int getSecondPhaseStartTicksDuration() { return secondPhaseStartTicksDuration; }
    public void setSecondPhaseStartTicksDuration(int secondPhaseStartTicksDuration) { this.secondPhaseStartTicksDuration = secondPhaseStartTicksDuration; }
    public int getTeleportCooldown() { return teleportCooldown; }
    public void setTeleportCooldown(int teleportCooldown) { this.teleportCooldown = teleportCooldown; }
    public int getTeleportMaxCooldown() { return teleportMaxCooldown; }
    public void setTeleportMaxCooldown(int teleportMaxCooldown) { this.teleportMaxCooldown = teleportMaxCooldown; }

    public void sendBossPacket() {
        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            this.level().players().forEach(player -> {
                boolean isNear = this.isAlive() && player.distanceTo(this) < 50;
                ModMessages.sendToPlayer(new BossSyncS2CPacket((int) getHealth(), (int) getMaxHealth(), isDeadOrDying(), isNear,2), (ServerPlayer) player);
            });
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            this.level().players().forEach(player -> {
                ModMessages.sendToPlayer(
                        new BossSyncS2CPacket((int) getHealth(), (int) getMaxHealth(), isDeadOrDying(), false,2),
                        (ServerPlayer) player
                );
            });
        }
    }
}