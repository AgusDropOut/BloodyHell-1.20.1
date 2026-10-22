package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.AbstractTrialRiftEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.CrawlingDelusionEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.agusdropout.bloodyhell.util.capability.InsightHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NamelessTrialRiftEntity extends AbstractTrialRiftEntity {

    private final List<UUID> spawnedDelusions = new ArrayList<>();

    public NamelessTrialRiftEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.getTargetPlayer() != null) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            Player player = serverLevel.getPlayerByUUID(this.getTargetPlayer());

            if (player != null && player.isAlive()) {
                if (this.tickCount % 40 == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, false, false));
                }

                if (this.tickCount % 20 == 0) {
                    if (!this.isPlayerNearSafeLamp(player)) {
                        MobEffectInstance currentFrenzy = player.getEffect(ModEffects.FRENZY.get());
                        int currentAmp = currentFrenzy != null ? currentFrenzy.getAmplifier() : -1;
                        int newAmp = Math.min(99, currentAmp + 2);

                        player.addEffect(new MobEffectInstance(ModEffects.FRENZY.get(), 60, newAmp, false, false, true));
                    }
                }

                if (this.tickCount % 40 == 0) {

                    int swarmSize = 1 + this.random.nextInt(2);
                    for (int i = 0; i < swarmSize; i++) {
                        this.spawnDelusionNear(player, serverLevel);
                    }
                }
            } else if (player == null || !player.isAlive()) {
                this.failRift();
            }
        }
    }

    private boolean isPlayerNearSafeLamp(Player player) {
        AABB searchBox = player.getBoundingBox().inflate(EchoOfTheNamelessEntity.REPEALING_LAMP_RADIUS);
        List<EchoOfTheNamelessEntity> nearbyLamps = this.level().getEntitiesOfClass(EchoOfTheNamelessEntity.class, searchBox);

        for (EchoOfTheNamelessEntity lamp : nearbyLamps) {
            if (lamp.getEnergy() > 0.0F && lamp.getEntityState() == EchoOfTheNamelessEntity.STATE_IDLE) {
                return true;
            }
        }
        return false;
    }

    private void spawnDelusionNear(Player player, ServerLevel level) {
        double angle = this.random.nextDouble() * Math.PI * 2;

        double distance = 4.0 + this.random.nextDouble() * 4.0;

        double spawnX = player.getX() + Math.cos(angle) * distance;
        double spawnZ = player.getZ() + Math.sin(angle) * distance;
        int spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) spawnX, (int) spawnZ);

        BlockPos spawnPos = new BlockPos((int) spawnX, spawnY, (int) spawnZ);

        if (level.getBlockState(spawnPos).isAir()) {
            CrawlingDelusionEntity delusion = ModEntityTypes.CRAWLING_DELUSION.get().create(level);
            if (delusion != null) {
                delusion.setPos(spawnX, spawnY, spawnZ);
                delusion.setLockedTarget(player.getUUID());
                delusion.setTarget(player);
                level.addFreshEntity(delusion);

                this.spawnedDelusions.add(delusion.getUUID());
            }
        }
    }

    @Override
    protected void onRiftSuccess(Player player) {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            player.removeEffect(ModEffects.FRENZY.get());
            player.removeEffect(MobEffects.DARKNESS);

            if (player instanceof ServerPlayer serverPlayer) {
                InsightHelper.addInsight(serverPlayer, 15);
            }

            this.level().playSound(null, this.blockPosition(), SoundEvents.AMETHYST_CLUSTER_BREAK, this.getSoundSource(), 2.0F, 1.0F);
            this.level().playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.5F, 1.2F);

            serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 0.5, this.getZ(), 40, 0.5, 0.5, 0.5, 0.1);

            this.clearRemainingLamps();
            this.clearSpawnedDelusions(serverLevel);
        }
    }

    @Override
    protected void onRiftFail() {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            if (this.getTargetPlayer() != null) {
                Entity target = serverLevel.getEntity(this.getTargetPlayer());
                if (target instanceof ServerPlayer serverPlayer) {
                    InsightHelper.subInsight(serverPlayer, 10);
                    serverPlayer.addEffect(new MobEffectInstance(ModEffects.FRENZY.get(), 400, 99, false, false, true));
                }
            }

            this.level().playSound(null, this.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, this.getSoundSource(), 2.0F, 0.5F);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 50, 0.5, 0.5, 0.5, 0.2);

            this.clearRemainingLamps();
            this.clearSpawnedDelusions(serverLevel);
        }
    }

    private void clearRemainingLamps() {
        if (this.getTargetPlayer() == null) return;

        AABB searchBox = this.getBoundingBox().inflate(1000.0D);
        List<EchoOfTheNamelessEntity> lamps = this.level().getEntitiesOfClass(EchoOfTheNamelessEntity.class, searchBox);

        for (EchoOfTheNamelessEntity lamp : lamps) {
            if (this.getTargetPlayer().equals(lamp.getOwnerUUID())) {
                lamp.setEntityState(EchoOfTheNamelessEntity.STATE_BURROWING);
            }
        }
    }

    private void clearSpawnedDelusions(ServerLevel serverLevel) {
        for (UUID uuid : this.spawnedDelusions) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof CrawlingDelusionEntity delusion) {
                delusion.discard();
            }
        }
        this.spawnedDelusions.clear();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag delusionList = new ListTag();
        for (UUID uuid : this.spawnedDelusions) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putUUID("UUID", uuid);
            delusionList.add(uuidTag);
        }
        tag.put("SpawnedDelusions", delusionList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SpawnedDelusions", 9)) {
            ListTag delusionList = tag.getList("SpawnedDelusions", 10);
            this.spawnedDelusions.clear();
            for (int i = 0; i < delusionList.size(); i++) {
                CompoundTag uuidTag = delusionList.getCompound(i);
                if (uuidTag.hasUUID("UUID")) {
                    this.spawnedDelusions.add(uuidTag.getUUID("UUID"));
                }
            }
        }
    }
}