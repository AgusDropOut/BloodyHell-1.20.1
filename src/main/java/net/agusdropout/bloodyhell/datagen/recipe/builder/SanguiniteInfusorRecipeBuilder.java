package net.agusdropout.bloodyhell.datagen.recipe.builder;

import com.google.gson.JsonObject;
import net.agusdropout.bloodyhell.recipe.ModRecipes;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SanguiniteInfusorRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient input;
    private final FluidStack fluid1;
    private final FluidStack fluid2;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public SanguiniteInfusorRecipeBuilder(Item result, Ingredient input, @Nullable FluidStack fluid1, @Nullable FluidStack fluid2) {
        this.result = result;
        this.input = input;
        this.fluid1 = fluid1 != null ? fluid1 : FluidStack.EMPTY;
        this.fluid2 = fluid2 != null ? fluid2 : FluidStack.EMPTY;
    }

    @Override
    public SanguiniteInfusorRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        this.advancement.addCriterion(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public SanguiniteInfusorRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);

        consumer.accept(new Result(id, this.result, this.input, this.fluid1, this.fluid2,
                this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final Ingredient input;
        private final FluidStack fluid1;
        private final FluidStack fluid2;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, Ingredient input, FluidStack fluid1, FluidStack fluid2, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.input = input;
            this.fluid1 = fluid1;
            this.fluid2 = fluid2;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson());

            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
            json.add("output", outputJson);

            if (!fluid1.isEmpty()) {
                JsonObject f1 = new JsonObject();
                f1.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(fluid1.getFluid()).toString());
                f1.addProperty("amount", fluid1.getAmount());
                json.add("fluid1", f1);
            }

            if (!fluid2.isEmpty()) {
                JsonObject f2 = new JsonObject();
                f2.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(fluid2.getFluid()).toString());
                f2.addProperty("amount", fluid2.getAmount());
                json.add("fluid2", f2);
            }
        }

        @Override
        public ResourceLocation getId() { return id; }

        @Override
        public RecipeSerializer<?> getType() { return ModRecipes.SANGUINITE_INFUSING_SERIALIZER.get(); }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() { return advancement.serializeToJson(); }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() { return advancementId; }
    }
}