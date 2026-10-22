package net.agusdropout.bloodyhell.worldgen.biome.surface;

import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import net.agusdropout.bloodyhell.worldgen.biome.ModBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class ModSurfaceRules {

    private static final SurfaceRules.RuleSource BLOOD_DIRT = makeStateRule(SoilBlocks.BLOOD_DIRT_BLOCK.get());
    private static final SurfaceRules.RuleSource BLOOD_GRASS = makeStateRule(SoilBlocks.BLOOD_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource BLOODY_STONE = makeStateRule(StoneBlocks.BLOODY_STONE_BLOCK.get());

    private static final SurfaceRules.RuleSource BLASPHEMOUS_SAND = makeStateRule(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get());
    private static final SurfaceRules.RuleSource BLASPHEMOUS_SANDSTONE = makeStateRule(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get());

    private static final SurfaceRules.RuleSource BEDROCK_FLOOR = SurfaceRules.ifTrue(
            SurfaceRules.verticalGradient("minecraft:bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
            SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())
    );

    private static final SurfaceRules.RuleSource AIR_ABOVE = SurfaceRules.ifTrue(
            SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(110), 1),
            SurfaceRules.state(Blocks.AIR.defaultBlockState())
    );

    private static final SurfaceRules.RuleSource BLOOD_GRASS_SURFACE = SurfaceRules.ifTrue(
            SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(107), 1),
            SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.hole()), BLOOD_GRASS)
    );

    private static final SurfaceRules.RuleSource BLASPHEMOUS_SAND_SURFACE = SurfaceRules.ifTrue(
            SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(107), 1),
            SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.hole()), BLASPHEMOUS_SAND)
    );

    private static final SurfaceRules.RuleSource BLOOD_DIRT_UNDER = SurfaceRules.ifTrue(
            SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
            SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(33), 0)), BLOOD_DIRT)
    );

    private static final SurfaceRules.RuleSource BLOOD_GRASS_FLOOR = SurfaceRules.ifTrue(
            SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
            BLOOD_GRASS
    );

    private static final SurfaceRules.RuleSource BLASPHEMOUS_SAND_FLOOR = SurfaceRules.ifTrue(
            SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
            BLASPHEMOUS_SAND
    );

    private static final SurfaceRules.RuleSource BLASPHEMOUS_SANDSTONE_UNDER = SurfaceRules.ifTrue(
            SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
            SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(33), 0)), BLASPHEMOUS_SANDSTONE)
    );

    private static final SurfaceRules.RuleSource BLOOD_GRASS_STONE_DEPTH_TRUE = SurfaceRules.ifTrue(
            SurfaceRules.stoneDepthCheck(0, true, 0, CaveSurface.FLOOR),
            BLOOD_GRASS
    );

    private static final SurfaceRules.RuleSource BLASPHEMOUS_SANDSTONE_STONE_DEPTH_TRUE = SurfaceRules.ifTrue(
            SurfaceRules.stoneDepthCheck(0, true, 20, CaveSurface.FLOOR),
            BLASPHEMOUS_SANDSTONE
    );

    private static SurfaceRules.RuleSource bloodRules() {
        SurfaceRules.ConditionSource isAtOrAboveWater = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource aLittleBelowSurface = SurfaceRules.abovePreliminarySurface();
        SurfaceRules.ConditionSource underSurface = SurfaceRules.UNDER_FLOOR;

        SurfaceRules.RuleSource bloodSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(isAtOrAboveWater, BLOOD_GRASS),
                BLOOD_DIRT
        );

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.BLOOD_BIOME),
                        SurfaceRules.sequence(
                                BEDROCK_FLOOR,
                                AIR_ABOVE,
                                BLOOD_GRASS_SURFACE,
                                SurfaceRules.ifTrue(aLittleBelowSurface, bloodSurface),
                                BLOOD_DIRT_UNDER,
                                BLOOD_GRASS_FLOOR,
                                BLOOD_GRASS_STONE_DEPTH_TRUE,
                                SurfaceRules.ifTrue(underSurface, BLOODY_STONE)
                        )
                )
        );
    }

    private static SurfaceRules.RuleSource blasphemousRules() {
        SurfaceRules.ConditionSource isAtOrAboveWater = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource aLittleBelowSurface = SurfaceRules.abovePreliminarySurface();
        SurfaceRules.ConditionSource underSurface = SurfaceRules.UNDER_FLOOR;

        SurfaceRules.RuleSource blasphemousSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(isAtOrAboveWater, BLASPHEMOUS_SAND),
                BLASPHEMOUS_SANDSTONE
        );

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.BLASPHEMOUS_BIOME),
                        SurfaceRules.sequence(
                                BEDROCK_FLOOR,
                                AIR_ABOVE,
                                BLASPHEMOUS_SAND_SURFACE,
                                SurfaceRules.ifTrue(aLittleBelowSurface, blasphemousSurface),
                                BLASPHEMOUS_SANDSTONE_UNDER,
                                BLASPHEMOUS_SAND_FLOOR,
                                BLASPHEMOUS_SANDSTONE_STONE_DEPTH_TRUE,
                                SurfaceRules.ifTrue(underSurface, BLASPHEMOUS_SANDSTONE)
                        )
                )
        );
    }

    public static SurfaceRules.RuleSource makeRules() {
        return SurfaceRules.sequence(
                bloodRules(),
                blasphemousRules()
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}