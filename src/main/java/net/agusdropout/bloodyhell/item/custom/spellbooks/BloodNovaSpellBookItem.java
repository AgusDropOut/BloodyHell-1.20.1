package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.projectile.spell.BloodNovaEntity;
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

public class BloodNovaSpellBookItem extends BaseSpellBookItem<BloodNovaSpellBookItem> {

    private static final double SPAWN_HEIGHT_OFFSET = 5.0;

    public BloodNovaSpellBookItem(Properties properties, SpellBookDefinition definition) {
        super(properties, definition);
    }

    @Override
    public void performSpell(SpellCastContext ctx) {
        if (!ctx.level().isClientSide) {

                    List<Gem> gems = ctx.gems();
                    float yaw = ctx.player().getYRot();
                    float pitch = ctx.player().getXRot();
                    double radians = Math.toRadians(-yaw);

                    double baseX = ctx.player().getX() + Math.sin(radians) * 1.0;
                    double baseY = ctx.player().getY() + 0.5;
                    double baseZ = ctx.player().getZ() + Math.cos(radians) * 1.0;

                    BloodNovaEntity projectile = new BloodNovaEntity(ctx.level(), baseX, baseY + SPAWN_HEIGHT_OFFSET, baseZ, 30.0F, ctx.player(), yaw, pitch, gems);
                    ctx.spawn(projectile);

        }

        ctx.playSound(SoundEvents.GENERIC_EXPLODE, 1.0f, 0.5f);
        ctx.playSound(SoundEvents.WITHER_SPAWN, 0.5f, 1.0f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        Vec3 playerPos = player.position();
        Vec3 targetPos = playerPos.add(0, SPAWN_HEIGHT_OFFSET, 0);
        float progress = (float) tick / getMinChargeTime();

        if (tick % 2 == 0) {
            ParticleHelper.spawnCylinder(level,
                    new MagicParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 0.5f, false, 10),
                    playerPos, 0.2, SPAWN_HEIGHT_OFFSET, 3 + (int)(progress * 5), 0.2);
        }

        double ringRadius = 3.0 * (1.0 - progress);
        ParticleHelper.spawnRing(level, ModParticles.BLOOD_PULSE_PARTICLE.get(),
                playerPos.add(0, 0.2, 0), ringRadius, 4, 0);

        int spiralCount = 2;
        for (int i = 0; i < spiralCount; i++) {
            double angle = (tick * 0.3) + (i * Math.PI);
            double y = (tick * 0.2) % SPAWN_HEIGHT_OFFSET;
            double r = 1.0;

            double x = player.getX() + r * Math.cos(angle);
            double z = player.getZ() + r * Math.sin(angle);

            level.addParticle(ModParticles.BLOOD_PARTICLES.get(),
                    x, player.getY() + y, z,
                    0, 0.1, 0);
        }

        if (tick == getMinChargeTime()) {

            ParticleHelper.spawnRing(level, ModParticles.BLOOD_PULSE_PARTICLE.get(), playerPos, 1.0, 40, 0.5);

            ParticleHelper.spawnExplosion(level, ModParticles.BLOOD_PULSE_PARTICLE.get(), targetPos, 50, 0.5, 1.0);
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {

        if (tick % 5 == 0 && tick < getMinChargeTime()) {
            float pitch = 0.5f + (tick / (float)getMinChargeTime());
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.8f, pitch);
        }
    }

}