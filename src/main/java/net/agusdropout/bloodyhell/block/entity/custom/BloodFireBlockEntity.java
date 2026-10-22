package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseFireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BloodFireBlockEntity extends BaseFireBlockEntity {

    public BloodFireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_FIRE_BLOCK_ENTITY.get(), pos, state);
    }

}