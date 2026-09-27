package net.agusdropout.bloodyhell.compat;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SanguiniteInfusorCategory implements IRecipeCategory<SanguiniteInfusorRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(BloodyHell.MODID, "sanguinite_infusing");
    public static final RecipeType<SanguiniteInfusorRecipe> RECIPE_TYPE = new RecipeType<>(UID, SanguiniteInfusorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SanguiniteInfusorCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(150, 60);
        this.icon = helper.createDrawableIngredient(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SANGUINITE_INFUSOR.get()));
    }

    @Override
    public RecipeType<SanguiniteInfusorRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.bloodyhell.sanguinite_infusor");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SanguiniteInfusorRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 50, 22)
                .addIngredients(recipe.getInputItem());


        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 22)
                .addItemStack(recipe.getResultItem(null));


        if (!recipe.getFluid1().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 10, 5)
                    .addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluid1())
                    .setFluidRenderer(4000, false, 16, 50);
        }


        if (!recipe.getFluid2().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 130, 5)
                    .addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluid2())
                    .setFluidRenderer(4000, false, 16, 50);
        }
    }

    @Override
    public void draw(SanguiniteInfusorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        int arrowX = 72;
        int arrowY = 26;
        guiGraphics.drawString(Minecraft.getInstance().font, "->", arrowX, arrowY, 0x555555, false);


        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.7f, 0.7f, 1.0f);

        if (!recipe.getFluid1().isEmpty()) {
            String text1 = recipe.getFluid1().getAmount() + "mB";
            guiGraphics.drawString(Minecraft.getInstance().font, text1, 14, 80, 0xFFFFFF, false);
        }

        if (!recipe.getFluid2().isEmpty()) {
            String text2 = recipe.getFluid2().getAmount() + "mB";
            guiGraphics.drawString(Minecraft.getInstance().font, text2, 175, 80, 0xFFFFFF, false);
        }

        guiGraphics.pose().popPose();
    }
}