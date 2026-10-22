package net.agusdropout.bloodyhell.compat;

import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class BloodAltarCategory implements IRecipeCategory<BloodAltarRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(BloodyHell.MODID, "blood_altar");
    public static final RecipeType<BloodAltarRecipe> RECIPE_TYPE = new RecipeType<>(UID, BloodAltarRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private final int WIDTH = 150;
    private final int HEIGHT = 140;

    public BloodAltarCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineBlocks.MAIN_BLOOD_ALTAR.get()));
    }

    @Override
    public RecipeType<BloodAltarRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.bloodyhell.main_blood_altar");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BloodAltarRecipe recipe, IFocusGroup focuses) {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;
        int dist = 45;


        builder.addSlot(RecipeIngredientRole.OUTPUT, cx - 8, cy - 8)
                .addItemStack(recipe.getResultItem(null));

        List<Ingredient> ingredients = recipe.getIngredients();


        if (ingredients.size() > 0) builder.addSlot(RecipeIngredientRole.INPUT, cx - 8, cy - dist - 8).addIngredients(ingredients.get(0));
        if (ingredients.size() > 1) builder.addSlot(RecipeIngredientRole.INPUT, cx - 8, cy + dist - 8).addIngredients(ingredients.get(1));
        if (ingredients.size() > 2) builder.addSlot(RecipeIngredientRole.INPUT, cx + dist - 8, cy - 8).addIngredients(ingredients.get(2));
        if (ingredients.size() > 3) builder.addSlot(RecipeIngredientRole.INPUT, cx - dist - 8, cy - 8).addIngredients(ingredients.get(3));
    }

    @Override
    public void draw(BloodAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;
        int dist = 45;

        int colorCirculo = 0xFF550000;
        int colorSangre = 0xFF990000;


        drawLine(guiGraphics, cx, cy - dist, cx + dist, cy, colorCirculo);
        drawLine(guiGraphics, cx + dist, cy, cx, cy + dist, colorCirculo);
        drawLine(guiGraphics, cx, cy + dist, cx - dist, cy, colorCirculo);
        drawLine(guiGraphics, cx - dist, cy, cx, cy - dist, colorCirculo);


        drawConnection(guiGraphics, cx, cy - dist, cx, cy - 16, colorSangre);
        drawConnection(guiGraphics, cx, cy + dist, cx, cy + 16, colorSangre);
        drawConnection(guiGraphics, cx + dist, cy, cx + 16, cy, colorSangre);
        drawConnection(guiGraphics, cx - dist, cy, cx - 16, cy, colorSangre);
    }

    private void drawConnection(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(Math.min(x1, x2) - 1, Math.min(y1, y2) - 1, Math.max(x1, x2) + 1, Math.max(y1, y2) + 1, color);
    }

    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1 - 1, y1 - 1, x1 + 1, y1 + 1, color);
    }
}