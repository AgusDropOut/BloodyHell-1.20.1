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

        this.tag(ModTags.Items.CRIMSONVEIL_CONSUMER)
                .add(ModItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get())
                .add(ModItems.BLOOD_SPHERE_SPELLBOOK.get())
                .add(ModItems.BLOOD_NOVA_SPELLBOOK.get())
                .add(ModItems.BLOOD_SCRATCH_SPELLBOOK.get())
                .add(ModItems.BLOOD_FIRE_SOUL_SPELLBOOK.get())
                .add(ModItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get())
                .add(ModItems.BLOOD_FIRE_METEOR_SPELLBOOK.get())
                .add(ModItems.RHNULL_IMPALERS_SPELLBOOK.get())
                .add(ModItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get())
                .add(ModItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get())
                .add(ModItems.RHNULL_ORB_EMITTER_SPELLBOOK.get())
                .add(ModItems.BLASPHEMOUS_TWIN_DAGGERS.get())
                .add(ModItems.BLASPHEMOUS_IMPALER.get())
                .add(ModItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get());


        this.tag(ModTags.Items.SANGUINITE_TIER_ITEMS)
                .add(ModItems.SANGUINITE.get())
                .add(ModItems.RAW_SANGUINITE.get())
                .add(ModItems.SANGUINITE_NUGGET.get())
                .add(ModItems.SANGUINITE_SWORD.get())
                .add(ModItems.SANGUINITE_PICKAXE.get())
                .add(ModItems.SANGUINITE_AXE.get())
                .add(ModItems.SANGUINITE_SHOVEL.get())
                .add(ModItems.SANGUINITE_HOE.get())
                .add(ModBlocks.SANGUINITE_BLOCK.get().asItem())
                .add(ModBlocks.SANGUINITE_ORE.get().asItem())
                .add(ModBlocks.SANGUINITE_BLOOD_HARVESTER.get().asItem())
                .add(ModBlocks.SANGUINITE_PIPE.get().asItem())
                .add(ModBlocks.SANGUINITE_TANK.get().asItem())
                .add(ModBlocks.SANGUINITE_INFUSOR.get().asItem());

        // 2. Rhnull Tier (Reddish Gold Frame, Gold Text)
        this.tag(ModTags.Items.RHNULL_TIER_ITEMS)
                .add(ModItems.RHNULL.get())
                .add(ModItems.RHNULL_NUGGET.get())
                .add(ModItems.RHNULL_SWORD.get())
                .add(ModItems.RHNULL_PICKAXE.get())
                .add(ModItems.RHNULL_AXE.get())
                .add(ModItems.RHNULL_SHOVEL.get())
                .add(ModItems.RHNULL_HOE.get())
                .add(ModItems.RHNULL_HELMET.get())
                .add(ModItems.RHNULL_CHESTPLATE.get())
                .add(ModItems.RHNULL_LEGGINGS.get())
                .add(ModItems.RHNULL_BOOTS.get())
                .add(ModBlocks.RHNULL_BLOCK.get().asItem())
                .add(ModItems.FILLED_RHNULL_BLOOD_FLASK.get());

        // 3. Blasphemous Tier (Gold Frame, Dark Yellow/Black BG)
        this.tag(ModTags.Items.BLASPHEMOUS_TIER_ITEMS)
                .add(ModItems.BLASPHEMITE.get())
                .add(ModItems.RAW_BLASPHEMITE.get())
                .add(ModItems.BLASPHEMITE_NUGGET.get())
                .add(ModItems.BLASPHEMITE_SWORD.get())
                .add(ModItems.BLASPHEMITE_PICKAXE.get())
                .add(ModItems.BLASPHEMITE_AXE.get())
                .add(ModItems.BLASPHEMITE_SHOVEL.get())
                .add(ModItems.BLASPHEMITE_HOE.get())
                .add(ModItems.BLASPHEMITE_HELMET.get())
                .add(ModItems.BLASPHEMITE_CHESTPLATE.get())
                .add(ModItems.BLASPHEMITE_LEGGINGS.get())
                .add(ModItems.BLASPHEMITE_BOOTS.get())
                .add(ModItems.BLASPHEMOUS_TWIN_DAGGERS.get())
                .add(ModItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get())
                .add(ModItems.BLASPHEMOUS_IMPALER.get())
                .add(ModItems.BLASPHEMOUS_RING.get());
    }
}
