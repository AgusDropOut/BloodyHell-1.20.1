package net.agusdropout.bloodyhell.datagen;



import com.google.gson.JsonObject;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SanguiniteInfusorRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient input;
    private final int bloodCost;
    private final int visceralCost;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public SanguiniteInfusorRecipeBuilder(Item result, Ingredient input, int bloodCost, int visceralCost) {
        this.result = result;
        this.input = input;
        this.bloodCost = bloodCost;
        this.visceralCost = visceralCost;
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

        consumer.accept(new Result(id, this.result, this.input, this.bloodCost, this.visceralCost,
                this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final Ingredient input;
        private final int bloodCost;
        private final int visceralCost;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, Ingredient input, int bloodCost, int visceralCost, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.input = input;
            this.bloodCost = bloodCost;
            this.visceralCost = visceralCost;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson());

            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
            json.add("output", outputJson);

            json.addProperty("blood_cost", bloodCost);
            json.addProperty("visceral_cost", visceralCost);
        }

        @Override
        public ResourceLocation getId() { return id; }
        @Override
        public RecipeSerializer<?> getType() { return SanguiniteInfusorRecipe.Serializer.INSTANCE; }
        @Nullable
        @Override
        public JsonObject serializeAdvancement() { return advancement.serializeToJson(); }
        @Nullable
        @Override
        public ResourceLocation getAdvancementId() { return advancementId; }
    }
}