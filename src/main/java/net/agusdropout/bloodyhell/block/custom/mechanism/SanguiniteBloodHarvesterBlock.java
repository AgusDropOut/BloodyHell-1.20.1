package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.agusdropout.bloodyhell.block.base.IFlaskInteractableBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;

import net.agusdropout.bloodyhell.block.entity.custom.SanguiniteBloodHarvesterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SanguiniteBloodHarvesterBlock extends BaseEntityBlock implements IFlaskInteractableBlock {

    public SanguiniteBloodHarvesterBlock(Properties properties) {
        super(properties);
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SanguiniteBloodHarvesterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        return createTickerHelper(type, ModBlockEntities.SANGUINITE_BLOOD_HARVESTER_BE.get(),
                SanguiniteBloodHarvesterBlockEntity::tick);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            InteractionResult filterResult = handleFlaskInteraction(state, level, pos, player, hand);
            if (filterResult.consumesAction()) {
                return filterResult;
            }

            return handleFlaskInteraction(state, level, pos, player, hand);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}