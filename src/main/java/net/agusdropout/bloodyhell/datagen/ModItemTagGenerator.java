package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                               CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, BloodyHell.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.BLOOD_LOG.get().asItem())
                .add(ModBlocks.BLOOD_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_BLOOD_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_BLOOD_WOOD.get().asItem())
                .add(ModBlocks.SOUL_LOG.get().asItem())
                .add(ModBlocks.SOUL_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_SOUL_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_SOUL_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.SOUL_PLANKS.get().asItem())
                .add(ModBlocks.BLOOD_PLANKS.get().asItem());
        this.tag(ItemTags.ARROWS)
                .add(ModItems.BLOOD_ARROW.get());
        this.tag(ItemTags.SWORDS)
                .add(ModItems.SANGUINITE_SWORD.get().asItem())
                .add(ModItems.RHNULL_SWORD.get().asItem())
                .add(ModItems.BLASPHEMITE_SWORD.get().asItem());
        this.tag(ItemTags.AXES)
                .add(ModItems.SANGUINITE_AXE.get().asItem())
                .add(ModItems.RHNULL_AXE.get().asItem())
                .add(ModItems.BLASPHEMITE_AXE.get().asItem());
        this.tag(ItemTags.PICKAXES)
                .add(ModItems.SANGUINITE_PICKAXE.get().asItem())
                .add(ModItems.RHNULL_PICKAXE.get().asItem())
                .add(ModItems.BLASPHEMITE_PICKAXE.get().asItem());
        this.tag(ItemTags.SHOVELS)
                .add(ModItems.SANGUINITE_SHOVEL.get().asItem())
                .add(ModItems.RHNULL_SHOVEL.get().asItem())
                .add(ModItems.BLASPHEMITE_SHOVEL.get().asItem());
        this.tag(ItemTags.HOES)
                .add(ModItems.SANGUINITE_HOE.get().asItem())
                .add(ModItems.RHNULL_HOE.get().asItem())
                 .add(ModItems.BLASPHEMITE_HOE.get().asItem());
        this.tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(ModBlocks.BLOODY_STONE_BLOCK.get().asItem());
        this.tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(ModBlocks.BLOODY_STONE_BLOCK.get().asItem());
        this.tag(ModTags.Items.BLOOD_LOGS)
                .add(ModBlocks.STRIPPED_BLOOD_LOG.get().asItem())
                .add(ModBlocks.BLOOD_LOG.get().asItem());
    }
}
