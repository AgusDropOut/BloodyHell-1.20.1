package net.agusdropout.bloodyhell.entity.minions.base;

import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.entity.base.InsightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractMinionEntity extends Monster implements GeoEntity, OwnableEntity, InsightEntity {
    private static final int DEFAULT_SUMMONING_DURATION = 40;
    private static final int DEFAULT_UNSUMMON_DURATION = 40;

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(AbstractMinionEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> STRIPE_COLOR = SynchedEntityData.defineId(AbstractMinionEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> IS_SUMMONING = SynchedEntityData.defineId(AbstractMinionEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected int summonTicks = 0;

    protected AbstractMinionEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(STRIPE_COLOR, 0xFFFFFF);
        this.entityData.define(IS_SUMMONING, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("StripeColor", this.getStripeColor());
        tag.putBoolean("IsSummoning", this.getIsSummoning());
        tag.putInt("SummonTicks", this.summonTicks);
        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setStripeColor(tag.getInt("StripeColor"));
        this.setIsSummoning(tag.getBoolean("IsSummoning"));
        this.summonTicks = tag.getInt("SummonTicks");
        if (tag.hasUUID("Owner")) {
            this.setOwnerUUID(tag.getUUID("Owner"));
        }
    }

    protected int getSummonDuration() {
        return DEFAULT_SUMMONING_DURATION;
    }

    protected int getUnsummonDuration() {
        return DEFAULT_UNSUMMON_DURATION;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getIsSummoning()) {
            this.summonTicks++;
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.setYRot(this.yRotO);
            this.setXRot(this.xRotO);

            if (this.summonTicks >= this.getSummonDuration()) {
                this.setIsSummoning(false);
            }
        }
    }

    @Override
    public void aiStep() {
        if (this.getIsSummoning() || this.isDeadOrDying()) {
            return;
        }
        super.aiStep();
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= this.getUnsummonDuration()) {
            this.remove(RemovalReason.KILLED);
        }
    }

    public boolean getIsSummoning() {
        return this.entityData.get(IS_SUMMONING);
    }

    public void setIsSummoning(boolean summoning) {
        this.entityData.set(IS_SUMMONING, summoning);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();
        if (uuid == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        Entity entity = serverLevel.getEntity(uuid);
        if (entity instanceof LivingEntity living) {
            return living;
        }
        return serverLevel.getServer().getPlayerList().getPlayer(uuid);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.isRemoved()) {
            return;
        }

        LivingEntity owner = this.getOwner();

        if (owner != null && !owner.isRemoved()) {
            if (this.level().dimension() != owner.level().dimension()) {
                this.discard();
            } else if (this.distanceToSqr(owner) > 1024.0D) {
                this.teleportTo(owner.getX(), owner.getY(), owner.getZ());
            }
        }
    }

    public int getStripeColor() {
        return this.entityData.get(STRIPE_COLOR);
    }

    public void setStripeColor(int color) {
        this.entityData.set(STRIPE_COLOR, color);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity == this.getOwner()) {
            return true;
        }
        if (entity instanceof AbstractMinionEntity minion && minion.getOwner() == this.getOwner()) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public abstract String getMinionId();

    protected boolean hasSufficientClientInsight() {
        if (this.level().isClientSide) {
            return ClientInsightData.getPlayerInsight() >= this.getMinimumInsight();
        }
        return true;
    }

    @Override
    public void playAmbientSound() {
        if (this.hasSufficientClientInsight()) {
            super.playAmbientSound();
        }
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (this.hasSufficientClientInsight()) {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if (this.hasSufficientClientInsight()) {
            super.playStepSound(pos, blockIn);
        }
    }
}