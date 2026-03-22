package net.agusdropout.bloodyhell.datagen.recipe.builder;

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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CondenserRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final int count;
    private Ingredient itemInput;
    private FluidStack fluidInput;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public CondenserRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
    }

    public static CondenserRecipeBuilder condense(ItemLike result) {
        return new CondenserRecipeBuilder(result, 1);
    }

    public CondenserRecipeBuilder requiresItem(Ingredient ingredient) {
        this.itemInput = ingredient;
        return this;
    }

    public CondenserRecipeBuilder requiresFluid(Fluid fluid, int amount) {
        this.fluidInput = new FluidStack(fluid, amount);
        return this;
    }

    @Override
    public CondenserRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        this.advancement.addCriterion(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public CondenserRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (this.itemInput == null || this.fluidInput == null) {
            throw new IllegalStateException("Condenser recipes require both an item input and a fluid input.");
        }

        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);

        consumer.accept(new Result(id, result, count, itemInput, fluidInput,
                this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final Ingredient itemInput;
        private final FluidStack fluidInput;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, int count, Ingredient itemInput, FluidStack fluidInput, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.count = count;
            this.itemInput = itemInput;
            this.fluidInput = fluidInput;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("item_input", itemInput.toJson());

            JsonObject jsonFluid = new JsonObject();
            jsonFluid.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(fluidInput.getFluid()).toString());
            jsonFluid.addProperty("amount", fluidInput.getAmount());
            json.add("fluid_input", jsonFluid);

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
            return ModRecipes.CONDENSER_SERIALIZER.get();
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