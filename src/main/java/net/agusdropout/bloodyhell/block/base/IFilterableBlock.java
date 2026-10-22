package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.item.registry.WeaponItems;
import net.agusdropout.bloodyhell.block.entity.base.IFluidFilterable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IFilterableBlock {

    default InteractionResult checkFilterInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(WeaponItems.SACRIFICIAL_DAGGER.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);

                if (be instanceof IFluidFilterable filterable) {
                    filterable.cycleFilter(level, pos, player);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}