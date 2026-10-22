package net.agusdropout.bloodyhell.datagen.recipe;

import net.agusdropout.bloodyhell.item.registry.BlasphemiteItems;
import net.agusdropout.bloodyhell.item.registry.BloodGearItems;
import net.agusdropout.bloodyhell.item.registry.DropItems;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.agusdropout.bloodyhell.item.registry.FoodItems;
import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.item.registry.MiscItems;
import net.agusdropout.bloodyhell.item.registry.PlaceableItems;
import net.agusdropout.bloodyhell.item.registry.ReliquaryItems;
import net.agusdropout.bloodyhell.item.registry.RhnullItems;
import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.WeaponItems;
import net.agusdropout.bloodyhell.block.registry.LightBlocks;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.block.registry.MetalBlocks;
import net.agusdropout.bloodyhell.block.registry.MiscBlocks;
import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import net.agusdropout.bloodyhell.block.registry.WoodBlocks;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.compat.FlaskFillCategory;
import net.agusdropout.bloodyhell.datagen.*;
import net.agusdropout.bloodyhell.datagen.recipe.builder.BlasphemousBloodAltarRecipeBuilder;
import net.agusdropout.bloodyhell.datagen.recipe.builder.BloodAltarRecipeBuilder;
import net.agusdropout.bloodyhell.datagen.recipe.builder.CondenserRecipeBuilder;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.recipe.FlaskFillRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MiscItems.UNKNOWN_GUIDE_BOOK.get(), 1)
                .pattern(" S ")
                .pattern(" B ")
                .pattern("   ")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('B', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("unknown_guide_book"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MiscItems.UNKNOWN_GUIDE_BOOK.get(), 1)
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

        BlasphemousBloodAltarRecipeBuilder.ritual(MiscItems.NAMELESS_WHISPER.get())
                .requires(MiscItems.GAZE_OF_THE_UNKNOWN.get())
                .requires(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(MetalBlocks.BLASPHEMITE_BLOCK.get())
                .unlockedBy("has_gaze_of_the_unknown", has(MiscItems.GAZE_OF_THE_UNKNOWN.get()))
                .save(consumer, name("ritual_nameless_whisper"));

        BloodAltarRecipeBuilder.ritual(DropItems.NAMELESS_ECHO_SHARD.get())
                .requires(Items.AMETHYST_SHARD)
                .requires(MetalBlocks.SANGUINITE_BLOCK.get())
                .requires(FlaskItems.FILLED_BLOOD_FLASK.get())
                .requires(LightBlocks.GLOWING_CRYSTAL.get())
                .unlockedBy("has_glowing_crystal", has(LightBlocks.GLOWING_CRYSTAL.get()))
                .save(consumer, name("ritual_nameless_echo_shard"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MachineBlocks.BLOOD_ALTAR.get(), 1)
                .pattern("GDG")
                .pattern("SCS")
                .pattern("TST")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('C', FlaskItems.CORRUPTED_BLOOD_FLASK.get())
                .define('D', MiscItems.BLOODY_SOUL_DUST.get())
                .define('T', StoneBlocks.BLOODY_STONE_TILES_BLOCK.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("blood_altar"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MachineBlocks.MAIN_BLOOD_ALTAR.get(), 1)
                .pattern("GDG")
                .pattern("SCS")
                .pattern("TST")
                .define('S', MetalBlocks.SANGUINITE_BLOCK.get())
                .define('G', Items.GOLD_INGOT)
                .define('C', FlaskItems.CORRUPTED_BLOOD_FLASK.get())
                .define('D', MiscItems.CHALICE_OF_THE_DAMMED.get())
                .define('T', StoneBlocks.BLOODY_STONE_TILES_BLOCK.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("main_blood_altar"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR.get(), 1)
                .pattern("FDF")
                .pattern("SLS")
                .pattern("BRB")
                .define('S', SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .define('B', BlasphemiteItems.BLASPHEMITE.get())
                .define('L', FlaskItems.FILLED_BLOOD_FLASK.get())
                .define('D', MiscItems.BLOODY_SOUL_DUST.get())
                .define('F', WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', MetalBlocks.BLASPHEMITE_BLOCK.get())
                .unlockedBy("has_blasphemite", has(BlasphemiteItems.BLASPHEMITE.get()))
                .save(consumer, name("blasphemous_blood_altar"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get(), 1)
                .pattern("FCF")
                .pattern("SLS")
                .pattern("RRR")
                .define('S', SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .define('L', FlaskItems.FILLED_BLOOD_FLASK.get())
                .define('C', MiscItems.CHALICE_OF_THE_DAMMED.get())
                .define('F', WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', MetalBlocks.BLASPHEMITE_BLOCK.get())
                .unlockedBy("has_blasphemite", has(BlasphemiteItems.BLASPHEMITE.get()))
                .save(consumer, name("main_blasphemous_blood_altar"));

        BlasphemousBloodAltarRecipeBuilder.ritual(Items.RECOVERY_COMPASS)
                .requires(DropItems.VEINREAVER_HORN.get())
                .requires(DropItems.AUREAL_REVENANT_DAGGER.get())
                .requires(FoodItems.GLOW_MUSHROOM.get())
                .unlockedBy("has_dagger", has(DropItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("ritual_find_mausoleum"));

        BlasphemousBloodAltarRecipeBuilder.ritual(PlaceableItems.BLOOD_GEM_SPROUT_SEED.get())
                .requires(PlantBlocks.BLOOD_FLOWER.get())
                .requires(MiscItems.BLOODY_SOUL_DUST.get())
                .requires(SanguiniteItems.SANGUINITE.get())
                .unlockedBy("has_dirty_blood_flower", has(MiscItems.DIRTY_BLOOD_FLOWER.get()))
                .save(consumer, name("ritual_blood_gem_sprout_seed"));

        BlasphemousBloodAltarRecipeBuilder.ritual(PlaceableItems.VORACIOUS_MUSHROOM.get())
                .requires(FoodItems.GLOW_MUSHROOM.get())
                .requires(WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .requires(MiscItems.BLOODY_SOUL_DUST.get())
                .unlockedBy("has_bloody_soul_dust", has(MiscItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("ritual_voracious_mushroom"));

        BlasphemousBloodAltarRecipeBuilder.ritual(PlaceableItems.CRIMSON_LURE_MUSHROOM.get())
                .requires(FoodItems.GLOW_MUSHROOM.get())
                .requires(FlaskItems.FILLED_BLOOD_FLASK.get())
                .requires(Items.ROTTEN_FLESH)
                .unlockedBy("has_glow_mushroom", has(FoodItems.GLOW_MUSHROOM.get()))
                .save(consumer, name("ritual_crimson_lure_mushroom"));

        BlasphemousBloodAltarRecipeBuilder.ritual(BlasphemiteItems.BLASPHEMOUS_EYE.get(), 4)
                .requires(Items.ENDER_EYE)
                .requires(BlasphemiteItems.BLASPHEMITE.get())
                .requires(FlaskItems.FILLED_BLOOD_FLASK.get())
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                .save(consumer, name("ritual_blasphemous_eye"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ReliquaryItems.MARK_OF_THE_RESTLESS_SLUMBER.get())
                .requires(FlaskItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get())
                .requires(GemItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(BlasphemiteItems.BLASPHEMOUS_EYE.get()))
                .save(consumer, name("ritual_mark_of_restless_slumber"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ReliquaryItems.RUNE_OF_THE_RAVENOUS_GAZE.get())
                .requires(BlasphemiteItems.BLASPHEMOUS_EYE.get())
                .requires(GemItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(BlasphemiteItems.BLASPHEMOUS_EYE.get()))
                .save(consumer, name("ritual_rune_of_ravenous_gaze"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ReliquaryItems.SEAL_OF_THE_HOLLOW_BULWARK.get())
                .requires(BlasphemiteItems.BLASPHEMOUS_EYE.get())
                .requires(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(BlasphemiteItems.BLASPHEMOUS_EYE.get()))
                .save(consumer, name("ritual_rune_of_hollow_bulwark"));

        BlasphemousBloodAltarRecipeBuilder.ritual(ReliquaryItems.OMEN_OF_THE_CRUSHING_TOLL.get())
                .requires(MiscItems.GAZE_OF_THE_UNKNOWN.get())
                .requires(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .unlockedBy("has_blasphemous_eye", has(MiscItems.GAZE_OF_THE_UNKNOWN.get()))
                .save(consumer, name("ritual_rune_of_crushing_toll"));

        BlasphemousBloodAltarRecipeBuilder.ritual(MiscItems.GAZE_OF_THE_UNKNOWN.get())
                .requires(BlasphemiteItems.BLASPHEMOUS_EYE.get())
                .requires(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requires(FlaskItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get())
                .unlockedBy("has_great_ancient_blasphemous_gem", has(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get()))
                .save(consumer, name("ritual_gaze_of_the_unknown"));

        BloodAltarRecipeBuilder.ritual(DropItems.BLOOD_ECHO_SHARD.get())
                .requires(Items.AMETHYST_SHARD)
                .requires(SanguiniteItems.SANGUINITE.get())
                .requires(FlaskItems.FILLED_BLOOD_FLASK.get())
                .requires(LightBlocks.GLOWING_CRYSTAL.get())
                .unlockedBy("has_glowing_crystal", has(LightBlocks.GLOWING_CRYSTAL.get()))
                .save(consumer, name("ritual_blood_echo_shard"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.BLOOD_SCRATCH_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(FlaskItems.FILLED_BLOOD_FLASK.get())
                .requires(DropItems.VEINREAVER_HORN.get())
                .requires(MiscItems.BLOODY_SOUL_DUST.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_scratch"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(FlaskItems.FILLED_BLOOD_FLASK.get())
                .requires(DropItems.AUREAL_REVENANT_DAGGER.get())
                .requires(GemItems.ANCIENT_GEM.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_sphere"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.BLOOD_NOVA_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(DropItems.AUREAL_REVENANT_DAGGER.get())
                .requires(RhnullItems.RHNULL.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_nova"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(DropItems.VEINREAVER_HORN.get())
                .requires(GemItems.ANCIENT_RHNULL_GEM.get())
                .unlockedBy("has_book", has(Items.BOOK))
                .save(consumer, name("ritual_spell_daggersrain"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.BLOOD_FIRE_METEOR_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(GemItems.GREAT_ANCIENT_GEM.get())
                .requires(DropItems.RITEKEEPER_HEART.get())
                .requires(Items.FIRE_CHARGE)
                .unlockedBy("has_ritekeeper_heart", has(DropItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_meteor"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(GemItems.GREAT_ANCIENT_GEM.get())
                .requires(DropItems.RITEKEEPER_HEART.get())
                .requires(DropItems.CINDER_ACOLYTE_FAINTED_EMBER.get())
                .unlockedBy("has_ritekeeper_heart", has(DropItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_column"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.BLOOD_FIRE_SOUL_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(GemItems.GREAT_ANCIENT_GEM.get())
                .requires(DropItems.RITEKEEPER_HEART.get())
                .requires(DropItems.FAILED_REMNANT_ASHES.get())
                .unlockedBy("has_ritekeeper_heart", has(DropItems.RITEKEEPER_HEART.get()))
                .save(consumer, name("ritual_spell_bloodfire_soul"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.RHNULL_IMPALERS_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(GemItems.GREAT_ANCIENT_RHNULL_GEM.get())
                .requires(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(BlasphemiteItems.BLASPHEMITE.get())
                .unlockedBy("has_great_ancient_rhnull_gem", has(GemItems.GREAT_ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_impalers"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(GemItems.GREAT_ANCIENT_RHNULL_GEM.get())
                .requires(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(RhnullItems.RHNULL_SWORD.get())
                .unlockedBy("has_great_ancient_rhnull_gem", has(GemItems.GREAT_ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_heavy_sword"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(GemItems.ANCIENT_RHNULL_GEM.get())
                .requires(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(Items.GOLD_BLOCK)
                .unlockedBy("has_ancient_rhnull_gem", has(GemItems.ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_golden_throne"));

        BloodAltarRecipeBuilder.ritual(SpellBookItems.RHNULL_ORB_EMITTER_SPELLBOOK.get())
                .requires(Items.BOOK)
                .requires(GemItems.ANCIENT_RHNULL_GEM.get())
                .requires(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .requires(BlasphemiteItems.BLASPHEMOUS_EYE.get())
                .unlockedBy("has_ancient_rhnull_gem", has(GemItems.ANCIENT_RHNULL_GEM.get()))
                .save(consumer, name("ritual_spell_rhnull_orb_emitter"));

        generateMaterialFamily(consumer, "sanguinite", MetalBlocks.SANGUINITE_BLOCK, SanguiniteItems.SANGUINITE, SanguiniteItems.SANGUINITE_NUGGET);
        generateMaterialFamily(consumer, "rhnull", MetalBlocks.RHNULL_BLOCK, RhnullItems.RHNULL, RhnullItems.RHNULL_NUGGET);
        generateMaterialFamily(consumer, "blasphemite", MetalBlocks.BLASPHEMITE_BLOCK, BlasphemiteItems.BLASPHEMITE, BlasphemiteItems.BLASPHEMITE_NUGGET);

        makePlanks(WoodBlocks.BLOOD_PLANKS, ModTags.Items.BLOOD_LOGS).save(consumer);

        makeBricks(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK, StoneBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeBricks(StoneBlocks.BLOODY_STONE_BRICKS, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeBricks(StoneBlocks.BLOODY_STONE_TILES_BLOCK, StoneBlocks.BLOODY_STONE_BRICKS).save(consumer);

        makeStairs(StoneBlocks.BLOODY_STONE_STAIRS, StoneBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeSlab(StoneBlocks.BLOODY_STONE_SLAB, StoneBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeWall(StoneBlocks.BLOODY_STONE_WALL, StoneBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeFence(StoneBlocks.BLOODY_STONE_FENCE, StoneBlocks.BLOODY_STONE_BLOCK).save(consumer);
        makeFenceGate(StoneBlocks.BLOODY_STONE_FENCE_GATE, StoneBlocks.BLOODY_STONE_BLOCK).save(consumer);

        makeStairs(StoneBlocks.POLISHED_BLOODY_STONE_STAIRS, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeSlab(StoneBlocks.POLISHED_BLOODY_STONE_SLAB, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeWall(StoneBlocks.POLISHED_BLOODY_STONE_WALL, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeFence(StoneBlocks.POLISHED_BLOODY_STONE_FENCE, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);
        makeFenceGate(StoneBlocks.POLISHED_BLOODY_STONE_FENCE_GATE, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK).save(consumer);

        makeStairs(StoneBlocks.BLOODY_STONE_TILES_STAIRS, StoneBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeSlab(StoneBlocks.BLOODY_STONE_TILES_SLAB, StoneBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeWall(StoneBlocks.BLOODY_STONE_TILES_WALL, StoneBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeFence(StoneBlocks.BLOODY_STONE_TILES_FENCE, StoneBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);
        makeFenceGate(StoneBlocks.BLOODY_STONE_FENCE_TILES_GATE, StoneBlocks.BLOODY_STONE_TILES_BLOCK).save(consumer);

        makeStairs(StoneBlocks.BLOODY_STONE_BRICKS_STAIRS, StoneBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeSlab(StoneBlocks.BLOODY_STONE_BRICKS_SLAB, StoneBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeWall(StoneBlocks.BLOODY_STONE_BRICKS_WALL, StoneBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeFence(StoneBlocks.BLOODY_STONE_BRICKS_FENCE, StoneBlocks.BLOODY_STONE_BRICKS).save(consumer);
        makeFenceGate(StoneBlocks.BLOODY_STONE_FENCE_BRICKS_GATE, StoneBlocks.BLOODY_STONE_BRICKS).save(consumer);

        makeStairs(WoodBlocks.BLOOD_PLANKS_STAIRS, WoodBlocks.BLOOD_PLANKS).save(consumer);
        makeSlab(WoodBlocks.BLOOD_PLANKS_SLAB, WoodBlocks.BLOOD_PLANKS).save(consumer);
        makeFence(WoodBlocks.BLOOD_PLANKS_FENCE, WoodBlocks.BLOOD_PLANKS).save(consumer);
        makeFenceGate(WoodBlocks.BLOOD_PLANKS_FENCE_GATE, WoodBlocks.BLOOD_PLANKS).save(consumer);

        makeSword(SanguiniteItems.SANGUINITE_SWORD, SanguiniteItems.SANGUINITE).save(consumer);
        makePickaxe(SanguiniteItems.SANGUINITE_PICKAXE, SanguiniteItems.SANGUINITE).save(consumer);
        makeShovel(SanguiniteItems.SANGUINITE_SHOVEL, SanguiniteItems.SANGUINITE).save(consumer);
        makeAxe(SanguiniteItems.SANGUINITE_AXE, SanguiniteItems.SANGUINITE).save(consumer);
        makeHoe(SanguiniteItems.SANGUINITE_HOE, SanguiniteItems.SANGUINITE).save(consumer);

        makeSword(RhnullItems.RHNULL_SWORD, RhnullItems.RHNULL).save(consumer);
        makePickaxe(RhnullItems.RHNULL_PICKAXE, RhnullItems.RHNULL).save(consumer);
        makeShovel(RhnullItems.RHNULL_SHOVEL, RhnullItems.RHNULL).save(consumer);
        makeAxe(RhnullItems.RHNULL_AXE, RhnullItems.RHNULL).save(consumer);
        makeHoe(RhnullItems.RHNULL_HOE, RhnullItems.RHNULL).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BloodGearItems.BLOOD_BOW.get(), 1)
                .pattern(" SG")
                .pattern("S G")
                .pattern(" SG")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(consumer, name("blood_bow"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BloodGearItems.BLOOD_ARROW.get(), 4)
                .pattern(" F ")
                .pattern(" S ")
                .pattern(" G ")
                .define('S', Items.STICK)
                .define('G', DropItems.SCARLET_FEATHER.get())
                .define('F', Items.FLINT)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_scarlet_feather", has(DropItems.SCARLET_FEATHER.get()))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(consumer, name("blood_arrow"));

        makeHelmet(BloodGearItems.BLOOD_HELMET, SanguiniteItems.SANGUINITE).save(consumer);
        makeChestplate(BloodGearItems.BLOOD_CHESTPLATE, SanguiniteItems.SANGUINITE).save(consumer);
        makeLeggings(BloodGearItems.BLOOD_LEGGINGS, SanguiniteItems.SANGUINITE).save(consumer);
        makeBoots(BloodGearItems.BLOOD_BOOTS, SanguiniteItems.SANGUINITE).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MiscItems.BLOODY_SOUL_DUST.get(), 1)
                .pattern("GP")
                .pattern("PG")
                .define('G', DropItems.VEINREAVER_HORN.get())
                .define('P', SanguiniteItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .unlockedBy("has_veinreaver_horn", has(DropItems.VEINREAVER_HORN.get()))
                .save(consumer, name("bloody_soul_dust_from_veinreaver_horn"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MiscItems.BLOODY_SOUL_DUST.get(), 1)
                .pattern("GP")
                .pattern("PG")
                .define('G', DropItems.AUREAL_REVENANT_DAGGER.get())
                .define('P', SanguiniteItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(DropItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("bloody_soul_dust_from_aureal_revenant_dagger"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MiscItems.CHALICE_OF_THE_DAMMED.get(), 1)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MiscItems.CRIMSON_IDOL_COIN.get(), 1)
                .pattern("HSH")
                .pattern("SGS")
                .pattern("HSH")
                .define('G', SanguiniteItems.SANGUINITE.get())
                .define('H', DropItems.VEINREAVER_HORN.get())
                .define('S', DropItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .unlockedBy("has_veinreaver_horn", has(DropItems.VEINREAVER_HORN.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(DropItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("crimson_idol_coin"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MiscItems.ROOTLET_POWDER.get(), 2)
        .requires(PlantBlocks.BLOOD_PETALS.get())
        .requires(PlantBlocks.BLOOD_FLOWER.get())
        .requires(Items.REDSTONE)
        .unlockedBy("has_blood_flower", has(PlantBlocks.BLOOD_FLOWER.get()))
        .save(consumer, name("rootlet_powder"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.REDSTONE, 1)
        .requires(StoneBlocks.BLOODY_STONE_BLOCK.get())
        .requires(PlantBlocks.BLOOD_GRASS.get())
        .unlockedBy("has_bloody_stone_block", has(StoneBlocks.BLOODY_STONE_BLOCK.get()))
        .save(consumer, name("redstone_from_blood_stone_and_blood_grass"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, LightBlocks.GLOWING_CRYSTAL_GLASS_BLOCK.get(), 8)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("GGG")
                .define('C', LightBlocks.GLOWING_CRYSTAL.get().asItem())
                .define('G', Items.GLASS)
                .unlockedBy("has_glowing_crystal", has(LightBlocks.GLOWING_CRYSTAL.get()))
                .unlockedBy("has_glass", has(Items.GLASS))
                .save(consumer, name("glowing_crystal_glass_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, LightBlocks.BLOOD_GLOW_STONE.get(), 8)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("GGG")
                .define('C', MiscItems.BLOODY_SOUL_DUST.get())
                .define('G', Items.GLOWSTONE)
                .unlockedBy("has_bloody_soul_dust", has(MiscItems.BLOODY_SOUL_DUST.get()))
                .unlockedBy("has_glowstone", has(Items.GLOWSTONE))
                .save(consumer, name("blood_glow_stone_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, LightBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get(), 8)
                .pattern("G")
                .pattern("C")
                .pattern("G")
                .define('C', MiscItems.BLOODY_SOUL_DUST.get())
                .define('G', LightBlocks.GLOWING_CRYSTAL.get())
                .unlockedBy("has_bloody_soul_dust", has(MiscItems.BLOODY_SOUL_DUST.get()))
                .unlockedBy("has_glowing_crystal", has(LightBlocks.GLOWING_CRYSTAL.get()))
                .save(consumer, name("blood_glowing_chains"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, LightBlocks.GLOWING_CRYSTAL_LANTERN.get(), 1)
                .pattern("PPP")
                .pattern("PGP")
                .pattern("PPP")
                .define('G', LightBlocks.GLOWING_CRYSTAL.get())
                .define('P', SanguiniteItems.SANGUINITE_NUGGET.get())
                .unlockedBy("has_glowing_crystal", has(LightBlocks.GLOWING_CRYSTAL.get()))
                .unlockedBy("has_sanguinite_nugget", has(SanguiniteItems.SANGUINITE_NUGGET.get()))
                .save(consumer, name("glowing_crystal_lantern"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MachineBlocks.SANGUINE_CRUCIBLE.get(), 1)
                .pattern("GCG")
                .pattern("GSG")
                .pattern("GGG")
                .define('G', SanguiniteItems.SANGUINITE.get())
                .define('S', Items.GOLD_INGOT)
                .define('C', MiscItems.SANGUINE_CRUCIBLE_CORE.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_sanguine_crucible_core", has(MiscItems.SANGUINE_CRUCIBLE_CORE.get()))
                .save(consumer, name("sanguine_crucible"));

        smeltingRecipe(SanguiniteItems.SANGUINITE.get(), SanguiniteItems.RAW_SANGUINITE.get(), 1F).save(consumer, name("smelt_raw_sanguinite"));
        blastingRecipe(SanguiniteItems.SANGUINITE.get(), SanguiniteItems.RAW_SANGUINITE.get(), 1F).save(consumer, name("blast_raw_sanguinite"));

        bloodyStonecutting(StoneBlocks.BLOODY_STONE_STAIRS.get(),2).save(consumer, name("bloody_stone_stairs_stonecutting"));
        bloodyStonecutting(StoneBlocks.BLOODY_STONE_SLAB.get(),2).save(consumer, name("bloody_stone_slab_stonecutting"));
        bloodyStonecutting(StoneBlocks.BLOODY_STONE_WALL.get(),1).save(consumer, name("bloody_stone_wall_stonecutting"));

        polishedBloodyStoneStonecutting(StoneBlocks.POLISHED_BLOODY_STONE_STAIRS.get(),2).save(consumer, name("polished_bloody_stone_stairs_stonecutting"));
        polishedBloodyStoneStonecutting(StoneBlocks.POLISHED_BLOODY_STONE_SLAB.get(),2).save(consumer, name("polished_bloody_stone_slab_stonecutting"));
        polishedBloodyStoneStonecutting(StoneBlocks.POLISHED_BLOODY_STONE_WALL.get(),1).save(consumer, name("polished_bloody_stone_wall_stonecutting"));

        bloodyStoneTilesStonecutting(StoneBlocks.BLOODY_STONE_TILES_STAIRS.get(),2).save(consumer, name("bloody_stone_tiles_stairs_stonecutting"));
        bloodyStoneTilesStonecutting(StoneBlocks.BLOODY_STONE_TILES_SLAB.get(),2).save(consumer, name("bloody_stone_tiles_slab_stonecutting"));
        bloodyStoneTilesStonecutting(StoneBlocks.BLOODY_STONE_TILES_WALL.get(),1).save(consumer, name("bloody_stone_tiles_wall_stonecutting"));

        bloodyStoneBricksStonecutting(StoneBlocks.BLOODY_STONE_BRICKS_STAIRS.get(),2).save(consumer, name("bloody_stone_bricks_stairs_stonecutting"));
        bloodyStoneBricksStonecutting(StoneBlocks.BLOODY_STONE_BRICKS_SLAB.get(),2).save(consumer, name("bloody_stone_bricks_slab_stonecutting"));
        bloodyStoneBricksStonecutting(StoneBlocks.BLOODY_STONE_BRICKS_WALL.get(),1).save(consumer, name("bloody_stone_bricks_wall_stonecutting"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FlaskItems.BLOOD_FLASK.get(), 3)
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(consumer, name("blood_flask"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WeaponItems.SACRIFICIAL_DAGGER.get(), 1)
                .pattern(" SS")
                .pattern("GS ")
                .pattern("DG ")
                .define('G', Items.GOLD_INGOT)
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('D', DropItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(DropItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("sacrificial_dagger"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WeaponItems.HERETIC_SACRIFICIAL_DAGGER.get(), 1)
                .pattern(" BB")
                .pattern("GB ")
                .pattern("DG ")
                .define('G', Items.GOLD_INGOT)
                .define('B', BlasphemiteItems.BLASPHEMITE.get())
                .define('D', DropItems.AUREAL_REVENANT_DAGGER.get())
                .unlockedBy("has_blasphemite", has(BlasphemiteItems.BLASPHEMITE.get()))
                .unlockedBy("has_aureal_revenant_dagger", has(DropItems.AUREAL_REVENANT_DAGGER.get()))
                .save(consumer, name("heretic_sacrificial_dagger"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.AMULET_OF_ANCESTRAL_BLOOD.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("BAB")
                .define('G', Items.GOLD_INGOT)
                .define('B', FlaskItems.FILLED_BLOOD_FLASK.get())
                .define('A', GemItems.ANCIENT_GEM.get())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_filled_blood_flask", has(FlaskItems.FILLED_BLOOD_FLASK.get()))
                .unlockedBy("has_ancient_gem", has(GemItems.ANCIENT_GEM.get()))
                .save(consumer, name("amulet_of_ancestral_blood"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("BAB")
                .define('G', RhnullItems.RHNULL.get())
                .define('B', FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .define('A', GemItems.GREAT_ANCIENT_GEM.get())
                .unlockedBy("has_rhnull", has(RhnullItems.RHNULL.get()))
                .unlockedBy("has_rhnull_filled_blood_flask", has(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get()))
                .unlockedBy("has_great_ancient_gem", has(GemItems.GREAT_ANCIENT_GEM.get()))
                .save(consumer, name("great_amulet_of_ancestral_blood"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.CRIMSON_WARD_RING.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GAG")
                .define('G', Items.GOLD_INGOT)
                .define('A', GemItems.ANCIENT_GEM.get())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .unlockedBy("has_ancient_gem", has(GemItems.ANCIENT_GEM.get()))
                .save(consumer, name("crimson_ward_ring"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.BLASPHEMOUS_RING.get(), 1)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GAG")
                .define('G', BlasphemiteItems.BLASPHEMITE.get())
                .define('A', GemItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .unlockedBy("has_blasphemite", has(BlasphemiteItems.BLASPHEMITE.get()))
                .unlockedBy("has_ancient_blasphemous_gem", has(GemItems.ANCIENT_BLASPHEMOUS_GEM.get()))
                .save(consumer, name("blasphemous_ring"));

        smokingRecipe(FoodItems.GOREHOG_COOKED_STEAK.get(), FoodItems.GOREHOG_RAW_STEAK.get(), 0.35F, 1).save(consumer, name("smoking_gorehog_beef"));
        smeltingRecipe(FoodItems.GOREHOG_COOKED_STEAK.get(), FoodItems.GOREHOG_RAW_STEAK.get(), 0.35F).save(consumer, name("smelting_gorehog_beef"));
        campfireRecipe(FoodItems.GOREHOG_COOKED_STEAK.get(), FoodItems.GOREHOG_RAW_STEAK.get(), 0.35F).save(consumer, name("campfire_gorehog_beef"));
        smokingRecipe(FoodItems.SCARLET_COOKED_CHICKEN.get(), FoodItems.SCARLET_RAW_CHICKEN.get(), 0.35F, 1).save(consumer, name("smoking_scarlet_raw_chicken"));
        smeltingRecipe(FoodItems.SCARLET_COOKED_CHICKEN.get(), FoodItems.SCARLET_RAW_CHICKEN.get(), 0.35F).save(consumer, name("smelting_scarlet_raw_chicken"));
        campfireRecipe(FoodItems.SCARLET_COOKED_CHICKEN.get(), FoodItems.SCARLET_RAW_CHICKEN.get(), 0.35F).save(consumer, name("campfire_scarlet_raw_chicken"));

        makeHelmet(RhnullItems.RHNULL_HELMET, RhnullItems.RHNULL).save(consumer);
        makeChestplate(RhnullItems.RHNULL_CHESTPLATE, RhnullItems.RHNULL).save(consumer);
        makeLeggings(RhnullItems.RHNULL_LEGGINGS, RhnullItems.RHNULL).save(consumer);
        makeBoots(RhnullItems.RHNULL_BOOTS, RhnullItems.RHNULL).save(consumer);

        List<ItemLike> blasphemiteSmeltables = List.of(BlasphemiteItems.RAW_BLASPHEMITE.get());

        oreSmelting(consumer, blasphemiteSmeltables, RecipeCategory.MISC, BlasphemiteItems.BLASPHEMITE.get(),
                0.7f, 200, "blasphemite");

        oreBlasting(consumer, blasphemiteSmeltables, RecipeCategory.MISC, BlasphemiteItems.BLASPHEMITE.get(),
                0.7f, 100, "blasphemite");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .pattern("SS")
                .pattern("SS")
                .define('S', SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get())
                .unlockedBy("has_blasphemous_sand", has(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get()))
                .save(consumer, name("blasphemous_sandstone_from_sand"));

        smeltingRecipe(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get(), SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get(), 0.1f)
                .save(consumer, name("smooth_blasphemous_sandstone_smelting"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("cut_blasphemous_sandstone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .pattern("S")
                .pattern("S")
                .define('S', SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get())
                .unlockedBy("has_smooth_blasphemous_sandstone_slab", has(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get()))
                .save(consumer, name("chiseled_blasphemous_sandstone"));

        makeStairs(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS, SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK).save(consumer);
        makeSlab(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB, SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK).save(consumer);

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_cut_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_chiseled_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_chiseled_detailed_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .unlockedBy("has_blasphemous_sandstone", has(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get())
                .unlockedBy("has_smooth_blasphemous_sandstone", has(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone_stairs"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), RecipeCategory.BUILDING_BLOCKS, SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get(), 2)
                .unlockedBy("has_smooth_blasphemous_sandstone", has(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()))
                .save(consumer, name("stonecutting_smooth_blasphemous_sandstone_slab"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MiscBlocks.DECORATED_POT_BLOCK.get())
                .pattern("B B")
                .pattern(" B ")
                .define('B', Items.BRICK)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(consumer, name("decorated_pot_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, LightBlocks.STAR_LAMP_BLOCK.get())
                .pattern(" S ")
                .pattern("SLS")
                .pattern(" S ")
                .define('S', SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .define('L', LightBlocks.GLOWING_CRYSTAL_LANTERN.get())
                .unlockedBy("has_glowing_lantern", has(LightBlocks.GLOWING_CRYSTAL_LANTERN.get()))
                .save(consumer, name("star_lamp_block"));

        makeSword(BlasphemiteItems.BLASPHEMITE_SWORD, BlasphemiteItems.BLASPHEMITE).save(consumer);
        makePickaxe(BlasphemiteItems.BLASPHEMITE_PICKAXE, BlasphemiteItems.BLASPHEMITE).save(consumer);
        makeAxe(BlasphemiteItems.BLASPHEMITE_AXE, BlasphemiteItems.BLASPHEMITE).save(consumer);
        makeShovel(BlasphemiteItems.BLASPHEMITE_SHOVEL, BlasphemiteItems.BLASPHEMITE).save(consumer);
        makeHoe(BlasphemiteItems.BLASPHEMITE_HOE, BlasphemiteItems.BLASPHEMITE).save(consumer);

        makeHelmet(BlasphemiteItems.BLASPHEMITE_HELMET, BlasphemiteItems.BLASPHEMITE).save(consumer);
        makeChestplate(BlasphemiteItems.BLASPHEMITE_CHESTPLATE, BlasphemiteItems.BLASPHEMITE).save(consumer);
        makeLeggings(BlasphemiteItems.BLASPHEMITE_LEGGINGS, BlasphemiteItems.BLASPHEMITE).save(consumer);
        makeBoots(BlasphemiteItems.BLASPHEMITE_BOOTS, BlasphemiteItems.BLASPHEMITE).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, WeaponItems.BLASPHEMOUS_IMPALER.get())
                .pattern("  B")
                .pattern(" S ")
                .pattern("R  ")
                .define('B', BlasphemiteItems.BLASPHEMITE.get())
                .define('S', DropItems.SELIORA_ESSENCE.get())
                .define('R', RhnullItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(DropItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_impaler"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, WeaponItems.BLASPHEMOUS_TWIN_DAGGERS.get())
                .pattern("B B")
                .pattern(" S ")
                .pattern("R R")
                .define('B', BlasphemiteItems.BLASPHEMITE.get())
                .define('S', DropItems.SELIORA_ESSENCE.get())
                .define('R', RhnullItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(DropItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_twin_daggers"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, WeaponItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get())
                .pattern("BBB")
                .pattern("BSB")
                .pattern(" R ")
                .define('B', BlasphemiteItems.BLASPHEMITE.get())
                .define('S', DropItems.SELIORA_ESSENCE.get())
                .define('R', RhnullItems.RHNULL.get())
                .unlockedBy("has_seliora_essence", has(DropItems.SELIORA_ESSENCE.get()))
                .save(consumer, name("blasphemous_hulking_mass_of_iron"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get(), 8)
                .pattern("PPP")
                .pattern("PGP")
                .pattern("PPP")
                .define('P', StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_polished_bloody_stone", has(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get()))
                .save(consumer, name("ancient_bloody_stone_bricks"));

        makeStairs(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);
        makeSlab(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);
        makeWall(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN.get(), 2)
                .pattern("S")
                .pattern("S")
                .define('S', StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get())
                .unlockedBy("has_ancient_bloody_stone_slab", has(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get()))
                .save(consumer, name("ancient_bloody_stone_bricks_column"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, StoneBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_chiseled"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, StoneBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_detailed"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get(), 2)
                .unlockedBy("has_ancient_bloody_stone_bricks", has(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_slab"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_stairs"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get())
                .unlockedBy("has_ancient_bloody_stone_bricks", has(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()))
                .save(consumer, name("stonecutting_ancient_wall"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PlaceableItems.ANCIENT_TORCH_ITEM.get(), 4)
                .pattern("D")
                .pattern("S")
                .define('D', MiscItems.BLOODY_SOUL_DUST.get())
                .define('S', Items.STICK)
                .unlockedBy("has_bloody_soul_dust", has(MiscItems.BLOODY_SOUL_DUST.get()))
                .save(consumer, name("ancient_torch"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, LightBlocks.ANCIENT_BLOODY_LAMP.get())
                .pattern("GGG")
                .pattern("GTG")
                .pattern("GGG")
                .define('G', Items.GOLD_NUGGET)
                .define('T', PlaceableItems.ANCIENT_TORCH_ITEM.get())
                .unlockedBy("has_ancient_torch", has(PlaceableItems.ANCIENT_TORCH_ITEM.get()))
                .save(consumer, name("ancient_bloody_lamp"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PlaceableItems.UNKNOWN_PORTAL_ITEM.get(), 1)
                .pattern("RPR")
                .pattern("FTF")
                .pattern("RPR")
                .define('F', WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', RhnullItems.RHNULL.get())
                .define('T', MachineBlocks.RHNULL_TANK.get())
                .define('P', PlaceableItems.RHNULL_PIPE_ITEM.get())
                .unlockedBy("has_finger", has(WeaponItems.UNKNOWN_ENTITY_FINGER.get()))
                .save(consumer, name("unknown_portal_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReliquaryItems.RELIQUARY.get(), 1)
                .pattern("F F")
                .pattern("RGR")
                .pattern("FRF")
                .define('F', WeaponItems.UNKNOWN_ENTITY_FINGER.get())
                .define('R', RhnullItems.RHNULL.get())
                .define('G', MiscItems.GAZE_OF_THE_UNKNOWN.get())
                .unlockedBy("has_gaze", has(MiscItems.GAZE_OF_THE_UNKNOWN.get()))
                .save(consumer, name("reliquary"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReliquaryItems.ANCIENT_OCULAR_LENSE.get(), 1)
                .pattern(" G ")
                .pattern("GAG")
                .pattern(" G ")
                .define('G', Items.GOLD_INGOT)
                .define('A', GemItems.ANCIENT_GEM.get())
                .unlockedBy("has_ancient_gem", has(GemItems.ANCIENT_GEM.get()))
                .save(consumer, name("ancient_ocular_lense"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE.get(), 1)
                .pattern(" B ")
                .pattern("BAB")
                .pattern(" B ")
                .define('B', BlasphemiteItems.BLASPHEMITE.get())
                .define('A', GemItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .unlockedBy("has_ancient_blasphemous_gem", has(GemItems.ANCIENT_BLASPHEMOUS_GEM.get()))
                .save(consumer, name("blasphemous_ocular_lense"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.SANGUINITE_GEM_FRAME.get(), 1)
                .pattern(" N ")
                .pattern("N N")
                .pattern(" N ")
                .define('N', SanguiniteItems.SANGUINITE_NUGGET.get())
                .unlockedBy("has_sanguinite_nugget", has(SanguiniteItems.SANGUINITE_NUGGET.get()))
                .save(consumer, name("sanguinite_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.SANGUINITE_GREAT_GEM_FRAME.get(), 1)
                .pattern(" S ")
                .pattern("S S")
                .pattern(" S ")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_great_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.RHNULL_GEM_FRAME.get(), 1)
                .pattern(" N ")
                .pattern("N N")
                .pattern(" N ")
                .define('N', RhnullItems.RHNULL_NUGGET.get())
                .unlockedBy("has_rhnull_nugget", has(RhnullItems.RHNULL_NUGGET.get()))
                .save(consumer, name("rhnull_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GemItems.RHNULL_GREAT_GEM_FRAME.get(), 1)
                .pattern(" R ")
                .pattern("R R")
                .pattern(" R ")
                .define('R', RhnullItems.RHNULL.get())
                .unlockedBy("has_rhnull", has(RhnullItems.RHNULL.get()))
                .save(consumer, name("rhnull_great_gem_frame"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PlaceableItems.SANGUINITE_PIPE_ITEM.get(), 8)
                .pattern("GGG")
                .pattern("SRS")
                .pattern("GGG")
                .define('G', Items.GLASS_PANE)
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('R', MiscItems.ROOTLET_POWDER.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_pipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PlaceableItems.RHNULL_PIPE_ITEM.get(), 1)
        .requires(PlaceableItems.SANGUINITE_PIPE_ITEM.get())
        .requires(PlaceableItems.SANGUINITE_PIPE_ITEM.get())
        .requires(PlaceableItems.SANGUINITE_PIPE_ITEM.get())
        .requires(PlaceableItems.SANGUINITE_PIPE_ITEM.get())
        .requires(RhnullItems.RHNULL.get())
        .unlockedBy("has_rhnull", has(RhnullItems.RHNULL.get()))
        .save(consumer, name("rhnull_pipe_from_sanguinite_pipes"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PlaceableItems.RHNULL_PIPE_ITEM.get(), 8)
                .pattern("GGG")
                .pattern("RPR")
                .pattern("GGG")
                .define('G', Items.GLASS_PANE)
                .define('R', RhnullItems.RHNULL.get())
                .define('P', MiscItems.ROOTLET_POWDER.get())
                .unlockedBy("has_rhnull", has(RhnullItems.RHNULL.get()))
                .save(consumer, name("rhnull_pipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SANGUINITE_TANK.get())
                .pattern("SGS")
                .pattern("G G")
                .pattern("SGS")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_tank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.RHNULL_TANK.get())
                .pattern("RGR")
                .pattern("G G")
                .pattern("RGR")
                .define('R', RhnullItems.RHNULL.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_rhnull", has(RhnullItems.RHNULL.get()))
                .save(consumer, name("rhnull_tank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PlaceableItems.SANGUINITE_CONDENSER_ITEM.get(), 1)
                .pattern("SGS")
                .pattern("G G")
                .pattern("SSS")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("sanguinite_condenser"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PlaceableItems.RHNULL_CONDENSER_ITEM.get(), 1)
                .pattern("RGR")
                .pattern("G G")
                .pattern("RRR")
                .define('R', RhnullItems.RHNULL.get())
                .define('G', Items.GLASS)
                .unlockedBy("has_rhnull", has(RhnullItems.RHNULL.get()))
                .save(consumer, name("rhnull_condenser"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PlaceableItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get())
                .pattern("GSG")
                .pattern("RDR")
                .pattern("GSG")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('R', Items.REDSTONE)
                .define('D', WeaponItems.SACRIFICIAL_DAGGER.get())
                .unlockedBy("has_sacrificial_dagger", has(WeaponItems.SACRIFICIAL_DAGGER.get()))
                .save(consumer, name("sanguinite_blood_harvester"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SANGUINITE_INFUSOR.get())
                .pattern("SGS")
                .pattern("GCG")
                .pattern("SBS")
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('G', Items.GOLD_INGOT)
                .define('B', MetalBlocks.SANGUINITE_BLOCK.get())
                .define('C', GemItems.ANCIENT_GEM.get())
                .unlockedBy("has_crucible_core", has(MiscItems.SANGUINE_CRUCIBLE_CORE.get()))
                .save(consumer, name("sanguinite_infusor"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SANGUINE_LAPIDARY.get())
                .pattern(" D ")
                .pattern("SPS")
                .pattern("BBB")
                .define('D', GemItems.GREAT_ANCIENT_GEM.get())
                .define('S', SanguiniteItems.SANGUINITE.get())
                .define('P', StoneBlocks.POLISHED_BLOODY_STONE_SLAB.get())
                .define('B', StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get())
                .unlockedBy("has_sanguinite", has(SanguiniteItems.SANGUINITE.get()))
                .save(consumer, name("sanguine_lapidary"));

        makeInfusion(consumer, RhnullItems.RHNULL.get(), SanguiniteItems.SANGUINITE.get(), new FluidStack(ModFluids.BLOOD_SOURCE.get(),500), new FluidStack(ModFluids.VISCERAL_BLOOD_SOURCE.get(),500), "rhnull_ingot_infusion");
        makeInfusion(consumer, FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get(), FlaskItems.FILLED_BLOOD_FLASK.get(), new FluidStack(ModFluids.BLOOD_SOURCE.get(),500), new FluidStack(ModFluids.VISCERAL_BLOOD_SOURCE.get(),500), "filled_rhnull_flask_infusion");
        makeInfusion(consumer, GemItems.ANCIENT_RHNULL_GEM.get(), GemItems.ANCIENT_GEM.get(), new FluidStack(ModFluids.BLOOD_SOURCE.get(),100), new FluidStack(ModFluids.VISCERAL_BLOOD_SOURCE.get(),100), "ancient_rhnull_gem_infusion");
        makeInfusion(consumer, GemItems.GREAT_ANCIENT_RHNULL_GEM.get(), GemItems.GREAT_ANCIENT_GEM.get(), new FluidStack(ModFluids.BLOOD_SOURCE.get(),2000), new FluidStack(ModFluids.VISCERAL_BLOOD_SOURCE.get(),2000), "great_ancient_rhnull_gem_infusion");

        CondenserRecipeBuilder.condense(GemItems.ANCIENT_GEM.get())
                .requiresItem(Ingredient.of(GemItems.SANGUINITE_GEM_FRAME.get()))
                .requiresFluid(ModFluids.BLOOD_SOURCE.get(), 1000)
                .unlockedBy("has_gem_frame", has(GemItems.SANGUINITE_GEM_FRAME.get()))
                .save(consumer, name("condense_ancient_gem"));

        CondenserRecipeBuilder.condense(GemItems.GREAT_ANCIENT_GEM.get())
                .requiresItem(Ingredient.of(GemItems.SANGUINITE_GREAT_GEM_FRAME.get()))
                .requiresFluid(ModFluids.BLOOD_SOURCE.get(), 2000)
                .unlockedBy("has_great_gem_frame", has(GemItems.SANGUINITE_GREAT_GEM_FRAME.get()))
                .save(consumer, name("condense_great_ancient_gem"));

        CondenserRecipeBuilder.condense(GemItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .requiresItem(Ingredient.of(GemItems.RHNULL_GEM_FRAME.get()))
                .requiresFluid(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 1000)
                .unlockedBy("has_gem_frame", has(GemItems.RHNULL_GEM_FRAME.get()))
                .save(consumer, name("condense_ancient_blasphemous_gem"));

        CondenserRecipeBuilder.condense(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .requiresItem(Ingredient.of(GemItems.RHNULL_GREAT_GEM_FRAME.get()))
                .requiresFluid(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 2000)
                .unlockedBy("has_great_gem_frame", has(GemItems.RHNULL_GREAT_GEM_FRAME.get()))
                .save(consumer, name("condense_great_ancient_blasphemous_gem"));

    }

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

