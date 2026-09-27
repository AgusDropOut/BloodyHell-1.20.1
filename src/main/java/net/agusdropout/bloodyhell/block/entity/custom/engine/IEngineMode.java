package net.agusdropout.bloodyhell.block.entity.custom.engine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public interface IEngineMode {
    boolean canProcess(RhnullBloodEngineBlockEntity engine);
    void tickProcess(Level level, BlockPos pos, BlockState state, RhnullBloodEngineBlockEntity engine);
    void finishProcess(Level level, BlockPos pos, RhnullBloodEngineBlockEntity engine);
    void resetProcess();


    default Vector3f getCustomBaseColor() {
        return null;
    }


    default Vector3f getCustomGlowColor() {
        return null;
    }
}