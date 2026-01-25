package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ai.goals.CinderAcolyteFlameAttackGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.CinderAcolyteMeleeGoal;
import net.agusdropout.bloodyhell.entity.interfaces.BloodFlammable;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncBloodFireEffectPacket;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class CinderAcolyteEntity extends Monster implements GeoEntity , BloodFlammable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Synced Data for Animations
    private static final EntityDataAccessor<Boolean> IS_MELEE_ATTACKING = SynchedEntityData.defineId(CinderAcolyteEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FLAME_ATTACKING = SynchedEntityData.defineId(CinderAcolyteEntity.class, EntityDataSerializers.BOOLEAN);

    // Cooldowns (Server Side)
    private int meleeCooldown = 0;
    private int flameCooldown = 0;

    // Constants
    public static final int FLAME_ATTACK_COOLDOWN_MAX = 200; // 10 Seconds
    public static final int MELEE_ATTACK_COOLDOWN_MAX = 30;  // 1.5 Seconds

    public CinderAcolyteEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0f) // Heavy hit
                .add(Attributes.ATTACK_SPEED, 0.5f)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D) // High resistance
                .add(Attributes.FOLLOW_RANGE, 32.0D).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // Special Attack (Priority 2)
        this.goalSelector.addGoal(2, new CinderAcolyteFlameAttackGoal(this));
        // Basic Attack (Priority 3)
        this.goalSelector.addGoal(3, new CinderAcolyteMeleeGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_MELEE_ATTACKING, false);
        this.entityData.define(IS_FLAME_ATTACKING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (meleeCooldown > 0) meleeCooldown--;
            if (flameCooldown > 0) flameCooldown--;


        }
    }

    @Override
    public void aiStep() {
        super.aiStep();


        if( this.getTarget() != null && !this.isMeleeAttacking() && !this.isFlameAttacking()) {
            double dist = this.distanceToSqr(this.getTarget());
            this.getNavigation().moveTo(getTarget(), 1.0D);
        }
    }

    // --- LOGIC: MELEE ATTACK ---
    public void performMeleeDamage(LivingEntity target) {
        if (target != null) {
            boolean hit = this.doHurtTarget(target);
            if (hit) {
                // Chain sound on hit
                this.playSound(SoundEvents.CHAIN_HIT, 1.0f, 0.8f);
                // Apply Fire
                target.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 100, 0));
            }
        }
    }
    public void performFlameAreaDamage() {
        // 1. Get the LOCKED Direction
        // Since the Goal stops rotating the mob, getLookAngle() returns the fixed firing line.
        Vec3 dir = this.getLookAngle();
        Vec3 origin = this.position().add(0, 1.2, 0); // Chest height

        // 2. Configuration
        double range = 8.0;
        double coneAngle = 0.5; // ~60 degrees

        // 3. SPAWN PARTICLE WAVE (Server Side)
        // This creates the visual "Magic Wave" effect synchronized with the damage tick.
        spawnFlameWaveParticles(origin, dir, range);

        // 4. DAMAGE LOGIC
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(range));

        for (LivingEntity e : targets) {
            // A. Safety Checks: Not self, Not Allied, Not Same Class (Cinder Acolytes don't burn each other)
            if (e != this && !e.isAlliedTo(this) && !(e instanceof CinderAcolyteEntity)) {

                Vec3 targetCenter = e.getBoundingBox().getCenter();
                Vec3 toEntity = targetCenter.subtract(origin).normalize();

                // B. Range & Cone Check
                double distSqr = origin.distanceToSqr(targetCenter);
                if (distSqr <= range * range) {
                    if (dir.dot(toEntity) > coneAngle) {

                        // C. Line of Sight (Optional, keeps it fair)
                        if (this.hasLineOfSight(e)) {
                            // D. DAMAGE (No Vanilla Fire)
                            e.hurt(this.damageSources().mobAttack(this), 3.0f); // Fast tick damage

                            // E. APPLY EFFECT
                            setOnBloodFire(e,200,0);
                        }
                    }
                }
            }
        }

        // Sound
        if (this.tickCount % 4 == 0) {
            this.playSound(SoundEvents.FIRECHARGE_USE, 1.0f, 0.8f);
        }
    }

    private void spawnFlameWaveParticles(Vec3 origin, Vec3 dir, double range) {
        if (this.level().isClientSide) return; // ParticleHelper handles server logic, but safety first

        // Spawn a cluster of magic particles moving outward in the cone
        for (int i = 0; i < 5; i++) {
            double spread = 0.3;
            double speed = 0.5;

            // Random Cone Vector
            double vx = dir.x + (random.nextGaussian() * spread);
            double vy = dir.y + (random.nextGaussian() * spread);
            double vz = dir.z + (random.nextGaussian() * spread);

            // Normalize and scale speed
            Vec3 vel = new Vec3(vx, vy, vz).normalize().scale(speed);

            // Alternating Colors: Dark Red vs Bright Red
            Vector3f color = random.nextBoolean() ?
                    new Vector3f(0.5f, 0.0f, 0.0f) : // Dark Blood
                    new Vector3f(0.9f, 0.1f, 0.1f);  // Bright Red

            ParticleHelper.spawn(this.level(),
                    new MagicParticleOptions(color, 0.6f, false, 15),
                    origin.x, origin.y, origin.z,
                    vel.x, vel.y, vel.z);
        }
    }

    // --- ACCESSORS ---
    public boolean isMeleeReady() { return meleeCooldown <= 0; }
    public boolean isFlameReady() { return flameCooldown <= 0; }
    public void setMeleeCooldown(int ticks) { this.meleeCooldown = ticks; }
    public void setFlameCooldown(int ticks) { this.flameCooldown = ticks; }

    public void setMeleeAttacking(boolean attacking) { this.entityData.set(IS_MELEE_ATTACKING, attacking); }
    public boolean isMeleeAttacking() { return this.entityData.get(IS_MELEE_ATTACKING); }

    public void setFlameAttacking(boolean attacking) { this.entityData.set(IS_FLAME_ATTACKING, attacking); }
    public boolean isFlameAttacking() { return this.entityData.get(IS_FLAME_ATTACKING); }

    // --- ANIMATION CONTROLLER ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            if (isFlameAttacking()) {
                return state.setAndContinue(RawAnimation.begin().then("flame_attack", Animation.LoopType.PLAY_ONCE));
            } else if (isMeleeAttacking()) {
                return state.setAndContinue(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            } else if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().then("walking", Animation.LoopType.LOOP));
            }
            return state.setAndContinue(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    // --- SOUNDS ---
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PHANTOM_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.PHANTOM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PHANTOM_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.CHAIN_STEP, 0.5F, 1.0F);
    }

    @Override
    public Level getLevel() {
        return this.level();
    }
}