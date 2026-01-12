package net.agusdropout.bloodyhell.worldgen.feature;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {


    public static final ResourceKey<PlacedFeature> SANGUINE_ORE_PLACED_KEY = createKey("sanguine_ore_placed");
    public static final ResourceKey<PlacedFeature> BLASPHEMITE_ORE_PLACED_KEY = createKey("blasphemite_ore_placed");

    public static final ResourceKey<PlacedFeature> BLOOD_TREE_PLACED_KEY = createKey("blood_tree_placed");
    public static final ResourceKey<PlacedFeature> GIANT_BLOOD_TREE_PLACED_KEY = createKey("giant_blood_tree_placed");
    public static final ResourceKey<PlacedFeature> SOUL_TREE_PLACED_KEY = createKey("soul_tree_placed");
    public static final ResourceKey<PlacedFeature> SMALL_BLOOD_TREE_PLACED_KEY = createKey("small_blood_tree_placed");

    public static final ResourceKey<PlacedFeature> BLOOD_FLOWER_PLACED_KEY = createKey("blood_flower_placed");
    public static final ResourceKey<PlacedFeature> LIGHT_MUSHROOM_PLACED_KEY = createKey("light_mushroom_placed");
    public static final ResourceKey<PlacedFeature> BLOOD_GRASS_PLACED_KEY = createKey("blood_grass_placed");
    public static final ResourceKey<PlacedFeature> BLOOD_BUSH_PLACED_KEY = createKey("blood_bush_placed");
    public static final ResourceKey<PlacedFeature> BLOOD_SMALL_ROCKS_PLACED_KEY = createKey("blood_small_rocks_placed");
    public static final ResourceKey<PlacedFeature> BLOOD_PETALS_PLACED_KEY = createKey("blood_petals_placed");
    public static final ResourceKey<PlacedFeature> BLEEDING_BLOCK_PLACED_KEY = createKey("bleeding_block_placed");
    public static final ResourceKey<PlacedFeature> BLOOD_LIQUID_PLACED_KEY = createKey("blood_liquid_placed");
    public static final ResourceKey<PlacedFeature> BLOOD_LILY_BLOCK_PLACED_KEY = createKey("blood_lily_block_placed");
    public static final ResourceKey<PlacedFeature> DROOPVINE_PLACED_KEY = createKey("droopvine_placed");
    public static final ResourceKey<PlacedFeature> STONE_PILLAR_PLACED_KEY = createKey("stone_pillar_placed");
    public static final ResourceKey<PlacedFeature> CINDER_CACTUS_PLACED_KEY = createKey("cinder_cactus_placed");
    public static final ResourceKey<PlacedFeature> CINDER_CACTUS_FLOWER_PLACED_KEY = createKey("cinder_cactus_flower_placed");
    public static final ResourceKey<PlacedFeature> GIANT_SPINE_PLACED_KEY = createKey("giant_spine_placed");
    public static final ResourceKey<PlacedFeature> GIANT_ROOT_PLACED_KEY = createKey("giant_root_placed");
    public static final ResourceKey<PlacedFeature> BLOOD_SCRAPPER_PLANT_PLACED_KEY = createKey("blood_scrapper_plant_placed");
    public static final ResourceKey<PlacedFeature> SPIKY_GRASS_PLACED_KEY = createKey("spiky_grass_placed");
    public static final ResourceKey<PlacedFeature> STING_FLOWER_PLACED_KEY = createKey("sting_flower_placed");
    public static final ResourceKey<PlacedFeature> ROUNDED_GRASS_PLACED_KEY = createKey("rounded_grass_placed");
    public static final ResourceKey<PlacedFeature> GLOWING_CRYSTAL_PLACED_KEY = createKey("glowing_crystal_placed_key");


    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);


        register(context, SANGUINE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SOUL_DIMENSION_SANGUINE_ORE_KEY),
                commonOrePlacement(12, // VeinsPerChunk
                        HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-10), VerticalAnchor.aboveBottom(100))));
        register(context, BLASPHEMITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SOUL_DIMENSION_BLASPHEMITE_ORE_KEY),
                commonOrePlacement(12, // VeinsPerChunk
                        HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-10), VerticalAnchor.aboveBottom(100))));
        register(context, BLEEDING_BLOCK_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BLEEDING_BLOCK_KEY),
                commonOrePlacement(24, // VeinsPerChunk
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(80))));
        register(context, BLOOD_LIQUID_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_LIQUID_KEY),
                commonOrePlacement(24, // VeinsPerChunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(55), VerticalAnchor.absolute(112))));
        register(context,BLOOD_TREE_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3,0.1f,2), ModBlocks.BLOOD_SAPLING.get()));
        register(context,GIANT_BLOOD_TREE_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.GIANT_BLOOD_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3,0.1f,2), ModBlocks.GIANT_BLOOD_SAPLING.get()));
        register(context,SMALL_BLOOD_TREE_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.SMALL_BLOOD_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3,0.1f,2), ModBlocks.SMALL_BLOOD_SAPLING.get()));
        register(context,SOUL_TREE_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.SOUL_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1,0.1f,1), ModBlocks.SOUL_SAPLING.get()));
        register(context, BLOOD_SMALL_ROCKS_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_SMALL_ROCKS_KEY),patch(50));
        register(context, BLOOD_FLOWER_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_FLOWER_KEY),patch(64));
        register(context, LIGHT_MUSHROOM_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.LIGHT_MUSHROOM_KEY),patch(35));
        register(context, BLOOD_GRASS_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_GRASS_KEY),patch(100));
        register(context, SPIKY_GRASS_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.SPIKY_GRASS_KEY),patch(10));
        register(context, ROUNDED_GRASS_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.ROUNDED_GRASS_KEY),patch(30));
        register(context, BLOOD_BUSH_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_BUSH_KEY),patch(64));
        register(context, BLOOD_PETALS_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_PETALS_KEY),patch(70));
        register(context, GLOWING_CRYSTAL_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.GLOWING_CRYSTAL_KEY),patch(70));
        register(context, BLOOD_LILY_BLOCK_PLACED_KEY,configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_LILY_KEY),patch(70));
        register(context,DROOPVINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.DROOPVINE_KEY), patch(100));
        register(context,STONE_PILLAR_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.STONE_PILLAR_KEY), patch(30));
        register(context,CINDER_CACTUS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CINDER_CACTUS_KEY), patch(30));
        register(context,CINDER_CACTUS_FLOWER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CINDER_CACTUS_FLOWER_KEY), patch(256));
        register(context,GIANT_SPINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.GIANT_SPINE_KEY), patch(1));
        register(context,GIANT_ROOT_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.GIANT_ROOT_KEY), patch(1));
        register(context,STING_FLOWER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.STING_FLOWER_KEY), patch(50));
        register(context,BLOOD_SCRAPPER_PLANT_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BLOOD_SCRAPPER_PLANT_KEY), patch(30));
        
    }

    private static List<PlacementModifier> patch(int count) {
        return List.of(CountPlacement.of(count), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
    }
    public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    public static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(BloodyHell.MODID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
