package net.agusdropout.bloodyhell.entity.soul;



import net.agusdropout.bloodyhell.block.entity.custom.SanguiniteBloodHarvesterBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulSize;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;

public class BloodSoulEntity extends Entity {

    private static final EntityDataAccessor<Optional<BlockPos>> TARGET_POS =
            SynchedEntityData.defineId(BloodSoulEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> SOUL_TYPE =
            SynchedEntityData.defineId(BloodSoulEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SOUL_SIZE =
            SynchedEntityData.defineId(BloodSoulEntity.class, EntityDataSerializers.INT);

    // Slower speed for better visuals
    private static final double SPEED = 0.2;

    public BloodSoulEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public BloodSoulEntity(Level level, BlockPos target, BloodSoulType soulType, BloodSoulSize size) {
        this(ModEntityTypes.BLOOD_SOUL.get(), level);
        this.entityData.set(TARGET_POS, Optional.of(target));
        this.setSoulType(soulType);
        this.setSoulSize(size);
        this.setPos(this.getX(), this.getY() + 0.5, this.getZ());
    }

    @Override
    public void tick() {
        super.tick();

        BloodSoulSize size = getSoulSize();

        // 1. VISUALS
        if (level().isClientSide) {
            // Scale the spread of particles based on size
            float spread = 0.2f * size.scale;

            // Spawn particles via the Enum, passing the Size for particle scaling
            getSoulType().spawnParticles(level(), this.position(), this.random, size.scale);
            return;
        }

        // 2. MOVEMENT
        Optional<BlockPos> targetOpt = this.entityData.get(TARGET_POS);
        if (targetOpt.isEmpty()) {
            this.discard();
            return;
        }

        BlockPos target = targetOpt.get();
        Vec3 targetVec = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        Vec3 direction = targetVec.subtract(this.position());
        double distance = direction.length();

        if (distance < SPEED) {
            BlockEntity be = level().getBlockEntity(target);
            if (be instanceof SanguiniteBloodHarvesterBlockEntity harvester) {
                // Pass Type AND Amount to the harvester
                harvester.receiveSoul(this.getSoulType(), size.fluidAmount);
            }
            this.discard();
        } else {
            Vec3 move = direction.normalize().scale(SPEED);
            this.setPos(this.position().add(move));
            this.setBoundingBox(this.getBoundingBox().move(move));
        }
    }

    // --- DATA ACCESSORS ---

    public void setSoulType(BloodSoulType type) {
        this.entityData.set(SOUL_TYPE, type.ordinal());
    }

    public BloodSoulType getSoulType() {
        return BloodSoulType.values()[Math.abs(this.entityData.get(SOUL_TYPE)) % BloodSoulType.values().length];
    }

    public void setSoulSize(BloodSoulSize size) {
        this.entityData.set(SOUL_SIZE, size.ordinal());
    }

    public BloodSoulSize getSoulSize() {
        return BloodSoulSize.values()[Math.abs(this.entityData.get(SOUL_SIZE)) % BloodSoulSize.values().length];
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_POS, Optional.empty());
        this.entityData.define(SOUL_TYPE, 0);
        this.entityData.define(SOUL_SIZE, 0); // Default Small
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("TargetX")) {
            this.entityData.set(TARGET_POS, Optional.of(new BlockPos(
                    nbt.getInt("TargetX"), nbt.getInt("TargetY"), nbt.getInt("TargetZ"))));
        }
        this.setSoulType(BloodSoulType.values()[nbt.getInt("SoulType")]);
        this.setSoulSize(BloodSoulSize.values()[nbt.getInt("SoulSize")]);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        this.entityData.get(TARGET_POS).ifPresent(pos -> {
            nbt.putInt("TargetX", pos.getX());
            nbt.putInt("TargetY", pos.getY());
            nbt.putInt("TargetZ", pos.getZ());
        });
        nbt.putInt("SoulType", this.getSoulType().ordinal());
        nbt.putInt("SoulSize", this.getSoulSize().ordinal());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
