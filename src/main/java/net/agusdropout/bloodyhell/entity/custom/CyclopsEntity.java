package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag; // Import necesario
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class CyclopsEntity extends Mob {

    private static final double AMBIENT_PARTICLE_RADIUS = 8.0;
    private static final double AMBIENT_PARTICLE_MIN_DISTANCE = 2.5;
    private static final int AMBIENT_PARTICLE_COUNT = 10;
    private static final int AMBIENT_PARTICLE_TICK_INTERVAL = 40;

    public static final int ATTACK_CHARGE_TIME_TICKS = 60;
    private static final int ATTACK_INTERVAL_TICKS = 10;
    private static final float ATTACK_DAMAGE = 1.5f;

    private static final EntityDataAccessor<Float> EYE_YAW = SynchedEntityData.defineId(CyclopsEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> EYE_PITCH = SynchedEntityData.defineId(CyclopsEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SYNCED_LINE_OF_SIGHT_TICKS = SynchedEntityData.defineId(CyclopsEntity.class, EntityDataSerializers.INT);

    private int lineOfSightTicks;

    public CyclopsEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.lineOfSightTicks = 0;

        this.setPersistenceRequired();
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
    // -------------------------------------------

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EYE_YAW, 0.0f);
        this.entityData.define(EYE_PITCH, 0.0f);
        this.entityData.define(SYNCED_LINE_OF_SIGHT_TICKS, 0);
    }

    public int getClientSideAttackTicks() {
        return this.entityData.get(SYNCED_LINE_OF_SIGHT_TICKS);
    }



    private void spawnAmbientEyeParticles() {
        ParticleHelper.spawnHollowSphere(level(), ModParticles.EYE_PARTICLE.get(),
                position().add(0, getBbHeight()/2.0, 0), 8.0, 10, 0);
    }

    private void spawnAttackBeamParticles(Vec3 startPos, Vec3 endPos) {
        ParticleHelper.spawnLine(level(), ParticleTypes.FLAME, startPos, endPos, (int)(startPos.distanceTo(endPos) * 4), Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        this.setYRot(90);

        if (this.tickCount % 2000 == 0 || this.tickCount == 1) {
            if (this.level() instanceof ServerLevel) {
                ((ServerLevel)this.level()).sendParticles(ModParticles.CYCLOPS_HALO_PARTICLE.get(), this.getX(), this.getY()+ 0.6, this.getZ(), 1, 0, 0, 0, 0);
            }
        }

        if (!this.level().isClientSide()) {
            Player player = this.level().getNearestPlayer(this, 64.0);

            if (player != null && !player.isCreative() && !player.isSpectator()) {
                Vec3 eyePos = this.position().add(0, this.getBbHeight() / 1.5, 0);
                Vec3 playerHeadPos = player.getEyePosition();

                Vec3 lookVec = playerHeadPos.subtract(eyePos);
                float yaw = (float) Mth.wrapDegrees(Mth.atan2(lookVec.z, lookVec.x) * (180.0 / Math.PI) - 90.0);
                float pitch = (float) Mth.wrapDegrees(-(Math.asin(lookVec.y / lookVec.length()) * (180.0 / Math.PI)));
                this.getEntityData().set(EYE_YAW, yaw);
                this.getEntityData().set(EYE_PITCH, -pitch);

                if (hasLineOfSight(player)) {
                    this.lineOfSightTicks++;
                    if (this.lineOfSightTicks >= ATTACK_CHARGE_TIME_TICKS) {
                        if (this.lineOfSightTicks % ATTACK_INTERVAL_TICKS == 0) {
                            player.hurt(this.damageSources().mobAttack(this), ATTACK_DAMAGE);
                            spawnAttackBeamParticles(eyePos, playerHeadPos);
                        }
                    }
                } else {
                    this.lineOfSightTicks = 0;
                }

                this.getEntityData().set(SYNCED_LINE_OF_SIGHT_TICKS, this.lineOfSightTicks);

            } else {
                this.lineOfSightTicks = 0;
                this.getEntityData().set(EYE_YAW, 0.0f);
                this.getEntityData().set(EYE_PITCH, 0.0f);
                this.getEntityData().set(SYNCED_LINE_OF_SIGHT_TICKS, 0);
            }
        }

        if (this.level().isClientSide()) {
            if (this.tickCount % AMBIENT_PARTICLE_TICK_INTERVAL == 0) {
                spawnAmbientEyeParticles();
            }
        }
    }

    @Override
    public boolean hasLineOfSight(Entity pEntity) {
        if (pEntity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }

        Vec3 myEyePos = this.position().add(0, this.getBbHeight() / 1.5, 0);
        Vec3 targetEyePos = pEntity.getEyePosition(1.0f);

        ClipContext context = new ClipContext(myEyePos, targetEyePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        BlockHitResult result = this.level().clip(context);

        return result.getType() == BlockHitResult.Type.MISS;
    }

    public float getEyeYaw(float partialTicks) {
        float prevYaw = this.entityData.get(EYE_YAW);
        float currentYaw = this.entityData.get(EYE_YAW);
        return Mth.lerp(partialTicks, prevYaw, currentYaw);
    }

    public float getEyePitch(float partialTicks) {
        float prevPitch = this.entityData.get(EYE_PITCH);
        float currentPitch = this.entityData.get(EYE_PITCH);
        return Mth.lerp(partialTicks, prevPitch, currentPitch);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0).build();
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {}
}