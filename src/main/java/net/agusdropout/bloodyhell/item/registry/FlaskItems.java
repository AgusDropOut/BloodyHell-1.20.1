package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.potions.BloodFlaskItem;
import net.agusdropout.bloodyhell.item.potions.BloodPotionItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class FlaskItems {
    public static final RegistryObject<Item> BLOOD_FLASK = ITEMS.register("blood_flask", () -> new BloodFlaskItem(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> CORRUPTED_BLOOD_FLASK = ITEMS.register("corrupted_blood_flask", () -> new BloodPotionItem(
            new Item.Properties().stacksTo(64), 10));
    public static final RegistryObject<Item> FILLED_BLOOD_FLASK = ITEMS.register("filled_blood_flask", () -> new BloodPotionItem(
            new Item.Properties().stacksTo(64), 20));
    public static final RegistryObject<Item> FILLED_RHNULL_BLOOD_FLASK = ITEMS.register("filled_rhnull_blood_flask", () -> new BloodPotionItem(
            new Item.Properties().stacksTo(64), 30));
    public static final RegistryObject<Item> FILLED_VISCOUS_BLASPHEMY_FLASK = ITEMS.register("filled_viscous_blasphemy_flask", () -> new BloodPotionItem(
            new Item.Properties().stacksTo(64), 0));
    public static final RegistryObject<Item> BOUND_BLOOD_FLASK = ITEMS.register("bound_blood_flask",
            () -> new BoundBloodFlaskItem(new Item.Properties().stacksTo(1)));

    private FlaskItems() {}

    public static void init() {}
}
