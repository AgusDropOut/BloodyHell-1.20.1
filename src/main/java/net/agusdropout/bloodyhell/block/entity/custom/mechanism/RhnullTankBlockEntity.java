package net.agusdropout.bloodyhell.block.entity.custom.mechanism;



import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class RhnullTankBlockEntity extends AbstractTankBlockEntity {

    public RhnullTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RHNULL_TANK_BE.get(), pos, state);
    }

    @Override
    public boolean isFluidSupported(FluidStack stack) {
        return true;
    }
}
