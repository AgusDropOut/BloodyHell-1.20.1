package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.projectile.spell.BloodSlashEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.SpellBookDefinition;
import net.agusdropout.bloodyhell.item.custom.base.SpellCastContext;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class BloodScratchSpellBookItem extends BaseSpellBookItem<BloodScratchSpellBookItem> {

    public BloodScratchSpellBookItem(Properties properties, SpellBookDefinition definition) {
        super(properties, definition);
    }

    @Override
    public void performSpell(SpellCastContext ctx) {
        if (!ctx.level().isClientSide) {
            List<Gem> gems = ctx.gems();

            int projectileCount = 3 + getProjectileAdditionalFromGems(gems);

            float yaw = ctx.player().getYRot();
            float pitch = ctx.player().getXRot();

            double radians = Math.toRadians(-yaw);
            double xDir = Math.sin(radians);
            double zDir = Math.cos(radians);

            double baseX = ctx.player().getX() + xDir * 1.5;
            double baseY = ctx.player().getEyeY() - 0.4;
            double baseZ = ctx.player().getZ() + zDir * 1.5;

            float spreadAngle = 10.0f;

            for (int i = 0; i < projectileCount; i++) {
                float multiplier = i - (projectileCount - 1) / 2.0f;
                float spawnYaw = yaw - (multiplier * spreadAngle);

                BloodSlashEntity slash = new BloodSlashEntity(ctx.level(), baseX, baseY, baseZ, 10.0F, ctx.player(), spawnYaw, pitch, gems);
                ctx.spawn(slash);
            }
        }

        ctx.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0f, 1.0f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {

        ItemStack itemStack = player.getMainHandItem();
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

            if (tick == getMinChargeTime()) {
                ParticleHelper.spawnCircle(level, ModParticles.BLOOD_PULSE_PARTICLE.get(), new Vec3(pX, baseY, pZ), 1.5, 10);
            }
        }

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

}