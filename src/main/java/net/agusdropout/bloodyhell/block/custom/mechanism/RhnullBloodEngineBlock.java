package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.RhnullBloodEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RhnullBloodEngineBlock extends Block implements EntityBlock {

    public RhnullBloodEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RhnullBloodEngineBlockEntity engine) {
                engine.setActive(!engine.isActive());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RhnullBloodEngineBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.RHNULL_BLOOD_ENGINE.get(),
                (l, p, s, be) -> be.tick(l, p, s));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker) {
        return expected == actual ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!oldState.is(state.getBlock())) {
            BlockPos[] offsets = {
                    pos.offset(1, 0, 1), pos.offset(1, 0, -1),
                    pos.offset(-1, 0, 1), pos.offset(-1, 0, -1)
            };
            for (BlockPos offsetPos : offsets) {
                BlockState offsetState = level.getBlockState(offsetPos);
                if (offsetState.getBlock() instanceof RhnullBloodEnginePillarBlock pillar) {
                    pillar.updateCornerState(level, offsetPos, offsetState);
                }
            }
        }
    }
}