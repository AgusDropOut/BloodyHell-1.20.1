package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullOrbEmitter;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.core.particles.ParticleOptions;
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

public class RhnullOrbEmitterSpellBookItem extends BaseSpellBookItem<RhnullOrbEmitterSpellBookItem> {

    private static final int COST = 20;
    private static final int DEFAULT_PROJECTILE_COUNT = 1;

    public RhnullOrbEmitterSpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (!level.isClientSide) {

            List<Gem> gems = getGemsFromItemStack(itemStack);


            Vec3 lookDirection = player.getForward().normalize();



                double spawnX = player.getX() + (lookDirection.x * 4.0) ;
                double spawnY = player.getY();
                double spawnZ = player.getZ() + (lookDirection.z * 4.0) ;

                RhnullOrbEmitter orb = new RhnullOrbEmitter(
                        ModEntityTypes.RHNULL_ORB_EMITTER_ENTITY.get(),
                        level,
                        player,
                        spawnX,
                        spawnY,
                        spawnZ,
                        gems
                );

                level.addFreshEntity(orb);
            }
        }



    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        if (!level.isClientSide) return;

        float progress = (float) tick / getMinChargeTime();
        Vec3 center = player.position().add(0, player.getBbHeight() / 2.0, 0);

        Vector3f c1 = SpellPalette.RHNULL.getColor(0);
        Vector3f c2 = SpellPalette.RHNULL.getColor(1);
        Vector3f c3 = SpellPalette.RHNULL.getColor(2);

        Vector3f blended = ParticleHelper.gradient3(progress, c1, c2, c3);

        if (tick % 2 == 0) {
            ParticleOptions magic = new MagicParticleOptions(blended, 0.3f + (progress * 0.2f), false, 15, true);
            ParticleHelper.spawnSpiralSpray(
                    level,
                    magic,
                    player.position(),
                    3 + (int)(progress * 3),
                    1.0 - (progress * 0.5),
                    0.05,
                    0.3 + (progress * 0.2)
            );
        }

        if (tick % 4 == 0 && progress > 0.3f) {
            ParticleOptions glitter = new GlitterParticleOptions(c1, 0.2f, false, 20, true);
            ParticleHelper.spawnHollowSphere(
                    level,
                    glitter,
                    center,
                    1.2 - (progress * 0.5),
                    8 + (int)(progress * 10),
                    -0.02 - (progress * 0.02)
            );
        }

        if (tick == getMinChargeTime()) {
            ParticleHelper.spawnBurst(
                    level,
                    new MagicParticleOptions(c1, 0.8f, false, 30, true),
                    center,
                    20,
                    0.15
            );
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        float progress = (float) tick / getMinChargeTime();

        if (tick % 4 == 0 && tick < getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 0.4f, 1.0f + progress);
        }

        if (tick % 10 == 0 && tick < getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.6f, 0.5f + progress);
        }

        if (tick == getMinChargeTime()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
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
        return "rhnull_orb_emitter_spellbook";
    }
}