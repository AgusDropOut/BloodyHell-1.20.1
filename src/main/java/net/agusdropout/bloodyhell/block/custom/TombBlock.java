package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.TombBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class TombBlock extends BaseEntityBlock {
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    // 1. Añadimos la propiedad de dirección horizontal (Norte, Sur, Este, Oeste)
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TombBlock(Properties properties) {
        super(properties);
        // 2. Definimos el estado por defecto (Mirando al Norte y Cerrado)
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(OPEN, false)
                .setValue(FACING, Direction.NORTH));
    }

    // 3. Este método detecta cómo colocar el bloque según dónde mira el jugador
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // .getOpposite() hace que la "cara" del bloque mire HACIA el jugador
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // 4. Registramos ambas propiedades
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }

    /* 5. (Opcional pero recomendado) Soporte para rotación y espejo
          (útil si usas world edit o structure blocks)
    */
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // --- El resto se mantiene igual ---
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TombBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.TOMB_ENTITY.get(), TombBlockEntity::tick);
    }
}