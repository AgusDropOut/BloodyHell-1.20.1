package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BHChestBlockEntity extends ChestBlockEntity {
    public BHChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BH_CHEST.get(), pos, state);
    }
}
