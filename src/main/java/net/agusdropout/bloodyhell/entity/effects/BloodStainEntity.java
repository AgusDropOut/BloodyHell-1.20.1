package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BloodStainEntity extends Entity {

    private static final EntityDataAccessor<Direction> ATTACH_FACE = SynchedEntityData.defineId(BloodStainEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(BloodStainEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_MAX_AGE = SynchedEntityData.defineId(BloodStainEntity.class, EntityDataSerializers.INT);

    private static final int DEFAULT_MAX_LIFE = 600;

    @Nullable
    private UUID ownerUUID;

    public BloodStainEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public BloodStainEntity(Level level, double x, double y, double z, Direction face) {
        this(level, x, y, z, face, 1.0f);
    }

    public BloodStainEntity(Level level, double x, double y, double z, Direction face, float size) {
        this(ModEntityTypes.BLOOD_STAIN_ENTITY.get(), level);
        this.setPos(x, y, z);
        this.setAttachFace(face);
        this.setSize(size);
    }

    public void setOwner(LivingEntity owner) {
        this.ownerUUID = owner.getUUID();
    }

    public boolean isSafe(LivingEntity entity) {
        if (this.ownerUUID == null) return false;

        if (entity.getUUID().equals(this.ownerUUID)) return true;

        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            Entity owner = serverLevel.getEntity(this.ownerUUID);
            if (owner != null) {
                return entity.isAlliedTo(owner) || owner.isAlliedTo(entity);
            }
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACH_FACE, Direction.UP);
        this.entityData.define(SIZE, 1.0f);
        this.entityData.define(DATA_MAX_AGE, DEFAULT_MAX_LIFE);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide && this.tickCount % 15 == 0) {
            spawnDrippingParticles();
        }

        if (!this.level().isClientSide) {
            if (this.tickCount >= this.getMaxAge()) {
                this.discard();
                return;
            }

            if (this.tickCount % 5 == 0) {
                AABB box = this.getBoundingBox().inflate(0.2 * getSize());

                List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, box);

                for (LivingEntity victim : victims) {
                    if (isSafe(victim)) continue;

                    victim.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 100, 0));

                    if (this.getAttachFace() == Direction.UP) {
                        victim.makeStuckInBlock(this.level().getBlockState(this.blockPosition()), new Vec3(0.7, 0.75, 0.7));
                    }
                }
            }
        }
    }

    private void spawnDrippingParticles() {
        Direction face = this.getAttachFace();
        if (face != Direction.UP) {
            double range = 0.6 * getSize();

            double x = this.getX() + (random.nextDouble() - 0.5) * range;
            double y = this.getY() + (random.nextDouble() - 0.5) * range;
            double z = this.getZ() + (random.nextDouble() - 0.5) * range;

            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), x, y, z, 0, -0.05, 0);
        }
    }

    public void setAttachFace(Direction face) { this.entityData.set(ATTACH_FACE, face); }
    public Direction getAttachFace() { return this.entityData.get(ATTACH_FACE); }

    public void setSize(float size) { this.entityData.set(SIZE, size); }
    public float getSize() { return this.entityData.get(SIZE); }

    public void setMaxAge(int ticks) { this.entityData.set(DATA_MAX_AGE, ticks); }
    public int getMaxAge() { return this.entityData.get(DATA_MAX_AGE); }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Age")) {
            this.tickCount = tag.getInt("Age");
        } else {
            this.tickCount = tag.getInt("Life") > 0 ? this.getMaxAge() - tag.getInt("Life") : 0;
        }

        this.setAttachFace(Direction.from3DDataValue(tag.getInt("Face")));

        if (tag.contains("Size")) {
            this.setSize(tag.getFloat("Size"));
        }

        if (tag.contains("MaxAge")) {
            this.setMaxAge(tag.getInt("MaxAge"));
        }

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", this.tickCount);
        tag.putInt("Face", getAttachFace().get3DDataValue());
        tag.putFloat("Size", getSize());
        tag.putInt("MaxAge", getMaxAge());

        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}