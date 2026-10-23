package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.ModArmorMaterials;
import net.agusdropout.bloodyhell.item.ModToolTiers;
import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoArmorItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class BlasphemiteItems {
    public static final RegistryObject<Item> BLASPHEMITE = ITEMS.register("blasphemite", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> RAW_BLASPHEMITE = ITEMS.register("raw_blasphemite", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> BLASPHEMITE_NUGGET = ITEMS.register("blasphemite_nugget", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMOUS_EYE = ITEMS.register("blasphemous_eye", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMITE_SWORD = ITEMS.register("blasphemite_sword",() -> new SwordItem(ModToolTiers.BLASPHEMITE,3,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_PICKAXE = ITEMS.register("blasphemite_pickaxe",() -> new PickaxeItem(ModToolTiers.BLASPHEMITE,1,-2.8F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_AXE = ITEMS.register("blasphemite_axe",() -> new AxeItem(ModToolTiers.BLASPHEMITE,5,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_HOE = ITEMS.register("blasphemite_hoe",() -> new HoeItem(ModToolTiers.BLASPHEMITE,0,0,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_SHOVEL = ITEMS.register("blasphemite_shovel",() -> new ShovelItem(ModToolTiers.BLASPHEMITE,1,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_HELMET = ITEMS.register("blasphemite_helmet",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.HELMET, new Item.Properties(), BaseGeckoArmorItem.BLASPHEMITE));
    public static final RegistryObject<Item> BLASPHEMITE_CHESTPLATE = ITEMS.register("blasphemite_chestplate",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), BaseGeckoArmorItem.BLASPHEMITE));
    public static final RegistryObject<Item> BLASPHEMITE_LEGGINGS = ITEMS.register("blasphemite_leggings",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.LEGGINGS, new Item.Properties(), BaseGeckoArmorItem.BLASPHEMITE));
    public static final RegistryObject<Item> BLASPHEMITE_BOOTS = ITEMS.register("blasphemite_boots",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.BOOTS, new Item.Properties(), BaseGeckoArmorItem.BLASPHEMITE));

    private BlasphemiteItems() {}

    public static void init() {}
}
