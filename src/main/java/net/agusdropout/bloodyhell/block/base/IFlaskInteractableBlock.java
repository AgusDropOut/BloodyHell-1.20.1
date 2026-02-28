package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFlaskInteractableBlock {

    int FLASK_CAPACITY = 250;

    default InteractionResult handleFlaskInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        InteractionResult fillFlaskResult = tryFillFlask(level, pos, player, hand, stack);
        if (fillFlaskResult.consumesAction()) {
            return fillFlaskResult;
        }

        return tryEmptyFlask(level, pos, player, hand, stack);
    }

    default InteractionResult tryFillFlask(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (stack.is(ModItems.BLOOD_FLASK.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) {
                    be.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
                        FluidStack simulatedDrain = fluidHandler.drain(FLASK_CAPACITY, IFluidHandler.FluidAction.SIMULATE);

                        if (simulatedDrain.getAmount() == FLASK_CAPACITY) {
                            ItemStack filledFlask = getFlaskForFluid(simulatedDrain.getFluid());

                            if (!filledFlask.isEmpty()) {
                                fluidHandler.drain(FLASK_CAPACITY, IFluidHandler.FluidAction.EXECUTE);

                                stack.shrink(1);
                                if (stack.isEmpty()) {
                                    player.setItemInHand(hand, filledFlask);
                                } else if (!player.getInventory().add(filledFlask)) {
                                    player.drop(filledFlask, false);
                                }

                                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                        }
                    });
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    default InteractionResult tryEmptyFlask(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        FluidStack fluidStackToInsert = getFluidStackFromFlask(stack);

        if (!fluidStackToInsert.isEmpty()) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) {
                    be.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
                        int simulatedFill = fluidHandler.fill(fluidStackToInsert, IFluidHandler.FluidAction.SIMULATE);

                        if (simulatedFill == FLASK_CAPACITY) {
                            fluidHandler.fill(fluidStackToInsert, IFluidHandler.FluidAction.EXECUTE);

                            ItemStack emptyFlask = new ItemStack(ModItems.BLOOD_FLASK.get());
                            stack.shrink(1);
                            if (stack.isEmpty()) {
                                player.setItemInHand(hand, emptyFlask);
                            } else if (!player.getInventory().add(emptyFlask)) {
                                player.drop(emptyFlask, false);
                            }

                            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    });
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    default ItemStack getFlaskForFluid(Fluid fluid) {

        if (fluid == ModFluids.BLOOD_SOURCE.get()) {
            return new ItemStack(ModItems.FILLED_BLOOD_FLASK.get());
        } else if (fluid == ModFluids.CORRUPTED_BLOOD_SOURCE.get()) {
            return new ItemStack(ModItems.CORRUPTED_BLOOD_FLASK.get());
        }

        return ItemStack.EMPTY;
    }



    default FluidStack getFluidStackFromFlask(ItemStack stack) {

        if (stack.is(ModItems.FILLED_BLOOD_FLASK.get())) {
            return new FluidStack(ModFluids.BLOOD_SOURCE.get(), FLASK_CAPACITY);
        } else if (stack.is(ModItems.CORRUPTED_BLOOD_FLASK.get())) {
            return new FluidStack(ModFluids.CORRUPTED_BLOOD_SOURCE.get(), FLASK_CAPACITY);
        }

        return FluidStack.EMPTY;
    }
}