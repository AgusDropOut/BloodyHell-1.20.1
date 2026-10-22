package net.agusdropout.bloodyhell.client.recipe;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.recipe.BlasphemousBloodAltarRecipe;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.agusdropout.bloodyhell.recipe.BloodWorkBenchRecipe;
import net.agusdropout.bloodyhell.recipe.CondenserRecipe;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModRecipeBookCategories {

    @SubscribeEvent
    public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerRecipeCategoryFinder(BlasphemousBloodAltarRecipe.Type.INSTANCE, recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(BloodAltarRecipe.Type.INSTANCE, recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(BloodWorkBenchRecipe.Type.INSTANCE, recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(CondenserRecipe.Type.INSTANCE, recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(SanguiniteInfusorRecipe.Type.INSTANCE, recipe -> RecipeBookCategories.UNKNOWN);
    }
}
