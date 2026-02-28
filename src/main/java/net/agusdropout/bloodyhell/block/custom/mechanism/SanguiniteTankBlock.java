package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.agusdropout.bloodyhell.block.base.IFilterableBlock;
import net.agusdropout.bloodyhell.block.base.IFlaskInteractableBlock;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class SanguiniteTankBlock extends BaseEntityBlock implements IFilterableBlock, IFlaskInteractableBlock {

    public static final EnumProperty<ConnectionType> NORTH = EnumProperty.create("north", ConnectionType.class);
    public static final EnumProperty<ConnectionType> SOUTH = EnumProperty.create("south", ConnectionType.class);
    public static final EnumProperty<ConnectionType> EAST = EnumProperty.create("east", ConnectionType.class);
    public static final EnumProperty<ConnectionType> WEST = EnumProperty.create("west", ConnectionType.class);
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public SanguiniteTankBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, true).setValue(DOWN, true)
                .setValue(NORTH, ConnectionType.SINGLE)
                .setValue(SOUTH, ConnectionType.SINGLE)
                .setValue(EAST, ConnectionType.SINGLE)
                .setValue(WEST, ConnectionType.SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return calculateState(state, level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return calculateState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }



    // --- LOGIC ---
    private BlockState calculateState(BlockState state, LevelAccessor level, BlockPos pos) {
        return state
                .setValue(UP, !isTank(level, pos.above()))
                .setValue(DOWN, !isTank(level, pos.below()))
                .setValue(NORTH, getConnection(level, pos, Direction.NORTH))
                .setValue(SOUTH, getConnection(level, pos, Direction.SOUTH))
                .setValue(EAST, getConnection(level, pos, Direction.EAST))
                .setValue(WEST, getConnection(level, pos, Direction.WEST));
    }

    private ConnectionType getConnection(LevelAccessor level, BlockPos pos, Direction face) {
        if (isTank(level, pos.relative(face))) return ConnectionType.NONE;

        Direction leftDir = face.getClockWise();
        Direction rightDir = face.getCounterClockWise();

        boolean tankLeft = isTank(level, pos.relative(leftDir));
        boolean tankRight = isTank(level, pos.relative(rightDir));

        boolean tankLeftOfLeft = isTank(level, pos.relative(leftDir).relative(leftDir));
        boolean tankRightOfRight = isTank(level, pos.relative(rightDir).relative(rightDir));


        if ((!tankLeft && !tankRight) || (tankLeft && tankRight)) return ConnectionType.SINGLE;


        if (!tankLeft && tankRight) {
            if (!tankRightOfRight) return ConnectionType.LEFT_SMALL;
            return ConnectionType.LEFT_BIG;
        }

        if (tankLeft && !tankRight) {
            if (!tankLeftOfLeft) return ConnectionType.RIGHT_SMALL;
            return ConnectionType.RIGHT_BIG;
        }

        return ConnectionType.SINGLE;
    }

    private boolean isTank(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() == this;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SanguiniteTankBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            InteractionResult filterResult = checkFilterInteraction(state, level, pos, player, hand);

            if (filterResult != InteractionResult.PASS) {
                return filterResult;
            }

            InteractionResult flaskResult = handleFlaskInteraction(state, level, pos, player, hand);

            if (flaskResult != InteractionResult.PASS) {
                return flaskResult;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SanguiniteTankBlockEntity cistern) {
                BlockEntity controller = cistern.getController();
                if (controller != null) {
                    boolean success = FluidUtil.interactWithFluidHandler(player, hand, controller.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null));
                    if (success) return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SanguiniteTankBlockEntity tank) {

            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}