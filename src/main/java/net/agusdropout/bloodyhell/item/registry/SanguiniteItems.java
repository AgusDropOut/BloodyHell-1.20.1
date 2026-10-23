package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.ModArmorMaterials;
import net.agusdropout.bloodyhell.item.ModToolTiers;
import net.agusdropout.bloodyhell.item.custom.ModBow;
import net.agusdropout.bloodyhell.item.custom.SanguiniteArrowItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoArmorItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class SanguiniteItems {
    public static final RegistryObject<Item> SANGUINITE = ITEMS.register("sanguinite", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> SANGUINITE_NUGGET = ITEMS.register("sanguinite_nugget", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> RAW_SANGUINITE = ITEMS.register("raw_sanguinite", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> SANGUINITE_SWORD = ITEMS.register("sanguinite_sword",() -> new SwordItem(ModToolTiers.SANGUINITE,3,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_PICKAXE = ITEMS.register("sanguinite_pickaxe",() -> new PickaxeItem(ModToolTiers.SANGUINITE,1,-2.8F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_AXE = ITEMS.register("sanguinite_axe",() -> new AxeItem(ModToolTiers.SANGUINITE,5,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_HOE = ITEMS.register("sanguinite_hoe",() -> new HoeItem(ModToolTiers.SANGUINITE,0,0,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_SHOVEL = ITEMS.register("sanguinite_shovel",() -> new ShovelItem(ModToolTiers.SANGUINITE,1,-3F,
            new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SANGUINITE_BOW = ITEMS.register("sanguinite_bow", () -> new ModBow(
            new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> SANGUINITE_ARROW = ITEMS.register("sanguinite_arrow", () -> new SanguiniteArrowItem(
            new Item.Properties()));
    public static final RegistryObject<Item> SANGUINITE_HELMET = ITEMS.register("sanguinite_helmet",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.SANGUINITE, ArmorItem.Type.HELMET, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));
    public static final RegistryObject<Item> SANGUINITE_CHESTPLATE = ITEMS.register("sanguinite_chestplate",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.SANGUINITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));
    public static final RegistryObject<Item> SANGUINITE_LEGGINGS = ITEMS.register("sanguinite_leggings",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.SANGUINITE, ArmorItem.Type.LEGGINGS, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));
    public static final RegistryObject<Item> SANGUINITE_BOOTS = ITEMS.register("sanguinite_boots",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.SANGUINITE, ArmorItem.Type.BOOTS, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));

    private SanguiniteItems() {}

    public static void init() {}
}
