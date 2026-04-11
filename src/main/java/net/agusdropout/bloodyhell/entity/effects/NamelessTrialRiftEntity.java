package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;

import net.agusdropout.bloodyhell.entity.custom.AbstractTrialRiftEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.CrawlingDelusionEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.agusdropout.bloodyhell.util.capability.InsightHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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

import java.util.List;

public class NamelessTrialRiftEntity extends AbstractTrialRiftEntity {

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
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false, false));
                }

                if (this.tickCount % 20 == 0) {
                    MobEffectInstance currentFrenzy = player.getEffect(ModEffects.FRENZY.get());
                    int currentAmp = currentFrenzy != null ? currentFrenzy.getAmplifier() : -1;
                    int newAmp = Math.min(99, currentAmp + 2);

                    player.addEffect(new MobEffectInstance(ModEffects.FRENZY.get(), 60, newAmp, false, false, true));
                }

                if (this.tickCount % 50 == 0 && this.random.nextFloat() < 0.7f) {

                    System.out.println("Spawning Delusion near player " + player.getName().getString());
                    this.spawnDelusionNear(player, serverLevel);
                }

            } else if (player == null || !player.isAlive()) {
                this.failRift();
            }
        }
    }

    private void spawnDelusionNear(Player player, ServerLevel level) {
        double angle = this.random.nextDouble() * Math.PI * 2;
        double distance = 10.0 + this.random.nextDouble() * 6.0;
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
        }
    }

    private void clearRemainingLamps() {
        if (this.getTargetPlayer() == null) return;

        AABB searchBox = this.getBoundingBox().inflate(200.0D);
        List<EchoOfTheNamelessEntity> lamps = this.level().getEntitiesOfClass(EchoOfTheNamelessEntity.class, searchBox);

        for (EchoOfTheNamelessEntity lamp : lamps) {
            if (this.getTargetPlayer().equals(lamp.getOwnerUUID())) {
                lamp.setEntityState(EchoOfTheNamelessEntity.STATE_BURROWING);
            }
        }
    }
}