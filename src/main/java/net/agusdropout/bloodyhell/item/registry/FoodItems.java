package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class FoodItems {
    public static final RegistryObject<Item> GLOW_MUSHROOM = ITEMS.register("glow_mushroom", () -> new BlockItem( PlantBlocks.LIGHT_MUSHROOM_BLOCK.get(),new Item.Properties().stacksTo(64).food(new FoodProperties.Builder().nutrition(3)
            .effect(
                    () -> new MobEffectInstance(MobEffects.GLOWING,200,0),1)
            .effect(
                    () -> new MobEffectInstance(MobEffects.CONFUSION,200,0),1
            ).build())));
    public static final RegistryObject<Item> GLOW_FRUIT = ITEMS.register("glow_fruit", () -> new BlockItem( PlantBlocks.DROOPVINE.get(),new Item.Properties().stacksTo(64).food(new FoodProperties.Builder().nutrition(3)
            .effect(
                    () -> new MobEffectInstance(MobEffects.GLOWING,200,0),1)
            .effect(
                    () -> new MobEffectInstance(MobEffects.CONFUSION,200,0),1
            ).build())));
    public static final RegistryObject<Item> EYEBALL = ITEMS.register("eyeball", () ->
                    new Item(new Item.Properties()
                            .food(new FoodProperties.Builder().nutrition(3).saturationMod(2f)
                                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0), 1.0f)
                                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 6000, 0), 1.0f)
                                    .build())
                    )
            );
    public static final RegistryObject<Item> SCARLET_RAW_CHICKEN = ITEMS.register("scarlet_raw_chicken", () ->
                    new Item(new Item.Properties()
                            .food(new FoodProperties.Builder().nutrition(2).saturationMod(2f)
                                    .build())
                    )
            );
    public static final RegistryObject<Item> SCARLET_COOKED_CHICKEN = ITEMS.register("scarlet_cooked_chicken", () ->
                    new Item(new Item.Properties()
                            .food(new FoodProperties.Builder().nutrition(4).saturationMod(2f)
                                    .build())
                    )
            );
    public static final RegistryObject<Item> GOREHOG_RAW_STEAK = ITEMS.register("gorehog_raw_steak", () ->
                    new Item(new Item.Properties()
                            .food(new FoodProperties.Builder().nutrition(2).saturationMod(2f)
                                    .build())
                    )
            );
    public static final RegistryObject<Item> GOREHOG_COOKED_STEAK = ITEMS.register("gorehog_cooked_steak", () ->
                    new Item(new Item.Properties()
                            .food(new FoodProperties.Builder().nutrition(5).saturationMod(2f)
                                    .build())
                    )
            );

    private FoodItems() {}

    public static void init() {}
}
