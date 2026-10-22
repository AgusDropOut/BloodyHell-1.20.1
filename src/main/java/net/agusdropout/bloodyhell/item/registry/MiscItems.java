package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.*;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class MiscItems {
    public static final RegistryObject<Item> BLOODY_SOUL_DUST = ITEMS.register("bloody_soul_dust", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> CHALICE_OF_THE_DAMMED = ITEMS.register("chalice_of_the_dammed", () -> new CatalystItem());
    public static final RegistryObject<Item> CRIMSON_IDOL_COIN = ITEMS.register("crimson_idol_coin", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINE_CRUCIBLE_CORE = ITEMS.register("sanguine_crucible_core", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ROOTLET_POWDER = ITEMS.register("rootlet_powder", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> UNKNOWN_GUIDE_BOOK = ITEMS.register("unknown_guide_book",
            () -> new UnknownGuideBookItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GAZE_OF_THE_UNKNOWN = ITEMS.register("gaze_of_the_unknown",
            () -> new GazeOfTheUnknownItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).alwaysEat().build()), 30.0D, 55.0D));
    public static final RegistryObject<Item> NAMELESS_WHISPER = ITEMS.register("nameless_whisper", () -> new NamelessWhisperItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(0).alwaysEat().build())));

    private MiscItems() {}

    public static void init() {}
}
