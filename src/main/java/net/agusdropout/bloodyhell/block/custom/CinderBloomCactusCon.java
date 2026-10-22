package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.base.BaseWallPlantBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CinderBloomCactusCon extends BaseWallPlantBlock {

    public CinderBloomCactusCon(Properties properties) {
        super(properties);
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        List<Block> validSupports = List.of(
                PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get(),
                PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get(),
                PlantBlocks.CINDER_BLOOM_CACTUS_CON.get()
        );


        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) continue;
            BlockPos checkPos = pos.relative(dir);
            BlockState checkState = level.getBlockState(checkPos);

            if (validSupports.contains(checkState.getBlock())) {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean isSupport(LevelReader world, BlockPos pos, BlockState blockState, Direction direction) {

        List<Block> validSupports = List.of(
                SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get(),
                Blocks.SAND,
                PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get(),
                PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get(),
                PlantBlocks.CINDER_BLOOM_CACTUS_CON.get()
        );

        return validSupports.contains(blockState.getBlock());
    }
}