package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseSanguineLapidaryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SanguineLapidaryBlockEntity extends BaseSanguineLapidaryBlockEntity {
    public SanguineLapidaryBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);
    }

    public SanguineLapidaryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINE_LAPIDARY_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Sanguine Lapidary");

    }


}
