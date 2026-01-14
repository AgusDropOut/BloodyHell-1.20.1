package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class  ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, BloodyHell.MODID, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.BLOOD_SCRAPPER_PLANT_PLACEABLE_ON).add(
                ModBlocks.BLOOD_DIRT_BLOCK.get(),
                Blocks.DIRT,ModBlocks.BLOOD_SCRAPPER_PLANT.get(),
                ModBlocks.BLOOD_SCRAPPER_PLANT_SAPLING.get(),
                ModBlocks.BLOOD_GRASS_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SANGUINITE_BLOCK.get())
                .add(ModBlocks.SANGUINITE_ORE.get())
                .add(ModBlocks.RHNULL_BLOCK.get())
                .add(ModBlocks.Jumpy_Block.get())
                .add(ModBlocks.SANGUINE_CRUCIBLE.get())
                .add(ModBlocks.BLEEDING_BLOCK.get())
                .add(ModBlocks.GLOWING_CRYSTAL.get())
                .add(ModBlocks.GLOWING_CRYSTAL_LANTERN.get())
                .add(ModBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get())

                .add(ModBlocks.BLOODY_STONE_BLOCK.get())
                .add(ModBlocks.BLOODY_STONE_SLAB.get())
                .add(ModBlocks.BLOODY_STONE_STAIRS.get())
                .add(ModBlocks.BLOODY_STONE_FENCE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE_GATE.get())
                .add(ModBlocks.BLOODY_STONE_WALL.get())

                .add(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_SLAB.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_STAIRS.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_FENCE.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_WALL.get())

                .add(ModBlocks.BLOODY_STONE_TILES_BLOCK.get())
                .add(ModBlocks.BLOODY_STONE_TILES_SLAB.get())
                .add(ModBlocks.BLOODY_STONE_TILES_STAIRS.get())
                .add(ModBlocks.BLOODY_STONE_TILES_FENCE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE_TILES_GATE.get())
                .add(ModBlocks.BLOODY_STONE_TILES_WALL.get())

                .add(ModBlocks.BLOODY_STONE_BRICKS.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_SLAB.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_STAIRS.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_FENCE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE_BRICKS_GATE.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_WALL.get())

                .add(ModBlocks.SMALL_ROCKS.get())

                .add(ModBlocks.BLOOD_ALTAR.get())
                .add(ModBlocks.MAIN_BLOOD_ALTAR.get())

                .add(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get())
                .add(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get())
                .add(ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.ERODED_BLASPHEMOUS_SANDSTONE.get())
                .add(ModBlocks.CRACKED_BLASPHEMOUS_SANDSTONE.get())
                .add(ModBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE.get())
                .add(ModBlocks.BLASPHEMITE_ORE.get());

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.BLOOD_DIRT_BLOCK.get())
                .add(ModBlocks.BLOOD_GRASS_BLOCK.get())
                .add(ModBlocks.BLASPHEMOUS_SAND_BLOCK.get());


        this.tag(BlockTags.DIRT)
                .add(ModBlocks.BLOOD_GRASS_BLOCK.get())
                .add(ModBlocks.BLOOD_DIRT_BLOCK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SANGUINITE_BLOCK.get())
                .add(ModBlocks.SANGUINITE_ORE.get())
                .add(ModBlocks.BLASPHEMITE_ORE.get())
                .add(ModBlocks.Jumpy_Block.get())
                .add(ModBlocks.BLEEDING_BLOCK.get())
                .add(ModBlocks.SANGUINE_CRUCIBLE.get())
                .add(ModBlocks.GLOWING_CRYSTAL.get());

        this.tag(ModTags.Blocks.NEEDS_SANGUINITE_TOOL)
                .add(ModBlocks.RHNULL_BLOCK.get())
                .add(ModBlocks.BLASPHEMITE_ORE.get());


        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.BLOODY_STONE_BLOCK.get())
                .add(ModBlocks.BLOODY_STONE_SLAB.get())
                .add(ModBlocks.BLOODY_STONE_STAIRS.get())
                .add(ModBlocks.BLOODY_STONE_FENCE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE_GATE.get())
                .add(ModBlocks.BLOODY_STONE_WALL.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_SLAB.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_STAIRS.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_FENCE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE_BRICKS_GATE.get())
                .add(ModBlocks.BLOODY_STONE_BRICKS_WALL.get())
                .add(ModBlocks.BLOODY_STONE_TILES_SLAB.get())
                .add(ModBlocks.BLOODY_STONE_TILES_STAIRS.get())
                .add(ModBlocks.BLOODY_STONE_TILES_BLOCK.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_SLAB.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_STAIRS.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_FENCE.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_WALL.get())
                .add(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get())
                .add(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get())
                .add(ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get())
                .add(ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get());






        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.BLOOD_LOG.get())
                .add(ModBlocks.BLOOD_WOOD.get())
                .add(ModBlocks.STRIPPED_BLOOD_LOG.get())
                .add(ModBlocks.STRIPPED_BLOOD_WOOD.get())
                .add(ModBlocks.SOUL_LOG.get())
                .add(ModBlocks.SOUL_WOOD.get())
                .add(ModBlocks.STRIPPED_SOUL_LOG.get())

                .add(ModBlocks.BLOOD_PLANKS.get())
                .add(ModBlocks.BLOOD_PLANKS_STAIRS.get())
                .add(ModBlocks.BLOOD_PLANKS_SLAB.get())
                .add(ModBlocks.BLOOD_PLANKS_FENCE.get())
                .add(ModBlocks.BLOOD_PLANKS_FENCE_GATE.get())

                .add(ModBlocks.STRIPPED_SOUL_WOOD.get());

        this.tag(ModTags.Blocks.BLOOD_ORE_REPLACEABLES)
                .add(ModBlocks.BLOODY_STONE_BLOCK.get());

        this.tag(ModTags.Blocks.BLASPHEMITE_ORE_REPLACEABLES)
                .add(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get());




        this.tag(BlockTags.LEAVES)
                .add(ModBlocks.BLOOD_LEAVES.get())
                .add(ModBlocks.SOUL_LEAVES.get())
                .add(ModBlocks.HANGING_BLOOD_TREE_LEAVES.get())
                .add(ModBlocks.LIGHT_MUSHROOM_BLOCK.get())
                .add(ModBlocks.BLOOD_GRASS.get())
                .add(ModBlocks.SPIKY_GRASS.get())
                .add(ModBlocks.ROUNDED_GRASS.get())
                .add(ModBlocks.BLOOD_PETALS.get())
                .add(ModBlocks.SMALL_ROCKS.get())
                .add(ModBlocks.BLOOD_FLOWER.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.BLOOD_PLANKS.get())
                .add(ModBlocks.SOUL_PLANKS.get());
        this.tag(BlockTags.FENCES)
                .add(ModBlocks.BLOOD_PLANKS_FENCE.get())
                .add(ModBlocks.BLOODY_STONE_TILES_FENCE.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_FENCE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.BLOOD_PLANKS_FENCE_GATE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE_TILES_GATE.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get())
                .add(ModBlocks.BLOODY_STONE_FENCE_GATE.get());
        this.tag(BlockTags.WALLS)
                .add(ModBlocks.BLOODY_STONE_TILES_WALL.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_WALL.get())
                .add(ModBlocks.BLOODY_STONE_WALL.get());





    }

}