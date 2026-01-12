package net.agusdropout.bloodyhell.worldgen.feature;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.custom.SmallRocks;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.worldgen.feature.custom.DroopvineFeature;
import net.agusdropout.bloodyhell.worldgen.tree.custom.*;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.MegaJungleTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class ModConfiguredFeatures {


    public static final ResourceKey<ConfiguredFeature<?, ?>> SOUL_DIMENSION_SANGUINE_ORE_KEY = registerKey("soul_dimension_sanguinite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOUL_DIMENSION_BLASPHEMITE_ORE_KEY = registerKey("soul_dimension_blasphemite_ore");
    public static final ResourceKey<ConfiguredFeature<?,?>> GIANT_BLOOD_TREE_KEY = registerKey("giant_blood_tree");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_TREE_KEY = registerKey("blood_tree");
    public static final ResourceKey<ConfiguredFeature<?,?>> SMALL_BLOOD_TREE_KEY = registerKey("small_blood_tree");
    public static final ResourceKey<ConfiguredFeature<?,?>> SOUL_TREE_KEY = registerKey("soul_tree");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_FLOWER_KEY = registerKey("blood_flower");
    public static final ResourceKey<ConfiguredFeature<?,?>> LIGHT_MUSHROOM_KEY = registerKey("light_mushroom");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_BUSH_KEY = registerKey("blood_bush_flower");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_SMALL_ROCKS_KEY = registerKey("small_rocks");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_PETALS_KEY = registerKey("blood_petals");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLEEDING_BLOCK_KEY = registerKey("bleeding_block");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_LIQUID_KEY = registerKey("blood_liquid_block");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_GRASS_KEY = registerKey("blood_grass");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_LILY_KEY = registerKey("blood_lily");
    public static final ResourceKey<ConfiguredFeature<?,?>> DROOPVINE_KEY = registerKey("droopvine");
    public static final ResourceKey<ConfiguredFeature<?,?>> STONE_PILLAR_KEY = registerKey("stone_pillar");
    public static final ResourceKey<ConfiguredFeature<?,?>> CINDER_CACTUS_KEY = registerKey("cinder_cactus");
    public static final ResourceKey<ConfiguredFeature<?,?>> CINDER_CACTUS_FLOWER_KEY = registerKey("cinder_cactus_flower");
    public static final ResourceKey<ConfiguredFeature<?,?>> GIANT_SPINE_KEY = registerKey("giant_spine");
    public static final ResourceKey<ConfiguredFeature<?,?>> GIANT_ROOT_KEY = registerKey("giant_root");
    public static final ResourceKey<ConfiguredFeature<?,?>> SPIKY_GRASS_KEY = registerKey("spiky_grass");
    public static final ResourceKey<ConfiguredFeature<?,?>> STING_FLOWER_KEY = registerKey("sting_flower");
    public static final ResourceKey<ConfiguredFeature<?,?>> ROUNDED_GRASS_KEY = registerKey("rounded_grass");
    public static final ResourceKey<ConfiguredFeature<?,?>> BLOOD_SCRAPPER_PLANT_KEY = registerKey("blood_scrapper_plant");
    public static final ResourceKey<ConfiguredFeature<?,?>> GLOWING_CRYSTAL_KEY = registerKey("glowing_crystal_key");





    public static final Supplier<List<OreConfiguration.TargetBlockState>> BLOOD_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(new TagMatchTest(ModTags.Blocks.BLOOD_ORE_REPLACEABLES), ModBlocks.SANGUINITE_ORE.get().defaultBlockState())));

    public static final Supplier<List<OreConfiguration.TargetBlockState>> BLASPHEMITE_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(new TagMatchTest(ModTags.Blocks.BLASPHEMITE_ORE_REPLACEABLES), ModBlocks.BLASPHEMITE_ORE.get().defaultBlockState())));

    public static final Supplier<List<OreConfiguration.TargetBlockState>> BLEEDING_BLOCK = Suppliers.memoize(() -> List.of(
           OreConfiguration.target(new TagMatchTest(BlockTags.DIRT), ModBlocks.BLEEDING_BLOCK.get().defaultBlockState())));
    public static final Supplier<List<OreConfiguration.TargetBlockState>> BLOOD_LIQUID = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(new TagMatchTest(BlockTags.DIRT), ModBlocks.BLOOD_FLUID_BLOCK.get().defaultBlockState())));



    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);



        register(context, SOUL_DIMENSION_SANGUINE_ORE_KEY, Feature.ORE, new OreConfiguration(BLOOD_ORES.get(),12));
        register(context, SOUL_DIMENSION_BLASPHEMITE_ORE_KEY, Feature.ORE, new OreConfiguration(BLASPHEMITE_ORES.get(),12));
        register(context, BLEEDING_BLOCK_KEY, Feature.ORE, new OreConfiguration(BLEEDING_BLOCK.get(),33));
        register(context, BLOOD_LIQUID_KEY, Feature.DELTA_FEATURE, new DeltaFeatureConfiguration(ModBlocks.BLOOD_FLUID_BLOCK.get().defaultBlockState(), ModBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), UniformInt.of(6, 8), UniformInt.of(2, 4)));


        register(context,BLOOD_TREE_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.BLOOD_LOG.get()),
                new BloodTrunkPlacer(1, 2, 2),
                BlockStateProvider.simple(ModBlocks.BLOOD_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2), 2),
                new TwoLayersFeatureSize(1,0,2)).decorators(ImmutableList.of(BloodTreeTrunkDecorator.INSTANCE)).build()
        );
        register(context,GIANT_BLOOD_TREE_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.BLOOD_LOG.get()),

                new MegaJungleTrunkPlacer(10, 5, 5),
                BlockStateProvider.simple(ModBlocks.BLOOD_LEAVES.get()),
                new MegaJungleFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 2),
                new TwoLayersFeatureSize(1,0,2)).decorators(ImmutableList.of(BloodTreeTrunkDecorator.INSTANCE,BloodTreeLeafDecorator.INSTANCE)).build()
        );
        register(context,SOUL_TREE_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SOUL_LOG.get()),
                new SoulTrunkPlacer(1, 2, 2),
                BlockStateProvider.simple(ModBlocks.SOUL_LEAVES.get()),
                new DarkOakFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1)),
                new TwoLayersFeatureSize(1,1,3)).decorators(ImmutableList.of(SoulTreeLeafDecorator.INSTANCE)).build()
        );
        register(context,SMALL_BLOOD_TREE_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.BLOOD_LOG.get()),
                new SmallBloodTrunkPlacer(1, 2, 2),
                BlockStateProvider.simple(ModBlocks.BLOOD_LEAVES.get()),
                new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 1),
                new TwoLayersFeatureSize(1,1,1)).build()
        );
        context.register(BLOOD_FLOWER_KEY, new ConfiguredFeature<>(Feature.FLOWER, patch(ModBlocks.BLOOD_FLOWER.get(), 64)));
        context.register(LIGHT_MUSHROOM_KEY, new ConfiguredFeature<>(Feature.FLOWER, patch(ModBlocks.LIGHT_MUSHROOM_BLOCK.get(), 35)));
        context.register(BLOOD_GRASS_KEY, new ConfiguredFeature<>(Feature.FLOWER, patch(ModBlocks.BLOOD_GRASS.get(), 100)));
        context.register(SPIKY_GRASS_KEY, new ConfiguredFeature<>(Feature.FLOWER, patch(ModBlocks.SPIKY_GRASS.get(), 10)));
        context.register(ROUNDED_GRASS_KEY, new ConfiguredFeature<>(Feature.FLOWER, patch(ModBlocks.ROUNDED_GRASS.get(), 30)));
        context.register(BLOOD_BUSH_KEY, new ConfiguredFeature<>(Feature.FLOWER, patch(ModBlocks.BLOOD_BUSH.get(), 32)));
        context.register(BLOOD_SMALL_ROCKS_KEY, new ConfiguredFeature<>(Feature.FLOWER, rocks(ModBlocks.SMALL_ROCKS.get(),List.of(ModBlocks.BLOODY_STONE_BLOCK.get(), ModBlocks.BLOOD_GRASS_BLOCK.get(),ModBlocks.BLOOD_DIRT_BLOCK.get()))));
        context.register(BLOOD_PETALS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH, patch(ModBlocks.BLOOD_PETALS.get(), 70)));
        context.register(GLOWING_CRYSTAL_KEY, new ConfiguredFeature<>(ModFeatures.GLOWING_CRYSTAL.get(),NoneFeatureConfiguration.NONE));
        context.register(BLOOD_LILY_KEY,new ConfiguredFeature<> (Feature.RANDOM_PATCH, new RandomPatchConfiguration(10, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BLOOD_LILY_BLOCK.get()))))));
        context.register(DROOPVINE_KEY, new ConfiguredFeature<>(ModFeatures.DROOPVINE.get(),NoneFeatureConfiguration.NONE));
        context.register(STONE_PILLAR_KEY, new ConfiguredFeature<>(ModFeatures.STONE_PILLAR.get(),NoneFeatureConfiguration.NONE));
        context.register(CINDER_CACTUS_KEY, new ConfiguredFeature<>(ModFeatures.CINDER_CACTUS.get(),NoneFeatureConfiguration.NONE));
        context.register(CINDER_CACTUS_FLOWER_KEY, new ConfiguredFeature<>(ModFeatures.CINDER_FLOWER.get(),NoneFeatureConfiguration.NONE));
        context.register(GIANT_SPINE_KEY, new ConfiguredFeature<>(ModFeatures.GIANT_SPINE.get(),NoneFeatureConfiguration.NONE));
        context.register(GIANT_ROOT_KEY, new ConfiguredFeature<>(ModFeatures.GIANT_ROOT.get(),NoneFeatureConfiguration.NONE));
        context.register(STING_FLOWER_KEY, new ConfiguredFeature<>(ModFeatures.STING_FLOWER_FEATURE.get(),NoneFeatureConfiguration.NONE));
        context.register(BLOOD_SCRAPPER_PLANT_KEY, new ConfiguredFeature<>(Feature.FLOWER, patch(ModBlocks.BLOOD_SCRAPPER_PLANT_SAPLING.get(), 5)));

}

    private static RandomPatchConfiguration patch(Block block, int tries) {
        return FeatureUtils.simpleRandomPatchConfiguration(tries, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(block))));
    }

    private static RandomPatchConfiguration patch(Block block, int tries, List<Block> whitelist) {
        return FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(block)), whitelist, tries);
    }



    private static RandomPatchConfiguration patch(BlockState block, int tries, List<Block> whitelist) {
        return FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(block)), whitelist, tries);
    }
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(BloodyHell.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
    private static RandomPatchConfiguration rocks(Block block, List<Block> whitelist) {
        return FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new RandomizedIntStateProvider(BlockStateProvider.simple(block), SmallRocks.ROCKS, UniformInt.of(1, 2))), whitelist, 32);
    }

}
