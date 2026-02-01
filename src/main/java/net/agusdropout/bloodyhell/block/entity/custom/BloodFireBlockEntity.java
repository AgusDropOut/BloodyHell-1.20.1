package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities; // You need to create this registry
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class BloodFireBlockEntity extends BlockEntity {
    @Nullable
    private UUID ownerUUID;

    public BloodFireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_FIRE_BLOCK_ENTITY.get(), pos, state);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.setChanged(); // Mark for saving
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isSafe(Entity entity) {
        if (ownerUUID == null) return false;

        // 1. Check if the entity IS the owner
        if (entity.getUUID().equals(ownerUUID)) return true;

        // 2. Check Alliance (Team/Pet logic)
        if (this.level instanceof ServerLevel serverLevel) {
            Entity owner = serverLevel.getEntity(ownerUUID);
            if (owner != null) {
                return entity.isAlliedTo(owner) || owner.isAlliedTo(entity);
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }
}