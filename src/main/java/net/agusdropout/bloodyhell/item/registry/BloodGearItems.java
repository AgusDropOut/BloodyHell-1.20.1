package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.ModArmorMaterials;
import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoArmorItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class BloodGearItems {
    public static final RegistryObject<Item> BLOOD_BOW = ITEMS.register("blood_bow",() -> new ModBow(
            new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> BLOOD_ARROW = ITEMS.register("blood_arrow", () -> new BloodArrowItem(
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_HELMET = ITEMS.register("blood_helmet",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.HELMET, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));
    public static final RegistryObject<Item> BLOOD_CHESTPLATE = ITEMS.register("blood_chestplate",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.CHESTPLATE, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));
    public static final RegistryObject<Item> BLOOD_LEGGINGS = ITEMS.register("blood_leggings",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.LEGGINGS, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));
    public static final RegistryObject<Item> BLOOD_BOOTS = ITEMS.register("blood_boots",
            () -> new BaseGeckoArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.BOOTS, new Item.Properties(), BaseGeckoArmorItem.SANGUINITE));

    private BloodGearItems() {}

    public static void init() {}
}
