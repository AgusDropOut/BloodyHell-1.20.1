package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public class SmallCrimsonDagger extends Projectile {

    private static final EntityDataAccessor<Boolean> STUCK = SynchedEntityData.defineId(SmallCrimsonDagger.class, EntityDataSerializers.BOOLEAN);

    // Trail History: Stores the last 12 positions (World Space)
    private final Deque<Vec3> trailHistory = new ArrayDeque<>();

    private int lifeTicks = 120;
    private int fadeTimer = 0;
    private float damage = 4.0f;
    @Nullable private UUID ownerUUID;

    public SmallCrimsonDagger(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public SmallCrimsonDagger(Level level, double x, double y, double z, LivingEntity owner) {
        this(ModEntityTypes.SMALL_CRIMSON_DAGGER.get(), level);
        this.setOwner(owner);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STUCK, false);
    }

    @Override
    public void tick() {
        super.tick();

        // --- TRAIL LOGIC ---
        // Only record history on client, while moving, and not stuck
        if (this.level().isClientSide) {
            if (!this.isStuckInGround() && this.getVehicle() == null) {
                trailHistory.addFirst(this.position());
                if (trailHistory.size() > 12) {
                    trailHistory.removeLast();
                }
            } else if (!trailHistory.isEmpty()) {
                // If stuck, shrink the trail so it catches up and disappears
                trailHistory.removeLast();
            }
        }

        // 1. Stuck in Ground
        if (this.entityData.get(STUCK)) {
            this.setDeltaMovement(Vec3.ZERO);
            if (fadeTimer++ > 60) {
                this.discard();
            }
            return;
        }

        // 2. Riding Entity
        if (this.getVehicle() != null) {
            if (!this.getVehicle().isAlive() || this.lifeTicks-- <= 0) {
                this.discard();
            }
            return;
        }

        // 3. Flight
        if (this.lifeTicks-- <= 0) {
            this.discard();
            return;
        }

        // Spawn Pop (Visual)
        if (this.tickCount == 1 && this.level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(),
                        this.getX(), this.getY(), this.getZ(),
                        (random.nextDouble()-0.5)*0.1, 0.1, (random.nextDouble()-0.5)*0.1);
            }
        }

        Vec3 motion = this.getDeltaMovement();
        HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (result.getType() != HitResult.Type.MISS) {
            this.onHit(result);
        }

        double nextX = this.getX() + motion.x;
        double nextY = this.getY() + motion.y;
        double nextZ = this.getZ() + motion.z;

        this.updateRotation();
        this.setPos(nextX, nextY, nextZ);

        if (!this.isNoGravity()) {
            this.setDeltaMovement(motion.scale(0.99).subtract(0, 0.05, 0));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            Entity target = result.getEntity();
            if (target == this.getOwner()) return;

            target.hurt(this.level().damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), this.damage);
            this.startRiding(target, true);
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
            this.lifeTicks = 60;
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.entityData.set(STUCK, true);
        this.playSound(SoundEvents.CHAIN_HIT, 0.5f, 1.2f);

        if (level().isClientSide) {
            for(int i=0; i<3; i++) {
                this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(),
                        this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }
        if (!this.level().isClientSide) {
            // Calculate spawn position relative to the hit face
            Direction face = result.getDirection();
            BlockPos hitPos = result.getBlockPos();

            // Move slightly out of the block so it isn't inside
            double spawnX = hitPos.getX() + 0.5 + (face.getStepX() * 0.05);
            double spawnY = hitPos.getY() + 0.5 + (face.getStepY() * 0.05); // Center of block face
            double spawnZ = hitPos.getZ() + 0.5 + (face.getStepZ() * 0.05);

            // If hitting the top of a block (Floor), we usually want it exactly on top
            if (face == Direction.UP) spawnY = hitPos.getY() + 1.0;
            if (face == Direction.DOWN) spawnY = hitPos.getY();
            if (face == Direction.NORTH) spawnZ = hitPos.getZ();
            if (face == Direction.SOUTH) spawnZ = hitPos.getZ() + 1.0;
            if (face == Direction.WEST) spawnX = hitPos.getX();
            if (face == Direction.EAST) spawnX = hitPos.getX() + 1.0;

            // Ideally, pass the exact hit location from result.getLocation()
            // but aligning to the block grid often looks cleaner for decals.
            // Let's use the exact hit location for accuracy:
            Vec3 exactHit = result.getLocation();

            // Spawn the Stain
            BloodStainEntity stain = new BloodStainEntity(this.level(),
                    exactHit.x, exactHit.y, exactHit.z, face);

            this.level().addFreshEntity(stain);
        }
    }

    public   void updateRotation() {
        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() > 0.001) {
            double hDist = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            this.setYRot((float) (Mth.atan2(motion.x, motion.z) * (180F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(motion.y, hDist) * (180F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }

    public boolean isStuckInGround() { return this.entityData.get(STUCK); }

    public float getFadeAlpha(float partialTicks) {
        if (!isStuckInGround()) return 1.0f;
        return Mth.clamp(1.0f - ((fadeTimer + partialTicks) / 60.0f), 0.0f, 1.0f);
    }

    // Accessor for Renderer
    public Deque<Vec3> getTrailHistory() {
        return this.trailHistory;
    }

    @Override public void setOwner(@Nullable Entity entity) { if (entity != null) this.ownerUUID = entity.getUUID(); }
    @Override @Nullable public Entity getOwner() { return ownerUUID != null && level() instanceof ServerLevel s ? s.getEntity(ownerUUID) : null; }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { this.entityData.set(STUCK, tag.getBoolean("Stuck")); if(tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner"); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putBoolean("Stuck", this.entityData.get(STUCK)); if(ownerUUID != null) tag.putUUID("Owner", ownerUUID); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}