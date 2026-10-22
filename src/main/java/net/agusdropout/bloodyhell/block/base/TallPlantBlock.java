package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class TallPlantBlock extends BushBlock {
    public static final EnumProperty<TallPlantPart> PART = EnumProperty.create("part", TallPlantPart.class);

    public TallPlantBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, TallPlantPart.TOP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }


    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos pos) {
        return blockState.is(this) || blockState.is(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get());
    }
}
