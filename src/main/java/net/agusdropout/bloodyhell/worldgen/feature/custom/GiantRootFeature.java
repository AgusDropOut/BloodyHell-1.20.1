package net.agusdropout.bloodyhell.worldgen.feature.custom;

import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GiantRootFeature extends Feature<NoneFeatureConfiguration> {

    public GiantRootFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }



        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            WorldGenLevel level = context.level();
            BlockPos origin = context.origin();
            RandomSource random = context.random();

            // --- Must be underground (no open sky above) ---
            for (int dy = 1; dy < 20; dy++) {
                if (level.canSeeSky(origin.above(dy))) return false;
            }

            // --- Ensure valid ground below ---
            BlockPos ground = origin;
            while (level.getBlockState(ground).isAir() && ground.getY() > level.getMinBuildHeight() + 5) {
                ground = ground.below();
            }

            // Only spawn if base is a solid, natural block
            BlockState groundState = level.getBlockState(ground);
            if (!(groundState.is(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get()) || groundState.is(Blocks.STONE))) {
                return false;
            }

            // --- Find ceiling above ---
            BlockPos ceiling = null;
            for (int dy = 6; dy < 60; dy++) {
                BlockPos check = ground.above(dy);
                if (level.getBlockState(check).isSolid()) {
                    ceiling = check;
                    break;
                }
            }
            if (ceiling == null) return false;

            int height = ceiling.getY() - ground.getY();
            if (height < 6) return false;

            // --- Shape properties ---
            Direction bendDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            float bendStrength = 0.05f + random.nextFloat() * 0.1f;
            double baseRadius = 2.5 + random.nextDouble() * 1.0;

            // --- Place base support ---
            level.setBlock(ground.above(), SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get().defaultBlockState(), 2);

            // --- Build column ---
            for (int y = 0; y <= height; y++) {
                double ratio = (double) y / height;
                double radius = baseRadius * (1.0 - 0.15 * Math.sin(ratio * Math.PI));
                double offset = Math.sin(ratio * Math.PI * 2.0) * bendStrength * height;

                BlockPos layerCenter = ground.above(y).offset(
                        bendDir.getStepX() * (int) offset,
                        0,
                        bendDir.getStepZ() * (int) offset
                );

                int r = (int) Math.ceil(radius);
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        if (dx * dx + dz * dz > radius * radius) continue;
                        BlockPos pos = layerCenter.offset(dx, 0, dz);

                        if (!level.getBlockState(pos).isAir() && !level.getBlockState(pos).is(Blocks.CAVE_AIR)) continue;

                        float ratioFromCenter = (float) Math.abs(ratio - 0.5);
                        float roll = random.nextFloat();


                        BlockState block;
                        if (y < 2) {
                            block = SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get().defaultBlockState(); // base
                        } else if (y > height - 2) {
                            block = SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get().defaultBlockState(); // near ceiling
                        } else if (roll < ratioFromCenter) {
                            block = SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get().defaultBlockState();
                        } else if (roll < 0.6f) {
                            block = SandstoneBlocks.CRACKED_BLASPHEMOUS_SANDSTONE.get().defaultBlockState();
                        } else if (roll < 0.85f) {
                            block = SandstoneBlocks.ERODED_BLASPHEMOUS_SANDSTONE.get().defaultBlockState();
                        } else {
                            block = SandstoneBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE.get().defaultBlockState();
                        }

                        level.setBlock(pos, block, 2);
                    }
                }
            }

            // --- Place connection to ceiling ---
            BlockPos ceilingAttach = ceiling.below();
            if (level.getBlockState(ceilingAttach).isAir() || level.getBlockState(ceilingAttach).is(Blocks.CAVE_AIR)) {
                level.setBlock(ceilingAttach, SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get().defaultBlockState(), 2);
            }

            return true;
        }
    }
