package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.RhnullBloodEngineBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.RhnullBloodEnginePillarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RhnullBloodEnginePillarBlock extends Block implements EntityBlock {

    public static final EnumProperty<Corner> CORNER = EnumProperty.create("corner", Corner.class);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 32, 16);

    public RhnullBloodEnginePillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CORNER, Corner.NONE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            updateCornerState(level, pos, state);
        }
    }

    public void updateCornerState(Level level, BlockPos pos, BlockState state) {
        BlockPos[] offsets = {
                pos.offset(1, 0, 1), pos.offset(1, 0, -1),
                pos.offset(-1, 0, 1), pos.offset(-1, 0, -1)
        };
        Corner newCorner = Corner.NONE;

        for (BlockPos offsetPos : offsets) {
            if (level.getBlockEntity(offsetPos) instanceof RhnullBloodEngineBlockEntity) {
                int dx = pos.getX() - offsetPos.getX();
                int dz = pos.getZ() - offsetPos.getZ();

                if (dx == -1 && dz == -1) newCorner = Corner.NORTH_WEST;
                else if (dx == 1 && dz == -1) newCorner = Corner.NORTH_EAST;
                else if (dx == -1 && dz == 1) newCorner = Corner.SOUTH_WEST;
                else if (dx == 1 && dz == 1) newCorner = Corner.SOUTH_EAST;
                break;
            }
        }

        if (state.getValue(CORNER) != newCorner) {
            level.setBlock(pos, state.setValue(CORNER, newCorner), 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof RhnullBloodEnginePillarBlockEntity be) {
            return be.interact(player, hand);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CORNER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RhnullBloodEnginePillarBlockEntity(pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.RHNULL_BLOOD_ENGINE_PILLAR.get(),
                (l, p, s, be) -> be.tick(l, p, s));
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker) {
        return expected == actual ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public enum Corner implements StringRepresentable {
        NONE("none"), NORTH_WEST("nw"), NORTH_EAST("ne"), SOUTH_WEST("sw"), SOUTH_EAST("se");

        private final String name;

        Corner(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}