package net.agusdropout.bloodyhell.block.custom.mushroom;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class VoraciousMushroomBlock extends Block {

    public VoraciousMushroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

      //  // 1. MUTATION: FUMESTALK (Infector)
      //  // Recipe: Fermented Spider Eye
      //  if (heldItem.getItem() == Items.FERMENTED_SPIDER_EYE) {
      //      mutate(level, pos, ModBlocks.FUMESTALK.get().defaultBlockState(), player, heldItem);
      //      return InteractionResult.sidedSuccess(level.isClientSide);
      //  }
//
      //  // 2. MUTATION: CONSTRICTOR (Trapper)
      //  // Recipe: Slime Ball
      //  if (heldItem.getItem() == Items.SLIME_BALL) {
      //      mutate(level, pos, ModBlocks.CONSTRICTOR_TENDRIL.get().defaultBlockState(), player, heldItem);
      //      return InteractionResult.sidedSuccess(level.isClientSide);
      //  }
//
      //  // 3. MUTATION: EROS POLYP (Breeder)
      //  // Recipe: Golden Carrot
      //  if (heldItem.getItem() == Items.GOLDEN_CARROT) {
      //      mutate(level, pos, ModBlocks.EROS_POLYP.get().defaultBlockState(), player, heldItem);
      //      return InteractionResult.sidedSuccess(level.isClientSide);
      //  }
//
      //  // 4. MUTATION: CARRION COCOON (Spawner)
      //  // Recipe: Rotten Flesh
      //  if (heldItem.getItem() == Items.ROTTEN_FLESH) {
      //      mutate(level, pos, ModBlocks.CARRION_COCOON.get().defaultBlockState(), player, heldItem);
      //      return InteractionResult.sidedSuccess(level.isClientSide);
      //  }

        return InteractionResult.PASS;
    }

    private void mutate(Level level, BlockPos pos, BlockState newState, Player player, ItemStack usedItem) {
        if (!level.isClientSide) {
            // Consume Item
            if (!player.isCreative()) {
                usedItem.shrink(1);
            }

            // Play Effect
            level.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1.0f, 1.0f);

            // Replace Block
            level.setBlock(pos, newState, 3);
        } else {
            // Client Visuals
            for (int i = 0; i < 20; i++) {
                level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5 + (Math.random() - 0.5),
                        pos.getY() + 0.5 + (Math.random() - 0.5),
                        pos.getZ() + 0.5 + (Math.random() - 0.5),
                        0, 0, 0);
            }
        }
    }
}