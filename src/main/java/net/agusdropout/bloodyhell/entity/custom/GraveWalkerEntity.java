package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class GraveWalkerEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    // --- ANIMACIONES ---
    // Definimos la animación de golpe en escudo (0.5s)
    private static final RawAnimation SHIELD_HIT = RawAnimation.begin().thenPlay("shield_hit");

    // DATA SYNCED
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(GraveWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_BLOCKING = SynchedEntityData.defineId(GraveWalkerEntity.class, EntityDataSerializers.BOOLEAN);

    // VARIABLES INTERNAS
    private int deathTimer = 30;
    private boolean isDeadAnimation = false;
    private int shieldCooldown = 0;

    public GraveWalkerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 20;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ATTACK_SPEED, 0.5f)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.FOLLOW_RANGE, 30.0D).build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(IS_BLOCKING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GraveWalkerShieldGoal(this));
        this.goalSelector.addGoal(3, new GraveWalkerAttackGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // --- LOGICA PRINCIPAL (TICK) ---
    @Override
    public void aiStep() {
        super.aiStep();

        // 1. Gestión del cooldown del escudo
        if (this.shieldCooldown > 0) {
            this.shieldCooldown--;
        }

        // 2. Partículas visuales (Ceniza)
        if (this.level().isClientSide && this.tickCount % 5 == 0) {
            this.level().addParticle(ParticleTypes.ASH,
                    this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(),
                    this.getY() + this.random.nextDouble() * (double)this.getBbHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(),
                    0.0D, 0.0D, 0.0D);
        }

        // 3. Movimiento al objetivo
        if(this.getTarget() != null && !this.isBlocking() && !this.isAttacking()) {
            this.getNavigation().moveTo(getTarget(), 1.0D);
        }
    }

    // --- GESTIÓN DE DAÑO Y FEEDBACK VISUAL ---
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBlocking()) {
            // Sonido de bloqueo
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);

            // Trigger Visual: Interrumpe el loop de escudo para mostrar el impacto
            if (!this.level().isClientSide) {
                // Esto envía un paquete al cliente para reproducir "shield_hit" inmediatamente
                this.triggerAnim("controller", "shield_hit");
            }

            // Rebote genérico de proyectiles
            if (source.getDirectEntity() != null && source.getDirectEntity() != source.getEntity()) {
                Entity projectile = source.getDirectEntity();
                Vec3 motion = projectile.getDeltaMovement();
                projectile.setDeltaMovement(motion.scale(-0.5));
                projectile.setYRot(projectile.getYRot() + 180.0F);
            }
            // Anular daño (return false)
            return false;
        }
        return super.hurt(source, amount);
    }

    // --- LOGICA DE MUERTE ---
    @Override
    protected void tickDeath() {
        if (!isDeadAnimation) {
            isDeadAnimation = true;
            this.deathTimer = 30;
            this.playSound(SoundEvents.WITHER_SKELETON_DEATH, 1.0F, 0.5F);
        }
        deathTimer--;

        // Partículas de muerte
        if (level().isClientSide && deathTimer > 0) {
            for (int i = 0; i < 3; i++) {
                level().addParticle(ModParticles.MAGIC_LINE_PARTICLE.get(),
                        this.getX() + (random.nextDouble() - 0.5),
                        this.getY() + random.nextDouble() * 2,
                        this.getZ() + (random.nextDouble() - 0.5),
                        0, 0.05, 0);
            }
        }

        if (deathTimer <= 0 && !level().isClientSide) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // --- GETTERS & SETTERS & NBT ---
    public void setAttacking(boolean attacking) { this.entityData.set(IS_ATTACKING, attacking); }
    public boolean isAttacking() { return this.entityData.get(IS_ATTACKING); }

    public void setBlocking(boolean blocking) { this.entityData.set(IS_BLOCKING, blocking); }
    public boolean isBlocking() { return this.entityData.get(IS_BLOCKING); }

    public boolean isShieldOnCooldown() { return this.shieldCooldown > 0; }
    public void setShieldCooldown(int ticks) { this.shieldCooldown = ticks; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShieldCooldown", this.shieldCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.shieldCooldown = tag.getInt("ShieldCooldown");
    }

    // --- GECKOLIB CONTROLLERS ---
    private PlayState predicate(AnimationState event) {
        // Muerte
        if (this.isDeadAnimation) {
            event.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        // Ataque (Con reset forzado para repetición)
        if (this.isAttacking()) {
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                event.getController().forceAnimationReset();
            }
            event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        // Escudo (Hold on last frame para postura estática)
        if (this.isBlocking()) {
            event.getController().setAnimation(RawAnimation.begin().then("shield", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }

        // Movimiento
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        // Idle
        event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Registramos el controlador con una transición suave de 5 ticks
        // Y añadimos el trigger "shield_hit" para feedback visual instantáneo
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate)
                .triggerableAnim("shield_hit", SHIELD_HIT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return factory; }

    // --- GOALS PERSONALIZADOS ---

    // 1. Goal de Ataque
    static class GraveWalkerAttackGoal extends Goal {
        private final GraveWalkerEntity mob;
        private int attackTicks;
        private int cooldownBetweenAttacks = 0;
        private final int ATTACK_DELAY = 16;     // Momento del impacto
        private final int ANIMATION_LENGTH = 30; // Duración total animación

        public GraveWalkerAttackGoal(GraveWalkerEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // Cooldown interno breve para permitir reset visual
            if (cooldownBetweenAttacks > 0) {
                cooldownBetweenAttacks--;
                return false;
            }
            LivingEntity target = this.mob.getTarget();
            // Distancia < 9.0D (3 bloques)
            return target != null && target.isAlive() && this.mob.distanceToSqr(target) < 9.0D && !this.mob.isBlocking();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && attackTicks < ANIMATION_LENGTH;
        }

        @Override
        public void start() {
            this.attackTicks = 0;
            this.mob.setAttacking(true);
            this.mob.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.mob.setAttacking(false);
            this.attackTicks = 0;
            this.cooldownBetweenAttacks = 5; // Pausa de 0.25s entre ataques
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            this.attackTicks++;

            if (this.attackTicks == ATTACK_DELAY && target != null) {
                double reach = 3.5D; // Alcance aumentado de la lanza
                double reachSqr = reach * reach;
                double distSqr = this.mob.distanceToSqr(target);

                if (distSqr <= reachSqr) {
                    boolean hit = this.mob.doHurtTarget(target);
                    if (hit) {
                        this.mob.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 0.5f);
                    }
                } else {
                    this.mob.playSound(SoundEvents.PLAYER_ATTACK_WEAK, 1.0f, 0.5f);
                }
            }
        }
    }

    // 2. Goal de Escudo
    static class GraveWalkerShieldGoal extends Goal {
        private final GraveWalkerEntity mob;
        private int shieldTime;

        public GraveWalkerShieldGoal(GraveWalkerEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP)); // Bloquea movimiento
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null
                    && !this.mob.isShieldOnCooldown()
                    && !this.mob.isAttacking()
                    && this.mob.distanceToSqr(target) < 64.0D
                    && this.mob.getRandom().nextInt(20) == 0; // 1 de cada 20 ticks (promedio 1 seg)
        }

        @Override
        public boolean canContinueToUse() {
            return shieldTime > 0;
        }

        @Override
        public void start() {
            this.shieldTime = 40; // Bloqueo dura 2 segundos
            this.mob.setBlocking(true);
            this.mob.getNavigation().stop();
            this.mob.playSound(SoundEvents.ARMOR_EQUIP_IRON, 1.0f, 0.5f);
        }

        @Override
        public void stop() {
            this.mob.setBlocking(false);
            this.mob.setShieldCooldown(60); // Cooldown de 3 segundos
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            this.shieldTime--;
        }
    }

    // Sonidos base
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.WITHER_SKELETON_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected void playStepSound(BlockPos p, BlockState b) { this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 0.5F); }
}