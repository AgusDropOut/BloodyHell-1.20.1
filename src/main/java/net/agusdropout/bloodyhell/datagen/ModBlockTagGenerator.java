package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.block.registry.MiscBlocks;
import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import net.agusdropout.bloodyhell.block.registry.WoodBlocks;
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
                SoilBlocks.BLOOD_DIRT_BLOCK.get(),
                Blocks.DIRT,PlantBlocks.BLOOD_SCRAPPER_PLANT.get(),
                PlantBlocks.BLOOD_SCRAPPER_PLANT_SAPLING.get(),
                SoilBlocks.BLOOD_GRASS_BLOCK.get());

        addToolRules();

        this.tag(BlockTags.DIRT)
                .add(SoilBlocks.BLOOD_GRASS_BLOCK.get())
                .add(SoilBlocks.BLOOD_DIRT_BLOCK.get());

        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(WoodBlocks.BLOOD_LOG.get())
                .add(WoodBlocks.BLOOD_WOOD.get())
                .add(WoodBlocks.STRIPPED_BLOOD_LOG.get())
                .add(WoodBlocks.STRIPPED_BLOOD_WOOD.get())
                .add(WoodBlocks.SOUL_LOG.get())
                .add(WoodBlocks.SOUL_WOOD.get())
                .add(WoodBlocks.STRIPPED_SOUL_LOG.get())

                .add(WoodBlocks.BLOOD_PLANKS.get())
                .add(WoodBlocks.BLOOD_PLANKS_STAIRS.get())
                .add(WoodBlocks.BLOOD_PLANKS_SLAB.get())
                .add(WoodBlocks.BLOOD_PLANKS_FENCE.get())
                .add(WoodBlocks.BLOOD_PLANKS_FENCE_GATE.get())

                .add(WoodBlocks.STRIPPED_SOUL_WOOD.get());

        this.tag(ModTags.Blocks.BLOOD_ORE_REPLACEABLES)
                .add(StoneBlocks.BLOODY_STONE_BLOCK.get());

        this.tag(ModTags.Blocks.BLASPHEMITE_ORE_REPLACEABLES)
                .add(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get());




        this.tag(BlockTags.LEAVES)
                .add(WoodBlocks.BLOOD_LEAVES.get())
                .add(WoodBlocks.SOUL_LEAVES.get())
                .add(PlantBlocks.HANGING_BLOOD_TREE_LEAVES.get())
                .add(PlantBlocks.LIGHT_MUSHROOM_BLOCK.get())
                .add(PlantBlocks.BLOOD_GRASS.get())
                .add(PlantBlocks.SPIKY_GRASS.get())
                .add(PlantBlocks.ROUNDED_GRASS.get())
                .add(PlantBlocks.BLOOD_PETALS.get())
                .add(MiscBlocks.SMALL_ROCKS.get())
                .add(PlantBlocks.BLOOD_FLOWER.get());

        this.tag(BlockTags.PLANKS)
                .add(WoodBlocks.BLOOD_PLANKS.get())
                .add(WoodBlocks.SOUL_PLANKS.get());
        this.tag(BlockTags.FENCES)
                .add(WoodBlocks.BLOOD_PLANKS_FENCE.get())
                .add(StoneBlocks.BLOODY_STONE_TILES_FENCE.get())
                .add(StoneBlocks.POLISHED_BLOODY_STONE_FENCE.get())
                .add(StoneBlocks.BLOODY_STONE_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(WoodBlocks.BLOOD_PLANKS_FENCE_GATE.get());
        this.tag(BlockTags.WALLS)
                .add(StoneBlocks.BLOODY_STONE_TILES_WALL.get())
                .add(StoneBlocks.POLISHED_BLOODY_STONE_WALL.get())
                .add(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get())
                .add(StoneBlocks.BLOODY_STONE_WALL.get());





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
