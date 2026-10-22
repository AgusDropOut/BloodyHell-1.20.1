package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.BlockToolRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        addToolRules();

        this.tag(BlockTags.DIRT)
                .add(ModBlocks.BLOOD_GRASS_BLOCK.get())
                .add(ModBlocks.BLOOD_DIRT_BLOCK.get());

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
                .add(ModBlocks.BLOOD_PLANKS_FENCE_GATE.get());
        this.tag(BlockTags.WALLS)
                .add(ModBlocks.BLOODY_STONE_TILES_WALL.get())
                .add(ModBlocks.POLISHED_BLOODY_STONE_WALL.get())
                .add(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get())
                .add(ModBlocks.BLOODY_STONE_WALL.get());





    }

    private void addToolRules() {
        List<String> unclassified = new ArrayList<>();
        for (RegistryObject<Block> entry : ModBlocks.BLOCKS.getEntries()) {
            if (BlockToolRules.ruleFor(entry.getId()) == null) unclassified.add(entry.getId().getPath());
        }
        if (!unclassified.isEmpty()) {
            throw new IllegalStateException("Blocks without a tool rule in BlockToolRules: " + unclassified);
        }
        for (Map.Entry<ResourceLocation, RegistryObject<? extends Block>> entry : BlockToolRules.blocks().entrySet()) {
            BlockToolRules.Rule rule = BlockToolRules.ruleFor(entry.getKey());
            Block block = entry.getValue().get();
            if (rule.tool().tag() != null) this.tag(rule.tool().tag()).add(block);
            if (rule.tier().tag() != null) this.tag(rule.tier().tag()).add(block);
        }
    }
}
