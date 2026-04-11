package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.effects.BlackHoleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractTrialRiftEntity extends BlackHoleEntity {

    private static final EntityDataAccessor<Optional<UUID>> TARGET_PLAYER = SynchedEntityData.defineId(AbstractTrialRiftEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public AbstractTrialRiftEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setMaxAge(6000);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_PLAYER, Optional.empty());
    }

    public void setTargetPlayer(UUID uuid) {
        this.entityData.set(TARGET_PLAYER, Optional.ofNullable(uuid));
    }

    public UUID getTargetPlayer() {
        return this.entityData.get(TARGET_PLAYER).orElse(null);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.tickCount >= 6000) {
            this.failRift();
            return;
        }
        super.tick();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TargetPlayer")) {
            this.setTargetPlayer(tag.getUUID("TargetPlayer"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.getTargetPlayer() != null) {
            tag.putUUID("TargetPlayer", this.getTargetPlayer());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        UUID targetId = this.getTargetPlayer();

        if (targetId != null && !targetId.equals(player.getUUID())) {
            return InteractionResult.FAIL;
        }

        if (!this.level().isClientSide()) {
            this.closeRift(player);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    protected void closeRift(Player player) {
        this.playSound(SoundEvents.GLASS_BREAK, 1.0F, 0.5F);
        this.playSound(SoundEvents.BEACON_DEACTIVATE, 2.0F, 0.8F);
        this.onRiftSuccess(player);
        this.discard();
    }

    protected void failRift() {
        this.onRiftFail();
        this.discard();
    }

    protected abstract void onRiftSuccess(Player player);
    protected abstract void onRiftFail();
}