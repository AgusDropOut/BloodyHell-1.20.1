package net.agusdropout.bloodyhell.worldgen.dimension;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.worldgen.ModBiomeModifiers;
import net.agusdropout.bloodyhell.worldgen.biome.ModBiomes;
import net.agusdropout.bloodyhell.worldgen.biome.surface.ModSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;
import java.util.OptionalLong;

import static net.agusdropout.bloodyhell.worldgen.biome.ModBiomes.buildBiomeSource;


public class ModDimensions {
    public static final ResourceKey<LevelStem> SOUL_DIMENSION_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(BloodyHell.MODID, "soul_dimension"));
    public static final ResourceKey<Level> SOUL_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(BloodyHell.MODID, "soul_dimension"));
    public static final ResourceKey<DimensionType> SOUL_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(BloodyHell.MODID, "soul_dimension"));
    public static final ResourceKey<NoiseGeneratorSettings> SOUL_DIMENSION_NOISE_GEN = ResourceKey.create(Registries.NOISE_SETTINGS,
            new ResourceLocation(BloodyHell.MODID, "soul_dimension"));


    private static ResourceLocation name(String name) {
        return new ResourceLocation(BloodyHell.MODID, name);
    }

    public static final ResourceLocation DIMENSION_RENDERER = BloodyHell.prefix("renderer");
    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(SOUL_DIMENSION_TYPE, new DimensionType(
                OptionalLong.of(18000L), // fixedTime
                true, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                true, // natural
                4.0D, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                144, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                DIMENSION_RENDERER, // effectsLocation
                0.1f, // ambientLight
                new DimensionType.MonsterSettings(true, false, UniformInt.of(0, 7), 2)));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        context.register(SOUL_DIMENSION_KEY, new LevelStem(
                dimTypes.getOrThrow(SOUL_DIMENSION_TYPE),
                new NoiseBasedChunkGenerator(
                        buildBiomeSource(biomeRegistry),  // Biomes encima del terreno
                        noiseGenSettings.getOrThrow(SOUL_DIMENSION_NOISE_GEN) // Terreno base
                )
        ));
    }
    public static void bootstrapNoise(BootstapContext<NoiseGeneratorSettings> context) {

        HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);
        DensityFunction densityfunction = NoiseRouterData.getFunction(functions, NoiseRouterData.SHIFT_X);
        DensityFunction densityfunction1 = NoiseRouterData.getFunction(functions, NoiseRouterData.SHIFT_Z);

        context.register(SOUL_DIMENSION_NOISE_GEN, new NoiseGeneratorSettings(
                NoiseSettings.create(0, 128, 2, 2),
                ModBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                new NoiseRouter(
                        DensityFunctions.zero(), //barrier
                        DensityFunctions.zero(), //fluid level floodedness
                        DensityFunctions.zero(), //fluid level spread
                        DensityFunctions.zero(), //lava
                        DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, noises.getOrThrow(Noises.TEMPERATURE)), //temperature
                        DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, noises.getOrThrow(Noises.VEGETATION)), //vegetation
                        NoiseRouterData.getFunction(functions, NoiseRouterData.CONTINENTS), //continents
                        NoiseRouterData.getFunction(functions, NoiseRouterData.EROSION), //erosion
                        DensityFunctions.rangeChoice(
                                NoiseRouterData.getFunction(functions, NoiseRouterData.Y),
                                0.0D,
                                32.0D,
                                DensityFunctions.constant(2.0D),
                                DensityFunctions.constant(-2.0D)), //depth
                        NoiseRouterData.getFunction(functions, NoiseRouterData.RIDGES), //ridges
                        DensityFunctions.zero(), //initial density
                        DensityFunctions.mul(
                                DensityFunctions.constant(0.64D),
                                DensityFunctions.interpolated(
                                        DensityFunctions.blendDensity(
                                                DensityFunctions.add(
                                                        DensityFunctions.constant(2.5D),
                                                        DensityFunctions.mul(
                                                                DensityFunctions.yClampedGradient(-8, 24, 0.0D, 1.0D),
                                                                DensityFunctions.add(
                                                                        DensityFunctions.constant(-2.5D),
                                                                        DensityFunctions.add(
                                                                                DensityFunctions.constant(0.5D),
                                                                                DensityFunctions.mul(
                                                                                        DensityFunctions.yClampedGradient(110, 128, 1.0D, 0.0D),
                                                                                        DensityFunctions.add(
                                                                                                DensityFunctions.constant(-0.5F),
                                                                                                BlendedNoise.createUnseeded(0.1D, 0.3D, 80.0D, 60.0D, 1.0D))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ).squeeze(), //final density
                        DensityFunctions.zero(), //vein toggle
                        DensityFunctions.zero(), //vein ridged
                        DensityFunctions.zero() //vein gap
                ),
                SurfaceRules.sequence(

                      //////bedrock floor
                      //SurfaceRules.ifTrue(SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())),
                      //SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(110),1),SurfaceRules.state(Blocks.AIR.defaultBlockState())),
                      //SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(107),1), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.hole()), SurfaceRules.state(ModBlocks.BLOOD_GRASS_BLOCK.get().defaultBlockState()))),
                      //SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(33), 0)), SurfaceRules.state(ModBlocks.BLOOD_DIRT_BLOCK.get().defaultBlockState()))),
                      //SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR), SurfaceRules.state(ModBlocks.BLOOD_GRASS_BLOCK.get().defaultBlockState())),

                      //SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, 0, CaveSurface.FLOOR), SurfaceRules.state(ModBlocks.BLOOD_GRASS_BLOCK.get().defaultBlockState())),
                        ModSurfaceRules.makeRules()

                ),
                List.of(), //spawn targets
                32,
                false,
                false,
                false,
                false
        ));

    }



}



