package net.agusdropout.bloodyhell.datagen.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.agusdropout.bloodyhell.recipe.ModRecipes;
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
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BlasphemousBloodAltarRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public BlasphemousBloodAltarRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
    }

    public static BlasphemousBloodAltarRecipeBuilder ritual(ItemLike result) {
        return new BlasphemousBloodAltarRecipeBuilder(result, 1);
    }

    public static BlasphemousBloodAltarRecipeBuilder ritual(ItemLike result, int count) {
        return new BlasphemousBloodAltarRecipeBuilder(result, count);
    }

    public BlasphemousBloodAltarRecipeBuilder requires(Ingredient ingredient) {
        if (this.ingredients.size() >= 3) {
            throw new IllegalStateException("Max 3 items per pedestal in Blood Altar recipes");
        }
        this.ingredients.add(ingredient);
        return this;
    }

    public BlasphemousBloodAltarRecipeBuilder requires(ItemLike item) {
        return requires(Ingredient.of(item));
    }

    @Override
    public BlasphemousBloodAltarRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        this.advancement.addCriterion(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public BlasphemousBloodAltarRecipeBuilder group(@Nullable String groupName) {
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

        consumer.accept(new Result(id, result, count, ingredients,
                this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, int count, List<Ingredient> ingredients, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.count = count;
            this.ingredients = ingredients;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray jsonIngredients = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                jsonIngredients.add(ingredient.toJson());
            }
            json.add("ingredients", jsonIngredients);

            JsonObject jsonResult = new JsonObject();
            jsonResult.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
            if (this.count > 1) {
                jsonResult.addProperty("count", this.count);
            }
            json.add("output", jsonResult);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.BLASPHEMOUS_BLOOD_ALTAR_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }
    }
}