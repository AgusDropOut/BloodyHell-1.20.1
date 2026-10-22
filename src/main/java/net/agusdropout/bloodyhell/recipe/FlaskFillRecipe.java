package net.agusdropout.bloodyhell.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public record FlaskFillRecipe(FluidStack requiredFluid, ItemStack emptyFlask, ItemStack filledFlask) {
}