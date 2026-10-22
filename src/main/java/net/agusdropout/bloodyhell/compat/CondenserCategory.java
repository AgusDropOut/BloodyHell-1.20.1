package net.agusdropout.bloodyhell.compat;

import net.agusdropout.bloodyhell.item.registry.PlaceableItems;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CondenserCategory implements IRecipeCategory<CondenserRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(BloodyHell.MODID, "condensing");
    public static final RecipeType<CondenserRecipe> RECIPE_TYPE = new RecipeType<>(UID, CondenserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrow;

    public CondenserCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(120, 36);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(PlaceableItems.SANGUINITE_CONDENSER_ITEM.get()));
        this.slotDrawable = helper.getSlotDrawable();

        // Extracts the standard filled arrow from the native vanilla furnace texture
        this.arrow = helper.createDrawable(new ResourceLocation("minecraft", "textures/gui/container/furnace.png"), 176, 14, 24, 17);
    }

    @Override
    public RecipeType<CondenserRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.bloodyhell.sanguinite_condenser");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CondenserRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10)
                .setBackground(this.slotDrawable, -1, -1)
                .addIngredients(recipe.getItemInput());

        builder.addSlot(RecipeIngredientRole.INPUT, 34, 10)
                .setBackground(this.slotDrawable, -1, -1)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluidInput())
                .setFluidRenderer(recipe.getFluidInput().getAmount(), true, 16, 16);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 10)
                .setBackground(this.slotDrawable, -1, -1)
                .addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(CondenserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Renders a plus symbol between the item and fluid slots
        guiGraphics.drawString(Minecraft.getInstance().font, "+", 27, 14, 0x888888, false);

        // Renders the vanilla progress arrow pointing to the output
        this.arrow.draw(guiGraphics, 58, 9);
    }
}