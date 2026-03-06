package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.agusdropout.bloodyhell.block.base.AbstractTankBlock;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.RhnullTankBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RhnullTankBlock extends AbstractTankBlock {

    public RhnullTankBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RhnullTankBlockEntity(pos, state);
    }
}