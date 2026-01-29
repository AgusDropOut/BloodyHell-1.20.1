package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.projectile.BloodFireSoulProjectile;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BloodFireSoulSpellBookItem extends BaseSpellBookItem<BloodFireSoulSpellBookItem> {

    public BloodFireSoulSpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (!level.isClientSide) {
            // The projectile's constructor handles the initial position and target selection
            BloodFireSoulProjectile soulProjectile = new BloodFireSoulProjectile(
                    ModEntityTypes.BLOOD_FIRE_SOUL.get(),
                    level,
                    player
            );
            level.addFreshEntity(soulProjectile);
        }

        // Release Sound: A sharp, ghostly release
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0f, 1.2f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        // Visual: Energy gathering TOWARDS the player (Suction effect)
        // Radius starts large and shrinks as charge completes
        float progress = (float) tick / getMinChargeTime();
        double radius = 2.5 * (1.0 - progress);

        // Spin speed increases as it gets closer
        double speed = 0.3 + (progress * 0.4);
        double angle = (tick * speed) % (2 * Math.PI);

        // Spawn 2 distinct streams of particles
        for (int i = 0; i < 2; i++) {
            double currentAngle = angle + (i * Math.PI); // Opposite sides

            double xOffset = radius * Math.cos(currentAngle);
            double zOffset = radius * Math.sin(currentAngle);

            // Height oscillation to make it look like a helix
            double yOffset = 1.0 + (Math.sin(tick * 0.2) * 0.5);

            // Use the "Chill Flame" or "Blood Sigil" particle
            level.addParticle(ModParticles.BLOOD_SIGIL_PARTICLE.get(),
                    player.getX() + xOffset,
                    player.getY() + yOffset,
                    player.getZ() + zOffset,
                    (player.getX() - (player.getX() + xOffset)) * 0.1, // Velocity towards player X
                    0.0,
                    (player.getZ() - (player.getZ() + zOffset)) * 0.1  // Velocity towards player Z
            );
        }

        // Burst visual when fully charged
        if (tick == getMinChargeTime()) {
            spawnParticleCircle(level, player, ModParticles.BLOOD_SIGIL_PARTICLE.get(), 1.0, 30);
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        // Eerie soul sound that rises in pitch
        if (tick % 4 == 0 && tick < getMinChargeTime()) {
            float pitch = 0.8f + (tick / (float)getMinChargeTime()) * 0.5f;
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.4f, pitch);
        }
    }

    protected void spawnParticleCircle(Level level, Player player, ParticleOptions particleData, double radius, int particleCount) {
        double y = player.getY() + 0.8; // Center mass
        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            // Add velocity outwards for the burst
            double vX = Math.cos(angle) * 0.1;
            double vZ = Math.sin(angle) * 0.1;

            level.addParticle(particleData,
                    player.getX(),
                    y,
                    player.getZ(),
                    vX, 0.0, vZ);
        }
    }

    @Override
    public int getMinChargeTime() {
        return 20; // 1 Second charge for a homing missile seems balanced
    }

    @Override
    public int getCooldown() {
        return 60; // 3 Seconds cooldown
    }

    @Override
    public String getSpellBookId() {
        return "bloodfire_soul_spellbook";
    }
}