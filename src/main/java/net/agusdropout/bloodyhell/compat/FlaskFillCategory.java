package net.agusdropout.bloodyhell.compat;

import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import mezz.jei.api.constants.VanillaTypes;
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
import net.agusdropout.bloodyhell.recipe.CondenserRecipe;
import net.agusdropout.bloodyhell.recipe.FlaskFillRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FlaskFillCategory implements IRecipeCategory<FlaskFillRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(BloodyHell.MODID, "flask_fill");
    public static final RecipeType<FlaskFillRecipe> RECIPE_TYPE = new RecipeType<>(UID, FlaskFillRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;

    public FlaskFillCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(120, 36);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineBlocks.SANGUINITE_TANK.get()));
        this.slotDrawable = helper.getSlotDrawable();
    }

    @Override
    public RecipeType<FlaskFillRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.bloodyhell.flask_fill");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FlaskFillRecipe recipe, IFocusGroup focuses) {
        // Slot 1: The Fluid required in the tank
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.requiredFluid());

        // Slot 2: The Empty Flask you click with
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 10)
                .addItemStack(recipe.emptyFlask());

        // Slot 3: The Output Flask
        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 10)
                .addItemStack(recipe.filledFlask());
    }

    @Override
    public void draw(FlaskFillRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, "+", 27, 14, 0x888888, false);
    }
}