package net.agusdropout.bloodyhell.worldgen.feature.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import com.mojang.serialization.Codec;
import net.agusdropout.bloodyhell.block.base.BaseWallPlantBlock;
import net.agusdropout.bloodyhell.block.custom.CinderBloomCactusFlower;
import net.agusdropout.bloodyhell.block.custom.CinderBloomCactusRoot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CinderBloomCactusFlowerFeature extends Feature<NoneFeatureConfiguration> {

    public CinderBloomCactusFlowerFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        if (!level.isEmptyBlock(pos)) {
            return false;
        }

        Block cactusBlock = PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get();
        BlockState flowerState = PlantBlocks.CINDER_BLOOM_CACTUS_FLOWER.get().defaultBlockState();

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos adjacentPos = pos.relative(dir);
            BlockState neighbor = level.getBlockState(adjacentPos);

            if (neighbor.is(cactusBlock)) {
                Direction facing = dir.getOpposite();
                level.setBlock(
                        pos,
                        flowerState.setValue(BaseWallPlantBlock.FACING, facing),
                        3
                );
                return true;
            }
        }

        return false;
    }
}