package net.agusdropout.bloodyhell.datagen.recipe;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.datagen.*;
import net.agusdropout.bloodyhell.datagen.recipe.builder.BlasphemousBloodAltarRecipeBuilder;
import net.agusdropout.bloodyhell.datagen.recipe.builder.BloodAltarRecipeBuilder;
import net.agusdropout.bloodyhell.datagen.recipe.builder.CondenserRecipeBuilder;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModRecipes extends ModRecipesProvider {

    public ModRecipes(PackOutput output) {
        super(output);
    }

    @SuppressWarnings("removal")
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        // =================================================================
        // GUIDE BOOKS
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UNKNOWN_GUIDE_BOOK.get(), 1)
                .pattern(" S ")
                .pattern(" B ")
                .pattern("   ")
                .define('S', ModItems.SANGUINITE.get())
                .define('B', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("unknown_guide_book"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UNKNOWN_GUIDE_BOOK.get(), 1)
                .pattern(" R ")
                .pattern("GB ")
                .pattern("   ")
                .define('R', Items.REDSTONE)
                .define('G', Items.GOLD_INGOT)
                .define('B', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(consumer, name("unknown_guide_book_alt"));

        // =================================================================
        // ALTARS CRAFTING
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLOOD_ALTAR.get(), 1)
                .pattern("GDG")
                .pattern("SCS")
                .pattern("TST")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('C', ModItems.CORRUPTED_BLOOD_FLASK.get())
                .define('D', ModItems.BLOODY_SOUL_DUST.get())
                .define('T', ModBlocks.BLOODY_STONE_TILES_BLOCK.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("blood_altar"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAIN_BLOOD_ALTAR.get(), 1)
                .pattern("GDG")
                .pattern("SCS")
                .pattern("TST")
                .define('S', ModBlocks.SANGUINITE_BLOCK.get())
                .define('G', Items.GOLD_INGOT)
                .define('C', ModItems.CORRUPTED_BLOOD_FLASK.get())
                .define('D', ModItems.CHALICE_OF_THE_DAMMED.get())
                .define('T', ModBlocks.BLOODY_STONE_TILES_BLOCK.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("main_blood_altar"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLASPHEMOUS_BLOOD_ALTAR.get(), 1)
                .pattern("FDF")
                .pattern("SLS")
                .pattern("BRB")
                .define('S', ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .define('B', ModItems.BLASPHEMITE.get())
                .define('L', ModItems.FILLED_BLOOD_FLASK.get())
                .define('D', ModItems.BLOODY_SOUL_DUST.get())
                .define('F', ModItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', ModBlocks.BLASPHEMITE_BLOCK.get())
                .unlockedBy("has_blasphemite", has(ModItems.BLASPHEMITE.get()))
                .save(consumer, name("blasphemous_blood_altar"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get(), 1)
                .pattern("FCF")
                .pattern("SLS")
                .pattern("RRR")
                .define('S', ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .define('L', ModItems.FILLED_BLOOD_FLASK.get())
                .define('C', ModItems.CHALICE_OF_THE_DAMMED.get())
                .define('F', ModItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', ModBlocks.BLASPHEMITE_BLOCK.get())
                .unlockedBy("has_blasphemite", has(ModItems.BLASPHEMITE.get()))
                .save(consumer, name("main_blasphemous_blood_altar"));


        // =================================================================
        // BLASPHEMOUS RITUALS
        // =================================================================

        BlasphemousBloodAltarRecipeBuilder.ritual(Items.RECOVERY_COMPASS)
                .requires(ModItems.VEINREAVER_HORN.get())
                .requires(ModItems.AUREAL_REVENANT_DAGGER.get())
                .requires(ModItems.GLOW_MUSHROOM.get())
                .unlockedBy("has_dagger", has(ModItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("ritual_find_mausoleum"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.BLOOD_GEM_SPROUT_SEED.get())
                .requires(ModBlocks.BLOOD_FLOWER.get())
                .requires(ModItems.BLOODY_SOUL_DUST.get())
                .requires(ModItems.SANGUINITE.get())
                .unlockedBy("has_dirty_blood_flower", has(ModItems.DIRTY_BLOOD_FLOWER.get()))
                .save(consumer, name("ritual_blood_gem_sprout_seed"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.VORACIOUS_MUSHROOM.get())
                .requires(ModItems.GLOW_MUSHROOM.get())
                .requires(ModItems.UNKNOWN_ENTITY_FINGER.get())
                .requires(ModItems.BLOODY_SOUL_DUST.get())
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("ritual_voracious_mushroom"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.CRIMSON_LURE_MUSHROOM.get())
                .requires(ModItems.GLOW_MUSHROOM.get())
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(Items.ROTTEN_FLESH)
                .unlockedBy("has_glow_mushroom", has(ModItems.GLOW_MUSHROOM.get()))
                .save(consumer, name("ritual_crimson_lure_mushroom"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.BLASPHEMOUS_EYE.get(), 4)
                .requires(Items.ENDER_EYE)
                .requires(ModItems.BLASPHEMITE.get())
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                .save(consumer, name("ritual_blasphemous_eye"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.MARK_OF_THE_RESTLESS_SLUMBER.get())
                .requires(ModItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get())
                .requires(ModItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(ModItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(ModItems.BLASPHEMOUS_EYE.get()))
                .save(consumer, name("ritual_mark_of_restless_slumber"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.RUNE_OF_THE_RAVENOUS_GAZE.get())
                .requires(ModItems.BLASPHEMOUS_EYE.get())
                .requires(ModItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(ModItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(ModItems.BLASPHEMOUS_EYE.get()))
                .save(consumer, name("ritual_rune_of_ravenous_gaze"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.SEAL_OF_THE_HOLLOW_BULWARK.get())
                .requires(ModItems.BLASPHEMOUS_EYE.get())
                .requires(ModItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(ModItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(ModItems.BLASPHEMOUS_EYE.get()))
                .save(consumer, name("ritual_rune_of_hollow_bulwark"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.OMEN_OF_THE_CRUSHING_TOLL.get())
                .requires(ModItems.GAZE_OF_THE_UNKNOWN.get())
                .requires(ModItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(ModItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(ModItems.GAZE_OF_THE_UNKNOWN.get()))
                .save(consumer, name("ritual_rune_of_crushing_toll"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ModItems.GAZE_OF_THE_UNKNOWN.get())
                .requires(ModItems.BLASPHEMOUS_EYE.get())
                .requires(ModItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(ModItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get())
                .unlockedBy("has_great_ancient_blasphemous_gem", has(ModItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get()))
                .save(consumer, name("ritual_gaze_of_the_unknown"));


        // =================================================================
        // STANDARD BLOOD ALTAR RITUALS (SPELLBOOKS & ITEMS)
        // =================================================================

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_ECHO_SHARD.get())
                .requires(Items.AMETHYST_SHARD)
                .requires(ModItems.SANGUINITE.get())
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(ModBlocks.GLOWING_CRYSTAL.get())
                .unlockedBy("has_glowing_crystal", has(ModBlocks.GLOWING_CRYSTAL.get()))
                .save(consumer, name("ritual_blood_echo_shard"));

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_SCRATCH_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(ModItems.VEINREAVER_HORN.get())
                .requires(ModItems.BLOODY_SOUL_DUST.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_scratch"));

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_SPHERE_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(ModItems.AUREAL_REVENANT_DAGGER.get())
                .requires(ModItems.ANCIENT_GEM.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_sphere"));

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_NOVA_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.AUREAL_REVENANT_DAGGER.get())
                .requires(ModItems.RHNULL.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_nova"));

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.VEINREAVER_HORN.get())
                .requires(ModItems.ANCIENT_RHNULL_GEM.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_daggersrain"));

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_FIRE_METEOR_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.GREAT_ANCIENT_GEM.get())
                .requires(ModItems.RITEKEEPER_HEART.get())
                .requires(Items.FIRE_CHARGE)
                .unlockedBy("has_ritekeeper_heart", has(ModItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_meteor"));

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.GREAT_ANCIENT_GEM.get())
                .requires(ModItems.RITEKEEPER_HEART.get())
                .requires(ModItems.CINDER_ACOLYTE_FAINTED_EMBER.get())
                .unlockedBy("has_ritekeeper_heart", has(ModItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_column"));

        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_FIRE_SOUL_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.GREAT_ANCIENT_GEM.get())
                .requires(ModItems.RITEKEEPER_HEART.get())
                .requires(ModItems.FAILED_REMNANT_ASHES.get())
                .unlockedBy("has_ritekeeper_heart", has(ModItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_soul"));

        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_IMPALERS_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.GREAT_ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.BLASPHEMITE.get())
                .unlockedBy("has_great_ancient_rhnull_gem", has(ModItems.GREAT_ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_impalers"));

        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.GREAT_ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.RHNULL_SWORD.get())
                .unlockedBy("has_great_ancient_rhnull_gem", has(ModItems.GREAT_ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_heavy_sword"));

        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(Items.GOLD_BLOCK)
                .unlockedBy("has_ancient_rhnull_gem", has(ModItems.ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_golden_throne"));

        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_ORB_EMITTER_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.BLASPHEMOUS_EYE.get())
                .unlockedBy("has_ancient_rhnull_gem", has(ModItems.ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_orb_emitter"));

        // =================================================================
        // MATERIAL CONVERSIONS (BLOCK <-> INGOT <-> NUGGET)
        // =================================================================

        generateMaterialFamily(consumer, "sanguinite", ModBlocks.SANGUINITE_BLOCK, ModItems.SANGUINITE, ModItems.SANGUINITE_NUGGET);
        generateMaterialFamily(consumer, "rhnull", ModBlocks.RHNULL_BLOCK, ModItems.RHNULL, ModItems.RHNULL_NUGGET);
        generateMaterialFamily(consumer, "blasphemite", ModBlocks.BLASPHEMITE_BLOCK, ModItems.BLASPHEMITE, ModItems.BLASPHEMITE_NUGGET);

        // =================================================================
        // BLOCKS & DECORATIONS
        // =================================================================

        makePlanks(ModBlocks.BLOOD_PLANKS, ModTags.Items.BLOOD_LOGS).save(consumer);

        makeBricks(ModBlocks.POLISHED_BLOODY_STONE_BLOCK, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeBricks(ModBlocks.BLOODY_STONE_BRICKS, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeBricks(ModBlocks.BLOODY_STONE_TILES_BLOCK, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);

        makeStairs(ModBlocks.BLOODY_STONE_STAIRS, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeSlab(ModBlocks.BLOODY_STONE_SLAB, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeWall(ModBlocks.BLOODY_STONE_WALL, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeFence(ModBlocks.BLOODY_STONE_FENCE, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeFenceGate(ModBlocks.BLOODY_STONE_FENCE_GATE, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);

        makeStairs(ModBlocks.POLISHED_BLOODY_STONE_STAIRS, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeSlab(ModBlocks.POLISHED_BLOODY_STONE_SLAB, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeWall(ModBlocks.POLISHED_BLOODY_STONE_WALL, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeFence(ModBlocks.POLISHED_BLOODY_STONE_FENCE, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeFenceGate(ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);

        makeStairs(ModBlocks.BLOODY_STONE_TILES_STAIRS, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeSlab(ModBlocks.BLOODY_STONE_TILES_SLAB, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeWall(ModBlocks.BLOODY_STONE_TILES_WALL, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeFence(ModBlocks.BLOODY_STONE_TILES_FENCE, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeFenceGate(ModBlocks.BLOODY_STONE_FENCE_TILES_GATE, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);

        makeStairs(ModBlocks.BLOODY_STONE_BRICKS_STAIRS, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeSlab(ModBlocks.BLOODY_STONE_BRICKS_SLAB, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeWall(ModBlocks.BLOODY_STONE_BRICKS_WALL, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeFence(ModBlocks.BLOODY_STONE_BRICKS_FENCE, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeFenceGate(ModBlocks.BLOODY_STONE_FENCE_BRICKS_GATE, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);

        makeStairs(ModBlocks.BLOOD_PLANKS_STAIRS, ModBlocks.BLOOD_PLANKS).save(consumer);
        makeSlab(ModBlocks.BLOOD_PLANKS_SLAB, ModBlocks.BLOOD_PLANKS).save(consumer);
        makeFence(ModBlocks.BLOOD_PLANKS_FENCE, ModBlocks.BLOOD_PLANKS).save(consumer);
        makeFenceGate(ModBlocks.BLOOD_PLANKS_FENCE_GATE, ModBlocks.BLOOD_PLANKS).save(consumer);

        // =================================================================
        // TOOLS CRAFTING
        // =================================================================

        makeSword(ModItems.SANGUINITE_SWORD, ModItems.SANGUINITE).save(consumer);
        makePickaxe(ModItems.SANGUINITE_PICKAXE, ModItems.SANGUINITE).save(consumer);
        makeShovel(ModItems.SANGUINITE_SHOVEL, ModItems.SANGUINITE).save(consumer);
        makeAxe(ModItems.SANGUINITE_AXE, ModItems.SANGUINITE).save(consumer);
        makeHoe(ModItems.SANGUINITE_HOE, ModItems.SANGUINITE).save(consumer);

        makeSword(ModItems.RHNULL_SWORD, ModItems.RHNULL).save(consumer);
        makePickaxe(ModItems.RHNULL_PICKAXE, ModItems.RHNULL).save(consumer);
        makeShovel(ModItems.RHNULL_SHOVEL, ModItems.RHNULL).save(consumer);
        makeAxe(ModItems.RHNULL_AXE, ModItems.RHNULL).save(consumer);
        makeHoe(ModItems.RHNULL_HOE, ModItems.RHNULL).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOOD_BOW.get(), 1)
                .pattern(" SG")
                .pattern("S G")
                .pattern(" SG")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(consumer, name("blood_bow"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOOD_ARROW.get(), 4)
                .pattern(" F ")
                .pattern(" S ")
                .pattern(" G ")
                .define('S', Items.STICK)
                .define('G', ModItems.SCARLET_FEATHER.get())
                .define('F', Items.FLINT)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_scarlet_feather", has(ModItems.SCARLET_FEATHER.get()))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(consumer, name("blood_arrow"));

        // =================================================================
        // ARMOR CRAFTING
        // =================================================================

        makeHelmet(ModItems.BLOOD_HELMET, ModItems.SANGUINITE).save(consumer);
        makeChestplate(ModItems.BLOOD_CHESTPLATE, ModItems.SANGUINITE).save(consumer);
        makeLeggings(ModItems.BLOOD_LEGGINGS, ModItems.SANGUINITE).save(consumer);
        makeBoots(ModItems.BLOOD_BOOTS, ModItems.SANGUINITE).save(consumer);

        // =================================================================
        // MISC ITEMS CRAFTING
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOODY_SOUL_DUST.get(), 1)
                .pattern("GP")
                .pattern("PG")
                .define('G', ModItems.VEINREAVER_HORN.get())
                .define('P', ModItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_veinreaver_horn", has(ModItems.VEINREAVER_HORN.get()))
                .save(consumer, name("bloody_soul_dust_from_veinreaver_horn"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOODY_SOUL_DUST.get(), 1)
                .pattern("GP")
                .pattern("PG")
                .define('G', ModItems.AUREAL_REVENANT_DAGGER.get())
                .define('P', ModItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(ModItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("bloody_soul_dust_from_aureal_revenant_dagger"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHALICE_OF_THE_DAMMED.get(), 1)
                .pattern("GRG")
                .pattern("DGD")
                .pattern("GGG")
                .define('G', Items.GOLD_INGOT)
                .define('D', Items.DIAMOND)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(consumer, name("chalice_of_the_dammed"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CRIMSON_IDOL_COIN.get(), 1)
                .pattern("HSH")
                .pattern("SGS")
                .pattern("HSH")
                .define('G', ModItems.SANGUINITE.get())
                .define('H', ModItems.VEINREAVER_HORN.get())
                .define('S', ModItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_veinreaver_horn", has(ModItems.VEINREAVER_HORN.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(ModItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("crimson_idol_coin"));

        // =================================================================
        // GLOWING BLOCKS CRAFTING
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.GLOWING_CRYSTAL_GLASS_BLOCK.get(), 8)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("GGG")
                .define('C', ModBlocks.GLOWING_CRYSTAL.get().asItem())
                .define('G', Items.GLASS)
                .unlockedBy("has_glowing_crystal", has(ModBlocks.GLOWING_CRYSTAL.get()))
                .unlockedBy("has_glass", has(Items.GLASS))
                .save(consumer, name("glowing_crystal_glass_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLOOD_GLOW_STONE.get(), 8)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("GGG")
                .define('C', ModItems.BLOODY_SOUL_DUST.get())
                .define('G', Items.GLOWSTONE)
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .unlockedBy("has_glowstone", has(Items.GLOWSTONE))
                .save(consumer, name("blood_glow_stone_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get(), 8)
                .pattern("G")
                .pattern("C")
                .pattern("G")
                .define('C', ModItems.BLOODY_SOUL_DUST.get())
                .define('G', ModBlocks.GLOWING_CRYSTAL.get())
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .unlockedBy("has_glowing_crystal", has(ModBlocks.GLOWING_CRYSTAL.get()))
                .save(consumer, name("blood_glowing_chains"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GLOWING_CRYSTAL_LANTERN.get(), 1)
                .pattern("PPP")
                .pattern("PGP")
                .pattern("PPP")
                .define('G', ModBlocks.GLOWING_CRYSTAL.get())
                .define('P', ModItems.SANGUINITE_NUGGET.get())
                .unlockedBy("has_glowing_crystal", has(ModBlocks.GLOWING_CRYSTAL.get()))
                .unlockedBy("has_sanguinite_nugget", has(ModItems.SANGUINITE_NUGGET.get()))
                .save(consumer, name("glowing_crystal_lantern"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SANGUINE_CRUCIBLE.get(), 1)
                .pattern("GCG")
                .pattern("GSG")
                .pattern("GGG")
                .define('G', ModItems.SANGUINITE.get())
                .define('S', Items.GOLD_INGOT)
                .define('C', ModItems.SANGUINE_CRUCIBLE_CORE.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_sanguine_crucible_core", has(ModItems.SANGUINE_CRUCIBLE_CORE.get()))
                .save(consumer, name("sanguine_crucible"));


        // =================================================================
        // SMELTING & BLASTING
        // =================================================================

        smeltingRecipe(ModItems.SANGUINITE.get(), ModItems.RAW_SANGUINITE.get(), 1F).save(consumer, name("smelt_raw_sanguinite"));
        blastingRecipe(ModItems.SANGUINITE.get(), ModItems.RAW_SANGUINITE.get(), 1F).save(consumer, name("blast_raw_sanguinite"));


        // =================================================================
        // STONECUTTING
        // =================================================================

        bloodyStonecutting(ModBlocks.BLOODY_STONE_STAIRS.get(),2).save(consumer, name("bloody_stone_stairs_stonecutting"));
        bloodyStonecutting(ModBlocks.BLOODY_STONE_SLAB.get(),2).save(consumer, name("bloody_stone_slab_stonecutting"));
        bloodyStonecutting(ModBlocks.BLOODY_STONE_WALL.get(),1).save(consumer, name("bloody_stone_wall_stonecutting"));

        polishedBloodyStoneStonecutting(ModBlocks.POLISHED_BLOODY_STONE_STAIRS.get(),2).save(consumer, name("polished_bloody_stone_stairs_stonecutting"));
        polishedBloodyStoneStonecutting(ModBlocks.POLISHED_BLOODY_STONE_SLAB.get(),2).save(consumer, name("polished_bloody_stone_slab_stonecutting"));
        polishedBloodyStoneStonecutting(ModBlocks.POLISHED_BLOODY_STONE_WALL.get(),1).save(consumer, name("polished_bloody_stone_wall_stonecutting"));

        bloodyStoneTilesStonecutting(ModBlocks.BLOODY_STONE_TILES_STAIRS.get(),2).save(consumer, name("bloody_stone_tiles_stairs_stonecutting"));
        bloodyStoneTilesStonecutting(ModBlocks.BLOODY_STONE_TILES_SLAB.get(),2).save(consumer, name("bloody_stone_tiles_slab_stonecutting"));
        bloodyStoneTilesStonecutting(ModBlocks.BLOODY_STONE_TILES_WALL.get(),1).save(consumer, name("bloody_stone_tiles_wall_stonecutting"));

        bloodyStoneBricksStonecutting(ModBlocks.BLOODY_STONE_BRICKS_STAIRS.get(),2).save(consumer, name("bloody_stone_bricks_stairs_stonecutting"));
        bloodyStoneBricksStonecutting(ModBlocks.BLOODY_STONE_BRICKS_SLAB.get(),2).save(consumer, name("bloody_stone_bricks_slab_stonecutting"));
        bloodyStoneBricksStonecutting(ModBlocks.BLOODY_STONE_BRICKS_WALL.get(),1).save(consumer, name("bloody_stone_bricks_wall_stonecutting"));


        // =================================================================
        // FLASKS
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOOD_FLASK.get(), 3)
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(consumer, name("blood_flask"));


        // =================================================================
        // DAGGERS
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SACRIFICIAL_DAGGER.get(), 1)
                .pattern(" SS")
                .pattern("GS ")
                .pattern("DG ")
                .define('G', Items.GOLD_INGOT)
                .define('S', ModItems.SANGUINITE.get())
                .define('D', ModItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(ModItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("sacrificial_dagger"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HERETIC_SACRIFICIAL_DAGGER.get(), 1)
                .pattern(" BB")
                .pattern("GB ")
                .pattern("DG ")
                .define('G', Items.GOLD_INGOT)
                .define('B', ModItems.BLASPHEMITE.get())
                .define('D', ModItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_blasphemite", has(ModItems.BLASPHEMITE.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(ModItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("heretic_sacrificial_dagger"));


        // =================================================================
        // CRIMSON VEIL
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AMULET_OF_ANCESTRAL_BLOOD.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("BAB")
                .define('G', Items.GOLD_INGOT)
                .define('B', ModItems.FILLED_BLOOD_FLASK.get())
                .define('A', ModItems.ANCIENT_GEM.get())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_filled_blood_flask", has(ModItems.FILLED_BLOOD_FLASK.get()))
                .unlockedBy("has_ancient_gem", has(ModItems.ANCIENT_GEM.get()))
                .save(consumer, name("amulet_of_ancestral_blood"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("BAB")
                .define('G', ModItems.RHNULL.get())
                .define('B', ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .define('A', ModItems.GREAT_ANCIENT_GEM.get())
                .unlockedBy("has_rhnull", has(ModItems.RHNULL.get()))
                .unlockedBy("has_rhnull_filled_blood_flask", has(ModItems.FILLED_RHNULL_BLOOD_FLASK.get()))
                .unlockedBy("has_great_ancient_gem", has(ModItems.GREAT_ANCIENT_GEM.get()))
                .save(consumer, name("great_amulet_of_ancestral_blood"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CRIMSON_WARD_RING.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GAG")
                .define('G', Items.GOLD_INGOT)
                .define('A', ModItems.ANCIENT_GEM.get())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_ancient_gem", has(ModItems.ANCIENT_GEM.get()))
                .save(consumer, name("crimson_ward_ring"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLASPHEMOUS_RING.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GAG")
                .define('G', ModItems.BLASPHEMITE.get())
                .define('A', ModItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .unlockedBy("has_blasphemite", has(ModItems.BLASPHEMITE.get()))
                .unlockedBy("has_ancient_blasphemous_gem", has(ModItems.ANCIENT_BLASPHEMOUS_GEM.get()))
                .save(consumer, name("blasphemous_ring"));


        // =================================================================
        // COOKING
        // =================================================================

        smokingRecipe(ModItems.GOREHOG_COOKED_STEAK.get(), ModItems.GOREHOG_RAW_STEAK.get(), 0.35F, 1).save(consumer, name("smoking_gorehog_beef"));
        smeltingRecipe(ModItems.GOREHOG_COOKED_STEAK.get(), ModItems.GOREHOG_RAW_STEAK.get(), 0.35F).save(consumer, name("smelting_gorehog_beef"));
        campfireRecipe(ModItems.GOREHOG_COOKED_STEAK.get(), ModItems.GOREHOG_RAW_STEAK.get(), 0.35F).save(consumer, name("campfire_gorehog_beef"));
        smokingRecipe(ModItems.SCARLET_COOKED_CHICKEN.get(), ModItems.SCARLET_RAW_CHICKEN.get(), 0.35F, 1).save(consumer, name("smoking_scarlet_raw_chicken"));
        smeltingRecipe(ModItems.SCARLET_COOKED_CHICKEN.get(), ModItems.SCARLET_RAW_CHICKEN.get(), 0.35F).save(consumer, name("smelting_scarlet_raw_chicken"));
        campfireRecipe(ModItems.SCARLET_COOKED_CHICKEN.get(), ModItems.SCARLET_RAW_CHICKEN.get(), 0.35F).save(consumer, name("campfire_scarlet_raw_chicken"));

        // =================================================================
        // BLASPHEMOUS BIOME RECIPES
        // =================================================================

        makeHelmet(ModItems.RHNULL_HELMET, ModItems.RHNULL).save(consumer);
        makeChestplate(ModItems.RHNULL_CHESTPLATE, ModItems.RHNULL).save(consumer);
        makeLeggings(ModItems.RHNULL_LEGGINGS, ModItems.RHNULL).save(consumer);
        makeBoots(ModItems.RHNULL_BOOTS, ModItems.RHNULL).save(consumer);

        List<ItemLike> blasphemiteSmeltables = List.of(ModItems.RAW_BLASPHEMITE.get());

        oreSmelting(consumer, blasphemiteSmeltables, RecipeCategory.MISC, ModItems.BLASPHEMITE.get(),
                0.7f, 200, "blasphemite");

        oreBlasting(consumer, blasphemiteSmeltables, RecipeCategory.MISC, ModItems.BLASPHEMITE.get(),
                0.7f, 100, "blasphemite");

        // --- BLASPHEMOUS SANDSTONE ---

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .pattern("SS")
                .pattern("SS")
                .define('S', ModBlocks.BLASPHEMOUS_SAND_BLOCK.get())
                .unlockedBy("has_blasphemous_sand", has(ModBlocks.BLASPHEMOUS_SAND_BLOCK.get()))
                .save(consumer, name("blasphemous_sandstone_from_sand"));

        smeltingRecipe(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get(), ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get(), 0.1f)
                .save(consumer, name("smooth_blasphemous_sandstone_smelting"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("cut_blasphemous_sandstone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get())
                .unlockedBy("has_smooth_blasphemous_sandstone_slab", has(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get()))
                .save(consumer, name("chiseled_blasphemous_sandstone"));

        // --- ESCALERAS Y LOSAS ---
        makeStairs(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK).save(consumer);
        makeSlab(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK).save(consumer);

        // --- STONECUTTING ---
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_cut_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_chiseled_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_chiseled_detailed_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get())
                .unlockedBy("has_smooth_blasphemous_sandstone", has(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone_stairs"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get(), 2)
                .unlockedBy("has_smooth_blasphemous_sandstone", has(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone_slab"));

        // --- VASIIJA DECORADA ---
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DECORATED_POT_BLOCK.get())
                .pattern("B B")
                .pattern(" B ")
                .define('B', Items.BRICK)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(consumer, name("decorated_pot_block"));

        // --- STAR LAMP ---
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.STAR_LAMP_BLOCK.get())
                .pattern(" S ")
                .pattern("SLS")
                .pattern(" S ")
                .define('S', ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .define('L', ModBlocks.GLOWING_CRYSTAL_LANTERN.get())
                .unlockedBy("has_glowing_lantern", has(ModBlocks.GLOWING_CRYSTAL_LANTERN.get()))
                .save(consumer, name("star_lamp_block"));


        // =================================================================
        // BLASPHEMITE GEAR
        // =================================================================

        makeSword(ModItems.BLASPHEMITE_SWORD, ModItems.BLASPHEMITE).save(consumer);
        makePickaxe(ModItems.BLASPHEMITE_PICKAXE, ModItems.BLASPHEMITE).save(consumer);
        makeAxe(ModItems.BLASPHEMITE_AXE, ModItems.BLASPHEMITE).save(consumer);
        makeShovel(ModItems.BLASPHEMITE_SHOVEL, ModItems.BLASPHEMITE).save(consumer);
        makeHoe(ModItems.BLASPHEMITE_HOE, ModItems.BLASPHEMITE).save(consumer);

        makeHelmet(ModItems.BLASPHEMITE_HELMET, ModItems.BLASPHEMITE).save(consumer);
        makeChestplate(ModItems.BLASPHEMITE_CHESTPLATE, ModItems.BLASPHEMITE).save(consumer);
        makeLeggings(ModItems.BLASPHEMITE_LEGGINGS, ModItems.BLASPHEMITE).save(consumer);
        makeBoots(ModItems.BLASPHEMITE_BOOTS, ModItems.BLASPHEMITE).save(consumer);


        // =================================================================
        // BOSS WEAPONS
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLASPHEMOUS_IMPALER.get())
                .pattern("  B")
                .pattern(" S ")
                .pattern("R  ")
                .define('B', ModItems.BLASPHEMITE.get())
                .define('S', ModItems.SELIORA_ESSENCE.get())
                .define('R', ModItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(ModItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_impaler"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLASPHEMOUS_TWIN_DAGGERS.get())
                .pattern("B B")
                .pattern(" S ")
                .pattern("R R")
                .define('B', ModItems.BLASPHEMITE.get())
                .define('S', ModItems.SELIORA_ESSENCE.get())
                .define('R', ModItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(ModItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_twin_daggers"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get())
                .pattern("BBB")
                .pattern("BSB")
                .pattern(" R ")
                .define('B', ModItems.BLASPHEMITE.get())
                .define('S', ModItems.SELIORA_ESSENCE.get())
                .define('R', ModItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(ModItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_hulking_mass_of_iron"));

        // =================================================================
        // ANCIENT BLOODY STONE BRICKS
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get(), 8)
                .pattern("PPP")
                .pattern("PGP")
                .pattern("PPP")
                .define('P', ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_polished_bloody_stone", has(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()))
                .save(consumer, name("ancient_bloody_stone_bricks"));

        makeStairs(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);
        makeSlab(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);
        makeWall(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN.get(), 2)
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get())
                .unlockedBy("has_ancient_bloody_stone_slab", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get()))
                .save(consumer, name("ancient_bloody_stone_bricks_column"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_chiseled"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_detailed"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get(), 2)
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_slab"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_stairs"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_wall"));


        // =================================================================
        // LIGHTING
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.ANCIENT_TORCH_ITEM.get(), 4)
                .pattern("D")
                .pattern("S")
                .define('D', ModItems.BLOODY_SOUL_DUST.get())
                .define('S', Items.STICK)
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("ancient_torch"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ANCIENT_BLOODY_LAMP.get())
                .pattern("GGG")
                .pattern("GTG")
                .pattern("GGG")
                .define('G', Items.GOLD_NUGGET)
                .define('T', ModItems.ANCIENT_TORCH_ITEM.get())
                .unlockedBy("has_ancient_torch", has(ModItems.ANCIENT_TORCH_ITEM.get()))
                .save(consumer, name("ancient_bloody_lamp"));


        // =================================================================
        // MECHANISMS & UNKNOWN TECH
        // =================================================================

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UNKNOWN_PORTAL_ITEM.get(), 1)
                .pattern("RPR")
                .pattern("FTF")
                .pattern("RPR")
                .define('F', ModItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', ModItems.RHNULL.get())
                .define('T', ModBlocks.RHNULL_TANK.get())
                .define('P', ModItems.RHNULL_PIPE_ITEM.get())
                .unlockedBy("has_finger", has(ModItems.UNKNOWN_ENTITY_FINGER.get()))
                .save(consumer, name("unknown_portal_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RELIQUARY.get(), 1)
                .pattern("F F")
                .pattern("RGR")
                .pattern("FRF")
                .define('F', ModItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', ModItems.RHNULL.get())
                .define('G', ModItems.GAZE_OF_THE_UNKNOWN.get())
                .unlockedBy("has_gaze", has(ModItems.GAZE_OF_THE_UNKNOWN.get()))
                .save(consumer, name("reliquary"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANCIENT_OCULAR_LENSE.get(), 1)
                .pattern(" G ")
                .pattern("GAG")
                .pattern(" G ")
                .define('G', Items.GOLD_INGOT)
                .define('A', ModItems.ANCIENT_GEM.get())
                .unlockedBy("has_ancient_gem", has(ModItems.ANCIENT_GEM.get()))
                .save(consumer, name("ancient_ocular_lense"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLASPHEMOUS_OCULAR_LENSE.get(), 1)
                .pattern(" B ")
                .pattern("BAB")
                .pattern(" B ")
                .define('B', ModItems.BLASPHEMITE.get())
                .define('A', ModItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .unlockedBy("has_ancient_blasphemous_gem", has(ModItems.ANCIENT_BLASPHEMOUS_GEM.get()))
                .save(consumer, name("blasphemous_ocular_lense"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SANGUINITE_GEM_FRAME.get(), 1)
                .pattern(" N ")
                .pattern("N N")
                .pattern(" N ")
                .define('N', ModItems.SANGUINITE_NUGGET.get())
                .unlockedBy("has_sanguinite_nugget", has(ModItems.SANGUINITE_NUGGET.get()))
                .save(consumer, name("sanguinite_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SANGUINITE_GREAT_GEM_FRAME.get(), 1)
                .pattern(" S ")
                .pattern("S S")
                .pattern(" S ")
                .define('S', ModItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_great_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RHNULL_GEM_FRAME.get(), 1)
                .pattern(" N ")
                .pattern("N N")
                .pattern(" N ")
                .define('N', ModItems.RHNULL_NUGGET.get())
                .unlockedBy("has_rhnull_nugget", has(ModItems.RHNULL_NUGGET.get()))
                .save(consumer, name("rhnull_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RHNULL_GREAT_GEM_FRAME.get(), 1)
                .pattern(" R ")
                .pattern("R R")
                .pattern(" R ")
                .define('R', ModItems.RHNULL.get())
                .unlockedBy("has_rhnull", has(ModItems.RHNULL.get()))
                .save(consumer, name("rhnull_great_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SANGUINITE_PIPE_ITEM.get(), 8)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("GGG")
                .define('G', Items.GLASS_PANE)
                .define('S', ModItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_pipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RHNULL_PIPE_ITEM.get(), 8)
                .pattern("GGG")
                .pattern("RRR")
                .pattern("GGG")
                .define('G', Items.GLASS_PANE)
                .define('R', ModItems.RHNULL.get())
                .unlockedBy("has_rhnull", has(ModItems.RHNULL.get()))
                .save(consumer, name("rhnull_pipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SANGUINITE_TANK.get())
                .pattern("SGS")
                .pattern("G G")
                .pattern("SGS")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_tank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RHNULL_TANK.get())
                .pattern("RGR")
                .pattern("G G")
                .pattern("RGR")
                .define('R', ModItems.RHNULL.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_rhnull", has(ModItems.RHNULL.get()))
                .save(consumer, name("rhnull_tank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SANGUINITE_CONDENSER.get(), 1)
                .pattern("SGS")
                .pattern("G G")
                .pattern("SSS")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_condenser"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RHNULL_CONDENSER.get(), 1)
                .pattern("RGR")
                .pattern("G G")
                .pattern("RRR")
                .define('R', ModItems.RHNULL.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_rhnull", has(ModItems.RHNULL.get()))
                .save(consumer, name("rhnull_condenser"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get())
                .pattern("GSG")
                .pattern("RDR")
                .pattern("GSG")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('R', Items.REDSTONE)
                .define('D', ModItems.SACRIFICIAL_DAGGER.get())
                .unlockedBy("has_sacrificial_dagger", has(ModItems.SACRIFICIAL_DAGGER.get()))
                .save(consumer, name("sanguinite_blood_harvester"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SANGUINITE_INFUSOR.get())
                .pattern("SGS")
                .pattern("GCG")
                .pattern("SBS")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('B', ModBlocks.SANGUINITE_BLOCK.get())
                .define('C', ModItems.ANCIENT_GEM.get())
                .unlockedBy("has_crucible_core", has(ModItems.SANGUINE_CRUCIBLE_CORE.get()))
                .save(consumer, name("sanguinite_infusor"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SANGUINE_LAPIDARY.get())
                .pattern(" D ")
                .pattern("SPS")
                .pattern("BBB")
                .define('D', ModItems.GREAT_ANCIENT_GEM.get())
                .define('S', ModItems.SANGUINITE.get())
                .define('P', ModBlocks.POLISHED_BLOODY_STONE_SLAB.get())
                .define('B', ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("sanguine_lapidary"));


        // =================================================================
        // FLUID INFUSION
        // =================================================================

        makeInfusion(consumer, ModItems.RHNULL.get(), ModItems.SANGUINITE.get(), 500, 500, "rhnull_ingot_infusion");
        makeInfusion(consumer, ModItems.FILLED_RHNULL_BLOOD_FLASK.get(), ModItems.FILLED_BLOOD_FLASK.get(), 500, 500, "filled_rhnull_flask_infusion");
        makeInfusion(consumer, ModItems.ANCIENT_RHNULL_GEM.get(), ModItems.ANCIENT_GEM.get(), 1000, 1000, "ancient_rhnull_gem_infusion");
        makeInfusion(consumer, ModItems.GREAT_ANCIENT_RHNULL_GEM.get(), ModItems.GREAT_ANCIENT_GEM.get(), 2000, 2000, "great_ancient_rhnull_gem_infusion");


        // =================================================================
        // CONDENSER RECIPES
        // =================================================================

        CondenserRecipeBuilder.condense(ModItems.ANCIENT_GEM.get())
                .requiresItem(Ingredient.of(ModItems.SANGUINITE_GEM_FRAME.get()))
                .requiresFluid(ModFluids.BLOOD_SOURCE.get(), 1000)
                .unlockedBy("has_gem_frame", has(ModItems.SANGUINITE_GEM_FRAME.get()))
                .save(consumer, name("condense_ancient_gem"));

        CondenserRecipeBuilder.condense(ModItems.GREAT_ANCIENT_GEM.get())
                .requiresItem(Ingredient.of(ModItems.SANGUINITE_GREAT_GEM_FRAME.get()))
                .requiresFluid(ModFluids.BLOOD_SOURCE.get(), 2000)
                .unlockedBy("has_great_gem_frame", has(ModItems.SANGUINITE_GREAT_GEM_FRAME.get()))
                .save(consumer, name("condense_great_ancient_gem"));

        CondenserRecipeBuilder.condense(ModItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .requiresItem(Ingredient.of(ModItems.RHNULL_GEM_FRAME.get()))
                .requiresFluid(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 1000)
                .unlockedBy("has_gem_frame", has(ModItems.RHNULL_GEM_FRAME.get()))
                .save(consumer, name("condense_ancient_blasphemous_gem"));

        CondenserRecipeBuilder.condense(ModItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requiresItem(Ingredient.of(ModItems.RHNULL_GREAT_GEM_FRAME.get()))
                .requiresFluid(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 2000)
                .unlockedBy("has_great_gem_frame", has(ModItems.RHNULL_GREAT_GEM_FRAME.get()))
                .save(consumer, name("condense_great_ancient_blasphemous_gem"));

    }

    // =================================================================
    // HELPER METHODS
    // =================================================================

    /**
     * Automatically generates and correctly names the 4 standard material conversion recipes.
     */
    private void generateMaterialFamily(Consumer<FinishedRecipe> consumer, String materialName, Supplier<Block> block, Supplier<Item> ingot, Supplier<Item> nugget) {


        makeIngotToBlock(block, ingot).save(consumer, name(materialName + "_block"));
        makeBlockToIngot(ingot, block).save(consumer, name(materialName + "_from_block"));

        if (nugget != null) {
            makeIngotToNugget(nugget, ingot).save(consumer, name(materialName + "_nugget_from_ingot"));
            makeNuggetToIngot(ingot, nugget).save(consumer, name(materialName + "_from_nuggets"));
        }
    }

    private ResourceLocation name(String name) {
        return new ResourceLocation(BloodyHell.MODID, name);
    }
}

