package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodSlashEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

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
            List<Gem> gems = super.getGemsFromItemStack(itemStack);
            // Calculate total projectiles: Minimum 3 + gems
            int projectileCount = 3 + getProjectileAdditionalFromGems(gems);

            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {
                if (playerCrimsonVeil.getCrimsonVeil() >= 10) {
                    playerCrimsonVeil.subCrimsomveil(10);

                    float yaw = player.getYRot();
                    float pitch = player.getXRot();

                    // Calculate Base Center Position
                    double radians = Math.toRadians(-yaw);
                    double xDir = Math.sin(radians);
                    double zDir = Math.cos(radians);

                    double baseX = player.getX() + xDir * 1.5;
                    double baseY = player.getEyeY() - 0.4;
                    double baseZ = player.getZ() + zDir * 1.5;

                    // Calculate Perpendicular Vector (for left/right spacing)
                    // -yaw + 90 gives us the vector pointing to the "Left" (or Right depending on coordinate system)
                    double offsetRadians = Math.toRadians(-yaw + 90);
                    double perpX = Math.sin(offsetRadians);
                    double perpZ = Math.cos(offsetRadians);

                    float spreadDistance = 1.2f; // Distance between projectiles
                    float spreadAngle = 10.0f;   // Angle variation between projectiles

                    for (int i = 0; i < projectileCount; i++) {
                        // Multiplier centers the projectiles.
                        // e.g., for 3 projectiles: -1.0, 0.0, 1.0
                        // e.g., for 4 projectiles: -1.5, -0.5, 0.5, 1.5
                        float multiplier = i - (projectileCount - 1) / 2.0f;

                        // Calculate position offset
                        double currentOffsetX = perpX * (multiplier * spreadDistance);
                        double currentOffsetZ = perpZ * (multiplier * spreadDistance);

                        double spawnX = baseX /*+ currentOffsetX*/;
                        double spawnZ = baseZ  /*currentOffsetZ*/;

                        // Calculate angle offset (flips multiplier to fan out correctly)
                        // You might need to swap +/- depending on if you want them to converge or diverge
                        float spawnYaw = yaw - (multiplier * spreadAngle);

                        BloodSlashEntity slash = new BloodSlashEntity(level, spawnX, baseY, spawnZ, 10.0F, player, spawnYaw, pitch, gems);
                        level.addFreshEntity(slash);
                    }

                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(playerCrimsonVeil.getCrimsonVeil()), ((ServerPlayer) player));
                }
            });
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        // Retrieve gems to ensure particle count matches projectile count
        ItemStack itemStack = player.getMainHandItem(); // Assuming spell is cast from main hand
        List<Gem> gems = super.getGemsFromItemStack(itemStack);
        int projectileCount = 3 + getProjectileAdditionalFromGems(gems);

        float yaw = player.getYRot();
        double radians = Math.toRadians(-yaw);
        double xDir = Math.sin(radians);
        double zDir = Math.cos(radians);

        double baseX = player.getX() + xDir * 1.2;
        double baseY = player.getEyeY() - 0.4;
        double baseZ = player.getZ() + zDir * 1.2;

        double offsetRadians = Math.toRadians(-yaw + 90);
        double perpX = Math.sin(offsetRadians);
        double perpZ = Math.cos(offsetRadians);

        double spreadDistance = 1.2;

        // 1. Magic Sparkles (Charging up at ALL scratch locations)
        for (int i = 0; i < projectileCount; i++) {
            float multiplier = i - (projectileCount - 1) / 2.0f;

            double pX = baseX + (perpX * multiplier * spreadDistance);
            double pZ = baseZ + (perpZ * multiplier * spreadDistance);

            if (level.random.nextFloat() < 0.3f) {
                level.addParticle(new MagicParticleOptions(
                                new Vector3f(1.0f, 0.0f, 0.0f),
                                0.4f, false, 10),
                        pX + (level.random.nextDouble() - 0.5) * 0.5,
                        baseY + (level.random.nextDouble() - 0.5) * 0.5,
                        pZ + (level.random.nextDouble() - 0.5) * 0.5,
                        0, 0, 0);
            }

            // Burst effect right before cast (moved inside loop to happen at all spots)
            if (tick == getMinChargeTime()) {
                ParticleHelper.spawnCircle(level, ModParticles.BLOOD_PULSE_PARTICLE.get(), new Vec3(pX, baseY, pZ), 1.5, 10);
            }
        }

        // 2. Pulsar Blood Particles (Emit from player center outwards)
        if (tick % 5 == 0) {
            level.addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    0, 0, 0);
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