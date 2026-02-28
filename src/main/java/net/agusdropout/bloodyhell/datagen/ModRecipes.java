package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipes extends ModRecipesProvider {

    public ModRecipes(PackOutput output) {
        super(output);
    }

    @SuppressWarnings("removal")
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        //---------------------------------Altars----------------------------------//
        //Blood altar
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
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_bloody_stone_tiles_block", has(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()))
                .unlockedBy("has_corrupted_blood_flask", has(ModItems.CORRUPTED_BLOOD_FLASK.get()))
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("blood_altar"));
        //Main blood altar
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAIN_BLOOD_ALTAR.get(), 1)
                .pattern("GDG")
                .pattern("SCS")
                .pattern("TST")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('C', ModItems.CORRUPTED_BLOOD_FLASK.get())
                .define('D', ModItems.CHALICE_OF_THE_DAMMED.get())
                .define('T', ModBlocks.BLOODY_STONE_TILES_BLOCK.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_bloody_stone_tiles_block", has(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()))
                .unlockedBy("has_corrupted_blood_flask", has(ModItems.CORRUPTED_BLOOD_FLASK.get()))
                .unlockedBy("has_chalice_of_the_dammed", has(ModItems.CHALICE_OF_THE_DAMMED.get()))
                .save(consumer, name("manin_blood_altar"));


        // =================================================================
        // RITUALES DEL ALTAR DE SANGRE (BLOOD ALTAR RECIPES)
        // =================================================================

        // 1. ANCIENT GEM
        BloodAltarRecipeBuilder.ritual(ModItems.ANCIENT_GEM.get())
                .requires(ModItems.VEINREAVER_HORN.get())
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(ModItems.SANGUINITE.get())
                .unlockedBy("has_horn", has(ModItems.VEINREAVER_HORN.get()))
                .save(consumer, name("ritual_ancient_gem"));

        // 2. GREAT AMULET
        BloodAltarRecipeBuilder.ritual(ModItems.GREAT_ANCIENT_GEM.get())
                .requires(ModBlocks.GLOWING_CRYSTAL.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.RHNULL.get())
                .unlockedBy("has_rhnull", has(ModItems.RHNULL.get()))
                .save(consumer, name("ritual_great_amulet"));

        // --- RITUALES ESPECIALES (Con items dummy/vanilla) ---

        // 3. SUMMON COW -> Resultado: LEATHER
        BloodAltarRecipeBuilder.ritual(Items.LEATHER)
                .requires(Items.WHEAT)
                .requires(Items.MILK_BUCKET)
                .requires(Items.WHEAT)
                .unlockedBy("has_milk", has(Items.MILK_BUCKET))
                .save(consumer, name("ritual_summon_cow"));

        // 4. FIND MAUSOLEUM -> Resultado: RECOVERY_COMPASS
        BloodAltarRecipeBuilder.ritual(Items.RECOVERY_COMPASS)
                .requires(ModItems.VEINREAVER_HORN.get())
                .requires(ModItems.AUREAL_REVENANT_DAGGER.get())
                .requires(ModItems.GLOW_MUSHROOM.get())
                .unlockedBy("has_dagger", has(ModItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("ritual_find_mausoleum"));

        // 5. TURN BLOOD TO RHNULL -> Resultado: RED_DYE
        BloodAltarRecipeBuilder.ritual(Items.RED_DYE)
                .requires(ModItems.BLOODY_SOUL_DUST.get())
                .requires(Items.GOLD_INGOT)
                .requires(ModItems.GLOW_MUSHROOM.get())
                .unlockedBy("has_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("ritual_transmute_rhnull"));

        // --- LIBROS DE HECHIZOS ---

        // --- EXISTING SPELLBOOKS (Refactored) ---

// --- EXISTING SPELLBOOKS ---

// 1. SCRATCH (Basic)
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_SCRATCH_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(ModItems.VEINREAVER_HORN.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_scratch"));

// 2. BLOOD SPHERE (Basic)
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_SPHERE_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(ModItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_sphere"));

// 3. BLOOD NOVA (Advanced)
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_NOVA_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.ANCIENT_GEM.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_nova"));

// 4. DAGGERS RAIN (Advanced)
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.VEINREAVER_HORN.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_daggersrain"));


// --- BLOOD FIRE SPELLBOOKS (Boss Tier) ---

// 5. BLOOD FIRE METEOR
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_FIRE_METEOR_SPELLBOOK.get())
                .requires(ModItems.GREAT_ANCIENT_GEM.get())
                .requires(ModItems.RITEKEEPER_HEART.get())
                .requires(ModItems.CINDER_ACOLYTE_FAINTED_EMBER.get())
                .unlockedBy("has_ritekeeper_heart", has(ModItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_meteor"));

// 6. BLOOD FIRE COLUMN
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get())
                .requires(ModItems.GREAT_ANCIENT_GEM.get())
                .requires(ModItems.RITEKEEPER_HEART.get())
                .requires(ModItems.CINDER_ACOLYTE_FAINTED_EMBER.get())
                .unlockedBy("has_ritekeeper_heart", has(ModItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_column"));

// 7. BLOOD FIRE SOUL
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_FIRE_SOUL_SPELLBOOK.get())
                .requires(ModItems.GREAT_ANCIENT_GEM.get())
                .requires(ModItems.RITEKEEPER_HEART.get())
                .requires(ModItems.FAILED_REMNANT_ASHES.get())
                .unlockedBy("has_ritekeeper_heart", has(ModItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_soul"));

        // --- RHNULL SPELLBOOKS  ---
        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_IMPALERS_SPELLBOOK.get())
                .requires(ModItems.GREAT_ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.RHNULL.get())
                .unlockedBy("has_great_ancient_rhnull_gem", has(ModItems.GREAT_ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_impalers"));

        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get())
                .requires(ModItems.GREAT_ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.RHNULL_SWORD.get())
                .unlockedBy("has_great_ancient_rhnull_gem", has(ModItems.GREAT_ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_heavy_sword"));

        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get())
                .requires(ModItems.ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.RHNULL_HOE.get())
                .unlockedBy("has_ancient_rhnull_gem", has(ModItems.ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_golden_throne"));

        BloodAltarRecipeBuilder.ritual(ModItems.RHNULL_ORB_EMITTER_SPELLBOOK.get())
                .requires(ModItems.ANCIENT_RHNULL_GEM.get())
                .requires(ModItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(ModItems.RHNULL.get())
                .unlockedBy("has_ancient_rhnull_gem", has(ModItems.ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_orb_emitter"));


        makePlanks(ModBlocks.BLOOD_PLANKS, ModTags.Items.BLOOD_LOGS).save(consumer);


        makeBricks(ModBlocks.POLISHED_BLOODY_STONE_BLOCK, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeBricks(ModBlocks.BLOODY_STONE_BRICKS, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeBricks(ModBlocks.BLOODY_STONE_TILES_BLOCK, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);

        //Bloody stone block
        makeStairs(ModBlocks.BLOODY_STONE_STAIRS, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeSlab(ModBlocks.BLOODY_STONE_SLAB, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeWall(ModBlocks.BLOODY_STONE_WALL, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeFence(ModBlocks.BLOODY_STONE_FENCE, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeFenceGate(ModBlocks.BLOODY_STONE_FENCE_GATE, ModBlocks.BLOODY_STONE_BLOCK).save(consumer);

        //Polished bloody stone block
        makeStairs(ModBlocks.POLISHED_BLOODY_STONE_STAIRS, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeSlab(ModBlocks.POLISHED_BLOODY_STONE_SLAB, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeWall(ModBlocks.POLISHED_BLOODY_STONE_WALL, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeFence(ModBlocks.POLISHED_BLOODY_STONE_FENCE, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeFenceGate(ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE, ModBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);

        //Bloody stone tiles block
        makeStairs(ModBlocks.BLOODY_STONE_TILES_STAIRS, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeSlab(ModBlocks.BLOODY_STONE_TILES_SLAB, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeWall(ModBlocks.BLOODY_STONE_TILES_WALL, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeFence(ModBlocks.BLOODY_STONE_TILES_FENCE, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeFenceGate(ModBlocks.BLOODY_STONE_FENCE_TILES_GATE, ModBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);

        //Bloody stone bricks block
        makeStairs(ModBlocks.BLOODY_STONE_BRICKS_STAIRS, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeSlab(ModBlocks.BLOODY_STONE_BRICKS_SLAB, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeWall(ModBlocks.BLOODY_STONE_BRICKS_WALL, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeFence(ModBlocks.BLOODY_STONE_BRICKS_FENCE, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeFenceGate(ModBlocks.BLOODY_STONE_FENCE_BRICKS_GATE, ModBlocks.BLOODY_STONE_BRICKS).save(consumer);

        //Blood Planks
        makeStairs(ModBlocks.BLOOD_PLANKS_STAIRS, ModBlocks.BLOOD_PLANKS).save(consumer);
        makeSlab(ModBlocks.BLOOD_PLANKS_SLAB, ModBlocks.BLOOD_PLANKS).save(consumer);
        makeFence(ModBlocks.BLOOD_PLANKS_FENCE, ModBlocks.BLOOD_PLANKS).save(consumer);
        makeFenceGate(ModBlocks.BLOOD_PLANKS_FENCE_GATE, ModBlocks.BLOOD_PLANKS).save(consumer);

        //Mineral blocks
        makeIngotToBlock(ModBlocks.SANGUINITE_BLOCK, ModItems.SANGUINITE).save(consumer);
        makeIngotToBlock(ModBlocks.RHNULL_BLOCK, ModItems.RHNULL).save(consumer);

        //------------------------------Tools crafting--------------------------------//
            //Sanguinite tools
        makeSword(ModItems.SANGUINITE_SWORD, ModItems.SANGUINITE).save(consumer);
        makePickaxe(ModItems.SANGUINITE_PICKAXE, ModItems.SANGUINITE).save(consumer);
        makeShovel(ModItems.SANGUINITE_SHOVEL, ModItems.SANGUINITE).save(consumer);
        makeAxe(ModItems.SANGUINITE_AXE, ModItems.SANGUINITE).save(consumer);
        makeHoe(ModItems.SANGUINITE_HOE, ModItems.SANGUINITE).save(consumer);

            //Rhnull tools
        makeSword(ModItems.RHNULL_SWORD, ModItems.RHNULL).save(consumer);
        makePickaxe(ModItems.RHNULL_PICKAXE, ModItems.RHNULL).save(consumer);
        makeShovel(ModItems.RHNULL_SHOVEL, ModItems.RHNULL).save(consumer);
        makeAxe(ModItems.RHNULL_AXE, ModItems.RHNULL).save(consumer);
        makeHoe(ModItems.RHNULL_HOE, ModItems.RHNULL).save(consumer);

        //Bow
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOOD_BOW.get(), 1)
                .pattern(" SG")
                .pattern("S G")
                .pattern(" SG")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(consumer, name("blood_bow"));
        //Arrow
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

        //------------------------------Armor crafting--------------------------------//
            //Sanguinite armor
        makeHelmet(ModItems.BLOOD_HELMET, ModItems.SANGUINITE).save(consumer);
        makeChestplate(ModItems.BLOOD_CHESTPLATE, ModItems.SANGUINITE).save(consumer);
        makeLeggings(ModItems.BLOOD_LEGGINGS, ModItems.SANGUINITE).save(consumer);
        makeBoots(ModItems.BLOOD_BOOTS, ModItems.SANGUINITE).save(consumer);

        //---------------------------------Misc Items crafting--------------------------------//
            //Bloody soul dust
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

        //Chalice of the dammed
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
        //Crimson idol coin
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
        //---------------------------------Glowing blocks crafting--------------------------------//
            //Glowing crystal glass block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.GLOWING_CRYSTAL_GLASS_BLOCK.get(), 8)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("GGG")
                .define('C', ModBlocks.GLOWING_CRYSTAL.get().asItem())
                .define('G', Items.GLASS)
                .unlockedBy("has_glowing_crystal", has(ModBlocks.GLOWING_CRYSTAL.get()))
                .unlockedBy("has_glass", has(Items.GLASS))
                .save(consumer, name("glowing_crystal_glass_block"));
            //Blood glow stone block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLOOD_GLOW_STONE.get(), 8)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("GGG")
                .define('C', ModItems.BLOODY_SOUL_DUST.get())
                .define('G', Items.GLOWSTONE)
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .unlockedBy("has_glowstone", has(Items.GLOWSTONE))
                .save(consumer, name("blood_glow_stone_block"));
            //Blood glowing chains
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get(), 8)
                .pattern("G")
                .pattern("C")
                .pattern("G")
                .define('C', ModItems.BLOODY_SOUL_DUST.get())
                .define('G', ModBlocks.GLOWING_CRYSTAL.get())
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .unlockedBy("has_glowing_crystal", has(ModBlocks.GLOWING_CRYSTAL.get()))
                .save(consumer, name("blood_glowing_chains"));
            //Glowing crystal lantern
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GLOWING_CRYSTAL_LANTERN.get(), 1)
                .pattern("PPP")
                .pattern("PGP")
                .pattern("PPP")
                .define('G', ModBlocks.GLOWING_CRYSTAL.get())
                .define('P', ModItems.SANGUINITE_NUGGET.get())
                .unlockedBy("has_glowing_crystal", has(ModBlocks.GLOWING_CRYSTAL.get()))
                .unlockedBy("has_sanguinite_nugget", has(ModItems.SANGUINITE_NUGGET.get()))
                .save(consumer, name("glowing_crystal_lantern"));

        //Sanguine Crucible
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

        //-------------------------------Block to ingot----------------------------------//
        makeBlockToIngot(ModItems.SANGUINITE,ModBlocks.SANGUINITE_BLOCK).save(consumer);
        makeBlockToIngot(ModItems.RHNULL,ModBlocks.RHNULL_BLOCK).save(consumer);

        //-------------------------------Ingot to nugget----------------------------------//
        makeIngotToNugget(ModItems.SANGUINITE_NUGGET,ModItems.SANGUINITE).save(consumer);
        makeIngotToNugget(ModItems.RHNULL_NUGGET,ModItems.RHNULL).save(consumer);

        //---------------------------------Smelting & Blasting--------------------------//
        //Sanguinite
        smeltingRecipe(ModItems.SANGUINITE.get(), ModItems.RAW_SANGUINITE.get(), 1F).save(consumer, name("smelt_raw_sanguinite"));
        blastingRecipe(ModItems.SANGUINITE.get(), ModItems.RAW_SANGUINITE.get(), 1F).save(consumer, name("blast_raw_sanguinite"));

        //---------------------------------Stonecutting----------------------------------//
            //Bloody stone
        bloodyStonecutting(ModBlocks.BLOODY_STONE_STAIRS.get(),2).save(consumer, name("bloody_stone_stairs_stonecutting"));
        bloodyStonecutting(ModBlocks.BLOODY_STONE_SLAB.get(),2).save(consumer, name("bloody_stone_slab_stonecutting"));
        bloodyStonecutting(ModBlocks.BLOODY_STONE_WALL.get(),1).save(consumer, name("bloody_stone_wall_stonecutting"));
            //Polished bloody stone
        polishedBloodyStoneStonecutting(ModBlocks.POLISHED_BLOODY_STONE_STAIRS.get(),2).save(consumer, name("polished_bloody_stone_stairs_stonecutting"));
        polishedBloodyStoneStonecutting(ModBlocks.POLISHED_BLOODY_STONE_SLAB.get(),2).save(consumer, name("polished_bloody_stone_slab_stonecutting"));
        polishedBloodyStoneStonecutting(ModBlocks.POLISHED_BLOODY_STONE_WALL.get(),1).save(consumer, name("polished_bloody_stone_wall_stonecutting"));
            //Bloody stone tiles
        bloodyStoneTilesStonecutting(ModBlocks.BLOODY_STONE_TILES_STAIRS.get(),2).save(consumer, name("bloody_stone_tiles_stairs_stonecutting"));
        bloodyStoneTilesStonecutting(ModBlocks.BLOODY_STONE_TILES_SLAB.get(),2).save(consumer, name("bloody_stone_tiles_slab_stonecutting"));
        bloodyStoneTilesStonecutting(ModBlocks.BLOODY_STONE_TILES_WALL.get(),1).save(consumer, name("bloody_stone_tiles_wall_stonecutting"));
            //Bloody stone bricks
        bloodyStoneBricksStonecutting(ModBlocks.BLOODY_STONE_BRICKS_STAIRS.get(),2).save(consumer, name("bloody_stone_bricks_stairs_stonecutting"));
        bloodyStoneBricksStonecutting(ModBlocks.BLOODY_STONE_BRICKS_SLAB.get(),2).save(consumer, name("bloody_stone_bricks_slab_stonecutting"));
        bloodyStoneBricksStonecutting(ModBlocks.BLOODY_STONE_BRICKS_WALL.get(),1).save(consumer, name("bloody_stone_bricks_wall_stonecutting"));

        //---------------------------------Flasks----------------------------------//
        //Blood flask
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOOD_FLASK.get(), 1)
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(consumer, name("blood_flask"));


        //---------------------------------Daggers----------------------------------//
        //sacrificial dagger
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
        //---------------------------------Crimson Veil----------------------------------//
        //Amulet of ancestral blood
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
        //Great amulet of ancestral blood
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
        //Crimson Ward ring
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CRIMSON_WARD_RING.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GAG")
                .define('G', Items.GOLD_INGOT)
                .define('A', ModItems.ANCIENT_GEM.get())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_ancient_gem", has(ModItems.ANCIENT_GEM.get()))
                .save(consumer, name("crimson_ward_ring"));
        //-------------------------------Cooking----------------------------------//
        smokingRecipe(ModItems.GOREHOG_COOKED_STEAK.get(), ModItems.GOREHOG_RAW_STEAK.get(), 0.35F, 1).save(consumer, name("smoking_gorehog_beef"));
        smeltingRecipe(ModItems.GOREHOG_COOKED_STEAK.get(), ModItems.GOREHOG_RAW_STEAK.get(), 0.35F).save(consumer, name("smelting_gorehog_beef"));
        campfireRecipe(ModItems.GOREHOG_COOKED_STEAK.get(), ModItems.GOREHOG_RAW_STEAK.get(), 0.35F).save(consumer, name("campfire_gorehog_beef"));
        smokingRecipe(ModItems.SCARLET_COOKED_CHICKEN.get(), ModItems.SCARLET_RAW_CHICKEN.get(), 0.35F, 1).save(consumer, name("smoking_scarlet_raw_chicken"));
        smeltingRecipe(ModItems.SCARLET_COOKED_CHICKEN.get(), ModItems.SCARLET_RAW_CHICKEN.get(), 0.35F).save(consumer, name("smelting_scarlet_raw_chicken"));
        campfireRecipe(ModItems.SCARLET_COOKED_CHICKEN.get(), ModItems.SCARLET_RAW_CHICKEN.get(), 0.35F).save(consumer, name("campfire_scarlet_raw_chicken"));

        //---------------------------------BLASPHEMOUS BIOME RECIPES--------------------------------//

        makeHelmet(ModItems.RHNULL_HELMET, ModItems.RHNULL).save(consumer);
        makeChestplate(ModItems.RHNULL_CHESTPLATE, ModItems.RHNULL).save(consumer);
        makeLeggings(ModItems.RHNULL_LEGGINGS, ModItems.RHNULL).save(consumer);
        makeBoots(ModItems.RHNULL_BOOTS, ModItems.RHNULL).save(consumer);

        // --- BLASPHEMITE (Material) ---
        // Nugget <-> Ingot/Item
        makeIngotToNugget(ModItems.BLASPHEMITE_NUGGET, ModItems.BLASPHEMITE).save(consumer);
        makeNuggetToIngot(ModItems.BLASPHEMITE, ModItems.BLASPHEMITE_NUGGET).save(consumer);

        // Solo cocinamos el RAW, no el bloque
        List<ItemLike> blasphemiteSmeltables = List.of(ModItems.RAW_BLASPHEMITE.get());

        // Receta Horno Normal
        oreSmelting(consumer, blasphemiteSmeltables, RecipeCategory.MISC, ModItems.BLASPHEMITE.get(),
                0.7f, 200, "blasphemite");

        // Receta Alto Horno (Blasting)
        oreBlasting(consumer, blasphemiteSmeltables, RecipeCategory.MISC, ModItems.BLASPHEMITE.get(),
                0.7f, 100, "blasphemite");

        // --- BLASPHEMOUS SANDSTONE (Arenisca) ---

        // Sand -> Sandstone (4 Arenas = 1 Arenisca)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .pattern("SS")
                .pattern("SS")
                .define('S', ModBlocks.BLASPHEMOUS_SAND_BLOCK.get())
                .unlockedBy("has_blasphemous_sand", has(ModBlocks.BLASPHEMOUS_SAND_BLOCK.get()))
                .save(consumer, name("blasphemous_sandstone_from_sand"));

        // Sandstone -> Smooth Sandstone (Horno)
        smeltingRecipe(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get(), ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get(), 0.1f)
                .save(consumer, name("smooth_blasphemous_sandstone_smelting"));

        // Sandstone -> Cut Sandstone (4 Areniscas = 4 Areniscas Cortadas)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("cut_blasphemous_sandstone"));

        // Sandstone Slab -> Chiseled Sandstone (2 Losas = 1 Cincelado)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get()) // Usualmente se hace con slabs de arenisca
                .unlockedBy("has_smooth_blasphemous_sandstone_slab", has(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get()))
                .save(consumer, name("chiseled_blasphemous_sandstone"));

        // --- ESCALERAS Y LOSAS (Smooth Sandstone) ---
        makeStairs(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK).save(consumer);
        makeSlab(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK).save(consumer);

        // --- STONECUTTING (Cortapiedras) ---

        // Input: Blasphemous Sandstone
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

        // Variantes de Smooth en Cortapiedras
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get())
                .unlockedBy("has_smooth_blasphemous_sandstone", has(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone_stairs"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get(), 2)
                .unlockedBy("has_smooth_blasphemous_sandstone", has(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone_slab"));

        // --- VASIIJA DECORADA (Decorated Pot) ---
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DECORATED_POT_BLOCK.get())
                .pattern("B B")
                .pattern(" B ")
                .define('B', Items.BRICK)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(consumer, name("decorated_pot_block"));

        // --- STAR LAMP (Lámpara Estrella) ---
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.STAR_LAMP_BLOCK.get())
                .pattern(" S ")
                .pattern("SLS")
                .pattern(" S ")
                .define('S', ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .define('L', ModBlocks.GLOWING_CRYSTAL_LANTERN.get()) // O Glowstone
                .unlockedBy("has_glowing_lantern", has(ModBlocks.GLOWING_CRYSTAL_LANTERN.get()))
                .save(consumer, name("star_lamp_block"));
        //---------------------------------BLASPHEMITE GEAR--------------------------------//

        // --- Herramientas (Usan Sticks normales por defecto según tus helpers) ---
        makeSword(ModItems.BLASPHEMITE_SWORD, ModItems.BLASPHEMITE).save(consumer);
        makePickaxe(ModItems.BLASPHEMITE_PICKAXE, ModItems.BLASPHEMITE).save(consumer);
        makeAxe(ModItems.BLASPHEMITE_AXE, ModItems.BLASPHEMITE).save(consumer);
        makeShovel(ModItems.BLASPHEMITE_SHOVEL, ModItems.BLASPHEMITE).save(consumer);
        makeHoe(ModItems.BLASPHEMITE_HOE, ModItems.BLASPHEMITE).save(consumer);

        // --- Armaduras ---
        makeHelmet(ModItems.BLASPHEMITE_HELMET, ModItems.BLASPHEMITE).save(consumer);
        makeChestplate(ModItems.BLASPHEMITE_CHESTPLATE, ModItems.BLASPHEMITE).save(consumer);
        makeLeggings(ModItems.BLASPHEMITE_LEGGINGS, ModItems.BLASPHEMITE).save(consumer);
        makeBoots(ModItems.BLASPHEMITE_BOOTS, ModItems.BLASPHEMITE).save(consumer);

        // --- BLASPHEMOUS IMPALER (Lanza) ---
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLASPHEMOUS_IMPALER.get())
                .pattern("  B")
                .pattern(" S ")
                .pattern("R  ")
                .define('B', ModItems.BLASPHEMITE.get())      // Punta
                .define('S', ModItems.SELIORA_ESSENCE.get())  // Núcleo del Jefe
                .define('R', ModItems.RHNULL.get())           // Mango fuerte
                // EL TRUCO: La receta solo se desbloquea si tienes la Esencia
                .unlockedBy("has_seliora_essence", has(ModItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_impaler"));

        //---------------------------------BOSS WEAPONS (LOCKED)--------------------------------//

        // --- BLASPHEMOUS TWIN DAGGERS (Dagas Gemelas) ---
        // Diseño: Dos hojas de Blasphemita laterales, Esencia al centro, Mangos de Rhnull
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLASPHEMOUS_TWIN_DAGGERS.get())
                .pattern("B B")
                .pattern(" S ")
                .pattern("R R")
                .define('B', ModItems.BLASPHEMITE.get())
                .define('S', ModItems.SELIORA_ESSENCE.get())
                .define('R', ModItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(ModItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_twin_daggers"));

        // --- BLASPHEMOUS HULKING MASS OF IRON (Espadón Gigante) ---
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get())
                .pattern("BBB")
                .pattern("BSB") // Bordeado de metal con el alma en el centro
                .pattern(" R ")
                .define('B', ModItems.BLASPHEMITE.get())
                .define('S', ModItems.SELIORA_ESSENCE.get())
                .define('R', ModItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(ModItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_hulking_mass_of_iron"));


        // 1. Ancient Bloody Stone Bricks (Base Block)
        // Recipe: 8 Polished Bloody Stone + 1 Gold Ingot in center
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get(), 8)
                .pattern("PPP")
                .pattern("PGP")
                .pattern("PPP")
                .define('P', ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_polished_bloody_stone", has(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()))
                .save(consumer, name("ancient_bloody_stone_bricks"));

        // 2. Ancient Bloody Stone Variants
        makeStairs(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);
        makeSlab(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);
        makeWall(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);

        // Column (Crafted like Chiseled Quartz: 2 slabs vertical)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN.get(), 2)
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get())
                .unlockedBy("has_ancient_bloody_stone_slab", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get()))
                .save(consumer, name("ancient_bloody_stone_bricks_column"));

        // 3. Ancient Stonecutting (Decorated Blocks)
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_chiseled"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_detailed"));

        // Also add stonecutting for basic variants for convenience
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get(), 2)
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_slab"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_stairs"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_wall"));


        // --------------------------------- LIGHTING ---------------------------------- //

        // 4. Ancient Torch (Item Variant)
        // Recipe: Bloody Soul Dust + Stick (Like normal torch but with magic dust)
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.ANCIENT_TORCH_ITEM.get(), 4)
                .pattern("D")
                .pattern("S")
                .define('D', ModItems.BLOODY_SOUL_DUST.get())
                .define('S', Items.STICK)
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("ancient_torch"));

        // 5. Ancient Bloody Lamp
        // Recipe: 8 Gold Nuggets surrounding an Ancient Torch
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ANCIENT_BLOODY_LAMP.get())
                .pattern("GGG")
                .pattern("GTG")
                .pattern("GGG")
                .define('G', Items.GOLD_NUGGET)
                .define('T', ModItems.ANCIENT_TORCH_ITEM.get())
                .unlockedBy("has_ancient_torch", has(ModItems.ANCIENT_TORCH_ITEM.get()))
                .save(consumer, name("ancient_bloody_lamp"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SANGUINITE_PIPE_ITEM.get(), 8)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("GGG")
                .define('G', Items.GLASS_PANE)
                .define('S', ModItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_pipe"));

        // Sanguinite Tank
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SANGUINITE_TANK.get())
                .pattern("SGS")
                .pattern("G G")
                .pattern("SGS")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_sanguinite", has(ModItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_tank"));

        // Sanguinite Blood Harvester
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get())
                .pattern("ISI")
                .pattern("RDR")
                .pattern("ISI")
                .define('S', ModItems.SANGUINITE.get())
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('D', ModItems.SACRIFICIAL_DAGGER.get()) // The "Harvesting" blade
                .unlockedBy("has_sacrificial_dagger", has(ModItems.SACRIFICIAL_DAGGER.get()))
                .save(consumer, name("sanguinite_blood_harvester"));

        // Sanguinite Infusor
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SANGUINITE_INFUSOR.get())
                .pattern("SGS")
                .pattern("GCG")
                .pattern("SBS")
                .define('S', ModItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('B', ModBlocks.SANGUINITE_BLOCK.get())
                .define('C', ModItems.ANCIENT_GEM.get()) // The magic core
                .unlockedBy("has_crucible_core", has(ModItems.SANGUINE_CRUCIBLE_CORE.get()))
                .save(consumer, name("sanguinite_infusor"));

        // Sanguine Lapidary
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

        BloodAltarRecipeBuilder.ritual(ModItems.VORACIOUS_MUSHROOM.get())
                .requires(ModItems.GLOW_MUSHROOM.get())
                .requires(ModItems.UNKNOWN_ENTITY_FINGER.get())
                .requires(ModItems.BLOODY_SOUL_DUST.get())
                .unlockedBy("has_bloody_soul_dust", has(ModItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("ritual_voracious_mushroom"));

        // Heretic Sacrificial Dagger
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HERETIC_SACRIFICIAL_DAGGER.get(), 1)
                .pattern(" BB")
                .pattern("GB ")
                .pattern("DG ")
                .define('G', Items.GOLD_INGOT)
                .define('B', ModItems.BLASPHEMITE.get()) // Replaces Sanguinite
                .define('D', ModItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_blasphemite", has(ModItems.BLASPHEMITE.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(ModItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("heretic_sacrificial_dagger"));

        //Gem sprout seed
        BloodAltarRecipeBuilder.ritual(ModItems.BLOOD_GEM_SPROUT_SEED.get())
                .requires(ModBlocks.BLOOD_FLOWER.get())
                .requires(ModItems.BLOODY_SOUL_DUST.get())
                .requires(ModItems.SANGUINITE.get())
                .unlockedBy("has_dirty_blood_flower", has(ModItems.DIRTY_BLOOD_FLOWER.get()))
                .save(consumer, name("ritual_blood_gem_sprout_seed"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOOD_GEM_SPROUT_SEED.get())
                .requires(Items.WHEAT_SEEDS)
                .requires(ModItems.FILLED_BLOOD_FLASK.get())
                .requires(ModItems.BLOODY_SOUL_DUST.get())
                .unlockedBy("has_filled_blood_flask", has(ModItems.FILLED_BLOOD_FLASK.get()))
                .save(consumer, name("blood_gem_sprout_seed_crafting"));


        makeInfusion(consumer, ModItems.RHNULL.get(), ModItems.SANGUINITE.get(), 500, 500, "rhnull_ingot_infusion");
        makeInfusion(consumer, ModItems.FILLED_RHNULL_BLOOD_FLASK.get(), ModItems.FILLED_BLOOD_FLASK.get(), 500, 500, "filled_rhnull_flask_infusion");
        makeInfusion(consumer, ModItems.ANCIENT_RHNULL_GEM.get(), ModItems.ANCIENT_GEM.get(), 1000, 1000, "ancient_rhnull_gem_infusion");
        makeInfusion(consumer, ModItems.GREAT_ANCIENT_RHNULL_GEM.get(), ModItems.GREAT_ANCIENT_GEM.get(), 2000, 2000, "great_ancient_rhnull_gem_infusion");

    }




    private ResourceLocation name(String name) {
        return new ResourceLocation(BloodyHell.MODID, name);
    }
}