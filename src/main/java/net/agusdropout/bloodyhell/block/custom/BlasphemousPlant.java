package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlasphemousPlant extends BushBlock {
    public BlasphemousPlant(Properties p_51021_) {
        super(p_51021_);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos pos) {
        return blockState.is(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get());
    }
}
