package net.agusdropout.bloodyhell.entity.projectile.spell;

import dev.kosmx.playerAnim.core.util.Vec3f;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulEntity;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulSize;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulType;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.S2CPainThronePacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TetherParticleOptions;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.bones.BoneManipulation;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class RhnullPainThroneEntity extends Projectile implements IGemSpell {

    // --- STATES ---
    public static final int STATE_GRABBING = 0;
    public static final int STATE_CLOSING = 1;
    public static final int STATE_DAMAGING = 2;
    private static final float DEFAULT_SCALE = 1.0f;

    public static final float HEIGHT_OFFSET = 1.5f;

    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(RhnullPainThroneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(RhnullPainThroneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(RhnullPainThroneEntity.class, EntityDataSerializers.FLOAT);


    // Vanilla Animation States (Client Only)
    public final AnimationState grabAnimationState = new AnimationState();
    public final AnimationState closeAnimationState = new AnimationState();
    public final AnimationState damageAnimationState = new AnimationState();

    // Spell Stats
    private float damage = 5.0f;
    private int maxDuration = 200;
    private int phaseTimer = 0;
    private int lifeTicks = 0;


    public RhnullPainThroneEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public RhnullPainThroneEntity(EntityType<? extends Projectile> type, Level level, LivingEntity owner, double x, double y, double z, List<Gem> gems) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(x, y, z);
        this.configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_STATE, STATE_GRABBING);
        this.entityData.define(DATA_TARGET_ID, -1);
        this.entityData.define(DATA_SCALE, DEFAULT_SCALE);
    }

    public int getSpellState() { return this.entityData.get(DATA_STATE); }
    public void setSpellState(int state) { this.entityData.set(DATA_STATE, state); }
    public int getTargetId() { return this.entityData.get(DATA_TARGET_ID); }
    public float getSize() { return this.entityData.get(DATA_SCALE); }
    public void setSize(float scale) { this.entityData.set(DATA_SCALE, scale); }
    public int getLifeTicks() { return this.lifeTicks; }
    public int getLifeTimeTicks() { return this.maxDuration; }

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;

        if (this.tickCount > maxDuration) {
            if (!this.level().isClientSide) this.discard();
            return;
        }

        int state = getSpellState();
        Entity target = getTargetId() != -1 ? this.level().getEntity(getTargetId()) : null;

        if (this.level().isClientSide) {
            handleClientVisuals(state, target);
            return;
        }

        // SERVER LOGIC
        switch (state) {
            case STATE_GRABBING -> {
                if (target instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                    Vec3 thisPosPlusOffset = this.position().add(0, HEIGHT_OFFSET, 0);
                    Vec3 diff = thisPosPlusOffset.subtract(livingTarget.position());
                    if (diff.length() < 2.0) {
                        livingTarget.setPos(this.getX(), this.getY()+ 1.5, this.getZ());
                        livingTarget.setDeltaMovement(Vec3.ZERO);
                        setSpellState(STATE_CLOSING);
                        this.phaseTimer = 0;
                        this.level().playSound(null, this.blockPosition(), SoundEvents.IRON_DOOR_CLOSE, SoundSource.MASTER, 1.0f, 0.8f);
                    } else {
                        Vec3 dir = diff.normalize();
                        double distance = diff.length();
                        double proximityFactor = Math.max(0.0, (8.0 - distance) / 8.0);
                        double lifeFactor = Math.min(1.0, (double) this.lifeTicks / maxDuration);
                        double combinedFactor = (proximityFactor + lifeFactor) / 2.0;
                        double baseSpeed = 0.02;
                        double maxBonusSpeed = 0.08;
                        double currentPullSpeed = baseSpeed + (combinedFactor * maxBonusSpeed);
                        Vec3 customPull = new Vec3(dir.x * currentPullSpeed, dir.y * 0.2, dir.z * currentPullSpeed);
                        livingTarget.setDeltaMovement(livingTarget.getDeltaMovement().add(customPull));
                        updateRotationTowardsTarget(livingTarget);
                    }
                } else {
                    AABB searchArea = this.getBoundingBox().inflate(8.0);
                    List<LivingEntity> potentialTargets = this.level().getEntitiesOfClass(
                            LivingEntity.class,
                            searchArea,
                            this::isValidTarget
                    );
                    if (!potentialTargets.isEmpty()) {
                        this.entityData.set(DATA_TARGET_ID, potentialTargets.get(0).getId());
                    }
                }
            }
            case STATE_CLOSING -> {
                if (target != null) {
                    target.setPos(this.getX(), this.getY()+HEIGHT_OFFSET, this.getZ());
                    target.setDeltaMovement(Vec3.ZERO);
                }
                this.phaseTimer++;
                if (this.phaseTimer >= 40) { // 2 seconds
                    setSpellState(STATE_DAMAGING);
                    this.phaseTimer = 0;
                }
            }
            case STATE_DAMAGING -> {
                if (target instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                    livingTarget.setPos(this.getX(), this.getY()+ HEIGHT_OFFSET, this.getZ());
                    livingTarget.setDeltaMovement(Vec3.ZERO);

                    this.phaseTimer++;
                    if (this.phaseTimer % 10 == 0) {
                        livingTarget.hurt(this.damageSources().magic(), this.damage);
                        this.level().playSound(null, this.blockPosition(), ModSounds.NECK_SNAP_SOUND.get(), SoundSource.MASTER, 0.5f, 0.5f + (this.random.nextFloat() - 0.5f) * 0.2f);
                        ModMessages.sendToPlayersTrackingEntity(new S2CPainThronePacket(livingTarget.getUUID(), 200, BoneManipulation.BREAK), livingTarget);

                        if (this.getOwner() instanceof Player playerOwner) {

                            BloodSoulEntity soul = new BloodSoulEntity(this.level(), playerOwner, BloodSoulType.BLOOD, this.determineSoulSize());

                            soul.setPos(this.getX(), this.getY() + 2.0, this.getZ());
                            this.level().addFreshEntity(soul);
                        }
                    }
                } else {
                    this.discard();
                }
            }
        }
    }

    private void handleClientVisuals(int state, Entity target) {
        // 1. Update Animation States
        if (state == STATE_GRABBING) {
            this.grabAnimationState.startIfStopped(this.tickCount);
            this.closeAnimationState.stop();
            this.damageAnimationState.stop();
        } else if (state == STATE_CLOSING) {
            this.grabAnimationState.stop();
            this.closeAnimationState.startIfStopped(this.tickCount);
            this.damageAnimationState.stop();
        } else if (state == STATE_DAMAGING) {
            this.grabAnimationState.stop();
            this.closeAnimationState.stop();
            this.damageAnimationState.startIfStopped(this.tickCount);
        }

        //start & end
        if(this.lifeTicks == 1){
            for(int i = 0; i < 20; i++) {
                Vector3f gradientColor = ParticleHelper.gradient3(random.nextFloat(), new Vector3f(1f, 0.97f, 0.0f), new Vector3f(1.0f, 0.8f, 0.0f), new Vector3f(1f, 0.5f, 0.0f));
                ParticleHelper.spawnExplosion(this.level(), new MagicParticleOptions(gradientColor, 4.0f, false, 40), this.position().add(0,HEIGHT_OFFSET+1,0), 5, 0.2, 2);
            }
            this.level().playLocalSound( this.blockPosition(), ModSounds.CREEPY_BELL.get(), SoundSource.MASTER, 1.0f, 0.8f, false);
        }

        if(this.lifeTicks == this.maxDuration - 1){
            for(int i = 0; i < 20; i++) {
                Vector3f gradientColor = ParticleHelper.gradient3(random.nextFloat(), new Vector3f(1f, 0.97f, 0.0f), new Vector3f(1.0f, 0.8f, 0.0f), new Vector3f(1f, 0.5f, 0.0f));
                ParticleHelper.spawnExplosion(this.level(), new MagicParticleOptions(gradientColor, 4.0f, false, 40), this.position().add(0,HEIGHT_OFFSET+1,0), 5, 0.2, 2);
            }
        }



        // 2. Spawn Particles
        if (state == STATE_GRABBING && target != null && this.tickCount % 2 == 0) {
            for(int i = 0; i < 5; i++) {
                this.level().addParticle(new TetherParticleOptions(target.getUUID(), 1f, 0.914f, 0.0f, 0.6f, 10),
                        this.getX()+ random.nextDouble(), this.getY() + 2.5, this.getZ() + random.nextDouble(), 0, 0, 0);

            }
        } else if (state == STATE_DAMAGING && this.random.nextBoolean()) {
            this.level().addParticle(ModParticles.BLOOD_DROP_PARTICLE.get(),
                    this.getX() + (this.random.nextDouble() - 0.5),
                    this.getY() + 1.0 + this.random.nextDouble(),
                    this.getZ() + (this.random.nextDouble() - 0.5), 0, 0, 0);
        }
    }

    // --- IGemSpell ---
    @Override
    public void increaseSpellDamage(double amount) { this.damage += amount; }
    @Override
    public void increaseSpellSize(double amount) { this.setSize(this.getSize()+ (float) amount); }
    @Override
    public void increaseSpellDuration(int amount) { this.maxDuration += amount; }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }


    private void updateRotationTowardsTarget(Entity target) {
        if (target != null) {

            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

            this.yRotO = this.getYRot();

            float newYaw = Mth.approachDegrees(this.getYRot(), targetYaw, 1.0F);
            this.setYRot(newYaw);
        }
    }



    private boolean isValidTarget(LivingEntity target) {
        if (!target.isAlive() || target.isInvulnerable()) {
            return false;
        }
        if (target == this.getOwner()) {
            return false;
        }

        if (this.getOwner() != null && target.isAlliedTo(this.getOwner())) {
            return false;
        }
        if (target instanceof net.minecraft.world.entity.player.Player player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        return true;
    }

    private BloodSoulSize determineSoulSize() {
        if (this.getSize() > DEFAULT_SCALE+0.5f) {
            return BloodSoulSize.LARGE;
        } else if (this.getSize() > DEFAULT_SCALE+0.25f) {
            return BloodSoulSize.MEDIUM;
        } else {
            return BloodSoulSize.SMALL;
        }
    }


}