package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.base.BaseSanguineLapidaryBlock;
import net.agusdropout.bloodyhell.block.entity.base.BaseSanguineLapidaryBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.SanguineLapidaryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SanguineLapidaryBlock extends BaseSanguineLapidaryBlock {
    public SanguineLapidaryBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SanguineLapidaryBlockEntity(pos, state);
    }
}
