package net.agusdropout.bloodyhell.worldgen.feature.custom;

import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GiantSpineFeature extends Feature<NoneFeatureConfiguration> {

    public GiantSpineFeature(Codec<NoneFeatureConfiguration> codec) {
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

        boolean hasOpenSky = true;
        for (int dy = 1; dy < 64; dy++) {
            BlockPos check = origin.above(dy);
            if (level.getBlockState(check).isSolid()) {
                hasOpenSky = false;
                break;
            }
        }
        if (!hasOpenSky) return false;

        int height = 25 + random.nextInt(16);
        Direction curveDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        float curveStrength = 0.15f + random.nextFloat() * 0.15f;
        double baseRadius = 3.5 + random.nextDouble();

        BlockPos current = origin;

        for (int y = 0; y < height; y++) {
            double ratio = (double) y / height;
            double radius = baseRadius * (1.0 - ratio / 1.1);
            int r = (int) Math.ceil(radius);

            double curveOffset = Math.sin(ratio * Math.PI) * curveStrength * y;
            BlockPos layerCenter = current.offset(
                    curveDir.getStepX() * (int) curveOffset,
                    0,
                    curveDir.getStepZ() * (int) curveOffset
            );

            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dz * dz > radius * radius) continue;
                    BlockPos pos = layerCenter.offset(dx, 0, dz);
                    if (!level.getBlockState(pos).isAir() && !level.getBlockState(pos).is(Blocks.CAVE_AIR)) continue;

                    float roll = random.nextFloat();

                    float sandstoneChance = (float) Math.max(0.2, 1.0 - ratio * 1.8);
                    float erodedChance = (float) Math.max(0.1, 0.5 - Math.abs(0.5 - ratio));
                    float crackedChance = (float) Math.max(0.1, ratio * 1.0);
                    float fullyErodedChance = (float) Math.max(0.0, ratio * ratio * 0.8);

                    float total = sandstoneChance + erodedChance + crackedChance + fullyErodedChance;
                    sandstoneChance /= total;
                    erodedChance /= total;
                    crackedChance /= total;
                    fullyErodedChance /= total;

                    BlockState block;
                    if (roll < sandstoneChance) {
                        block = SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get().defaultBlockState();
                    } else if (roll < sandstoneChance + erodedChance) {
                        block = SandstoneBlocks.ERODED_BLASPHEMOUS_SANDSTONE.get().defaultBlockState();
                    } else if (roll < sandstoneChance + erodedChance + crackedChance) {
                        block = SandstoneBlocks.CRACKED_BLASPHEMOUS_SANDSTONE.get().defaultBlockState();
                    } else {
                        block = SandstoneBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE.get().defaultBlockState();
                    }

                    level.setBlock(pos, block, 2);
                }
            }

            current = current.above();
        }

        return true;
    }
}