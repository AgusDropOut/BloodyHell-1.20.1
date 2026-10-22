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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class SanguiniteInfusorRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient recipeItem;
    private final FluidStack fluid1;
    private final FluidStack fluid2;

    public SanguiniteInfusorRecipe(ResourceLocation id, ItemStack output, Ingredient recipeItem, FluidStack fluid1, FluidStack fluid2) {
        this.id = id;
        this.output = output;
        this.recipeItem = recipeItem;
        this.fluid1 = fluid1;
        this.fluid2 = fluid2;
    }

    public FluidStack getFluid1() { return fluid1; }
    public FluidStack getFluid2() { return fluid2; }
    public Ingredient getInputItem() { return recipeItem; }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide()) return false;
        return recipeItem.test(container.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.recipeItem);
        return list;
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
        return ModRecipes.SANGUINITE_INFUSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<SanguiniteInfusorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(BloodyHell.MODID, "sanguinite_infusing");

        @Override
        public SanguiniteInfusorRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            Ingredient input = Ingredient.fromJson(json.get("input"));

            FluidStack fluid1 = FluidStack.EMPTY;
            if (json.has("fluid1")) {
                JsonObject f1Json = GsonHelper.getAsJsonObject(json, "fluid1");
                fluid1 = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(f1Json, "fluid"))), GsonHelper.getAsInt(f1Json, "amount"));
            }

            FluidStack fluid2 = FluidStack.EMPTY;
            if (json.has("fluid2")) {
                JsonObject f2Json = GsonHelper.getAsJsonObject(json, "fluid2");
                fluid2 = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(f2Json, "fluid"))), GsonHelper.getAsInt(f2Json, "amount"));
            }

            return new SanguiniteInfusorRecipe(id, output, input, fluid1, fluid2);
        }

        @Override
        public @Nullable SanguiniteInfusorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            FluidStack fluid1 = buf.readFluidStack();
            FluidStack fluid2 = buf.readFluidStack();
            return new SanguiniteInfusorRecipe(id, output, input, fluid1, fluid2);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SanguiniteInfusorRecipe recipe) {
            recipe.recipeItem.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeFluidStack(recipe.fluid1);
            buf.writeFluidStack(recipe.fluid2);
        }
    }

    public static class Type implements RecipeType<SanguiniteInfusorRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "sanguinite_infusing";
    }
}