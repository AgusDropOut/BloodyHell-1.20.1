package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodFireSoulEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.SpellBookDefinition;
import net.agusdropout.bloodyhell.item.custom.base.SpellCastContext;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BloodFireSoulSpellBookItem extends BaseSpellBookItem {

    private static final float SPREAD_DEGREES = 15.0f;

    public BloodFireSoulSpellBookItem(Properties properties, SpellBookDefinition definition) {
        super(properties, definition);
    }

    @Override
    public void performSpell(SpellCastContext ctx) {
        if (!ctx.level().isClientSide) {
            List<Gem> gems = ctx.gems();

            int count = 1 + getProjectileAdditionalFromGems(gems);

            Vec3 look = ctx.player().getLookAngle();

            for (int i = 0; i < count; i++) {
                BloodFireSoulEntity soulProjectile = new BloodFireSoulEntity(
                        ModEntityTypes.BLOOD_FIRE_SOUL.get(),
                        ctx.level(),
                        ctx.player(),
                        gems
                );

                if (count > 1) {

                    float yawOffset = (i - (count - 1) / 2.0f) * SPREAD_DEGREES;

                    Vec3 spreadDir = look.yRot((float) Math.toRadians(-yawOffset));

                    soulProjectile.setDeltaMovement(spreadDir.scale(0.55));
                }

                ctx.spawn(soulProjectile);
            }
        }

        ctx.playSound(SoundEvents.GHAST_SHOOT, 1.0f, 1.2f);
        ctx.playSound(SoundEvents.BLAZE_SHOOT, 1.0f, 0.8f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        float progress = (float) tick / getMinChargeTime();
        double radius = 2.5 * (1.0 - progress);

        double speed = 0.3 + (progress * 0.4);
        double angle = (tick * speed) % (2 * Math.PI);

        for (int i = 0; i < 2; i++) {
            double currentAngle = angle + (i * Math.PI);
            double xOffset = radius * Math.cos(currentAngle);
            double zOffset = radius * Math.sin(currentAngle);
            double yOffset = 1.0 + (Math.sin(tick * 0.2) * 0.5);

            level.addParticle(ModParticles.BLOOD_SIGIL_PARTICLE.get(),
                    player.getX() + xOffset,
                    player.getY() + yOffset,
                    player.getZ() + zOffset,
                    (player.getX() - (player.getX() + xOffset)) * 0.1,
                    0.0,
                    (player.getZ() - (player.getZ() + zOffset)) * 0.1
            );
        }

        if (tick == getMinChargeTime()) {
            spawnParticleCircle(level, player, ModParticles.BLOOD_SIGIL_PARTICLE.get(), 1.0, 30);
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        if (tick % 4 == 0 && tick < getMinChargeTime()) {
            float pitch = 0.8f + (tick / (float)getMinChargeTime()) * 0.5f;
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.4f, pitch);
        }
    }

    protected void spawnParticleCircle(Level level, Player player, ParticleOptions particleData, double radius, int particleCount) {
        double y = player.getY() + 0.8;
        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            double vX = Math.cos(angle) * 0.1;
            double vZ = Math.sin(angle) * 0.1;

            level.addParticle(particleData,
                    player.getX(), y, player.getZ(),
                    vX, 0.0, vZ);
        }
    }

}