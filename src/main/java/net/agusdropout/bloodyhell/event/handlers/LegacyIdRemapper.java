package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LegacyIdRemapper {
    public static final Map<String, String> ITEMS = Map.of(
            "blood_bow", "sanguinite_bow",
            "blood_arrow", "sanguinite_arrow",
            "blood_helmet", "sanguinite_helmet",
            "blood_chestplate", "sanguinite_chestplate",
            "blood_leggings", "sanguinite_leggings",
            "blood_boots", "sanguinite_boots");

    public static final Map<String, String> ENTITY_TYPES = Map.of(
            "blood_arrow_entity", "sanguinite_arrow_entity");

    private LegacyIdRemapper() {}

    @SubscribeEvent
    public static void remapRenamedIds(MissingMappingsEvent event) {
        remap(event, ForgeRegistries.Keys.ITEMS, ForgeRegistries.ITEMS, ITEMS);
        remap(event, ForgeRegistries.Keys.ENTITY_TYPES, ForgeRegistries.ENTITY_TYPES, ENTITY_TYPES);
    }

    private static <T> void remap(MissingMappingsEvent event, ResourceKey<? extends Registry<T>> key, IForgeRegistry<T> registry, Map<String, String> renames) {
        for (MissingMappingsEvent.Mapping<T> mapping : event.getMappings(key, BloodyHell.MODID)) {
            String renamed = renames.get(mapping.getKey().getPath());
            if (renamed == null) continue;
            ResourceLocation replacement = new ResourceLocation(BloodyHell.MODID, renamed);
            if (registry.containsKey(replacement)) {
                mapping.remap(registry.getValue(replacement));
            }
        }
    }
}
