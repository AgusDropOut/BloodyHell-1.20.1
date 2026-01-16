package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.entity.BloodAltarBlockEntity;
import net.agusdropout.bloodyhell.util.VanillaPacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodAltarBlock extends BaseEntityBlock {
    private BloodAltarBlockEntity mainBloodAltarEntity;
    public static final BooleanProperty ITEMINSIDE = BooleanProperty.create("iteminside");

    public BloodAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ITEMINSIDE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ITEMINSIDE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        mainBloodAltarEntity = new BloodAltarBlockEntity(pos, state);
        return mainBloodAltarEntity;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BloodAltarBlockEntity) {
                ((BloodAltarBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(blockPos) instanceof BloodAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (interactionHand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();

            if (!heldItem.isEmpty()) {
                if (altar.isSpace()) {
                    ItemStack itemToStore = heldItem.copy();
                    itemToStore.setCount(1);
                    heldItem.shrink(1);
                    boolean result = altar.storeItem(itemToStore);
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
                    if (result) {
                        level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                        level.setBlock(blockPos, blockState.setValue(ITEMINSIDE, true), 3);
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            } else {
                if (altar.isSomethingInside()) {
                    ItemStack retrievedItem = altar.retrieveItem();
                    if (!retrievedItem.isEmpty()) {
                        player.getInventory().placeItemBackInInventory(retrievedItem);
                        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
                        level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                        if (!altar.isSomethingInside()) {
                            level.setBlock(blockPos, blockState.setValue(ITEMINSIDE, false), 3);
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0 && state.getValue(ITEMINSIDE)) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WARDEN_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
        super.animateTick(state, level, pos, random);
    }

    public List<Item> getItemsInside() {
        return mainBloodAltarEntity.getItemsInside();
    }

    public boolean clearItemsInside() {
        return mainBloodAltarEntity.clearItemsInside();
    }
}