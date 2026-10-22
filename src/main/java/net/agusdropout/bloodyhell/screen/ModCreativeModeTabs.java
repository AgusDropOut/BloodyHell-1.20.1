package net.agusdropout.bloodyhell.screen;

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
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BloodyHell.MODID);

    public static final RegistryObject<CreativeModeTab> BLOODY_HELL_TAB = CREATIVE_MODE_TABS.register("bloody_hell_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SANGUINITE.get()))
                    .title(Component.translatable("creativetab.bloody_hell_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        // --- Guide Book ---
                        output.accept(ModItems.UNKNOWN_GUIDE_BOOK.get());

                        // --- Ores & Minerals ---
                        output.accept(MetalBlocks.SANGUINITE_ORE.get());
                        output.accept(MiscBlocks.Jumpy_Block.get());
                        output.accept(MetalBlocks.BLASPHEMITE_ORE.get());

                        // --- Base Materials ---
                        output.accept(ModItems.RAW_SANGUINITE.get());
                        output.accept(ModItems.SANGUINITE_NUGGET.get());
                        output.accept(ModItems.SANGUINITE.get());
                        output.accept(MetalBlocks.SANGUINITE_BLOCK.get());
                        output.accept(ModItems.RHNULL_NUGGET.get());
                        output.accept(ModItems.RHNULL.get());
                        output.accept(MetalBlocks.RHNULL_BLOCK.get());
                        output.accept(ModItems.RAW_BLASPHEMITE.get());
                        output.accept(ModItems.BLASPHEMITE_NUGGET.get());
                        output.accept(ModItems.BLASPHEMITE.get());
                        output.accept(MetalBlocks.BLASPHEMITE_BLOCK.get());

                        // --- Tools & Weapons ---
                        output.accept(ModItems.SANGUINITE_SWORD.get());
                        output.accept(ModItems.SANGUINITE_PICKAXE.get());
                        output.accept(ModItems.SANGUINITE_AXE.get());
                        output.accept(ModItems.SANGUINITE_SHOVEL.get());
                        output.accept(ModItems.SANGUINITE_HOE.get());

                        output.accept(ModItems.RHNULL_SWORD.get());
                        output.accept(ModItems.RHNULL_PICKAXE.get());
                        output.accept(ModItems.RHNULL_AXE.get());
                        output.accept(ModItems.RHNULL_SHOVEL.get());
                        output.accept(ModItems.RHNULL_HOE.get());

                        output.accept(ModItems.BLASPHEMITE_SWORD.get());
                        output.accept(ModItems.BLASPHEMITE_PICKAXE.get());
                        output.accept(ModItems.BLASPHEMITE_AXE.get());
                        output.accept(ModItems.BLASPHEMITE_SHOVEL.get());
                        output.accept(ModItems.BLASPHEMITE_HOE.get());

                        output.accept(ModItems.BLOOD_BOW.get());
                        output.accept(ModItems.BLOOD_ARROW.get());
                        output.accept(ModItems.BLOOD_SCYTHE.get());
                        output.accept(ModItems.SACRIFICIAL_DAGGER.get());
                        output.accept(ModItems.HERETIC_SACRIFICIAL_DAGGER.get());

                        output.accept(ModItems.BLASPHEMOUS_TWIN_DAGGERS.get());
                        output.accept(ModItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get());
                        output.accept(ModItems.BLASPHEMOUS_IMPALER.get());

                        // --- Armor ---
                        output.accept(ModItems.BLOOD_HELMET.get());
                        output.accept(ModItems.BLOOD_CHESTPLATE.get());
                        output.accept(ModItems.BLOOD_LEGGINGS.get());
                        output.accept(ModItems.BLOOD_BOOTS.get());

                        output.accept(ModItems.RHNULL_HELMET.get());
                        output.accept(ModItems.RHNULL_CHESTPLATE.get());
                        output.accept(ModItems.RHNULL_LEGGINGS.get());
                        output.accept(ModItems.RHNULL_BOOTS.get());

                        output.accept(ModItems.BLASPHEMITE_HELMET.get());
                        output.accept(ModItems.BLASPHEMITE_CHESTPLATE.get());
                        output.accept(ModItems.BLASPHEMITE_LEGGINGS.get());
                        output.accept(ModItems.BLASPHEMITE_BOOTS.get());

                        // --- Accessories & Artifacts ---
                        output.accept(ModItems.AMULET_OF_ANCESTRAL_BLOOD.get());
                        output.accept(ModItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get());
                        output.accept(ModItems.CRIMSON_WARD_RING.get());
                        output.accept(ModItems.BLASPHEMOUS_RING.get());
                        output.accept(ModItems.CHALICE_OF_THE_DAMMED.get());
                        output.accept(ModItems.CRIMSON_IDOL_COIN.get());
                        output.accept(ModItems.Eight_ball.get());
                        output.accept(ModItems.BLOOD_ECHO_SHARD.get());
                        output.accept(ModItems.NAMELESS_ECHO_SHARD.get());

                        // --- Magic, Spell Books & Reliquary ---
                        output.accept(ModItems.BLOOD_SCRATCH_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_SPHERE_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_NOVA_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_FIRE_METEOR_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_FIRE_SOUL_SPELLBOOK.get());
                        output.accept(ModItems.RHNULL_IMPALERS_SPELLBOOK.get());
                        output.accept(ModItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get());
                        output.accept(ModItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get());
                        output.accept(ModItems.RHNULL_ORB_EMITTER_SPELLBOOK.get());
                        output.accept(ModItems.THE_OMNITOME.get());

                        output.accept(ModItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get());
                        output.accept(ModItems.STAFF_OF_THE_GOLDEN_SORROW.get());
                        output.accept(ModItems.STAFF_OF_SCARLET_CINDER.get());

                        output.accept(ModItems.RELIQUARY.get());
                        output.accept(ModItems.ANCIENT_OCULAR_LENSE.get());
                        output.accept(ModItems.BLASPHEMOUS_OCULAR_LENSE.get());
                        output.accept(ModItems.MARK_OF_THE_RESTLESS_SLUMBER.get());
                        output.accept(ModItems.RUNE_OF_THE_RAVENOUS_GAZE.get());
                        output.accept(ModItems.OMEN_OF_THE_CRUSHING_TOLL.get());
                        output.accept(ModItems.SEAL_OF_THE_HOLLOW_BULWARK.get());

                        // --- Gems & Components ---
                        output.accept(ModItems.PURE_BLOOD_GEM.get());
                        output.accept(ModItems.TANZARINE_BLOOD_GEM.get());
                        output.accept(ModItems.AVENTURINE_BLOOD_GEM.get());
                        output.accept(ModItems.CITRINE_BLOOD_GEM.get());
                        output.accept(ModItems.ANCIENT_GEM.get());
                        output.accept(ModItems.GREAT_ANCIENT_GEM.get());
                        output.accept(ModItems.ANCIENT_RHNULL_GEM.get());
                        output.accept(ModItems.GREAT_ANCIENT_RHNULL_GEM.get());
                        output.accept(ModItems.ANCIENT_BLASPHEMOUS_GEM.get());
                        output.accept(ModItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get());

                        output.accept(ModItems.SANGUINITE_GEM_FRAME.get());
                        output.accept(ModItems.SANGUINITE_GREAT_GEM_FRAME.get());
                        output.accept(ModItems.RHNULL_GEM_FRAME.get());
                        output.accept(ModItems.RHNULL_GREAT_GEM_FRAME.get());

                        // --- Mechanisms & Stations ---
                        output.accept(MachineBlocks.BLOOD_ALTAR.get());
                        output.accept(MachineBlocks.MAIN_BLOOD_ALTAR.get());
                        output.accept(ModItems.BLASPHEMOUS_BLOOD_ALTAR_ITEM.get());
                        output.accept(ModItems.MAIN_BLASPHEMOUS_BLOOD_ALTAR_ITEM.get());
                        output.accept(MachineBlocks.SANGUINE_LAPIDARY.get());
                        output.accept(ModItems.SANGUINITE_CONDENSER_ITEM.get());
                        output.accept(ModItems.RHNULL_CONDENSER_ITEM.get());
                        output.accept(MachineBlocks.SANGUINITE_INFUSOR.get());
                        output.accept(ModItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get());
                        output.accept(MachineBlocks.SANGUINITE_TANK.get());
                        output.accept(MachineBlocks.RHNULL_TANK.get());
                        output.accept(ModItems.SANGUINITE_PIPE_ITEM.get());
                        output.accept(ModItems.RHNULL_PIPE_ITEM.get());
                        output.accept(ModItems.UNKNOWN_PORTAL_ITEM.get());
                        output.accept(ModItems.RHNULL_BLOOD_ENGINE_ITEM.get());
                        output.accept(ModItems.RHNULL_BLOOD_ENGINE_PILLAR_ITEM.get());

                        // --- Fluids & Flasks ---
                        output.accept(ModItems.BLOOD_BUCKET.get());
                        output.accept(ModItems.CORRUPTED_BLOOD_BUCKET.get());
                        output.accept(ModItems.VISCERAL_BLOOD_BUCKET.get());
                        output.accept(ModItems.VISCOUS_BLASPHEMY_BUCKET.get());

                        output.accept(ModItems.BLOOD_FLASK.get());
                        output.accept(ModItems.FILLED_BLOOD_FLASK.get());
                        output.accept(ModItems.CORRUPTED_BLOOD_FLASK.get());
                        output.accept(ModItems.FILLED_RHNULL_BLOOD_FLASK.get());
                        output.accept(ModItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get());

                        // --- Ingredients ---
                        output.accept(ModItems.ROOTLET_POWDER.get());

                        // --- Mob Drops & Boss Items ---
                        output.accept(ModItems.MATERIALIZED_SOUL.get());
                        output.accept(ModItems.BLOODY_SOUL_DUST.get());
                        output.accept(ModItems.AUREAL_REVENANT_DAGGER.get());
                        output.accept(ModItems.VEINREAVER_HORN.get());
                        output.accept(ModItems.CRIMSON_SHELL.get());
                        output.accept(ModItems.SCARLET_FEATHER.get());
                        output.accept(ModItems.UNKNOWN_ENTITY_FINGER.get());
                        output.accept(ModItems.BLASPHEMOUS_EYE.get());
                        output.accept(ModItems.GAZE_OF_THE_UNKNOWN.get());
                        output.accept(ModItems.NAMELESS_WHISPER.get());
                        output.accept(ModItems.SELIORA_ESSENCE.get());
                        output.accept(ModItems.RITEKEEPER_HEART.get());
                        output.accept(ModItems.CINDER_ACOLYTE_FAINTED_EMBER.get());
                        output.accept(ModItems.FAILED_REMNANT_ASHES.get());

                        // --- Food ---
                        output.accept(ModItems.SCARLET_RAW_CHICKEN.get());
                        output.accept(ModItems.SCARLET_COOKED_CHICKEN.get());
                        output.accept(ModItems.GOREHOG_RAW_STEAK.get());
                        output.accept(ModItems.GOREHOG_COOKED_STEAK.get());
                        output.accept(ModItems.Eyeball.get());
                        output.accept(ModItems.Eyeball_seed.get());
                        output.accept(ModItems.GLOW_FRUIT.get());

                        // --- Nature, Plants & Mushrooms ---
                        output.accept(SoilBlocks.BLOOD_DIRT_BLOCK.get());
                        output.accept(SoilBlocks.BLOOD_GRASS_BLOCK.get());
                        output.accept(PlantBlocks.BLOOD_SCRAPPER_PLANT.get());
                        output.accept(PlantBlocks.BLOOD_SAPLING.get());
                        output.accept(PlantBlocks.SOUL_SAPLING.get());
                        output.accept(WoodBlocks.BLOOD_LEAVES.get());
                        output.accept(WoodBlocks.SOUL_LEAVES.get());
                        output.accept(PlantBlocks.HANGING_BLOOD_TREE_LEAVES.get());
                        output.accept(PlantBlocks.HANGING_SOUL_TREE_LEAVES.get());
                        output.accept(WoodBlocks.BLOOD_LOG.get());
                        output.accept(WoodBlocks.STRIPPED_BLOOD_LOG.get());
                        output.accept(WoodBlocks.BLOOD_WOOD.get());
                        output.accept(WoodBlocks.STRIPPED_BLOOD_WOOD.get());
                        output.accept(WoodBlocks.SOUL_LOG.get());
                        output.accept(WoodBlocks.STRIPPED_SOUL_LOG.get());
                        output.accept(WoodBlocks.SOUL_WOOD.get());
                        output.accept(WoodBlocks.STRIPPED_SOUL_WOOD.get());
                        output.accept(PlantBlocks.BLOOD_FLOWER.get());
                        output.accept(ModItems.DIRTY_BLOOD_FLOWER.get());
                        output.accept(PlantBlocks.BLOOD_GRASS.get());
                        output.accept(PlantBlocks.BLOOD_BUSH.get());
                        output.accept(PlantBlocks.BLOOD_PETALS.get());
                        output.accept(ModItems.BLOOD_LILY.get());
                        output.accept(PlantBlocks.SPIKY_GRASS.get());
                        output.accept(PlantBlocks.ROUNDED_GRASS.get());
                        output.accept(PlantBlocks.STING_FLOWER.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_CON.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_FLOWER.get());

                        output.accept(ModItems.BLOOD_GEM_SPROUT_SEED.get());
                        output.accept(ModItems.GLOW_MUSHROOM.get());
                        output.accept(PlantBlocks.BLOOD_WALL_MUSHROOM_BLOCK.get());
                        output.accept(ModItems.VORACIOUS_MUSHROOM.get());
                        output.accept(ModItems.CRIMSON_LURE_MUSHROOM.get());
                        output.accept(PlantBlocks.VISCERAL_INFECTED_VEIN.get());

                        // --- Building Blocks ---
                        output.accept(WoodBlocks.BLOOD_PLANKS.get());
                        output.accept(WoodBlocks.BLOOD_PLANKS_STAIRS.get());
                        output.accept(WoodBlocks.BLOOD_PLANKS_SLAB.get());
                        output.accept(WoodBlocks.BLOOD_PLANKS_FENCE.get());
                        output.accept(WoodBlocks.BLOOD_PLANKS_FENCE_GATE.get());
                        output.accept(WoodBlocks.SOUL_PLANKS.get());

                        output.accept(StoneBlocks.BLOODY_STONE_BLOCK.get());
                        output.accept(StoneBlocks.BLOODY_STONE_STAIRS.get());
                        output.accept(StoneBlocks.BLOODY_STONE_SLAB.get());
                        output.accept(StoneBlocks.BLOODY_STONE_WALL.get());
                        output.accept(StoneBlocks.BLOODY_STONE_FENCE.get());
                        output.accept(StoneBlocks.BLOODY_STONE_FENCE_GATE.get());

                        output.accept(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get());
                        output.accept(StoneBlocks.POLISHED_BLOODY_STONE_STAIRS.get());
                        output.accept(StoneBlocks.POLISHED_BLOODY_STONE_SLAB.get());
                        output.accept(StoneBlocks.POLISHED_BLOODY_STONE_WALL.get());
                        output.accept(StoneBlocks.POLISHED_BLOODY_STONE_FENCE.get());
                        output.accept(StoneBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get());

                        output.accept(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get());
                        output.accept(StoneBlocks.BLOODY_STONE_TILES_STAIRS.get());
                        output.accept(StoneBlocks.BLOODY_STONE_TILES_SLAB.get());
                        output.accept(StoneBlocks.BLOODY_STONE_TILES_WALL.get());
                        output.accept(StoneBlocks.BLOODY_STONE_TILES_FENCE.get());
                        output.accept(StoneBlocks.BLOODY_STONE_FENCE_TILES_GATE.get());

                        output.accept(StoneBlocks.BLOODY_STONE_BRICKS.get());
                        output.accept(StoneBlocks.BLOODY_STONE_BRICKS_STAIRS.get());
                        output.accept(StoneBlocks.BLOODY_STONE_BRICKS_SLAB.get());
                        output.accept(StoneBlocks.BLOODY_STONE_BRICKS_WALL.get());
                        output.accept(StoneBlocks.BLOODY_STONE_BRICKS_FENCE.get());
                        output.accept(StoneBlocks.BLOODY_STONE_FENCE_BRICKS_GATE.get());

                        output.accept(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get());
                        output.accept(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS.get());
                        output.accept(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get());
                        output.accept(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get());
                        output.accept(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN.get());
                        output.accept(StoneBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS.get());
                        output.accept(StoneBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS.get());

                        output.accept(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get());
                        output.accept(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get());
                        output.accept(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get());
                        output.accept(SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(SandstoneBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(SandstoneBlocks.ERODED_BLASPHEMOUS_SANDSTONE.get());
                        output.accept(SandstoneBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE.get());
                        output.accept(SandstoneBlocks.CRACKED_BLASPHEMOUS_SANDSTONE.get());

                        // --- Decorations & Misc Blocks ---
                        output.accept(LightBlocks.GLOWING_CRYSTAL.get());
                        output.accept(LightBlocks.GLOWING_CRYSTAL_GLASS_BLOCK.get());
                        output.accept(LightBlocks.GLOWING_CRYSTAL_LANTERN.get());
                        output.accept(LightBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get());
                        output.accept(LightBlocks.SOUL_LAMP.get());
                        output.accept(LightBlocks.BLOOD_GLOW_STONE.get());
                        output.accept(LightBlocks.ANCIENT_BLOODY_LAMP.get());
                        output.accept(ModItems.ANCIENT_TORCH_ITEM.get());
                        output.accept(MiscBlocks.ANCIENT_BLOOD_CAPSULE.get());
                        output.accept(LightBlocks.STAR_LAMP_BLOCK.get());
                        output.accept(MiscBlocks.DECORATED_POT_BLOCK.get());
                        output.accept(MiscBlocks.FORBIDDEN_BOOKSHELF_BLOCK.get());
                        output.accept(MiscBlocks.TOMB_BLOCK.get());
                        output.accept(MiscBlocks.SMALL_ROCKS.get());
                        output.accept(MiscBlocks.BLEEDING_BLOCK.get());
                        output.accept(MiscBlocks.EYEBALLSHELL_SNAIL_GOO_BLOCK.get());
                        output.accept(MiscBlocks.EYEBALLSHELL_SNAIL_GOO.get());

                        // --- Spawn Eggs ---
                        //output.accept(ModItems.BLOODTHIRSTYBEAST_SPAWN_EGG.get());
                        output.accept(ModItems.BLOOD_SEEKER_SPAWN_EGG.get());
                        output.accept(ModItems.CRIMSON_RAVEN_SPAWN_EGG.get());
                        output.accept(ModItems.EYESHELLSNAIL_SPAWN_EGG.get());
                        output.accept(ModItems.BLOOD_PIG_SPAWN_EGG.get());
                        output.accept(ModItems.SCARLETSPECKLED_FISH_SPAWN_EGG.get());
                        output.accept(ModItems.OMEN_GAZER_ENTITY_SPAWN_EGG.get());
                        //output.accept(ModItems.VEINRAVER_ENTITY_SPAWN_EGG.get());
                        output.accept(ModItems.OFFSPRING_OF_THE_UNKNOWN_SPAWN_EGG.get());
                        output.accept(ModItems.BLASPHEMOUS_MALFORMATION_SPAWN_EGG.get());
                        output.accept(ModItems.SELIORA_SPAWN_EGG.get());
                        output.accept(ModItems.HORNED_WORM_SPAWN_EGG.get());
                        output.accept(ModItems.VEIL_STALKER_SPAWN_EGG.get());
                        output.accept(ModItems.GRAVE_WALKER_SPAWN_EGG.get());
                        output.accept(ModItems.CYCLOPS_ENTITY_SPAWN_EGG.get());
                        output.accept(ModItems.CINDER_ACOLYTE_SPAWN_EGG.get());
                        output.accept(ModItems.FAILED_REMNANT_SPAWN_EGG.get());
                        output.accept(ModItems.FAILED_REMNANT_SPAWN_EGG.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}