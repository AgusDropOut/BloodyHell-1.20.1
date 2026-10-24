package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID)
public class LegacyIdRemapper {
    public static final Map<String, String> ITEMS = Map.ofEntries(
            Map.entry("sanguinite_pipe", "sanguinite_pipe_item"),
            Map.entry("rhnull_pipe", "rhnull_pipe_item"),
            Map.entry("sanguinite_condenser", "sanguinite_condenser_item"),
            Map.entry("rhnull_condenser", "rhnull_condenser_item"),
            Map.entry("sanguinite_blood_harvester", "sanguinite_blood_harvester_item"),
            Map.entry("unknown_portal_block", "unknown_portal_item"),
            Map.entry("seliora_resting_block", "seliora_resting_item"),
            Map.entry("blood_gem_sprout", "blood_gem_sprout_seed"),
            Map.entry("blood_lily_block", "blood_lily"),
            Map.entry("ancient_torch_block", "ancient_torch")
    );

    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        for (MissingMappingsEvent.Mapping<Item> mapping : event.getMappings(Registries.ITEM, BloodyHell.MODID)) {
            String replacement = ITEMS.get(mapping.getKey().getPath());
            if (replacement == null) continue;
            ResourceLocation target = new ResourceLocation(BloodyHell.MODID, replacement);
            if (ForgeRegistries.ITEMS.containsKey(target)) {
                mapping.remap(ForgeRegistries.ITEMS.getValue(target));
            }
        }
    }
}
