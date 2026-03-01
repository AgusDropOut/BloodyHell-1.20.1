package net.agusdropout.bloodyhell.entity.soul;

import net.agusdropout.bloodyhell.block.entity.custom.SanguiniteBloodHarvesterBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.util.capability.CrimsonVeilHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class BloodSoulEntity extends Entity {

    private static final EntityDataAccessor<Optional<BlockPos>> TARGET_POS = SynchedEntityData.defineId(BloodSoulEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY_ID = SynchedEntityData.defineId(BloodSoulEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> SOUL_TYPE = SynchedEntityData.defineId(BloodSoulEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SOUL_SIZE = SynchedEntityData.defineId(BloodSoulEntity.class, EntityDataSerializers.INT);

    private static final double SPEED = 0.2;
    private UUID targetUUID;

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


    public BloodSoulEntity(Level level, LivingEntity targetEntity, BloodSoulType soulType, BloodSoulSize size) {
        this(ModEntityTypes.BLOOD_SOUL.get(), level);
        this.entityData.set(TARGET_ENTITY_ID, targetEntity.getId());
        this.targetUUID = targetEntity.getUUID();
        this.setSoulType(soulType);
        this.setSoulSize(size);
        this.setPos(this.getX(), this.getY() + 0.5, this.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_POS, Optional.empty());
        this.entityData.define(TARGET_ENTITY_ID, -1); // -1 means no entity target
        this.entityData.define(SOUL_TYPE, 0);
        this.entityData.define(SOUL_SIZE, 0);
    }

    @Override
    public void tick() {
        super.tick();
        BloodSoulSize size = getSoulSize();


        if (level().isClientSide) {
            float spread = 0.2f * size.scale;
            getSoulType().spawnParticles(level(), this.position(), this.random, size.scale);
            return;
        }


        int targetId = this.entityData.get(TARGET_ENTITY_ID);


        if (targetId != -1) {
            Entity targetEntity = level().getEntity(targetId);
            if (targetEntity instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                // Aim for their chest/center, not their toes
                Vec3 targetVec = livingTarget.position().add(0, livingTarget.getBbHeight() / 2.0, 0);
                moveTowardsTarget(targetVec, livingTarget, null);
            } else {
                this.discard(); // Target died or logged off
            }
        }

        else {
            Optional<BlockPos> targetOpt = this.entityData.get(TARGET_POS);
            if (targetOpt.isPresent()) {
                BlockPos target = targetOpt.get();
                Vec3 targetVec = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
                moveTowardsTarget(targetVec, null, target);
            } else {
                this.discard();
            }
        }
    }

    private void moveTowardsTarget(Vec3 targetVec, LivingEntity targetEntity, BlockPos targetBlock) {
        Vec3 direction = targetVec.subtract(this.position());
        double distance = direction.length();

        if (distance < SPEED) {

            BloodSoulSize size = getSoulSize();

            if (targetEntity instanceof Player player) {
                CrimsonVeilHelper.restore(player, 5 * size.ordinal() );

            } else if (targetBlock != null) {
                BlockEntity be = level().getBlockEntity(targetBlock);
                if (be instanceof SanguiniteBloodHarvesterBlockEntity harvester) {
                    harvester.receiveSoul(this.getSoulType(), size.fluidAmount);
                }
            }
            this.discard();
        } else {
            Vec3 move = direction.normalize().scale(SPEED);
            this.setPos(this.position().add(move));
            this.setBoundingBox(this.getBoundingBox().move(move));
        }
    }

    public void setSoulType(BloodSoulType type) { this.entityData.set(SOUL_TYPE, type.ordinal()); }
    public BloodSoulType getSoulType() { return BloodSoulType.values()[Math.abs(this.entityData.get(SOUL_TYPE)) % BloodSoulType.values().length]; }
    public void setSoulSize(BloodSoulSize size) { this.entityData.set(SOUL_SIZE, size.ordinal()); }
    public BloodSoulSize getSoulSize() { return BloodSoulSize.values()[Math.abs(this.entityData.get(SOUL_SIZE)) % BloodSoulSize.values().length]; }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("TargetX")) {
            this.entityData.set(TARGET_POS, Optional.of(new BlockPos(nbt.getInt("TargetX"), nbt.getInt("TargetY"), nbt.getInt("TargetZ"))));
        }
        if (nbt.hasUUID("TargetUUID")) {
            this.targetUUID = nbt.getUUID("TargetUUID");
        }
        this.setSoulType(BloodSoulType.values()[nbt.getInt("SoulType")]);
        this.setSoulSize(BloodSoulSize.values()[nbt.getInt("SoulSize")]);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        this.entityData.get(TARGET_POS).ifPresent(pos -> {
            nbt.putInt("TargetX", pos.getX()); nbt.putInt("TargetY", pos.getY()); nbt.putInt("TargetZ", pos.getZ());
        });
        if (this.targetUUID != null) {
            nbt.putUUID("TargetUUID", this.targetUUID);
        }
        nbt.putInt("SoulType", this.getSoulType().ordinal());
        nbt.putInt("SoulSize", this.getSoulSize().ordinal());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}