package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.entity.custom.AbstractTrialRiftEntity;
import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class UnknownLanternRiftEntity extends AbstractTrialRiftEntity {

    private UUID lanternOwnerId;

    public UnknownLanternRiftEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public void setLanternOwner(UUID uuid) {
        this.lanternOwnerId = uuid;
    }

    public UUID getLanternOwner() {
        return this.lanternOwnerId;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 50 == 0 && !this.level().isClientSide()) {
            Entity owner = this.level() instanceof ServerLevel serverLevel ? serverLevel.getEntity(this.lanternOwnerId) : null;
            if (owner == null) {
                this.failRift();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("LanternOwner")) {
            this.lanternOwnerId = tag.getUUID("LanternOwner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.lanternOwnerId != null) {
            tag.putUUID("LanternOwner", this.lanternOwnerId);
        }
    }

    @Override
    protected void onRiftSuccess(Player player) {
        if (this.lanternOwnerId != null && this.level() instanceof ServerLevel serverLevel) {
            Entity owner = serverLevel.getEntity(this.lanternOwnerId);
            if (owner instanceof UnknownLanternEntity lantern) {
                lantern.success(player);
            }
        }
    }

    @Override
    protected void onRiftFail() {
        if (this.lanternOwnerId != null && this.level() instanceof ServerLevel serverLevel) {
            Entity owner = serverLevel.getEntity(this.lanternOwnerId);
            if (owner instanceof UnknownLanternEntity lantern) {
                lantern.fail();
            }
        }
    }
}