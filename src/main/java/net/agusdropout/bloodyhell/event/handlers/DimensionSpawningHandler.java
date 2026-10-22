package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.worldgen.dimension.ModDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DimensionSpawningHandler {

    @SubscribeEvent
    public static void onTraderSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof WanderingTrader || event.getEntity() instanceof TraderLlama) {

            if (event.getLevel().getLevel().dimension() == ModDimensions.SOUL_LEVEL_KEY) {
                    event.setSpawnCancelled(true);

            }
        }
    }
}