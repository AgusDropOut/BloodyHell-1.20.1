package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullPainThroneEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class RhnullGoldenThroneSpellBookItem extends BaseSpellBookItem<RhnullGoldenThroneSpellBookItem> {

    private static final int COST = 20;
    private static final int DEFAULT_PROJECTILE_COUNT = 1;

    public RhnullGoldenThroneSpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (!level.isClientSide) {
            List<Gem> gems = getGemsFromItemStack(itemStack);
            int projectileCount = DEFAULT_PROJECTILE_COUNT + getProjectileAdditionalFromGems(gems);
            RandomSource random = level.random;

            Vec3 lookDirection = player.getForward().normalize();

            for(int i = 0; i < projectileCount; i++) {
                double spreadX = projectileCount > 1 ? (random.nextDouble() - 0.5) * 4.0 : 0;
                double spreadZ = projectileCount > 1 ? (random.nextDouble() - 0.5) * 4.0 : 0;

                double spawnX = player.getX() + (lookDirection.x * 4.0) + spreadX;
                double spawnY = player.getY();
                double spawnZ = player.getZ() + (lookDirection.z * 4.0) + spreadZ;

                RhnullPainThroneEntity throne = new RhnullPainThroneEntity(
                        ModEntityTypes.RHNULL_PAIN_THRONE.get(),
                        level,
                        player,
                        spawnX,
                        spawnY,
                        spawnZ,
                        gems
                );

                level.addFreshEntity(throne);
            }
        }
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        if (!level.isClientSide) return;

        float progress = (float) tick / getMinChargeTime();
        Vec3 center = player.position().add(0, 0.1, 0);

        Vector3f c1 = SpellPalette.RHNULL.getColor(0);
        Vector3f c2 = SpellPalette.RHNULL.getColor(1);
        Vector3f blended = ParticleHelper.gradient3(progress, c2, c1, c2);

        if (tick % 3 == 0) {
            ParticleHelper.spawnRing(
                    level,
                    new MagicFloorParticleOptions(blended, 1.5f + progress, false, 35),
                    center,
                    1.0 + (progress * 2.0),
                    15 + (int)(progress * 15),
                    0.02
            );
        }

        if (tick % 5 == 0 && progress > 0.4f) {
            ParticleHelper.spawnCylinder(
                    level,
                    new SmallGlitterParticleOptions(SpellPalette.RHNULL.getRandomColor(), 0.3f, false, 45, true),
                    center,
                    2.0,
                    0.5 + (progress * 1.5),
                    12,
                    0.03
            );
        }

        if (tick == getMinChargeTime()) {
            ParticleHelper.spawnCrownSplash(
                    level,
                    new MagicParticleOptions(c1, 1.2f, false, 50, true),
                    center.add(0, 0.5, 0),
                    30,
                    2.5,
                    0.2,
                    0.1
            );
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        float progress = (float) tick / getMinChargeTime();

        if (tick % 10 == 0 && tick < getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.PLAYERS, 0.4f, 0.8f + (progress * 0.3f));
        }

        if (tick % 5 == 0 && tick < getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.3f, 1.2f - (progress * 0.2f));
        }

        if (tick == getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.2f, 0.8f);
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 0.6f);
        }
    }

    @Override
    public int getMinChargeTime() {
        return 20;
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public int getCrimsonCost() {
        return COST;
    }

    @Override
    public String getSpellBookId() {
        return "rhnull_golden_throne_spellbook";
    }
}