package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class BloodSlashDecalEntity extends Entity {

    private int age = 0;

    // Increased life to 10 seconds (200 ticks)
    private static final int MAX_AGE = 200;

    private static final EntityDataAccessor<Direction> DATA_FACE = SynchedEntityData.defineId(BloodSlashDecalEntity.class, EntityDataSerializers.DIRECTION);

    public BloodSlashDecalEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public BloodSlashDecalEntity(Level level, double x, double y, double z, float yaw, Direction face) {
        this(ModEntityTypes.BLOOD_SLASH_DECAL.get(), level);
        this.setPos(x, y, z);
        this.setYRot(yaw);
        this.setFace(face);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_FACE, Direction.UP);
    }

    public void setFace(Direction face) {
        this.entityData.set(DATA_FACE, face);
    }

    public Direction getFace() {
        return this.entityData.get(DATA_FACE);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (this.age++ >= MAX_AGE) {
                this.discard();
            }
        }
    }

    public float getAge(float partialTicks) {
        return this.age + partialTicks;
    }

    public float getMaxAge() {
        return MAX_AGE;
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) { this.age = tag.getInt("Age"); this.setFace(Direction.from3DDataValue(tag.getByte("Face"))); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Age", this.age); tag.putByte("Face", (byte)this.getFace().get3DDataValue()); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}