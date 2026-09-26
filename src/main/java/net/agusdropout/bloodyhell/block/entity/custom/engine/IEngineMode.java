package net.agusdropout.bloodyhell.block.entity.custom.engine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IEngineMode {
    boolean canProcess(RhnullBloodEngineBlockEntity engine);
    void tickProcess(Level level, BlockPos pos, BlockState state, RhnullBloodEngineBlockEntity engine);
    void finishProcess(Level level, BlockPos pos, RhnullBloodEngineBlockEntity engine);
    void resetProcess();
}