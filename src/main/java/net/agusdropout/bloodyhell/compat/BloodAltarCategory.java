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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class BloodAltarCategory implements IRecipeCategory<BloodAltarRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(BloodyHell.MODID, "blood_altar");
    public static final RecipeType<BloodAltarRecipe> RECIPE_TYPE = new RecipeType<>(UID, BloodAltarRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    // Dimensiones del Canvas
    private final int WIDTH = 176;
    private final int HEIGHT = 140; // Aumentado para que quepan los 3 items cómodamente

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
        // Centro del Canvas
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;

        // 1. OUTPUT (Centro absoluto)
        builder.addSlot(RecipeIngredientRole.OUTPUT, cx - 8, cy - 8)
                .addItemStack(recipe.getResultItem(null));

        // Obtener ingredientes (Lista de 1 a 3 items para UN pedestal)
        List<Ingredient> ingredients = recipe.getIngredients();

        // 2. COLOCAR GRUPOS EN LOS 4 PUNTOS CARDINALES
        // Distancia del centro del altar al centro del grupo de items
        int dist = 45;

        // Norte (Arriba)
        placeItemCluster(builder, ingredients, cx, cy - dist, 0);
        // Sur (Abajo)
        placeItemCluster(builder, ingredients, cx, cy + dist, 1);
        // Este (Derecha)
        placeItemCluster(builder, ingredients, cx + dist, cy, 2);
        // Oeste (Izquierda)
        placeItemCluster(builder, ingredients, cx - dist, cy, 3);
    }

    /**
     * Coloca un cluster de hasta 3 items simulando la posición en el pedestal.
     * @param orientation 0=N, 1=S, 2=E, 3=W
     */
    private void placeItemCluster(IRecipeLayoutBuilder builder, List<Ingredient> ingredients, int baseX, int baseY, int orientation) {
        // Configuraciones de desplazamiento para los 3 items
        // {xOffset, yOffset} relativos a (baseX, baseY)
        int[][] offsets = new int[3][2];
        int spacing = 14; // Separación entre items laterales
        int depth = 8;    // Qué tan "adentro" o "afuera" están

        // Lógica: El item [0] es el central (más alejado del centro del altar).
        // Los items [1] y [2] están a los lados y un poco más cerca del centro del altar.

        switch (orientation) {
            case 0: // NORTE (Cluster Arriba)
                offsets[0] = new int[]{0, -depth};      // Central (Más arriba)
                offsets[1] = new int[]{-spacing, depth}; // Izq (Más abajo/cerca centro)
                offsets[2] = new int[]{spacing, depth};  // Der (Más abajo/cerca centro)
                break;
            case 1: // SUR (Cluster Abajo)
                offsets[0] = new int[]{0, depth};       // Central (Más abajo)
                offsets[1] = new int[]{-spacing, -depth};// Izq (Más arriba)
                offsets[2] = new int[]{spacing, -depth}; // Der (Más arriba)
                break;
            case 2: // ESTE (Cluster Derecha)
                offsets[0] = new int[]{depth, 0};       // Central (Más derecha)
                offsets[1] = new int[]{-depth, -spacing};// Arriba (Más izq)
                offsets[2] = new int[]{-depth, spacing}; // Abajo (Más izq)
                break;
            case 3: // OESTE (Cluster Izquierda)
                offsets[0] = new int[]{-depth, 0};      // Central (Más izquierda)
                offsets[1] = new int[]{depth, -spacing}; // Arriba (Más der)
                offsets[2] = new int[]{depth, spacing};  // Abajo (Más der)
                break;
        }

        // Añadir slots
        for (int i = 0; i < 3; i++) {
            if (i < ingredients.size()) {
                builder.addSlot(RecipeIngredientRole.INPUT, baseX + offsets[i][0] - 8, baseY + offsets[i][1] - 8)
                        .addIngredients(ingredients.get(i));
            } else {
                // Si la receta tiene menos de 3 items, dejamos el hueco vacío o ponemos un placeholder transparente si quieres
                // Por ahora no añadimos nada, queda el espacio vacío geométrico.
            }
        }
    }

    @Override
    public void draw(BloodAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;
        int dist = 45; // Misma distancia usada en setRecipe

        // --- 1. DIBUJAR CÍRCULO RITUAL (Conexión entre altares) ---
        // Dibujamos líneas que conectan N->E->S->W->N formando un rombo/círculo
        int colorCirculo = 0xFF550000; // Rojo muy oscuro

        drawLine(guiGraphics, cx, cy - dist, cx + dist, cy, colorCirculo, 1); // N -> E
        drawLine(guiGraphics, cx + dist, cy, cx, cy + dist, colorCirculo, 1); // E -> S
        drawLine(guiGraphics, cx, cy + dist, cx - dist, cy, colorCirculo, 1); // S -> W
        drawLine(guiGraphics, cx - dist, cy, cx, cy - dist, colorCirculo, 1); // W -> N

        // --- 2. DIBUJAR FLUJO DE SANGRE (Hacia el centro) ---
        int colorSangre = 0xFF990000; // Rojo sangre vivo

        // Dibujar desde el centro de cada cluster hacia el centro absoluto
        // Dejamos un espacio en el centro (radio 10) para no tapar el item de resultado
        drawConnection(guiGraphics, cx, cy - dist, cx, cy - 12, colorSangre); // N
        drawConnection(guiGraphics, cx, cy + dist, cx, cy + 12, colorSangre); // S
        drawConnection(guiGraphics, cx + dist, cy, cx + 12, cy, colorSangre); // E
        drawConnection(guiGraphics, cx - dist, cy, cx - 12, cy, colorSangre); // W

        // --- 3. TEXTO INFORMATIVO (Rituales especiales) ---
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

    // Helper para dibujar líneas con grosor simulado
    private void drawConnection(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        // Línea principal
        graphics.fill(Math.min(x1, x2) - 1, Math.min(y1, y2) - 1, Math.max(x1, x2) + 1, Math.max(y1, y2) + 1, color);
    }

    // Helper para líneas finas (Bresenham simplificado o rectángulos si son ortogonales, aquí uso fill para líneas rectas simples)
    // Para líneas diagonales perfectas en GUI sin shaders es complicado, usaremos rectángulos aproximados o fill
    // Dado que JEI no expone drawLine directo fácilmente en 1.20 sin PoseStack complejo, usaremos fill para las cruces
    // Para el rombo exterior, lo haremos ortogonal si es posible, o usamos Math para puntos.
    // Simplificación: Dibujar 4 puntos en las esquinas de los clusters para decorar.
    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, int width) {
        // Implementación muy básica, para diagonales es mejor usar el RenderSystem directo,
        // pero para evitar crashes, dibujaremos puntos en los nodos.

        // Dibujamos un "nodo" en cada punto cardinal
        graphics.fill(x1 - 2, y1 - 2, x1 + 2, y1 + 2, color);
    }

    private void drawRitualName(GuiGraphics graphics, Font font, String text, int y) {
        int width = font.width(text);
        graphics.drawString(font, text, (WIDTH / 2) - (width / 2), y, 0x555555, false);
    }
}