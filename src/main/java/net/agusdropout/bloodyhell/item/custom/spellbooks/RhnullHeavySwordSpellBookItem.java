package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullHeavySwordEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
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

public class RhnullHeavySwordSpellBookItem extends BaseSpellBookItem<RhnullHeavySwordSpellBookItem> {

    private static final int COST = 60;
    private static final int DEFAULT_PROJECTILE_COUNT = 1;

    public RhnullHeavySwordSpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (!level.isClientSide) {
            List<Gem> gems = getGemsFromItemStack(itemStack);
            int projectileCount = DEFAULT_PROJECTILE_COUNT + getProjectileAdditionalFromGems(gems);
            RandomSource random = level.random;

            for(int i = 0; i < projectileCount; i++) {
                RhnullHeavySwordEntity sword = new RhnullHeavySwordEntity(
                        level,
                        player,
                        random.nextInt(0,20 * projectileCount),
                        gems
                );
                level.addFreshEntity(sword);
            }
        }
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        if (!level.isClientSide) return;

        float progress = (float) tick / getMinChargeTime();
        Vec3 center = player.position();

        Vector3f color1 = SpellPalette.RHNULL.getColor(0);
        Vector3f color2 = SpellPalette.RHNULL.getColor(1);
        Vector3f blended = ParticleHelper.gradient3(progress, color1, color2, SpellPalette.RHNULL.getColor(2));


        if (tick % 2 == 0) {
            ParticleHelper.spawnRing(
                    level,
                    new MagicParticleOptions(blended, 1.2f, false, 25, true),
                    center,
                    2.0 - progress,
                    10 + (int)(progress * 10),
                    -0.05
            );
        }


        if (tick % 3 == 0) {
            ParticleHelper.spawnRisingBurst(
                    level,
                    new GlitterParticleOptions(SpellPalette.RHNULL.getRandomColor(), 0.8f, false, 20, false),
                    center.add(0, 0.5, 0),
                    3 + (int)(progress * 5),
                    0.5,
                    0.02,
                    0.1 + (progress * 0.1)
            );
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        float progress = (float) tick / getMinChargeTime();


        if (tick % 5 == 0 && tick < getMinChargeTime()) {
            float pitch = 0.5f + (progress * 0.5f);
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.8f, pitch);
        }


        if (tick == getMinChargeTime() - 5) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.0f, 0.6f);
        }


        if (tick == getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.5f, 0.8f);
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_CLUSTER_STEP, SoundSource.PLAYERS, 1.5f, 0.5f);
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
        return "rhnull_heavy_sword_spellbook";
    }
}