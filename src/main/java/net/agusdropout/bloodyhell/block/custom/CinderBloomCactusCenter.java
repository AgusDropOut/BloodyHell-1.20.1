package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CinderBloomCactusCenter extends Block {

    public CinderBloomCactusCenter(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {

        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        boolean belowSupport = belowState.is(PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get()) ||
                belowState.is(PlantBlocks.CINDER_BLOOM_CACTUS_CON.get());




        return belowSupport;


    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();


        return this.defaultBlockState().canSurvive(level, pos) ? this.defaultBlockState() : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}