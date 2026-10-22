package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.ModArmorMaterials;
import net.agusdropout.bloodyhell.item.ModToolTiers;
import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.*;
import net.agusdropout.bloodyhell.item.custom.spellbooks.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class RhnullItems {
    public static final RegistryObject<Item> RHNULL = ITEMS.register("rhnull", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_NUGGET = ITEMS.register("rhnull_nugget", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_SWORD = ITEMS.register("rhnull_sword",() -> new SoulItem(ModToolTiers.RHNULL,3,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_PICKAXE = ITEMS.register("rhnull_pickaxe",() -> new PickaxeItem(ModToolTiers.RHNULL,1,-2.8F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_AXE = ITEMS.register("rhnull_axe",() -> new AxeItem(ModToolTiers.RHNULL,5,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_HOE = ITEMS.register("rhnull_hoe",() -> new HoeItem(ModToolTiers.RHNULL,0,0,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_SHOVEL = ITEMS.register("rhnull_shovel",() -> new ShovelItem(ModToolTiers.RHNULL,1,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_HELMET = ITEMS.register("rhnull_helmet",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_CHESTPLATE = ITEMS.register("rhnull_chestplate",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_LEGGINGS = ITEMS.register("rhnull_leggings",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_BOOTS = ITEMS.register("rhnull_boots",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.BOOTS, new Item.Properties()));

    private RhnullItems() {}

    public static void init() {}
}
