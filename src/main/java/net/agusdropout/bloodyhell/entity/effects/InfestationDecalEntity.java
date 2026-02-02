package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class InfestationDecalEntity extends Entity {

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(InfestationDecalEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(InfestationDecalEntity.class, EntityDataSerializers.FLOAT);
    // NEW: Store the face index (0=Down, 1=Up, 2=North...)
    private static final EntityDataAccessor<Byte> FACE = SynchedEntityData.defineId(InfestationDecalEntity.class, EntityDataSerializers.BYTE);

    private MobEffect effectToApply;
    private int duration;

    public InfestationDecalEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public InfestationDecalEntity(Level level, double x, double y, double z, Direction face, int colorHex, MobEffect effect, float radius, int lifeTimeTicks) {
        this(ModEntityTypes.INFESTATION_DECAL.get(), level);
        this.setPos(x, y, z);
        this.setFace(face);
        this.setColor(colorHex);
        this.setRadius(radius);
        this.effectToApply = effect;
        this.duration = lifeTimeTicks; // Custom lifetime
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0xFFFFFFFF);
        this.entityData.define(RADIUS, 1.0f);
        this.entityData.define(FACE, (byte) Direction.UP.ordinal()); // Default to Floor
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            // Lifetime Logic
            if (this.tickCount >= duration) {
                this.discard();
                return;
            }

            // Effect Logic (every 5 ticks)
            if (this.tickCount % 5 == 0 && effectToApply != null) {
                // Inflate box slightly to catch entities touching the surface
                List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2));
                for (LivingEntity victim : victims) {
                    if (!victim.hasEffect(effectToApply)) {
                        victim.addEffect(new MobEffectInstance(effectToApply, 200, 0));
                    }
                }
            }
        }
    }

    // --- GETTERS / SETTERS ---

    public void setFace(Direction face) {
        this.entityData.set(FACE, (byte) face.ordinal());
    }

    public Direction getFace() {
        return Direction.values()[this.entityData.get(FACE)];
    }

    public void setColor(int rgb) { this.entityData.set(COLOR, rgb); }
    public int getColor() { return this.entityData.get(COLOR); }

    public void setRadius(float r) { this.entityData.set(RADIUS, r); }
    public float getRadius() { return this.entityData.get(RADIUS); }

    // --- SAVE / LOAD ---

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.tickCount = nbt.getInt("Age");
        this.duration = nbt.getInt("Duration");
        this.setColor(nbt.getInt("Color"));
        this.setRadius(nbt.getFloat("Radius"));
        this.setFace(Direction.from3DDataValue(nbt.getByte("Face")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Age", this.tickCount);
        nbt.putInt("Duration", this.duration);
        nbt.putInt("Color", this.getColor());
        nbt.putFloat("Radius", this.getRadius());
        nbt.putByte("Face", (byte) this.getFace().ordinal());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}