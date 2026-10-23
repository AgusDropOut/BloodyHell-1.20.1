package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.base.AbstractCondenserBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class SanguiniteCondenserBlockEntity extends AbstractCondenserBlockEntity {

    public SanguiniteCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_CONDENSER_BE.get(), pos, state);
    }

    @Override
    protected boolean isValidFluid(FluidStack stack) {
        return !stack.getFluid().isSame(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get()) && !stack.getFluid().isSame(ModFluids.CORRUPTED_BLOOD_SOURCE.get());
    }


    @Override
    public String getAssetPathName() {
        return "sanguinite_condenser";
    }
}