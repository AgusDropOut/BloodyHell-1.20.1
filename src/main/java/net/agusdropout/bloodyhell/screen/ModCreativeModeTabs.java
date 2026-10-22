package net.agusdropout.bloodyhell.screen;

import net.agusdropout.bloodyhell.item.registry.BlasphemiteItems;
import net.agusdropout.bloodyhell.item.registry.BloodGearItems;
import net.agusdropout.bloodyhell.item.registry.BucketItems;
import net.agusdropout.bloodyhell.item.registry.DropItems;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.agusdropout.bloodyhell.item.registry.FoodItems;
import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.item.registry.MiscItems;
import net.agusdropout.bloodyhell.item.registry.PlaceableItems;
import net.agusdropout.bloodyhell.item.registry.ReliquaryItems;
import net.agusdropout.bloodyhell.item.registry.RhnullItems;
import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import net.agusdropout.bloodyhell.item.registry.SpawnEggItems;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
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
                    .icon(() -> new ItemStack(SanguiniteItems.SANGUINITE.get()))
                    .title(Component.translatable("creativetab.bloody_hell_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(MiscItems.UNKNOWN_GUIDE_BOOK.get());

                        output.accept(MetalBlocks.SANGUINITE_ORE.get());
                        output.accept(MetalBlocks.BLASPHEMITE_ORE.get());

                        output.accept(SanguiniteItems.RAW_SANGUINITE.get());
                        output.accept(SanguiniteItems.SANGUINITE_NUGGET.get());
                        output.accept(SanguiniteItems.SANGUINITE.get());
                        output.accept(MetalBlocks.SANGUINITE_BLOCK.get());
                        output.accept(RhnullItems.RHNULL_NUGGET.get());
                        output.accept(RhnullItems.RHNULL.get());
                        output.accept(MetalBlocks.RHNULL_BLOCK.get());
                        output.accept(BlasphemiteItems.RAW_BLASPHEMITE.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_NUGGET.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE.get());
                        output.accept(MetalBlocks.BLASPHEMITE_BLOCK.get());

                        output.accept(SanguiniteItems.SANGUINITE_SWORD.get());
                        output.accept(SanguiniteItems.SANGUINITE_PICKAXE.get());
                        output.accept(SanguiniteItems.SANGUINITE_AXE.get());
                        output.accept(SanguiniteItems.SANGUINITE_SHOVEL.get());
                        output.accept(SanguiniteItems.SANGUINITE_HOE.get());

                        output.accept(RhnullItems.RHNULL_SWORD.get());
                        output.accept(RhnullItems.RHNULL_PICKAXE.get());
                        output.accept(RhnullItems.RHNULL_AXE.get());
                        output.accept(RhnullItems.RHNULL_SHOVEL.get());
                        output.accept(RhnullItems.RHNULL_HOE.get());

                        output.accept(BlasphemiteItems.BLASPHEMITE_SWORD.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_PICKAXE.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_AXE.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_SHOVEL.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_HOE.get());

                        output.accept(BloodGearItems.BLOOD_BOW.get());
                        output.accept(BloodGearItems.BLOOD_ARROW.get());
                        output.accept(WeaponItems.SACRIFICIAL_DAGGER.get());
                        output.accept(WeaponItems.HERETIC_SACRIFICIAL_DAGGER.get());

                        output.accept(WeaponItems.BLASPHEMOUS_TWIN_DAGGERS.get());
                        output.accept(WeaponItems.BLASPHEMOUS_HULKING_MASS_OF_IRON.get());
                        output.accept(WeaponItems.BLASPHEMOUS_IMPALER.get());

                        output.accept(BloodGearItems.BLOOD_HELMET.get());
                        output.accept(BloodGearItems.BLOOD_CHESTPLATE.get());
                        output.accept(BloodGearItems.BLOOD_LEGGINGS.get());
                        output.accept(BloodGearItems.BLOOD_BOOTS.get());

                        output.accept(RhnullItems.RHNULL_HELMET.get());
                        output.accept(RhnullItems.RHNULL_CHESTPLATE.get());
                        output.accept(RhnullItems.RHNULL_LEGGINGS.get());
                        output.accept(RhnullItems.RHNULL_BOOTS.get());

                        output.accept(BlasphemiteItems.BLASPHEMITE_HELMET.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_CHESTPLATE.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_LEGGINGS.get());
                        output.accept(BlasphemiteItems.BLASPHEMITE_BOOTS.get());

                        output.accept(GemItems.AMULET_OF_ANCESTRAL_BLOOD.get());
                        output.accept(GemItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get());
                        output.accept(GemItems.CRIMSON_WARD_RING.get());
                        output.accept(GemItems.BLASPHEMOUS_RING.get());
                        output.accept(MiscItems.CHALICE_OF_THE_DAMMED.get());
                        output.accept(MiscItems.CRIMSON_IDOL_COIN.get());
                        output.accept(DropItems.BLOOD_ECHO_SHARD.get());
                        output.accept(DropItems.NAMELESS_ECHO_SHARD.get());

                        output.accept(SpellBookItems.BLOOD_SCRATCH_SPELLBOOK.get());
                        output.accept(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get());
                        output.accept(SpellBookItems.BLOOD_NOVA_SPELLBOOK.get());
                        output.accept(SpellBookItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get());
                        output.accept(SpellBookItems.BLOOD_FIRE_METEOR_SPELLBOOK.get());
                        output.accept(SpellBookItems.BLOOD_FIRE_COLUMM_SPELLBOOK.get());
                        output.accept(SpellBookItems.BLOOD_FIRE_SOUL_SPELLBOOK.get());
                        output.accept(SpellBookItems.RHNULL_IMPALERS_SPELLBOOK.get());
                        output.accept(SpellBookItems.RHNULL_HEAVY_SWORD_SPELLBOOK.get());
                        output.accept(SpellBookItems.RHNULL_GOLDEN_THRONE_SPELLBOOK.get());
                        output.accept(SpellBookItems.RHNULL_ORB_EMITTER_SPELLBOOK.get());
                        output.accept(SpellBookItems.THE_OMNITOME.get());

                        output.accept(StaffItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get());
                        output.accept(StaffItems.STAFF_OF_THE_GOLDEN_SORROW.get());
                        output.accept(StaffItems.STAFF_OF_SCARLET_CINDER.get());

                        output.accept(ReliquaryItems.RELIQUARY.get());
                        output.accept(ReliquaryItems.ANCIENT_OCULAR_LENSE.get());
                        output.accept(ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE.get());
                        output.accept(ReliquaryItems.MARK_OF_THE_RESTLESS_SLUMBER.get());
                        output.accept(ReliquaryItems.RUNE_OF_THE_RAVENOUS_GAZE.get());
                        output.accept(ReliquaryItems.OMEN_OF_THE_CRUSHING_TOLL.get());
                        output.accept(ReliquaryItems.SEAL_OF_THE_HOLLOW_BULWARK.get());

                        output.accept(GemItems.PURE_BLOOD_GEM.get());
                        output.accept(GemItems.TANZARINE_BLOOD_GEM.get());
                        output.accept(GemItems.AVENTURINE_BLOOD_GEM.get());
                        output.accept(GemItems.CITRINE_BLOOD_GEM.get());
                        output.accept(GemItems.ANCIENT_GEM.get());
                        output.accept(GemItems.GREAT_ANCIENT_GEM.get());
                        output.accept(GemItems.ANCIENT_RHNULL_GEM.get());
                        output.accept(GemItems.GREAT_ANCIENT_RHNULL_GEM.get());
                        output.accept(GemItems.ANCIENT_BLASPHEMOUS_GEM.get());
                        output.accept(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get());

                        output.accept(GemItems.SANGUINITE_GEM_FRAME.get());
                        output.accept(GemItems.SANGUINITE_GREAT_GEM_FRAME.get());
                        output.accept(GemItems.RHNULL_GEM_FRAME.get());
                        output.accept(GemItems.RHNULL_GREAT_GEM_FRAME.get());

                        output.accept(MachineBlocks.BLOOD_ALTAR.get());
                        output.accept(MachineBlocks.MAIN_BLOOD_ALTAR.get());
                        output.accept(PlaceableItems.BLASPHEMOUS_BLOOD_ALTAR_ITEM.get());
                        output.accept(PlaceableItems.MAIN_BLASPHEMOUS_BLOOD_ALTAR_ITEM.get());
                        output.accept(MachineBlocks.SANGUINE_LAPIDARY.get());
                        output.accept(PlaceableItems.SANGUINITE_CONDENSER_ITEM.get());
                        output.accept(PlaceableItems.RHNULL_CONDENSER_ITEM.get());
                        output.accept(MachineBlocks.SANGUINITE_INFUSOR.get());
                        output.accept(PlaceableItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get());
                        output.accept(MachineBlocks.SANGUINITE_TANK.get());
                        output.accept(MachineBlocks.RHNULL_TANK.get());
                        output.accept(PlaceableItems.SANGUINITE_PIPE_ITEM.get());
                        output.accept(PlaceableItems.RHNULL_PIPE_ITEM.get());
                        output.accept(PlaceableItems.UNKNOWN_PORTAL_ITEM.get());
                        output.accept(PlaceableItems.RHNULL_BLOOD_ENGINE_ITEM.get());
                        output.accept(PlaceableItems.RHNULL_BLOOD_ENGINE_PILLAR_ITEM.get());

                        output.accept(BucketItems.BLOOD_BUCKET.get());
                        output.accept(BucketItems.CORRUPTED_BLOOD_BUCKET.get());
                        output.accept(BucketItems.VISCERAL_BLOOD_BUCKET.get());
                        output.accept(BucketItems.VISCOUS_BLASPHEMY_BUCKET.get());

                        output.accept(FlaskItems.BLOOD_FLASK.get());
                        output.accept(FlaskItems.FILLED_BLOOD_FLASK.get());
                        output.accept(FlaskItems.CORRUPTED_BLOOD_FLASK.get());
                        output.accept(FlaskItems.FILLED_RHNULL_BLOOD_FLASK.get());
                        output.accept(FlaskItems.FILLED_VISCOUS_BLASPHEMY_FLASK.get());

                        output.accept(MiscItems.ROOTLET_POWDER.get());

                        output.accept(MiscItems.BLOODY_SOUL_DUST.get());
                        output.accept(DropItems.AUREAL_REVENANT_DAGGER.get());
                        output.accept(DropItems.VEINREAVER_HORN.get());
                        output.accept(DropItems.CRIMSON_SHELL.get());
                        output.accept(DropItems.SCARLET_FEATHER.get());
                        output.accept(WeaponItems.UNKNOWN_ENTITY_FINGER.get());
                        output.accept(BlasphemiteItems.BLASPHEMOUS_EYE.get());
                        output.accept(MiscItems.GAZE_OF_THE_UNKNOWN.get());
                        output.accept(MiscItems.NAMELESS_WHISPER.get());
                        output.accept(DropItems.SELIORA_ESSENCE.get());
                        output.accept(DropItems.RITEKEEPER_HEART.get());
                        output.accept(DropItems.CINDER_ACOLYTE_FAINTED_EMBER.get());
                        output.accept(DropItems.FAILED_REMNANT_ASHES.get());

                        output.accept(FoodItems.SCARLET_RAW_CHICKEN.get());
                        output.accept(FoodItems.SCARLET_COOKED_CHICKEN.get());
                        output.accept(FoodItems.GOREHOG_RAW_STEAK.get());
                        output.accept(FoodItems.GOREHOG_COOKED_STEAK.get());
                        output.accept(FoodItems.EYEBALL.get());
                        output.accept(PlaceableItems.EYEBALL_SEED.get());
                        output.accept(FoodItems.GLOW_FRUIT.get());

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
                        output.accept(PlantBlocks.BLOOD_GRASS.get());
                        output.accept(PlantBlocks.BLOOD_BUSH.get());
                        output.accept(PlantBlocks.BLOOD_PETALS.get());
                        output.accept(PlaceableItems.BLOOD_LILY.get());
                        output.accept(PlantBlocks.SPIKY_GRASS.get());
                        output.accept(PlantBlocks.ROUNDED_GRASS.get());
                        output.accept(PlantBlocks.STING_FLOWER.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_CON.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get());
                        output.accept(PlantBlocks.CINDER_BLOOM_CACTUS_FLOWER.get());

                        output.accept(PlaceableItems.BLOOD_GEM_SPROUT_SEED.get());
                        output.accept(FoodItems.GLOW_MUSHROOM.get());
                        output.accept(PlantBlocks.BLOOD_WALL_MUSHROOM_BLOCK.get());
                        output.accept(PlaceableItems.VORACIOUS_MUSHROOM.get());
                        output.accept(PlaceableItems.CRIMSON_LURE_MUSHROOM.get());
                        output.accept(PlantBlocks.VISCERAL_INFECTED_VEIN.get());

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

                        output.accept(LightBlocks.GLOWING_CRYSTAL.get());
                        output.accept(LightBlocks.GLOWING_CRYSTAL_GLASS_BLOCK.get());
                        output.accept(LightBlocks.GLOWING_CRYSTAL_LANTERN.get());
                        output.accept(LightBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get());
                        output.accept(LightBlocks.SOUL_LAMP.get());
                        output.accept(LightBlocks.BLOOD_GLOW_STONE.get());
                        output.accept(LightBlocks.ANCIENT_BLOODY_LAMP.get());
                        output.accept(PlaceableItems.ANCIENT_TORCH_ITEM.get());
                        output.accept(MiscBlocks.ANCIENT_BLOOD_CAPSULE.get());
                        output.accept(LightBlocks.STAR_LAMP_BLOCK.get());
                        output.accept(MiscBlocks.DECORATED_POT_BLOCK.get());
                        output.accept(MiscBlocks.FORBIDDEN_BOOKSHELF_BLOCK.get());
                        output.accept(MiscBlocks.TOMB_BLOCK.get());
                        output.accept(MiscBlocks.SMALL_ROCKS.get());
                        output.accept(MiscBlocks.BLEEDING_BLOCK.get());
                        output.accept(MiscBlocks.EYEBALLSHELL_SNAIL_GOO_BLOCK.get());
                        output.accept(MiscBlocks.EYEBALLSHELL_SNAIL_GOO.get());

                        output.accept(SpawnEggItems.BLOOD_SEEKER_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.CRIMSON_RAVEN_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.EYESHELLSNAIL_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.BLOOD_PIG_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.SCARLETSPECKLED_FISH_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.OMEN_GAZER_ENTITY_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.OFFSPRING_OF_THE_UNKNOWN_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.BLASPHEMOUS_MALFORMATION_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.SELIORA_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.HORNED_WORM_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.VEIL_STALKER_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.GRAVE_WALKER_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.CYCLOPS_ENTITY_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.CINDER_ACOLYTE_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.FAILED_REMNANT_SPAWN_EGG.get());
                        output.accept(SpawnEggItems.FAILED_REMNANT_SPAWN_EGG.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}