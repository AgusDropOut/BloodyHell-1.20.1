package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class RhnullImpalersSpellBookItem extends BaseSpellBookItem<RhnullImpalersSpellBookItem> {

    private static final int COST = 30;
    private static final int DEFAULT_PROJECTILE_COUNT = 3;

    public RhnullImpalersSpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (!level.isClientSide) {
            List<Gem> gems = getGemsFromItemStack(itemStack);
            int projectileCount = DEFAULT_PROJECTILE_COUNT + getProjectileAdditionalFromGems(gems);

            for(int i = 0; i < projectileCount; i++) {
                RhnullImpalerEntity impaler = new RhnullImpalerEntity(
                        level,
                        player,
                        i,
                        projectileCount,gems
                );
                level.addFreshEntity(impaler);
            }
        }
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        if (!level.isClientSide) return;


        float progress = (float) tick / getMinChargeTime();


        Vec3 center = player.position().add(0, player.getBbHeight() / 2.0 + 0.5, 0);


        double radius = Math.max(0.5, 2.0 - (progress * 1.5));


        Vector3f color1 = SpellPalette.RHNULL.getColor(0);
        Vector3f color2 = SpellPalette.RHNULL.getColor(1);


        float colorRatio = (float) Math.abs(Math.sin(tick * 0.1));
        Vector3f blendedColor = ParticleHelper.gradient3(colorRatio, color1, color2, SpellPalette.RHNULL.getColor(2));


        if (tick % 2 == 0) {
            ParticleHelper.spawnRing(
                    level,
                    new MagicParticleOptions(blendedColor, 0.3f + (progress * 0.3f), false, 20 + (int)(progress * 10), true),
                    center,
                    radius,
                    12 + (int)(progress * 8),
                    -0.05
            );
        }


        if (tick % 4 == 0) {
            ParticleHelper.spawnRisingBurst(
                    level,
                    new GlitterParticleOptions(SpellPalette.RHNULL.getRandomColor(), 0.2f, false, 30, false),
                    center,
                    5 + (int)(progress * 10),
                    radius,
                    0.1,
                    0.2 + (progress * 0.2)
            );
        }


        if (progress > 0.7f && tick % 2 == 0) {
            ParticleHelper.spawnSphereVolume(
                    level,
                    new SmallGlitterParticleOptions(SpellPalette.RHNULL.getColor(0), 0.3f, true, 15, true),
                    center,
                    0.8,
                    8,
                    new Vec3(0, 0.05, 0)
            );
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        float progress = (float) tick / getMinChargeTime();


        if (tick % 5 == 0 && tick < getMinChargeTime()) {
            float pitch = 0.5f + progress;
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.5f, pitch);
        }


        if (tick == getMinChargeTime() - 10) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0f, 1.2f);
        }

        if (tick == getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_CLUSTER_STEP, SoundSource.PLAYERS, 1.5f, 1.5f);
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
        return "rhnull_impalers_spellbook";
    }
}