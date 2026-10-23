package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.base.SpellAffinity;
import net.agusdropout.bloodyhell.item.custom.base.SpellBookDefinition;
import net.agusdropout.bloodyhell.item.custom.base.StaffCastType;
import net.agusdropout.bloodyhell.item.custom.spellbooks.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

import static net.agusdropout.bloodyhell.item.ModItems.*;
import static net.agusdropout.bloodyhell.item.custom.base.SpellAffinity.*;
import static net.agusdropout.bloodyhell.item.custom.base.StaffCastType.*;

public final class SpellBookItems {
    public static final RegistryObject<Item> BLOOD_FIRE_METEOR_SPELLBOOK = book(
            "bloodfire_meteor_spellbook", "blood_fire_meteor_icon", THROW, BLOOD_FIRE, 50, 20, 40, BloodFireMeteorSpellBookItem::new);
    public static final RegistryObject<Item> BLOOD_FIRE_COLUMM_SPELLBOOK = book(
            "bloodfire_column_spellbook", "blood_fire_column_icon", RISE, BLOOD_FIRE, 20, 20, 40, BloodFireColumnSpellBookItem::new);
    public static final RegistryObject<Item> BLOOD_FIRE_SOUL_SPELLBOOK = book(
            "bloodfire_soul_spellbook", "blood_fire_soul_icon", THROW, BLOOD_FIRE, 20, 20, 60, BloodFireSoulSpellBookItem::new);
    public static final RegistryObject<Item> BLOOD_SCRATCH_SPELLBOOK = book(
            "blood_scratch_spellbook", "blood_scratch_icon", SEQUENTIAL, BLOOD, 5, 5, 5, BloodScratchSpellBookItem::new);
    public static final RegistryObject<Item> BLOOD_SPHERE_SPELLBOOK = book(
            "blood_sphere_spellbook", "blood_sphere_icon", SEQUENTIAL, BLOOD, 10, 5, 5, BloodSphereSpellBookItem::new);
    public static final RegistryObject<Item> BLOOD_NOVA_SPELLBOOK = book(
            "blood_nova_spellbook", "blood_nova_icon", CIRCLE, BLOOD, 70, 30, 50, BloodNovaSpellBookItem::new);
    public static final RegistryObject<Item> BLOOD_DAGGERSRAIN_SPELLBOOK = book(
            "blood_daggersrain_spellbook", "daggers_rain_icon", CIRCLE, BLOOD, 40, 40, 200, BloodDaggersRainSpellBookItem::new);
    public static final RegistryObject<Item> RHNULL_IMPALERS_SPELLBOOK = book(
            "rhnull_impalers_spellbook", "rhnull_impalers_icon", THROW, RHNULL, 30, 20, 40, RhnullImpalersSpellBookItem::new);
    public static final RegistryObject<Item> RHNULL_HEAVY_SWORD_SPELLBOOK = book(
            "rhnull_heavy_sword_spellbook", "rhnull_heavy_sword_icon", RISE, RHNULL, 60, 20, 40, RhnullHeavySwordSpellBookItem::new);
    public static final RegistryObject<Item> RHNULL_GOLDEN_THRONE_SPELLBOOK = book(
            "rhnull_golden_throne_spellbook", "rhnull_golden_throne_icon", CIRCLE, RHNULL, 30, 20, 40, RhnullGoldenThroneSpellBookItem::new);
    public static final RegistryObject<Item> RHNULL_ORB_EMITTER_SPELLBOOK = book(
            "rhnull_orb_emitter_spellbook", "rhnull_orb_emitter_icon", CIRCLE, RHNULL, 20, 20, 40, RhnullOrbEmitterSpellBookItem::new);
    public static final RegistryObject<Item> THE_OMNITOME = ITEMS.register("the_omnitome",
            () -> new OmnitomeItem(new Item.Properties().stacksTo(1).fireResistant()));

    private SpellBookItems() {}

    private static RegistryObject<Item> book(String id, String icon, StaffCastType castType,
                                             SpellAffinity affinity, int crimsonCost, int chargeTicks, int cooldownTicks,
                                             BiFunction<Item.Properties, SpellBookDefinition, Item> factory) {
        SpellBookDefinition definition = new SpellBookDefinition(id, icon, castType, affinity, crimsonCost, chargeTicks, cooldownTicks);
        return ITEMS.register(id, () -> factory.apply(new Item.Properties().stacksTo(1), definition));
    }

    public static void init() {}
}
