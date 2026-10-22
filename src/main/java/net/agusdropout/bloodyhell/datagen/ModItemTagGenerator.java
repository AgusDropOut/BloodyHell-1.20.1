package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.item.registry.BlasphemiteItems;
import net.agusdropout.bloodyhell.item.registry.BloodGearItems;
import net.agusdropout.bloodyhell.item.registry.BucketItems;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.item.registry.PlaceableItems;
import net.agusdropout.bloodyhell.item.registry.ReliquaryItems;
import net.agusdropout.bloodyhell.item.registry.RhnullItems;
import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
import net.agusdropout.bloodyhell.item.registry.WeaponItems;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.block.registry.MetalBlocks;
import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import net.agusdropout.bloodyhell.block.registry.WoodBlocks;
import com.eliotlash.mclib.math.functions.classic.Mod;
import net.agusdropout.bloodyhell.BloodyHell;
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
                .add(WoodBlocks.BLOOD_LOG.get().asItem())
                .add(WoodBlocks.BLOOD_WOOD.get().asItem())
                .add(WoodBlocks.STRIPPED_BLOOD_LOG.get().asItem())
                .add(WoodBlocks.STRIPPED_BLOOD_WOOD.get().asItem())
                .add(WoodBlocks.SOUL_LOG.get().asItem())
                .add(WoodBlocks.SOUL_WOOD.get().asItem())
                .add(WoodBlocks.STRIPPED_SOUL_LOG.get().asItem())
                .add(WoodBlocks.STRIPPED_SOUL_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(WoodBlocks.SOUL_PLANKS.get().asItem())
                .add(WoodBlocks.BLOOD_PLANKS.get().asItem());

        this.tag(ItemTags.ARROWS)
                .add(BloodGearItems.BLOOD_ARROW.get());

        this.tag(ItemTags.SWORDS)
                .add(SanguiniteItems.SANGUINITE_SWORD.get())
                .add(RhnullItems.RHNULL_SWORD.get())
                .add(BlasphemiteItems.BLASPHEMITE_SWORD.get());

        this.tag(ItemTags.AXES)
                .add(SanguiniteItems.SANGUINITE_AXE.get())
                .add(RhnullItems.RHNULL_AXE.get())
                .add(BlasphemiteItems.BLASPHEMITE_AXE.get());

        this.tag(ItemTags.PICKAXES)
                .add(SanguiniteItems.SANGUINITE_PICKAXE.get())
                .add(RhnullItems.RHNULL_PICKAXE.get())
                .add(BlasphemiteItems.BLASPHEMITE_PICKAXE.get());

        this.tag(ItemTags.SHOVELS)
                .add(SanguiniteItems.SANGUINITE_SHOVEL.get())
                .add(RhnullItems.RHNULL_SHOVEL.get())
                .add(BlasphemiteItems.BLASPHEMITE_SHOVEL.get());

        this.tag(ItemTags.HOES)
                .add(SanguiniteItems.SANGUINITE_HOE.get())
                .add(RhnullItems.RHNULL_HOE.get())
                .add(BlasphemiteItems.BLASPHEMITE_HOE.get());

        this.tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(StoneBlocks.BLOODY_STONE_BLOCK.get().asItem());

        this.tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(StoneBlocks.BLOODY_STONE_BLOCK.get().asItem());

        this.tag(ModTags.Items.BLOOD_LOGS)
                .add(WoodBlocks.STRIPPED_BLOOD_LOG.get().asItem())
                .add(WoodBlocks.BLOOD_LOG.get().asItem());

        this.tag(ModTags.Items.CRIMSONVEIL_CONSUMER)
                .add(SpellBookItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_NOVA_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_SCRATCH_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_FIRE_SOUL_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_FIRE_METEOR_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_IMPALERS_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_ORB_EMITTER_SPELLBOOK.get())
                .add(WeaponItems.BLASPHEMOUS_TWIN_DAGGERS.get())
                .add(WeaponItems.BLASPHEMOUS_IMPALER.get())
                .add(WeaponItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get())
                .add(StaffItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get())
                .add(StaffItems.STAFF_OF_THE_GOLDEN_SORROW.get())
                .add(StaffItems.STAFF_OF_SCARLET_CINDER.get())
                .add(SpellBookItems.THE_OMNITOME.get());

        this.tag(ModTags.Items.RELIQUARY_UPGRADE_ITEM)
                .add(ReliquaryItems.ANCIENT_OCULAR_LENSE.get())
                .add(ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE.get());

        this.tag(ModTags.Items.RELIQUARY_RUNE_ITEM)
                .add(ReliquaryItems.MARK_OF_THE_RESTLESS_SLUMBER.get())
                .add(ReliquaryItems.RUNE_OF_THE_RAVENOUS_GAZE.get())
                .add(ReliquaryItems.SEAL_OF_THE_HOLLOW_BULWARK.get())
                .add(ReliquaryItems.OMEN_OF_THE_CRUSHING_TOLL.get());

        this.tag(ModTags.Items.SANGUINITE_TIER_ITEMS)
                .add(SanguiniteItems.SANGUINITE.get())
                .add(SanguiniteItems.RAW_SANGUINITE.get())
                .add(SanguiniteItems.SANGUINITE_NUGGET.get())
                .add(SanguiniteItems.SANGUINITE_SWORD.get())
                .add(SanguiniteItems.SANGUINITE_PICKAXE.get())
                .add(SanguiniteItems.SANGUINITE_AXE.get())
                .add(SanguiniteItems.SANGUINITE_SHOVEL.get())
                .add(SanguiniteItems.SANGUINITE_HOE.get())
                .add(BloodGearItems.BLOOD_HELMET.get())
                .add(BloodGearItems.BLOOD_CHESTPLATE.get())
                .add(BloodGearItems.BLOOD_LEGGINGS.get())
                .add(BloodGearItems.BLOOD_BOOTS.get())
                .add(BloodGearItems.BLOOD_BOW.get())
                .add(BloodGearItems.BLOOD_ARROW.get())
                .add(MetalBlocks.SANGUINITE_BLOCK.get().asItem())
                .add(MetalBlocks.SANGUINITE_ORE.get().asItem())
                .add(PlaceableItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get())
                .add(PlaceableItems.SANGUINITE_PIPE_ITEM.get())
                .add(MachineBlocks.SANGUINITE_TANK.get().asItem())
                .add(MachineBlocks.SANGUINITE_INFUSOR.get().asItem())
                .add(PlaceableItems.SANGUINITE_CONDENSER_ITEM.get())
                .add(MachineBlocks.SANGUINE_LAPIDARY.get().asItem())
                .add(MachineBlocks.SANGUINE_CRUCIBLE.get().asItem())
                .add(GemItems.SANGUINITE_GEM_FRAME.get())
                .add(GemItems.SANGUINITE_GREAT_GEM_FRAME.get())
                .add(GemItems.ANCIENT_GEM.get())
                .add(GemItems.GREAT_ANCIENT_GEM.get())
                .add(GemItems.PURE_BLOOD_GEM.get())
                .add(GemItems.CITRINE_BLOOD_GEM.get())
                .add(GemItems.TANZARINE_BLOOD_GEM.get())
                .add(GemItems.AVENTURINE_BLOOD_GEM.get());

        this.tag(ModTags.Items.RHNULL_TIER_ITEMS)
                .add(RhnullItems.RHNULL.get())
                .add(RhnullItems.RHNULL_NUGGET.get())
                .add(MetalBlocks.RHNULL_BLOCK.get().asItem())
                .add(RhnullItems.RHNULL_SWORD.get())
                .add(RhnullItems.RHNULL_PICKAXE.get())
                .add(RhnullItems.RHNULL_AXE.get())
                .add(RhnullItems.RHNULL_SHOVEL.get())
                .add(RhnullItems.RHNULL_HOE.get())
                .add(RhnullItems.RHNULL_HELMET.get())
                .add(RhnullItems.RHNULL_CHESTPLATE.get())
                .add(RhnullItems.RHNULL_LEGGINGS.get())
                .add(RhnullItems.RHNULL_BOOTS.get())
                .add(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get())
                .add(PlaceableItems.RHNULL_PIPE_ITEM.get())
                .add(MachineBlocks.RHNULL_TANK.get().asItem())
                .add(PlaceableItems.RHNULL_CONDENSER_ITEM.get())
                .add(GemItems.RHNULL_GEM_FRAME.get())
                .add(GemItems.RHNULL_GREAT_GEM_FRAME.get())
                .add(GemItems.ANCIENT_RHNULL_GEM.get())
                .add(GemItems.GREAT_ANCIENT_RHNULL_GEM.get())
                .add(SpellBookItems.RHNULL_IMPALERS_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_ORB_EMITTER_SPELLBOOK.get());

        this.tag(ModTags.Items.BLASPHEMOUS_TIER_ITEMS)
                .add(BlasphemiteItems.BLASPHEMITE.get())
                .add(BlasphemiteItems.RAW_BLASPHEMITE.get())
                .add(MetalBlocks.BLASPHEMITE_ORE.get().asItem())
                .add(BlasphemiteItems.BLASPHEMITE_NUGGET.get())
                .add(MetalBlocks.BLASPHEMITE_BLOCK.get().asItem())
                .add(BlasphemiteItems.BLASPHEMITE_SWORD.get())
                .add(BlasphemiteItems.BLASPHEMITE_PICKAXE.get())
                .add(BlasphemiteItems.BLASPHEMITE_AXE.get())
                .add(BlasphemiteItems.BLASPHEMITE_SHOVEL.get())
                .add(BlasphemiteItems.BLASPHEMITE_HOE.get())
                .add(BlasphemiteItems.BLASPHEMITE_HELMET.get())
                .add(BlasphemiteItems.BLASPHEMITE_CHESTPLATE.get())
                .add(BlasphemiteItems.BLASPHEMITE_LEGGINGS.get())
                .add(BlasphemiteItems.BLASPHEMITE_BOOTS.get())
                .add(WeaponItems.BLASPHEMOUS_TWIN_DAGGERS.get())
                .add(WeaponItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get())
                .add(WeaponItems.BLASPHEMOUS_IMPALER.get())
                .add(GemItems.BLASPHEMOUS_RING.get())
                .add(BlasphemiteItems.BLASPHEMOUS_EYE.get())
                .add(ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE.get())
                .add(GemItems.ANCIENT_BLASPHEMOUS_GEM.get())
                .add(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get())
                .add(FlaskItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get())
                .add(BucketItems.VISCOUS_BLASPHEMY_BUCKET.get())
                .add(PlaceableItems.BLASPHEMOUS_BLOOD_ALTAR_ITEM.get())
                .add(PlaceableItems.MAIN_BLASPHEMOUS_BLOOD_ALTAR_ITEM.get())
                .add(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get().asItem())
                .add(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get().asItem())
                .add(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get().asItem())
                .add(SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get().asItem())
                .add(SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get().asItem())
                .add(SandstoneBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK.get().asItem());

        this.tag(ModTags.Items.GEM_FRAMES)
                .add(GemItems.SANGUINITE_GEM_FRAME.get())
                .add(GemItems.SANGUINITE_GREAT_GEM_FRAME.get())
                .add(GemItems.RHNULL_GREAT_GEM_FRAME.get())
                .add(GemItems.RHNULL_GEM_FRAME.get());

        this.tag(ModTags.Items.CRIMSONVEIL_PASSIVE_RECHARGER)
                .add(GemItems.AMULET_OF_ANCESTRAL_BLOOD.get())
                .add(GemItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get());

        this.tag(ModTags.Items.SPELLBOOKS)
                .add(SpellBookItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_NOVA_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_SCRATCH_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_FIRE_SOUL_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get())
                .add(SpellBookItems.BLOOD_FIRE_METEOR_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_IMPALERS_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get())
                .add(SpellBookItems.RHNULL_ORB_EMITTER_SPELLBOOK.get());
    }

}