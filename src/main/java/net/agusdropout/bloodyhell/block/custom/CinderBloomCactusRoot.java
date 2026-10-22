package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
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

public class CinderBloomCactusRoot extends RotatedPillarBlock {

    public CinderBloomCactusRoot(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        boolean hasValidBelow =
                belowState.is(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get()) ||
                        belowState.is(Blocks.SAND) ||
                        belowState.is(PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get()) ||
                        belowState.is(PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get()) ||
                        belowState.is(PlantBlocks.CINDER_BLOOM_CACTUS_CON.get());

        boolean hasValidSide = false;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos sidePos = pos.relative(dir);
            BlockState sideState = level.getBlockState(sidePos);

            if (sideState.is(PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get())){
                hasValidSide = true;
                break;
            }
        }

        return hasValidBelow || hasValidSide;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();

        BlockState defaultState = this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
        return defaultState.canSurvive(level, pos) ? defaultState : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {

        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}
