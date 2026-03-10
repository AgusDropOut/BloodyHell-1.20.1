package net.agusdropout.bloodyhell.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

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

        registration.addRecipeCategories(new BloodAltarCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SanguiniteInfusorCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();


        List<BloodAltarRecipe> altarRecipes = recipeManager.getAllRecipesFor(BloodAltarRecipe.Type.INSTANCE);
        registration.addRecipes(BloodAltarCategory.RECIPE_TYPE, altarRecipes);


        List<SanguiniteInfusorRecipe> infusorRecipes = recipeManager.getAllRecipesFor(SanguiniteInfusorRecipe.Type.INSTANCE);
        registration.addRecipes(SanguiniteInfusorCategory.RECIPE_TYPE, infusorRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get()), BloodAltarCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SANGUINITE_INFUSOR.get()), SanguiniteInfusorCategory.RECIPE_TYPE);
    }
}