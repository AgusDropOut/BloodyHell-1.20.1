package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.agusdropout.bloodyhell.block.base.AbstractCondenserBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SanguiniteCondenserBlock extends AbstractCondenserBlock {


    private static final VoxelShape LOWER_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);
    private static final VoxelShape UPPER_SHAPE = box(0.0D, -16.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public SanguiniteCondenserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return ModBlockEntities.SANGUINITE_CONDENSER_BE.get().create(pos, state);
        }
        return null;
    }
}