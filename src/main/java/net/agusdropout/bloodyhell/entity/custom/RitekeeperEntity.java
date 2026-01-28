package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ai.goals.RitekeeperEvasionGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.RitekeeperFireMeteorGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.RitekeeperFlamePillarGoal;
import net.agusdropout.bloodyhell.entity.ai.goals.RitekeeperSoulAttackGoal;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.BossSyncS2CPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RitekeeperEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // --- SYNCED DATA ---
    private static final EntityDataAccessor<Boolean> IS_CASTING = SynchedEntityData.defineId(RitekeeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_EVADING = SynchedEntityData.defineId(RitekeeperEntity.class, EntityDataSerializers.BOOLEAN); // NEW
    private static final EntityDataAccessor<Integer> CURRENT_SPELL = SynchedEntityData.defineId(RitekeeperEntity.class, EntityDataSerializers.INT);

    // --- COOLDOWNS ---
    private int meteorCooldown = 0;
    private int soulCooldown = 0;
    private int pillarCooldown = 0;

    // --- CONSTANTS ---
    public static final int CD_METEOR_MAX = 300;
    public static final int CD_SOUL_MAX = 100;
    public static final int CD_PILLAR_MAX = 200;

    public RitekeeperEntity(EntityType<? extends Monster> type, Level level) {

        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RitekeeperEvasionGoal(this)); // High Priority
        this.goalSelector.addGoal(2, new RitekeeperFlamePillarGoal(this));
        this.goalSelector.addGoal(3, new RitekeeperFireMeteorGoal(this));
        this.goalSelector.addGoal(4, new RitekeeperSoulAttackGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
    }



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CASTING, false);
        this.entityData.define(IS_EVADING, false);
        this.entityData.define(CURRENT_SPELL, 0);
    }

    // --- INVULNERABILITY LOGIC ---
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // If evading, ignore all damage except from Creative Players or OutOfWorld (Void)
        if (this.isEvading() && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (meteorCooldown > 0) meteorCooldown--;
            if (soulCooldown > 0) soulCooldown--;
            if (pillarCooldown > 0) pillarCooldown--;
        }

        // Ambient Particles (Only if NOT evading, to avoid clutter)
        if (this.level().isClientSide && !this.isEvading() && this.tickCount % 5 == 0) {
            this.level().addParticle(ModParticles.BLOOD_SIGIL_PARTICLE.get(),
                    this.getX() + (random.nextDouble() - 0.5),
                    this.getY() + 1.5,
                    this.getZ() + (random.nextDouble() - 0.5),
                    0, 0.05, 0);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        sendBossPacket();
    }

    // --- STATE MANAGEMENT ---
    public boolean isCasting() { return this.entityData.get(IS_CASTING); }
    public void setCasting(boolean casting) { this.entityData.set(IS_CASTING, casting); }

    public boolean isEvading() { return this.entityData.get(IS_EVADING); }
    public void setEvading(boolean evading) { this.entityData.set(IS_EVADING, evading); }

    public int getCurrentSpell() { return this.entityData.get(CURRENT_SPELL); }
    public void setCurrentSpell(int id) { this.entityData.set(CURRENT_SPELL, id); }

    public boolean isMeteorReady() { return meteorCooldown <= 0; }
    public void setMeteorCooldown(int t) { this.meteorCooldown = t; }

    public boolean isSoulReady() { return soulCooldown <= 0; }
    public void setSoulCooldown(int t) { this.soulCooldown = t; }

    public boolean isPillarReady() { return pillarCooldown <= 0; }
    public void setPillarCooldown(int t) { this.pillarCooldown = t; }

    // --- ANIMATION ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 5, event -> {
            if (isEvading()) return PlayState.STOP; // Stop animations while invisible

            if (isCasting()) {
                int spell = getCurrentSpell();
                if (spell == 1) return event.setAndContinue(RawAnimation.begin().then("fire_meteor_attack", Animation.LoopType.PLAY_ONCE));
                if (spell == 2) return event.setAndContinue(RawAnimation.begin().then("blood_fire_soul_attack", Animation.LoopType.PLAY_ONCE));
                if (spell == 3) return event.setAndContinue(RawAnimation.begin().then("flame_pillar_attack", Animation.LoopType.PLAY_ONCE));
            }
            if (event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().then("walking", Animation.LoopType.LOOP));
            }
            return event.setAndContinue(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PHANTOM_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.PHANTOM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PHANTOM_DEATH; }

    public void sendBossPacket() {
        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            this.level().players().forEach(player -> {
                boolean isNear = this.isAlive() && player.distanceTo(this) < 50;
                ModMessages.sendToPlayer(new BossSyncS2CPacket((int) getHealth(), (int) getMaxHealth(), isDeadOrDying(), isNear,1), (ServerPlayer) player);
            });
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            this.level().players().forEach(player -> {
                ModMessages.sendToPlayer(
                        new BossSyncS2CPacket((int) getHealth(), (int) getMaxHealth(), isDeadOrDying(), false,1),
                        (ServerPlayer) player
                );
            });
        }
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {

        return false;
    }

    @Override
    public boolean isPersistenceRequired() {

        return true;
    }
}