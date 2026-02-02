package net.agusdropout.bloodyhell.compat;

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
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
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
        // Create a smaller background: 150x60 pixels
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
        // 1. INPUT ITEM (Center Left)
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 22)
                .addIngredients(recipe.getInputItem());

        // 2. OUTPUT ITEM (Center Right)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 22)
                .addItemStack(recipe.getResultItem(null));

        // 3. BLOOD TANK (Far Left)
        // 4000 is tank capacity. We show ratio based on cost.
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 5)
                .addFluidStack(ModFluids.BLOOD_SOURCE.get(), recipe.getBloodCost())
                .setFluidRenderer(4000, false, 16, 50); // Capacity, showCapacity, width, height

        // 4. VISCERAL TANK (Far Right)
        builder.addSlot(RecipeIngredientRole.INPUT, 130, 5)
                .addFluidStack(ModFluids.VISCERAL_BLOOD_SOURCE.get(), recipe.getVisceralCost())
                .setFluidRenderer(4000, false, 16, 50);
    }

    @Override
    public void draw(SanguiniteInfusorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw an Arrow between slots
        // Uses standard ASCII arrow or texture if you have one
        // Simple line drawing for now:
        int arrowX = 72;
        int arrowY = 26;
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, "->", arrowX, arrowY, 0x555555, false);

        // Draw Cost Text under tanks
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.7f, 0.7f, 1.0f); // Small text

        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, recipe.getBloodCost() + "mB", 14, 80, 0xCC0000, false);
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, recipe.getVisceralCost() + "mB", 175, 80, 0x88AA00, false);

        guiGraphics.pose().popPose();
    }
}