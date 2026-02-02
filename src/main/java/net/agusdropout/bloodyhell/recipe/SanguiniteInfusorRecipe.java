package net.agusdropout.bloodyhell.recipe;

import com.google.gson.JsonObject;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SanguiniteInfusorRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient recipeItem;
    // We store fluid costs as integers since fluid ingredients aren't standard in vanilla recipes
    private final int bloodCost;
    private final int visceralCost;

    public SanguiniteInfusorRecipe(ResourceLocation id, ItemStack output, Ingredient recipeItem, int bloodCost, int visceralCost) {
        this.id = id;
        this.output = output;
        this.recipeItem = recipeItem;
        this.bloodCost = bloodCost;
        this.visceralCost = visceralCost;
    }

    // --- GETTERS FOR JEI ---
    public int getBloodCost() { return bloodCost; }
    public int getVisceralCost() { return visceralCost; }
    public Ingredient getInputItem() { return recipeItem; }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide()) return false;
        // Logic checks strictly the ITEM match here.
        // Fluid checks happen in the Block Entity logic using getBloodCost().
        return recipeItem.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    // --- SERIALIZER ---
    public static class Serializer implements RecipeSerializer<SanguiniteInfusorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(BloodyHell.MODID, "sanguinite_infusing");

        @Override
        public SanguiniteInfusorRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            Ingredient input = Ingredient.fromJson(json.get("input"));
            int blood = GsonHelper.getAsInt(json, "blood_cost", 500); // Default 500 if missing
            int visceral = GsonHelper.getAsInt(json, "visceral_cost", 500);

            return new SanguiniteInfusorRecipe(id, output, input, blood, visceral);
        }

        @Override
        public @Nullable SanguiniteInfusorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            int blood = buf.readInt();
            int visceral = buf.readInt();
            return new SanguiniteInfusorRecipe(id, output, input, blood, visceral);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SanguiniteInfusorRecipe recipe) {
            recipe.recipeItem.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.bloodCost);
            buf.writeInt(recipe.visceralCost);
        }
    }

    // --- TYPE REGISTRATION ---
    public static class Type implements RecipeType<SanguiniteInfusorRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "sanguinite_infusing";
    }
}