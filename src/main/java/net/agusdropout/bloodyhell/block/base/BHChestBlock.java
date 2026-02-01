package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.block.entity.custom.BHChestBlockEntity;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BHChestBlock extends ChestBlock {

        public BHChestBlock(Properties properties) {
            super(properties,   ModBlockEntities.BH_CHEST::get);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new BHChestBlockEntity(pos, state);
        }
    }

