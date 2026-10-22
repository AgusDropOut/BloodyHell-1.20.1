package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

import static net.minecraft.world.level.levelgen.structure.Structure.simpleCodec;

public class SpreadingBloodSoilBlock extends SpreadingSnowyDirtBlock {
    //public static final MapCodec<SpreadingBloodSoilBlock> CODEC = simpleCodec(SpreadingBloodSoilBlock::new);

    public SpreadingBloodSoilBlock(Properties properties) {
        super(properties);
    }



    private static boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        return canBeGrass(state, level, pos) && !level.getFluidState(blockpos).is(FluidTags.WATER);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canBeGrass(state, level, pos)) {
            if (!level.isAreaLoaded(pos, 3))
                return;
            level.setBlockAndUpdate(pos, SoilBlocks.BLOOD_DIRT_BLOCK.get().defaultBlockState());
        } else {
            if (!level.isAreaLoaded(pos, 3)) return;
            BlockState blockstate = this.defaultBlockState();

            for (int i = 0; i < 4; ++i) {
                BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (level.getBlockState(blockpos).is(SoilBlocks.BLOOD_DIRT_BLOCK.get()) && canPropagate(blockstate, level, blockpos)) {
                    level.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, level.getBlockState(blockpos.above()).is(Blocks.SNOW)));
                }
            }
        }
    }
    private static boolean canBeGrass(BlockState p_56824_, LevelReader p_56825_, BlockPos p_56826_) {
        BlockPos blockpos = p_56826_.above();
        BlockState blockstate = p_56825_.getBlockState(blockpos);
        if (blockstate.is(Blocks.SNOW) && (Integer)blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockstate.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(p_56825_, p_56824_, p_56826_, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(p_56825_, blockpos));
            return i < p_56825_.getMaxLightLevel();
        }
    }
}
