package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.entity.projectile.BloodSlashEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class BloodScratchSpellBookItem extends BaseSpellBookItem<BloodScratchSpellBookItem> {
    private static final int COST = 20;


    public BloodScratchSpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getCrimsonCost() {
        return COST;
    }


    @Override
    public void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (!level.isClientSide) {
            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {
                if (playerCrimsonVeil.getCrimsonVeil() >= 10) {

                    playerCrimsonVeil.subCrimsomveil(10);

                    float yaw = player.getYRot();
                    float pitch = player.getXRot();

                    double radians = Math.toRadians(-yaw);
                    double xDir = Math.sin(radians);
                    double zDir = Math.cos(radians);

                    double baseX = player.getX() + xDir * 1.5;
                    double baseY = player.getEyeY() - 0.4;
                    double baseZ = player.getZ() + zDir * 1.5;

                    double offsetRadians = Math.toRadians(-yaw + 90);
                    double offsetX = Math.sin(offsetRadians) * 1.2;
                    double offsetZ = Math.cos(offsetRadians) * 1.2;

                    double leftX = baseX - offsetX;
                    double leftZ = baseZ - offsetZ;

                    double rightX = baseX + offsetX;
                    double rightZ = baseZ + offsetZ;

                    BloodSlashEntity centerSlash = new BloodSlashEntity(level, baseX, baseY, baseZ, 10.0F, player, yaw, pitch);

                    float spreadAngle = 10.0f;

                    BloodSlashEntity rightSlash = new BloodSlashEntity(level, leftX, baseY, leftZ, 10.0F, player, yaw + spreadAngle, pitch);
                    BloodSlashEntity leftSlash = new BloodSlashEntity(level, rightX, baseY, rightZ, 10.0F, player, yaw - spreadAngle, pitch);

                    level.addFreshEntity(centerSlash);
                    level.addFreshEntity(leftSlash);
                    level.addFreshEntity(rightSlash);

                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(playerCrimsonVeil.getCrimsonVeil()), ((ServerPlayer) player));
                }
            });
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {

        float yaw = player.getYRot();
        double radians = Math.toRadians(-yaw);
        double xDir = Math.sin(radians);
        double zDir = Math.cos(radians);

        // Calculate the three "claw" points in front of the player
        double baseX = player.getX() + xDir * 1.2;
        double baseY = player.getEyeY() - 0.4;
        double baseZ = player.getZ() + zDir * 1.2;

        double offsetRadians = Math.toRadians(-yaw + 90);
        double offsetX = Math.sin(offsetRadians) * 1.2;
        double offsetZ = Math.cos(offsetRadians) * 1.2;

        // Visualizing the 3 scratch points before they spawn
        double[] xPoints = {baseX, baseX - offsetX, baseX + offsetX};
        double[] zPoints = {baseZ, baseZ - offsetZ, baseZ + offsetZ};

        // 1. Magic Sparkles (Charging up at the scratch locations)
        for (int i = 0; i < 3; i++) {
            if (level.random.nextFloat() < 0.3f) {
                level.addParticle(new MagicParticleOptions(
                                new Vector3f(1.0f, 0.0f, 0.0f),
                                0.4f, false, 10),
                        xPoints[i] + (level.random.nextDouble() - 0.5) * 0.5,
                        baseY + (level.random.nextDouble() - 0.5) * 0.5,
                        zPoints[i] + (level.random.nextDouble() - 0.5) * 0.5,
                        0, 0, 0);
            }
        }

        // 2. Pulsar Blood Particles (Emit from player center outwards to the claws)
        if (tick % 5 == 0) {
            level.addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    0, 0, 0);
        }

        // Burst effect right before cast
        if (tick == getMinChargeTime()) {
            for(int i=0; i<3; i++) {
                ParticleHelper.spawnCircle(level,  ModParticles.BLOOD_PULSE_PARTICLE.get(),player.position(), 1.5, 10);
            }
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        if (tick % 5 == 0 && tick < getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5f, 1.5f + (tick * 0.05f));
        }
    }

    @Override
    public int getMinChargeTime() {
        return 15;
    }

    @Override
    public int getCooldown() {
        return 30;
    }

    @Override
    public String getSpellBookId() {
        return "blood_scratch_spellbook";
    }
}