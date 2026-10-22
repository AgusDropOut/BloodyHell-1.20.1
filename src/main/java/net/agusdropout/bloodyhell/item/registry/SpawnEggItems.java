package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class SpawnEggItems {
    public static final RegistryObject<Item> BLOODTHIRSTYBEAST_SPAWN_EGG = ITEMS.register("bloodthirstybeast_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLOODTHIRSTYBEAST,0x400303,0x00FFA0,
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_SEEKER_SPAWN_EGG = ITEMS.register("bloodseeker_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLOOD_SEEKER,0x660000 ,0xFFDD00,
            new Item.Properties()));
    public static final RegistryObject<Item> EYESHELLSNAIL_SPAWN_EGG = ITEMS.register("eyeshellsnail_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.EYESHELL_SNAIL,0xFF0000  ,0xFFB400 ,
            new Item.Properties()));
    public static final RegistryObject<Item> CRIMSON_RAVEN_SPAWN_EGG = ITEMS.register("crimsonraven_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.CRIMSON_RAVEN,0xFF3434 ,0xFCFF00,
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_PIG_SPAWN_EGG = ITEMS.register("bloodpig_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLOODPIG,0x000000 ,0xDA0000,
            new Item.Properties()));
    public static final RegistryObject<Item> SCARLETSPECKLED_FISH_SPAWN_EGG = ITEMS.register("scarletspeckledfish_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.SCARLETSPECKLED_FISH,0xFF3434 ,0xFCFF00,
            new Item.Properties()));
    public static final RegistryObject<Item> OMEN_GAZER_ENTITY_SPAWN_EGG = ITEMS.register("omen_gazer_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.OMEN_GAZER_ENTITY,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> VEINRAVER_ENTITY_SPAWN_EGG = ITEMS.register("veinraver_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.VEINRAVER_ENTITY,0xfc2403 ,0xfcd303,
            new Item.Properties()));
    public static final RegistryObject<Item> OFFSPRING_OF_THE_UNKNOWN_SPAWN_EGG = ITEMS.register("offspring_of_the_unknown_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMOUS_MALFORMATION_SPAWN_EGG = ITEMS.register("blasphemous_malformation_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLASPHEMOUS_MALFORMATION,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> SELIORA_SPAWN_EGG = ITEMS.register("seliora_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.SELIORA,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> HORNED_WORM_SPAWN_EGG = ITEMS.register("horned_worm_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.HORNED_WORM,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> VEIL_STALKER_SPAWN_EGG = ITEMS.register("veil_stalker_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.VEIL_STALKER,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> CYCLOPS_ENTITY_SPAWN_EGG = ITEMS.register("cyclops_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.CYCLOPS_ENTITY,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> GRAVE_WALKER_SPAWN_EGG = ITEMS.register("grave_walker_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.GRAVE_WALKER_ENTITY,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> CINDER_ACOLYTE_SPAWN_EGG = ITEMS.register("cinder_acolyte_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.CINDER_ACOLYTE,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> FAILED_REMNANT_SPAWN_EGG = ITEMS.register("failed_remnant_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.FAILED_REMNANT,0x000000 ,0xd4a600,
            new Item.Properties()));

    private SpawnEggItems() {}

    public static void init() {}
}
