package net.agusdropout.bloodyhell.compat;

import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.recipe.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class BloodyHellJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(BloodyHell.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

        registration.addRecipeCategories(new BlasphemousBloodAltarCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new BloodAltarCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SanguiniteInfusorCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CondenserCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FlaskFillCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();


        List<BlasphemousBloodAltarRecipe> blasphemousAltarRecipes = recipeManager.getAllRecipesFor(BlasphemousBloodAltarRecipe.Type.INSTANCE);
        registration.addRecipes(BlasphemousBloodAltarCategory.RECIPE_TYPE, blasphemousAltarRecipes);


        List<BloodAltarRecipe> altarRecipes = recipeManager.getAllRecipesFor(BloodAltarRecipe.Type.INSTANCE);
        registration.addRecipes(BloodAltarCategory.RECIPE_TYPE, altarRecipes);


        List<SanguiniteInfusorRecipe> infusorRecipes = recipeManager.getAllRecipesFor(SanguiniteInfusorRecipe.Type.INSTANCE);
        registration.addRecipes(SanguiniteInfusorCategory.RECIPE_TYPE, infusorRecipes);

        List<CondenserRecipe> condenserRecipes = recipeManager.getAllRecipesFor(CondenserRecipe.Type.INSTANCE);
        registration.addRecipes(CondenserCategory.RECIPE_TYPE, condenserRecipes);


        List<FlaskFillRecipe> flaskRecipes = List.of(
                new FlaskFillRecipe(
                        new FluidStack(ModFluids.BLOOD_SOURCE.get(), 1000),
                        new ItemStack(ModItems.BLOOD_FLASK.get()),
                        new ItemStack(ModItems.FILLED_BLOOD_FLASK.get())
                ),
                new FlaskFillRecipe(
                        new FluidStack(ModFluids.CORRUPTED_BLOOD_SOURCE.get(), 1000),
                        new ItemStack(ModItems.BLOOD_FLASK.get()),
                        new ItemStack(ModItems.CORRUPTED_BLOOD_FLASK.get())
                ),
                new FlaskFillRecipe(
                        new FluidStack(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 1000),
                        new ItemStack(ModItems.BLOOD_FLASK.get()),
                        new ItemStack(ModItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get())
                )
        );


        registration.addRecipes(FlaskFillCategory.RECIPE_TYPE, flaskRecipes);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get()), BlasphemousBloodAltarCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.MAIN_BLOOD_ALTAR.get()), BloodAltarCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SANGUINITE_INFUSOR.get()), SanguiniteInfusorCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.SANGUINITE_CONDENSER_ITEM.get()), CondenserCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.RHNULL_CONDENSER_ITEM.get()), CondenserCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SANGUINITE_TANK.get()), FlaskFillCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.RHNULL_TANK.get()), FlaskFillCategory.RECIPE_TYPE);
    }
}