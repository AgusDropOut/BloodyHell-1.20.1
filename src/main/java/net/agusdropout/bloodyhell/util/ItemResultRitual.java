package net.agusdropout.bloodyhell.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class ItemResultRitual extends Ritual {
    protected ItemStack result;
    public ItemResultRitual(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, List<List<Item>> itemsInput) {
        super(blockState, level, blockPos, player, interactionHand, blockHitResult, itemsInput);
    }
    @Override
    public void applyResults() {

    }
}
