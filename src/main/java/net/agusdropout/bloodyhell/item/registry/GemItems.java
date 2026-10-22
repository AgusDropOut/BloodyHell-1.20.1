package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.*;
import net.agusdropout.bloodyhell.item.custom.base.BasePowerGemItem;
import net.agusdropout.bloodyhell.item.custom.spellbooks.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class GemItems {
    public static final RegistryObject<Item> PURE_BLOOD_GEM = ITEMS.register("pure_blood_gem", () -> new BasePowerGemItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CITRINE_BLOOD_GEM = ITEMS.register("citrine_blood_gem", () -> new BasePowerGemItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TANZARINE_BLOOD_GEM = ITEMS.register("tanzarine_blood_gem", () -> new BasePowerGemItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AVENTURINE_BLOOD_GEM = ITEMS.register("aventurine_blood_gem", () -> new BasePowerGemItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AMULET_OF_ANCESTRAL_BLOOD = ITEMS.register("amulet_of_ancestral_blood", () -> new AmuletOfAncestralBlood(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_GEM_FRAME = ITEMS.register("sanguinite_gem_frame",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SANGUINITE_GREAT_GEM_FRAME = ITEMS.register("sanguinite_great_gem_frame",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_GEM_FRAME = ITEMS.register("rhnull_gem_frame",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_GREAT_GEM_FRAME = ITEMS.register("rhnull_great_gem_frame",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_GEM = ITEMS.register("ancient_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ANCIENT_RHNULL_GEM = ITEMS.register("ancient_rhnull_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> GREAT_AMULET_OF_ANCESTRAL_BLOOD = ITEMS.register("great_amulet_of_ancestral_blood", () -> new GreatAmuletOfAncestralBlood(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GREAT_ANCIENT_GEM = ITEMS.register("great_ancient_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> GREAT_ANCIENT_RHNULL_GEM = ITEMS.register("great_ancient_rhnull_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> GREAT_ANCIENT_BLASPHEMOUS_GEM = ITEMS.register("great_ancient_blasphemous_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ANCIENT_BLASPHEMOUS_GEM = ITEMS.register("ancient_blasphemous_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> CRIMSON_WARD_RING = ITEMS.register("crimson_ward_ring", () -> new CrimsonWardRing(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_RING = ITEMS.register("blasphemous_ring", () -> new BlasphemousRing(
            new Item.Properties().stacksTo(1)));

    private GemItems() {}

    public static void init() {}
}
