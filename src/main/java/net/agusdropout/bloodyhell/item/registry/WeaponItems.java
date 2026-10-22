package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.ModToolTiers;
import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class WeaponItems {
    public static final RegistryObject<Item> SACRIFICIAL_DAGGER = ITEMS.register("sacrificial_dagger",() -> new SacrificialDagger(Tiers.WOOD,1,5,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HERETIC_SACRIFICIAL_DAGGER = ITEMS.register("heretic_sacrificial_dagger",() -> new HereticSacrificialDagger(Tiers.IRON,1,5,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> UNKNOWN_ENTITY_FINGER = ITEMS.register("unknown_entity_finger", () -> new UnknownEntityFingers(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_TWIN_DAGGERS = ITEMS.register("blasphemous_twin_daggers", () -> new BlasphemousTwinDaggerItem(ModToolTiers.RHNULL,10,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_HULKING_MASS_OF_IRON = ITEMS.register("blasphemous_hulking_mass_of_iron", () -> new BlasphemousHulkingMassOfIronItem(ModToolTiers.RHNULL,10,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_IMPALER = ITEMS.register("blasphemous_impaler", () -> new BlasphemousImpalerItem(ModToolTiers.BLASPHEMITE, 10, -3.0F,
            new Item.Properties().stacksTo(0)));

    private WeaponItems() {}

    public static void init() {}
}
