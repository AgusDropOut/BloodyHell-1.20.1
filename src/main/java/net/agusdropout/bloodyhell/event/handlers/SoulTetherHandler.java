package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.block.entity.custom.engine.RhnullBloodEngineBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.engine.SoulTetherEngineMode;
import net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid = "bloodyhell", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulTetherHandler {

    private static final int SALVATION_COST = 5000;

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {

            GlobalPos tetherPos = SoulTetherEngineMode.ACTIVE_TETHERS.get(player.getUUID());

            if (tetherPos != null && player.server != null) {
                ServerLevel tetherLevel = player.server.getLevel(tetherPos.dimension());

                if (tetherLevel != null && tetherLevel.getBlockEntity(tetherPos.pos()) instanceof RhnullBloodEngineBlockEntity engine) {

                    if (engine.getBloodTank().getFluidAmount() >= SALVATION_COST) {

                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth() / 2.0f);
                        player.removeAllEffects();
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

                        Vector3f bloomColor = new Vector3f(0.9f, 0.05f, 0.05f);
                        Vector3f glitterColor = new Vector3f(1.0f, 0.3f, 0.3f);

                        if (player.level() instanceof ServerLevel currentLevel) {
                            currentLevel.playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 1.0F, 0.8F);

                            currentLevel.sendParticles(new TinyBloomParticleOptions(bloomColor, 1.8f),
                                    player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.4D, 0.4D, 0.4D, 0.1D);
                            currentLevel.sendParticles(new GlitterParticleOptions(glitterColor, 1.5f, false, 60, true),
                                    player.getX(), player.getY() + 1.0, player.getZ(), 80, 0.8D, 0.8D, 0.8D, 0.15D);
                        }

                        player.teleportTo(tetherLevel, tetherPos.pos().getX() + 0.5, tetherPos.pos().getY() + 1.0, tetherPos.pos().getZ() + 1.5, player.getYRot(), player.getXRot());

                        tetherLevel.playSound(null, tetherPos.pos(), SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 1.0F, 0.6F);
                        tetherLevel.playSound(null, tetherPos.pos(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.5F, 1.2F);

                        tetherLevel.sendParticles(new TinyBloomParticleOptions(bloomColor, 2.5f),
                                tetherPos.pos().getX() + 0.5, tetherPos.pos().getY() + 2.5, tetherPos.pos().getZ() + 0.5,
                                60, 0.5D, 0.5D, 0.5D, 0.1D);

                        tetherLevel.sendParticles(new GlitterParticleOptions(glitterColor, 1.8f, false, 100, true),
                                tetherPos.pos().getX() + 0.5, tetherPos.pos().getY() + 2.5, tetherPos.pos().getZ() + 0.5,
                                150, 1.2D, 1.2D, 1.2D, 0.08D);

                        engine.getBloodTank().drain(SALVATION_COST, IFluidHandler.FluidAction.EXECUTE);
                        engine.clearInventory();
                        engine.setCraftingFinished(true);
                    }
                }
            }
        }
    }
}