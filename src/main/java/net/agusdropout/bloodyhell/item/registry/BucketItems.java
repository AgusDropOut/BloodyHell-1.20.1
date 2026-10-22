package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class BucketItems {
    public static final RegistryObject<Item> BLOOD_BUCKET = ITEMS.register("blood_bucket",
            () -> new BucketItem(ModFluids.BLOOD_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> CORRUPTED_BLOOD_BUCKET = ITEMS.register("corrupted_blood_bucket",
            () -> new BucketItem(ModFluids.CORRUPTED_BLOOD_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> VISCOUS_BLASPHEMY_BUCKET = ITEMS.register("viscous_blasphemy_bucket",
            () -> new BucketItem(ModFluids.VISCOUS_BLASPHEMY_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> VISCERAL_BLOOD_BUCKET = ITEMS.register("visceral_blood_bucket",
            () -> new BucketItem(ModFluids.VISCERAL_BLOOD_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private BucketItems() {}

    public static void init() {}
}
