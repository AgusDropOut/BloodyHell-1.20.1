package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.spellbooks.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class SpellBookItems {
    public static final RegistryObject<Item> BLOOD_FIRE_METEOR_SPELLBOOK = ITEMS.register("bloodfire_meteor_spellbook", () -> new BloodFireMeteorSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_FIRE_COLUMM_SPELLBOOK = ITEMS.register("bloodfire_column_spellbook", () -> new BloodFireColumnSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_FIRE_SOUL_SPELLBOOK = ITEMS.register("bloodfire_soul_spellbook", () -> new BloodFireSoulSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_SCRATCH_SPELLBOOK = ITEMS.register("blood_scratch_spellbook", () -> new BloodScratchSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_SPHERE_SPELLBOOK = ITEMS.register("blood_sphere_spellbook", () -> new BloodSphereSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_NOVA_SPELLBOOK = ITEMS.register("blood_nova_spellbook", () -> new BloodNovaSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_DAGGERSRAIN_SPELLBOOK = ITEMS.register("blood_daggersrain_spellbook", () -> new BloodDaggersRainSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_IMPALERS_SPELLBOOK = ITEMS.register("rhnull_impalers_spellbook", () -> new RhnullImpalersSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_HEAVY_SWORD_SPELLBOOK = ITEMS.register("rhnull_heavy_sword_spellbook", () -> new RhnullHeavySwordSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_GOLDEN_THRONE_SPELLBOOK = ITEMS.register("rhnull_golden_throne_spellbook", () -> new RhnullGoldenThroneSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_ORB_EMITTER_SPELLBOOK = ITEMS.register("rhnull_orb_emitter_spellbook", () -> new RhnullOrbEmitterSpellBookItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> THE_OMNITOME = ITEMS.register("the_omnitome",
            () -> new OmnitomeItem(new Item.Properties().stacksTo(1).fireResistant()));

    private SpellBookItems() {}

    public static void init() {}
}
