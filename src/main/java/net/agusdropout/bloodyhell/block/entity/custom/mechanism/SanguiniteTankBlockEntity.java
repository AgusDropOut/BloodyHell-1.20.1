package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractTankBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class SanguiniteTankBlockEntity extends AbstractTankBlockEntity {

    public SanguiniteTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_TANK_BE.get(), pos, state);
    }

    @Override
    public boolean isFluidSupported(FluidStack stack) {
        return stack.getFluid() != ModFluids.CORRUPTED_BLOOD_SOURCE.get()
                && stack.getFluid() != ModFluids.CORRUPTED_BLOOD_FLOWING.get()
                && stack.getFluid() != ModFluids.VISCOUS_BLASPHEMY_SOURCE.get()
                && stack.getFluid() != ModFluids.VISCOUS_BLASPHEMY_FLOWING.get();
    }
}