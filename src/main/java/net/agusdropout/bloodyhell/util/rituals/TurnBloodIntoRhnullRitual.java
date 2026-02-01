package net.agusdropout.bloodyhell.util.rituals;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.Ritual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class TurnBloodIntoRhnullRitual extends Ritual {

    public TurnBloodIntoRhnullRitual(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, List<List<Item>> itemsInput) {
        super(blockState, level, blockPos, player, interactionHand, blockHitResult, itemsInput);
        super.itemsNeeded = List.of(List.of(ModItems.BLOODY_SOUL_DUST.get(),Items.GOLD_INGOT,ModItems.GLOW_MUSHROOM.get()), List.of(ModItems.BLOODY_SOUL_DUST.get(),Items.GOLD_INGOT,ModItems.GLOW_MUSHROOM.get()), List.of(ModItems.BLOODY_SOUL_DUST.get(),Items.GOLD_INGOT,ModItems.GLOW_MUSHROOM.get()), List.of(ModItems.BLOODY_SOUL_DUST.get(),Items.GOLD_INGOT,ModItems.GLOW_MUSHROOM.get()));
    }

    @Override
    public void applyResults() {
        boolean isBloodPrepared = true;
        BlockPos blockPosDown = this.blockPos.below();
        BlockPos[] altarPositions = {
                blockPosDown.north(),      // Norte
                blockPosDown.east(),       // Este
                blockPosDown.south(),      // Sur
                blockPosDown.west(),       // Oeste
                blockPosDown.north().east(),  // Noreste
                blockPosDown.north().west(),  // Noroeste
                blockPosDown.south().east(),  // Sureste
                blockPosDown.south().west()   // Suroeste
        };
        for (BlockPos pos : altarPositions) {

            if (!level.getBlockState(pos).getBlock().equals(ModBlocks.BLOOD_FLUID_BLOCK.get())) {
                isBloodPrepared = false;
            }
        }
        if (isBloodPrepared) {
            for (BlockPos pos : altarPositions) {
               // level.setBlockAndUpdate(pos, ModBlocks.RHNULL_BLOOD_FLUID_BLOCK.get().defaultBlockState());

                for (int i = 0; i < 10; i++) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 1.0;
                    double z = pos.getZ() + 0.5;

                    double velX = (Math.random() - 0.5) * 0.1;
                    double velY = 0.2 + Math.random() * 0.1;
                    double velZ = (Math.random() - 0.5) * 0.1;

                    level.addParticle(ModParticles.BLOOD_PARTICLES.get(), x, y, z, velX, velY, velZ);
                }
            }

        }
    }
}