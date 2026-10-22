package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.*;
import net.agusdropout.bloodyhell.item.custom.reliquary.ReliquaryItem;
import net.agusdropout.bloodyhell.item.custom.spellbooks.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class ReliquaryItems {
    public static final RegistryObject<Item> RELIQUARY = ITEMS.register("reliquary", () -> new ReliquaryItem(
            new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_OCULAR_LENSE = ITEMS.register("ancient_ocular_lense", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_OCULAR_LENSE = ITEMS.register("blasphemous_ocular_lense", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MARK_OF_THE_RESTLESS_SLUMBER = ITEMS.register("mark_of_the_restless_slumber", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RUNE_OF_THE_RAVENOUS_GAZE = ITEMS.register("rune_of_the_ravenous_gaze", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SEAL_OF_THE_HOLLOW_BULWARK = ITEMS.register("seal_of_the_hollow_bulwark", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> OMEN_OF_THE_CRUSHING_TOLL = ITEMS.register("omen_of_the_crushing_toll", () -> new Item(
            new Item.Properties().stacksTo(1)));

    private ReliquaryItems() {}

    public static void init() {}
}
