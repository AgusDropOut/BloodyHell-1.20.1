package net.agusdropout.bloodyhell.compat;

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
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class BloodAltarCategory implements IRecipeCategory<BloodAltarRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(BloodyHell.MODID, "blood_altar");
    public static final RecipeType<BloodAltarRecipe> RECIPE_TYPE = new RecipeType<>(UID, BloodAltarRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private final int WIDTH = 176;
    private final int HEIGHT = 140;

    public BloodAltarCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MAIN_BLOOD_ALTAR.get()));
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
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BloodAltarRecipe recipe, IFocusGroup focuses) {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;

        builder.addSlot(RecipeIngredientRole.OUTPUT, cx - 8, cy - 8)
                .addItemStack(recipe.getResultItem(null));

        List<Ingredient> ingredients = recipe.getIngredients();

        int dist = 45;

        placeItemCluster(builder, ingredients, cx, cy - dist, 0);
        placeItemCluster(builder, ingredients, cx, cy + dist, 1);
        placeItemCluster(builder, ingredients, cx + dist, cy, 2);
        placeItemCluster(builder, ingredients, cx - dist, cy, 3);
    }

    private void placeItemCluster(IRecipeLayoutBuilder builder, List<Ingredient> ingredients, int baseX, int baseY, int orientation) {
        int[][] offsets = new int[3][2];
        int spacing = 14;
        int depth = 8;

        switch (orientation) {
            case 0:
                offsets[0] = new int[]{0, -depth};
                offsets[1] = new int[]{-spacing, depth};
                offsets[2] = new int[]{spacing, depth};
                break;
            case 1:
                offsets[0] = new int[]{0, depth};
                offsets[1] = new int[]{-spacing, -depth};
                offsets[2] = new int[]{spacing, -depth};
                break;
            case 2:
                offsets[0] = new int[]{depth, 0};
                offsets[1] = new int[]{-depth, -spacing};
                offsets[2] = new int[]{-depth, spacing};
                break;
            case 3:
                offsets[0] = new int[]{-depth, 0};
                offsets[1] = new int[]{depth, -spacing};
                offsets[2] = new int[]{depth, spacing};
                break;
        }

        for (int i = 0; i < 3; i++) {
            if (i < ingredients.size()) {
                builder.addSlot(RecipeIngredientRole.INPUT, baseX + offsets[i][0] - 8, baseY + offsets[i][1] - 8)
                        .addIngredients(ingredients.get(i));
            }
        }
    }

    @Override
    public void draw(BloodAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;
        int dist = 45;

        int colorCirculo = 0xFF550000;

        drawLine(guiGraphics, cx, cy - dist, cx + dist, cy, colorCirculo, 1);
        drawLine(guiGraphics, cx + dist, cy, cx, cy + dist, colorCirculo, 1);
        drawLine(guiGraphics, cx, cy + dist, cx - dist, cy, colorCirculo, 1);
        drawLine(guiGraphics, cx - dist, cy, cx, cy - dist, colorCirculo, 1);

        int colorSangre = 0xFF990000;

        drawConnection(guiGraphics, cx, cy - dist, cx, cy - 12, colorSangre);
        drawConnection(guiGraphics, cx, cy + dist, cx, cy + 12, colorSangre);
        drawConnection(guiGraphics, cx + dist, cy, cx + 12, cy, colorSangre);
        drawConnection(guiGraphics, cx - dist, cy, cx - 12, cy, colorSangre);

        ItemStack result = recipe.getResultItem(null);
        Font font = Minecraft.getInstance().font;

        if (result.getItem() == Items.LEATHER) {
            drawRitualName(guiGraphics, font, "Ritual: Summon Cow", 10);
        } else if (result.getItem() == Items.RECOVERY_COMPASS) {
            drawRitualName(guiGraphics, font, "Ritual: Locate Mausoleum", 10);
        } else if (result.getItem() == Items.RED_DYE) {
            drawRitualName(guiGraphics, font, "Ritual: Rhnull Transmutation", 10);
        }
    }

    private void drawConnection(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(Math.min(x1, x2) - 1, Math.min(y1, y2) - 1, Math.max(x1, x2) + 1, Math.max(y1, y2) + 1, color);
    }

    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, int width) {
        graphics.fill(x1 - 2, y1 - 2, x1 + 2, y1 + 2, color);
    }

    private void drawRitualName(GuiGraphics graphics, Font font, String text, int y) {
        int width = font.width(text);
        graphics.drawString(font, text, (WIDTH / 2) - (width / 2), y, 0x555555, false);
    }
}