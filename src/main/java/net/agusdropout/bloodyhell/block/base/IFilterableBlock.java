package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.block.entity.base.IFluidFilterable;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IFilterableBlock {

    /**
     * Handles the interaction logic for filtering fluids.
     * Call this inside your Block's use() method.
     *
     * @return InteractionResult.SUCCESS if the filter was cycled, PASS otherwise.
     */
    default InteractionResult checkFilterInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Check for Sacrificial Dagger
        if (stack.is(ModItems.SACRIFICIAL_DAGGER.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                // Interact with the Block Entity abstraction we made earlier
                if (be instanceof IFluidFilterable filterable) {
                    filterable.cycleFilter(level, pos, player);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // Return PASS so the block can continue with its normal logic (e.g. opening GUIs)
        return InteractionResult.PASS;
    }
}