package net.agusdropout.bloodyhell.screen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
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

                        // --- Sanguinite Items ---
                        output.accept(ModItems.RAW_SANGUINITE.get());
                        output.accept(ModItems.SANGUINITE.get());
                        output.accept(ModItems.SANGUINITE_NUGGET.get());
                        output.accept(ModItems.SANGUINITE_PICKAXE.get());
                        output.accept(ModItems.SANGUINITE_SWORD.get());
                        output.accept(ModItems.SANGUINITE_AXE.get());
                        output.accept(ModItems.SANGUINITE_SHOVEL.get());
                        output.accept(ModItems.SANGUINITE_HOE.get());
                        output.accept(ModItems.BLOOD_BOW.get());
                        output.accept(ModItems.BLOOD_ARROW.get());
                        output.accept(ModItems.BLOOD_HELMET.get());
                        output.accept(ModItems.BLOOD_CHESTPLATE.get());
                        output.accept(ModItems.BLOOD_LEGGINGS.get());
                        output.accept(ModItems.BLOOD_BOOTS.get());
                        output.accept(ModItems.BLOOD_BUCKET.get());
                        output.accept(ModItems.CORRUPTED_BLOOD_BUCKET.get());
                        output.accept(ModItems.VISCOUS_BLASPHEMY_BUCKET.get());
                        output.accept(ModItems.VISCERAL_BLOOD_BUCKET.get());
                        output.accept(ModItems.SANGUINITE_PIPE_ITEM.get());
                        output.accept(ModBlocks.SANGUINITE_INFUSOR.get());
                        output.accept(ModBlocks.SANGUINITE_TANK.get());
                        output.accept(ModItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get());
                        output.accept(ModItems.BLOOD_SCYTHE.get());

                        // --- Rhnull Items ---
                        output.accept(ModItems.RHNULL.get());
                        output.accept(ModItems.RHNULL_NUGGET.get());
                        output.accept(ModBlocks.RHNULL_BLOCK.get());
                        output.accept(ModItems.RHNULL_PICKAXE.get());
                        output.accept(ModItems.RHNULL_SWORD.get());
                        output.accept(ModItems.RHNULL_AXE.get());
                        output.accept(ModItems.RHNULL_SHOVEL.get());
                        output.accept(ModItems.RHNULL_HOE.get());
                        output.accept(ModItems.RHNULL_HELMET.get());
                        output.accept(ModItems.RHNULL_CHESTPLATE.get());
                        output.accept(ModItems.RHNULL_LEGGINGS.get());
                        output.accept(ModItems.RHNULL_BOOTS.get());

                        // --- Spawn Eggs ---
                        output.accept(ModItems.BLOODTHIRSTYBEAST_SPAWN_EGG.get());
                        output.accept(ModItems.BLOOD_SEEKER_SPAWN_EGG.get());
                        output.accept(ModItems.CRIMSON_RAVEN_SPAWN_EGG.get());
                        output.accept(ModItems.EYESHELLSNAIL_SPAWN_EGG.get());
                        output.accept(ModItems.BLOOD_PIG_SPAWN_EGG.get());
                        output.accept(ModItems.SCARLETSPECKLED_FISH_SPAWN_EGG.get());
                        output.accept(ModItems.OMEN_GAZER_ENTITY_SPAWN_EGG.get());
                        output.accept(ModItems.VEINRAVER_ENTITY_SPAWN_EGG.get());
                        output.accept(ModItems.OFFSPRING_OF_THE_UNKNOWN_SPAWN_EGG.get());
                        output.accept(ModItems.BLASPHEMOUS_MALFORMATION_SPAWN_EGG.get());
                        output.accept(ModItems.SELIORA_SPAWN_EGG.get());
                        output.accept(ModItems.HORNED_WORM_SPAWN_EGG.get());
                        output.accept(ModItems.VEIL_STALKER_SPAWN_EGG.get());
                        output.accept(ModItems.GRAVE_WALKER_SPAWN_EGG.get());
                        output.accept(ModItems.CYCLOPS_ENTITY_SPAWN_EGG.get());
                        output.accept(ModItems.CINDER_ACOLYTE_SPAWN_EGG.get());
                        output.accept(ModItems.FAILED_REMNANT_SPAWN_EGG.get());

                        // --- Food Items ---
                        output.accept(ModItems.Eyeball.get());
                        output.accept(ModItems.Eyeball_seed.get());
                        output.accept(ModItems.GLOW_FRUIT.get());
                        output.accept(ModItems.GLOW_MUSHROOM.get());
                        output.accept(ModBlocks.VORACIOUS_MUSHROOM_BLOCK.get());
                        output.accept(ModBlocks.VISCERAL_INFECTED_VEIN.get());
                        output.accept(ModItems.SCARLET_RAW_CHICKEN.get());
                        output.accept(ModItems.SCARLET_COOKED_CHICKEN.get());
                        output.accept(ModItems.GOREHOG_RAW_STEAK.get());
                        output.accept(ModItems.GOREHOG_COOKED_STEAK.get());

                        // --- Mob Drops ---
                        output.accept(ModItems.AUREAL_REVENANT_DAGGER.get());
                        output.accept(ModItems.VEINREAVER_HORN.get());
                        output.accept(ModItems.CRIMSON_SHELL.get());
                        output.accept(ModItems.SCARLET_FEATHER.get());
                        output.accept(ModItems.RITEKEEPER_HEART.get());
                        output.accept(ModItems.FAILED_REMNANT_ASHES.get());
                        output.accept(ModItems.CINDER_ACOLYTE_FAINTED_EMBER.get());


                        // --- Misc Items ---
                        output.accept(ModItems.BLOODY_SOUL_DUST.get());
                        output.accept(ModItems.MATERIALIZED_SOUL.get());
                        output.accept(ModItems.CRIMSON_IDOL_COIN.get());
                        output.accept(ModItems.CHALICE_OF_THE_DAMMED.get());
                        output.accept(ModItems.Eight_ball.get());
                        output.accept(ModItems.DIRTY_BLOOD_FLOWER.get());
                        output.accept(ModItems.BLOOD_LILY.get());
                        output.accept(ModItems.SANGUINE_CRUCIBLE_CORE.get());

                        // --- Sanguinite (Blocks) ---
                        output.accept(ModBlocks.SANGUINE_CRUCIBLE.get());
                        output.accept(ModBlocks.SANGUINITE_BLOCK.get());

                        // --- Ores ---
                        output.accept(ModBlocks.SANGUINITE_ORE.get());
                        output.accept(ModBlocks.Jumpy_Block.get());

                        // --- Vegetation ---
                        output.accept(ModBlocks.BLOOD_SAPLING.get());
                        output.accept(ModBlocks.SOUL_SAPLING.get());
                        output.accept(ModBlocks.HANGING_BLOOD_TREE_LEAVES.get());
                        output.accept(ModBlocks.HANGING_SOUL_TREE_LEAVES.get());
                        output.accept(ModBlocks.BLOOD_FLOWER.get());
                        output.accept(ModBlocks.BLOOD_GRASS.get());
                        output.accept(ModBlocks.BLOOD_BUSH.get());
                        output.accept(ModBlocks.BLOOD_PETALS.get());
                        output.accept(ModBlocks.BLOOD_WALL_MUSHROOM_BLOCK.get());
                        output.accept(ModBlocks.LIGHT_MUSHROOM_BLOCK.get());

                        // --- Dirt ---
                        output.accept(ModBlocks.BLOOD_GRASS_BLOCK.get());
                        output.accept(ModBlocks.BLOOD_DIRT_BLOCK.get());
                        output.accept(ModBlocks.BLOOD_SCRAPPER_PLANT.get());

                        // --- Bloody Stone ---
                        output.accept(ModBlocks.BLOODY_STONE_BLOCK.get());
                        output.accept(ModBlocks.BLOODY_STONE_STAIRS.get());
                        output.accept(ModBlocks.BLOODY_STONE_WALL.get());
                        output.accept(ModBlocks.BLOODY_STONE_FENCE.get());
                        output.accept(ModBlocks.BLOODY_STONE_FENCE_GATE.get());
                        output.accept(ModBlocks.BLOODY_STONE_SLAB.get());

                        // --- Polished Bloody Stone ---
                        output.accept(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get());
                        output.accept(ModBlocks.POLISHED_BLOODY_STONE_STAIRS.get());
                        output.accept(ModBlocks.POLISHED_BLOODY_STONE_WALL.get());
                        output.accept(ModBlocks.POLISHED_BLOODY_STONE_FENCE.get());
                        output.accept(ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get());
                        output.accept(ModBlocks.POLISHED_BLOODY_STONE_SLAB.get());

                        // --- Bloody Stone Tiles ---
                        output.accept(ModBlocks.BLOODY_STONE_TILES_BLOCK.get());
                        output.accept(ModBlocks.BLOODY_STONE_TILES_STAIRS.get());
                        output.accept(ModBlocks.BLOODY_STONE_TILES_WALL.get());
                        output.accept(ModBlocks.BLOODY_STONE_TILES_FENCE.get());
                        output.accept(ModBlocks.BLOODY_STONE_FENCE_TILES_GATE.get());
                        output.accept(ModBlocks.BLOODY_STONE_TILES_SLAB.get());

                        // --- Bloody Stone Bricks ---
                        output.accept(ModBlocks.BLOODY_STONE_BRICKS.get());
                        output.accept(ModBlocks.BLOODY_STONE_BRICKS_STAIRS.get());
                        output.accept(ModBlocks.BLOODY_STONE_BRICKS_WALL.get());
                        output.accept(ModBlocks.BLOODY_STONE_BRICKS_FENCE.get());
                        output.accept(ModBlocks.BLOODY_STONE_FENCE_BRICKS_GATE.get());
                        output.accept(ModBlocks.BLOODY_STONE_BRICKS_SLAB.get());

                        // --- Ancient Bloody Bricks ---
                        output.accept(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get());
                        output.accept(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS.get());
                        output.accept(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get());
                        output.accept(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get());
                        output.accept(ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN.get());
                        output.accept(ModBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS.get());
                        output.accept(ModBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS.get());
                        output.accept(ModBlocks.ANCIENT_BLOODY_LAMP.get());
                        output.accept(ModItems.ANCIENT_TORCH_ITEM.get());
                        output.accept(ModBlocks.ANCIENT_BLOOD_CAPSULE.get());


                        // --- Wood ---
                        output.accept(ModBlocks.BLOOD_LOG.get());
                        output.accept(ModBlocks.STRIPPED_BLOOD_LOG.get());
                        output.accept(ModBlocks.BLOOD_PLANKS.get());
                        output.accept(ModBlocks.BLOOD_PLANKS_STAIRS.get());
                        output.accept(ModBlocks.BLOOD_PLANKS_SLAB.get());
                        output.accept(ModBlocks.BLOOD_PLANKS_FENCE.get());
                        output.accept(ModBlocks.BLOOD_PLANKS_FENCE_GATE.get());
                        output.accept(ModBlocks.BLOOD_LEAVES.get());
                        output.accept(ModBlocks.SOUL_LOG.get());
                        output.accept(ModBlocks.STRIPPED_SOUL_LOG.get());
                        output.accept(ModBlocks.SOUL_PLANKS.get());
                        output.accept(ModBlocks.SOUL_LEAVES.get());

                        // --- Glowing ---
                        output.accept(ModBlocks.GLOWING_CRYSTAL.get());
                        output.accept(ModBlocks.GLOWING_CRYSTAL_GLASS_BLOCK.get());
                        output.accept(ModBlocks.GLOWING_CRYSTAL_LANTERN.get());
                        output.accept(ModBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get());
                        output.accept(ModBlocks.SOUL_LAMP.get());
                        output.accept(ModBlocks.BLOOD_GLOW_STONE.get());

                        // --- Mob Generated ---
                        output.accept(ModBlocks.EYEBALLSHELL_SNAIL_GOO_BLOCK.get());
                        output.accept(ModBlocks.EYEBALLSHELL_SNAIL_GOO.get());

                        // --- Misc (Blocks) ---
                        output.accept(ModBlocks.SMALL_ROCKS.get());
                        output.accept(ModBlocks.BLEEDING_BLOCK.get());
                        output.accept(ModBlocks.ONI_STATUE.get());

                        // --- Animated Block Items ---
                        // FIX: We only add ModItems.BLOOD_ALTAR, not ModBlocks.BLOOD_ALTAR
                        // because ModBlocks.BLOOD_ALTAR has no item.
                        output.accept(ModItems.BLOOD_ALTAR.get());

                        // FIX: Same for Main Blood Altar
                        output.accept(ModItems.MAIN_BLOOD_ALTAR.get());

                        // --- Potions ---
                        output.accept(ModItems.BLOOD_FLASK.get());
                        output.accept(ModItems.CORRUPTED_BLOOD_FLASK.get());
                        output.accept(ModItems.FILLED_BLOOD_FLASK.get());
                        output.accept(ModItems.FILLED_RHNULL_BLOOD_FLASK.get());

                        // --- Dagger ---
                        output.accept(ModItems.SACRIFICIAL_DAGGER.get());
                        output.accept(ModItems.HERETIC_SACRIFICIAL_DAGGER.get());

                        // --- Unknown Entity ---
                        output.accept(ModItems.UNKNOWN_ENTITY_FINGER.get());

                        // --- Spell Book ---
                        output.accept(ModItems.BLOOD_FIRE_METEOR_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_FIRE_SOUL_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_SCRATCH_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_SPHERE_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_NOVA_SPELLBOOK.get());
                        output.accept(ModItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get());

                        // --- Crimson Blood Items ---
                        output.accept(ModItems.AMULET_OF_ANCESTRAL_BLOOD.get());
                        output.accept(ModItems.ANCIENT_GEM.get());
                        output.accept(ModItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get());
                        output.accept(ModItems.GREAT_ANCIENT_GEM.get());
                        output.accept(ModItems.CRIMSON_WARD_RING.get());
                        output.accept(ModItems.BLASPHEMOUS_RING.get());



                        // --- Blasphemous Biome ---
                        output.accept(ModItems.BLASPHEMITE.get());
                        output.accept(ModItems.RAW_BLASPHEMITE.get());
                        output.accept(ModItems.BLASPHEMITE_NUGGET.get());
                        output.accept(ModItems.BLASPHEMITE_PICKAXE.get());
                        output.accept(ModItems.BLASPHEMITE_SWORD.get());
                        output.accept(ModItems.BLASPHEMITE_AXE.get());
                        output.accept(ModItems.BLASPHEMITE_SHOVEL.get());
                        output.accept(ModItems.BLASPHEMITE_HOE.get());
                        output.accept(ModBlocks.BLASPHEMOUS_SAND_BLOCK.get());
                        output.accept(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get());
                        output.accept(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get());
                        output.accept(ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(ModBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK.get());
                        output.accept(ModBlocks.CINDER_BLOOM_CACTUS_ROOT.get());
                        output.accept(ModBlocks.CINDER_BLOOM_CACTUS_CON.get());
                        output.accept(ModBlocks.CINDER_BLOOM_CACTUS_CENTER.get());
                        output.accept(ModBlocks.CINDER_BLOOM_CACTUS_FLOWER.get());
                        output.accept(ModBlocks.ERODED_BLASPHEMOUS_SANDSTONE.get());
                        output.accept(ModBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE.get());
                        output.accept(ModBlocks.CRACKED_BLASPHEMOUS_SANDSTONE.get());
                        output.accept(ModBlocks.DECORATED_POT_BLOCK.get());
                        output.accept(ModBlocks.FORBIDDEN_BOOKSHELF_BLOCK.get());
                        output.accept(ModBlocks.STAR_LAMP_BLOCK.get());
                        output.accept(ModBlocks.SPIKY_GRASS.get());
                        output.accept(ModBlocks.ROUNDED_GRASS.get());
                        output.accept(ModBlocks.STING_FLOWER.get());
                        output.accept(ModItems.BLASPHEMOUS_TWIN_DAGGERS.get());
                        output.accept(ModItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get());
                        output.accept(ModItems.BLASPHEMOUS_IMPALER.get());
                        output.accept(ModItems.BLASPHEMITE_HELMET.get());
                        output.accept(ModItems.BLASPHEMITE_CHESTPLATE.get());
                        output.accept(ModItems.BLASPHEMITE_LEGGINGS.get());
                        output.accept(ModItems.BLASPHEMITE_BOOTS.get());
                        output.accept(ModBlocks.BLASPHEMITE_ORE.get());
                        output.accept(ModItems.SELIORA_ESSENCE.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}