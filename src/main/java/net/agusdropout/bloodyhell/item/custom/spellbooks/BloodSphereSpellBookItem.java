package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodSphereEntity;
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

public class BloodSphereSpellBookItem extends BaseSpellBookItem<BloodSphereSpellBookItem> {

    private static final int COST = 10;
    private static final int COOLDOWN = 50;
    private static final int CHARGE_TIME = 30;

    public BloodSphereSpellBookItem(Properties properties) {
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
            int projectileCount = 1 + getProjectileAdditionalFromGems(gems);

            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(provider -> {
                if (provider.getCrimsonVeil() >= COST ) {
                    for (int i = 0 ; i < projectileCount; i++) {
                        int delay = i * 5;
                        BloodSphereEntity projectile = new BloodSphereEntity(level, player, 5.0f, delay, gems);
                        level.addFreshEntity(projectile);


                    }
                    provider.subCrimsomveil(COST);
                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(provider.getCrimsonVeil()), ((ServerPlayer) player));
                }
            });
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, 0.5f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        float progress = (float) tick / CHARGE_TIME;

        Vec3 look = player.getLookAngle();
        Vec3 center = player.getEyePosition().add(look.scale(1.2));

        double outerRadius = 3.0 * (1.0 - (progress * 0.5));
        int particleCount = 2 + (int) (progress * 4);

        for (int i = 0; i < particleCount; i++) {
            double theta = level.random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * level.random.nextDouble() - 1);

            double x = center.x + outerRadius * Math.sin(phi) * Math.cos(theta);
            double y = center.y + outerRadius * Math.sin(phi) * Math.sin(theta);
            double z = center.z + outerRadius * Math.cos(phi);

            Vec3 motion = center.subtract(x, y, z).normalize().scale(0.2 + (progress * 0.3));

            level.addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), x, y, z, motion.x, motion.y, motion.z);
        }

        if (tick > 5) {
            double coreSize = 0.5 * progress;
            ParticleHelper.spawnSphereVolume(level,
                    new MagicParticleOptions(new Vector3f(0.6f, 0.0f, 0.0f), 0.8f, false, 5),
                    center, coreSize, 2, new Vec3(0, 0, 0));
        }

        if (tick == CHARGE_TIME) {
            ParticleHelper.spawnHollowSphere(level,
                    ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    center, 0.8, 30, 0.05);

            level.addParticle(ModParticles.CHILL_FLAME_PARTICLE.get(), center.x, center.y, center.z, 0, 0, 0);
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {

        if (tick % 4 == 0 && tick < CHARGE_TIME) {
            float pitch = 0.8f + (tick / (float) CHARGE_TIME);
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.5f, pitch);

            if (tick % 10 == 0) {
                level.playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0f, 1.0f + progressToPitch(tick));
            }
        }
    }

    private float progressToPitch(int tick) {
        return (float) tick / CHARGE_TIME;
    }

    @Override
    public int getMinChargeTime() {
        return CHARGE_TIME;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN;
    }

    @Override
    public String getSpellBookId() {
        return "blood_sphere_spellbook";
    }
}