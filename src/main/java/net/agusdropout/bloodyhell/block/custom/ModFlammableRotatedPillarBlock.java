package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.registry.WoodBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;

public class ModFlammableRotatedPillarBlock extends RotatedPillarBlock {

    public ModFlammableRotatedPillarBlock(Properties p_55926_) {
        super(p_55926_);
    }
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if(context.getItemInHand().getItem() instanceof AxeItem) {
            if(state.is(WoodBlocks.BLOOD_LOG.get())) {
                return WoodBlocks.STRIPPED_BLOOD_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
            if(state.is(WoodBlocks.SOUL_LOG.get())) {
                return WoodBlocks.STRIPPED_SOUL_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }

            if(state.is(WoodBlocks.BLOOD_WOOD.get())) {
                return WoodBlocks.STRIPPED_BLOOD_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
            if(state.is(WoodBlocks.SOUL_WOOD.get())) {
                return WoodBlocks.STRIPPED_SOUL_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }

        return super.getToolModifiedState(state, context, toolAction, simulate);
    }


}
