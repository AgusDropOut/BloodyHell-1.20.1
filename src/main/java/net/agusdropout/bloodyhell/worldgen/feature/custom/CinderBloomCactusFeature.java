package net.agusdropout.bloodyhell.worldgen.feature.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import com.mojang.serialization.Codec;
import net.agusdropout.bloodyhell.block.base.BaseWallPlantBlock;
import net.agusdropout.bloodyhell.block.custom.CinderBloomCactusRoot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CinderBloomCactusFeature extends Feature<NoneFeatureConfiguration> {

    public CinderBloomCactusFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        BlockPos ground = origin.below();
        if (!level.getBlockState(ground).is(Blocks.SAND) &&
                !level.getBlockState(ground).is(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get())) {
            return false;
        }

        int height = 2 + random.nextInt(3);

        for (int y = 0; y <= height; y++) {
            if (!level.getBlockState(origin.above(y)).isAir()) {
                return false;
            }
        }

        placeRoot(level, origin, random);

        for (int y = 1; y < height; y++) {
            BlockPos segmentPos = origin.above(y);
            placeRoot(level, segmentPos, random);
        }

        BlockPos topPos = origin.above(height);
        level.setBlock(topPos, PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get().defaultBlockState(), 2);
        maybePlaceFlowersAround(level, topPos, random);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            generateArm(level, topPos, dir, random);
        }

        return true;
    }

    private void placeRoot(WorldGenLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get().defaultBlockState(), 2);
        maybePlaceFlowersAround(level, pos, random);
    }

    private void maybePlaceFlowersAround(WorldGenLevel level, BlockPos rootPos, RandomSource random) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.1F) {
                BlockPos flowerPos = rootPos.relative(dir);
                if (level.getBlockState(flowerPos).isAir()) {
                    level.setBlock(
                            flowerPos,
                            PlantBlocks.CINDER_BLOOM_CACTUS_FLOWER.get()
                                    .defaultBlockState()
                                    .setValue(BaseWallPlantBlock.FACING, dir),
                            3
                    );
                }
            }
        }
    }

    private void generateArm(WorldGenLevel level, BlockPos startPos, Direction dir, RandomSource random) {
        if (random.nextFloat() < 0.35F || random.nextFloat() > 0.75F) {
            BlockPos armBase = startPos.relative(dir);
            if (!level.getBlockState(armBase).isAir()) return;

            level.setBlock(armBase, PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get()
                    .defaultBlockState()
                    .setValue(CinderBloomCactusRoot.AXIS, dir.getAxis()), 2);
            maybePlaceFlowersAround(level, armBase, random);

            BlockPos tipPos = armBase.relative(dir);
            if (level.getBlockState(tipPos).isAir()) {
                level.setBlock(tipPos, PlantBlocks.CINDER_BLOOM_CACTUS_CON.get()
                        .defaultBlockState()
                        .setValue(BaseWallPlantBlock.FACING, dir), 2);
                maybePlaceFlowersAround(level, tipPos, random);

                int armHeight = 2 + random.nextInt(3);
                for (int y = 1; y <= armHeight; y++) {
                    BlockPos segmentPos = tipPos.above(y);
                    if (!level.getBlockState(segmentPos).isAir()) break;

                    level.setBlock(segmentPos, PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get()
                            .defaultBlockState()
                            .setValue(CinderBloomCactusRoot.AXIS, Direction.Axis.Y), 2);
                    maybePlaceFlowersAround(level, segmentPos, random);
                }
            }
        }
    }
}