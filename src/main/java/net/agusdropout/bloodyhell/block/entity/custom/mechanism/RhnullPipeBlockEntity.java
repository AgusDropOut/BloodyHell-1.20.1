package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class RhnullPipeBlockEntity extends AbstractPipeBlockEntity {

    public RhnullPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RHNULL_PIPE_BE.get(), pos, state, 1000);
    }

    @Override
    protected boolean isFluidSupported(FluidStack stack) {
        return true;
    }

    @Override
    protected void handleUnsupportedFluidTick(Level level, BlockPos pos, BlockState state) {
    }

    @Override
    protected void onSupportedFluidTick(Level level, BlockPos pos, BlockState state) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
}