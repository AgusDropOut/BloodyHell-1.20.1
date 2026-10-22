package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.*;
import net.agusdropout.bloodyhell.item.custom.spellbooks.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class DropItems {
    public static final RegistryObject<Item> VEINREAVER_HORN = ITEMS.register("veinreaver_horn", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> AUREAL_REVENANT_DAGGER = ITEMS.register("aureal_revenant_dagger", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> CRIMSON_SHELL = ITEMS.register("crimson_shell", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> SCARLET_FEATHER = ITEMS.register("scarlet_feather", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> RITEKEEPER_HEART = ITEMS.register("ritekeeper_heart", () -> new Item(
            new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> CINDER_ACOLYTE_FAINTED_EMBER = ITEMS.register("cinder_acolyte_fainted_ember", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> FAILED_REMNANT_ASHES = ITEMS.register("failed_remnant_ashes", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_ECHO_SHARD = ITEMS.register("blood_echo_shard",
            () -> new BloodEchoShardItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> NAMELESS_ECHO_SHARD = ITEMS.register("nameless_echo_shard",
            () -> new NamelessEchoShardItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> SELIORA_ESSENCE = ITEMS.register("seliora_essence",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    private DropItems() {}

    public static void init() {}
}
