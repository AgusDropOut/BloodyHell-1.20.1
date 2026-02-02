package net.agusdropout.bloodyhell.block.custom.plant;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;

import net.agusdropout.bloodyhell.block.entity.custom.plant.BloodGemSproutBlockEntity;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BloodGemSproutBlock extends BaseEntityBlock {
    // 0 = Empty/Just Planted, 3 = Fully Grown
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public BloodGemSproutBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BloodGemSproutBlockEntity sprout) {
                // If fully grown (Age 3), harvest
                if (state.getValue(AGE) == 3) {
                    // Logic to drop specific gem based on sprout.getGemColor()
                    // For now, dropping a placeholder
                    popResource(level, pos, new ItemStack(ModItems.FILLED_BLOOD_FLASK.get()));

                    // Reset growth
                    level.setBlock(pos, state.setValue(AGE, 0), 3);
                    sprout.resetGrowth();
                    level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                } else {
                    // Debug: Check fluids or status
                    // sprout.printDebugInfo(player);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        // MODEL = Render the "Base" (Pedestal) via JSON
        // The Crystal Overlay is rendered via BER
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BloodGemSproutBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null; // Only server handles growth
        return createTickerHelper(type, ModBlockEntities.BLOOD_GEM_SPROUT_BE.get(),
                (l, p, s, be) -> be.tick(l, p, s));
    }
}