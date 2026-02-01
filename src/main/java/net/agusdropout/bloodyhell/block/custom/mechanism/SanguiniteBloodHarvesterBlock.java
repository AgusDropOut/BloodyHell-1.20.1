package net.agusdropout.bloodyhell.block.custom.mechanism;

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

public class SanguiniteBloodHarvesterBlock extends BaseEntityBlock {

    public SanguiniteBloodHarvesterBlock(Properties properties) {
        super(properties);
    }

    // 1. Render Shape: ENTITYBLOCK_ANIMATED is vital for GeckoLib!
    // If you use MODEL, the static JSON and the animated model will render on top of each other (Z-Fighting).
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    // 2. Link the BlockEntity
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SanguiniteBloodHarvesterBlockEntity(pos, state);
    }

    // 3. Register the Ticker (This makes the tick() method in your BE actually run)
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // We only tick on the Server side (Logic), but we need the client to potentially
        // handle animation state syncing if you did manual packet stuff.
        // However, standard ticking for logic is usually server-side.
        // Since we are syncing data via 'sendBlockUpdated', ticking on Server is sufficient logic-wise.

        return createTickerHelper(type, ModBlockEntities.SANGUINITE_BLOOD_HARVESTER_BE.get(),
                SanguiniteBloodHarvesterBlockEntity::tick);
    }

    // 4. Debug Interaction (Optional)
    // Useful to verify if the machine is working by Right-Clicking it
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            // Optional: Add logic here to open a GUI or check status
            // For now, standard success ensures the hand animation plays
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}