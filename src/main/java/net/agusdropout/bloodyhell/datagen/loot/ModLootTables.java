package net.agusdropout.bloodyhell.datagen.loot;

import net.agusdropout.bloodyhell.item.registry.BlasphemiteItems;
import net.agusdropout.bloodyhell.item.registry.DropItems;
import net.agusdropout.bloodyhell.item.registry.FoodItems;
import net.agusdropout.bloodyhell.item.registry.MiscItems;
import net.agusdropout.bloodyhell.item.registry.PlaceableItems;
import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import net.agusdropout.bloodyhell.block.registry.LightBlocks;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.block.registry.MetalBlocks;
import net.agusdropout.bloodyhell.block.registry.MiscBlocks;
import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import net.agusdropout.bloodyhell.block.registry.WoodBlocks;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.custom.Droopvine;
import net.agusdropout.bloodyhell.block.custom.EyeballCropBlock;
import net.agusdropout.bloodyhell.datagen.ModBlockLootTableProvider;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.world.item.Items.SHEARS;

public class ModLootTables extends LootTableProvider {
    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public ModLootTables(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(Entities::new, LootContextParamSets.ENTITY)));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
    }

    public static class Blocks extends ModBlockLootTableProvider {

        @Override
        protected void generate() {
            this.dropSelf(MetalBlocks.SANGUINITE_BLOCK.get());
            this.dropSelf(MetalBlocks.RHNULL_BLOCK);
            this.dropSelf(MiscBlocks.JUMPY_BLOCK.get());
            this.dropSelf(LightBlocks.SOUL_LAMP.get());
            this.dropSelf(MachineBlocks.SANGUINE_CRUCIBLE.get());
            this.dropSelf(MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR.get());
            this.dropSelf(MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get());
            this.dropSelf(MachineBlocks.RHNULL_BLOOD_ENGINE_BLOCK.get());
            this.dropSelf(MachineBlocks.RHNULL_BLOOD_ENGINE_PILLAR_BLOCK.get());
            this.dropSelf(MachineBlocks.BLOOD_ALTAR.get());
            this.dropSelf(MachineBlocks.MAIN_BLOOD_ALTAR.get());
            this.dropOther(MachineBlocks.SANGUINITE_CONDENSER.get(), PlaceableItems.SANGUINITE_CONDENSER_ITEM.get());
            this.dropOther(MachineBlocks.RHNULL_CONDENSER.get(), PlaceableItems.RHNULL_CONDENSER_ITEM.get());
            this.dropSelf(PlantBlocks.BLOOD_SAPLING.get());
            this.dropSelf(WoodBlocks.STRIPPED_BLOOD_WOOD.get());
            this.dropSelf(WoodBlocks.STRIPPED_BLOOD_LOG.get());
            this.dropSelf(WoodBlocks.BLOOD_WOOD.get());
            this.dropSelf(WoodBlocks.BLOOD_LOG.get());
            this.dropSelf(MiscBlocks.BLEEDING_BLOCK.get());
            this.dropSelf(WoodBlocks.SOUL_PLANKS.get());
            this.dropSelf(PlantBlocks.SOUL_SAPLING.get());
            this.dropSelf(WoodBlocks.STRIPPED_SOUL_WOOD.get());
            this.dropSelf(WoodBlocks.STRIPPED_SOUL_LOG.get());
            this.dropSelf(WoodBlocks.SOUL_WOOD.get());
            this.dropSelf(WoodBlocks.SOUL_LOG.get());
            this.dropSelf(PlantBlocks.HANGING_SOUL_TREE_LEAVES.get());
            this.dropSelf(MiscBlocks.EYEBALLSHELL_SNAIL_GOO.get());
            this.dropSelf(MiscBlocks.EYEBALLSHELL_SNAIL_GOO_BLOCK.get());
            this.dropSelf(SoilBlocks.BLOOD_GRASS_BLOCK.get());
            this.dropSelf(SoilBlocks.BLOOD_DIRT_BLOCK.get());
            this.dropSelf(SoilBlocks.BLOODDIRT_FARMLAND.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_BLOCK.get());

            this.dropOther(MachineBlocks.SANGUINITE_PIPE.get(),PlaceableItems.SANGUINITE_PIPE_ITEM.get());
            this.dropOther(MachineBlocks.RHNULL_PIPE.get(),PlaceableItems.RHNULL_PIPE_ITEM.get());
            this.dropSelf(MachineBlocks.SANGUINITE_TANK.get());
            this.dropSelf(MachineBlocks.RHNULL_TANK.get());
            this.dropOther(MachineBlocks.SANGUINITE_BLOOD_HARVESTER.get(),PlaceableItems.SANGUINITE_BLOOD_HARVESTER_ITEM.get());


            this.createSilkTouchOrShearsDispatchTable(PlantBlocks.VORACIOUS_MUSHROOM_BLOCK.get(), PlaceableItems.VORACIOUS_MUSHROOM.get());
            this.createSilkTouchOrShearsDispatchTable(PlantBlocks.CRIMSON_LURE_MUSHROOM_BLOCK.get(), PlaceableItems.CRIMSON_LURE_MUSHROOM.get());
            this.dropOther(MachineBlocks.UNKNOWN_PORTAL_BLOCK.get(),PlaceableItems.UNKNOWN_PORTAL_ITEM.get());
            this.dropSelf(MachineBlocks.SANGUINITE_INFUSOR.get());





            this.dropSelf(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get());
            this.dropSelf(StoneBlocks.POLISHED_BLOODY_STONE_SLAB.get());
            this.dropSelf(StoneBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get());
            this.dropSelf(StoneBlocks.POLISHED_BLOODY_STONE_FENCE.get());
            this.dropSelf(StoneBlocks.POLISHED_BLOODY_STONE_WALL.get());
            this.dropSelf(StoneBlocks.POLISHED_BLOODY_STONE_STAIRS.get());

            this.dropSelf(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_TILES_SLAB.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_FENCE_TILES_GATE.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_TILES_FENCE.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_TILES_WALL.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_TILES_STAIRS.get());

            this.dropSelf(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get());
            this.dropSelf(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get());
            this.dropSelf(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS.get());
            this.dropSelf(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get());
            this.dropSelf(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN.get());
            this.dropSelf(StoneBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS.get());
            this.dropSelf(StoneBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS.get());
            this.dropSelf(LightBlocks.ANCIENT_BLOODY_LAMP.get());
            this.dropOther(LightBlocks.ANCIENT_TORCH_BLOCK.get(),PlaceableItems.ANCIENT_TORCH_ITEM.get());
            this.dropSelf(MiscBlocks.ANCIENT_BLOOD_CAPSULE.get());
            this.dropSelf(MachineBlocks.SANGUINE_LAPIDARY.get());




            this.dropSelf(StoneBlocks.BLOODY_STONE_SLAB.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_FENCE_GATE.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_FENCE.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_WALL.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_STAIRS.get());

            this.dropSelf(StoneBlocks.BLOODY_STONE_BRICKS.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_BRICKS_SLAB.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_FENCE_BRICKS_GATE.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_BRICKS_FENCE.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_BRICKS_WALL.get());
            this.dropSelf(StoneBlocks.BLOODY_STONE_BRICKS_STAIRS.get());

            this.dropSelf(WoodBlocks.BLOOD_PLANKS.get());
            this.dropSelf(WoodBlocks.BLOOD_PLANKS_STAIRS.get());
            this.dropSelf(WoodBlocks.BLOOD_PLANKS_SLAB.get());
            this.dropSelf(WoodBlocks.BLOOD_PLANKS_FENCE.get());
            this.dropSelf(WoodBlocks.BLOOD_PLANKS_FENCE_GATE.get());


            this.dropSelf(PlantBlocks.BLOOD_LILY_BLOCK.get());
            this.dropSelf(PlantBlocks.SMALL_BLOOD_SAPLING.get());
            this.dropSelf(PlantBlocks.BLOOD_WALL_MUSHROOM_BLOCK.get());
            this.dropSelf(PlantBlocks.GIANT_BLOOD_SAPLING.get());
            this.dropSelf(PlantBlocks.HANGING_BLOOD_TREE_LEAVES.get());
            this.dropSelf(LightBlocks.BLOOD_GLOW_STONE.get());
            this.dropSelf(PlantBlocks.DROOPVINE.get());
            this.dropSelf(PlantBlocks.DROOPVINE_PLANT.get());
            this.dropSelf(PlantBlocks.BLOOD_SCRAPPER_PLANT.get());
            this.dropSelf(PlantBlocks.BLOOD_SCRAPPER_PLANT_SAPLING.get());
            this.dropSelf(LightBlocks.GLOWING_CRYSTAL.get());
            this.dropSelf(LightBlocks.GLOWING_CRYSTAL_GLASS_BLOCK.get());
            this.dropSelf(LightBlocks.GLOWING_CRYSTAL_LANTERN.get());
            this.dropSelf(LightBlocks.BLOOD_GLOWING_CHAINS_BLOCK.get());
            this.dropSelf(WoodBlocks.BLOOD_WOOD_CHEST.get());
            this.dropOther(PlantBlocks.DROOPVINE.get(),FoodItems.GLOW_FRUIT.get());
            this.dropOther(PlantBlocks.DROOPVINE_PLANT.get(),FoodItems.GLOW_FRUIT.get());

            this.dropSelf(SoilBlocks.BLASPHEMOUS_SAND_BLOCK.get());
            this.dropSelf(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK.get());
            this.dropSelf(SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK.get());
            this.dropSelf(SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK.get());
            this.dropSelf(SandstoneBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK.get());
            this.dropSelf(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get());
            this.dropSelf(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get());
            this.dropSelf(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get());
            this.dropSelf(PlantBlocks.CINDER_BLOOM_CACTUS_ROOT.get());
            this.dropSelf(PlantBlocks.CINDER_BLOOM_CACTUS_CON.get());
            this.dropSelf(PlantBlocks.CINDER_BLOOM_CACTUS_CENTER.get());
            this.dropSelf(PlantBlocks.CINDER_BLOOM_CACTUS_FLOWER.get());
            this.dropSelf(SandstoneBlocks.ERODED_BLASPHEMOUS_SANDSTONE.get());
            this.dropSelf(SandstoneBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE.get());
            this.dropSelf(SandstoneBlocks.CRACKED_BLASPHEMOUS_SANDSTONE.get());
            this.dropSelf(MetalBlocks.BLASPHEMITE_BLOCK.get());
            this.dropSelf(PlantBlocks.SPIKY_GRASS.get());
            this.dropSelf(PlantBlocks.ROUNDED_GRASS.get());
            this.dropSelf(PlantBlocks.STING_FLOWER.get());
            this.add(MiscBlocks.DECORATED_POT_BLOCK.get(), createPotDrops(MiscBlocks.DECORATED_POT_BLOCK.get()));
            this.dropOther(MiscBlocks.SELIORA_RESTING_BLOCK.get(),PlaceableItems.SELIORA_RESTING_ITEM.get());
            this.dropSelf(MiscBlocks.FORBIDDEN_BOOKSHELF_BLOCK.get());
            this.dropSelf(LightBlocks.STAR_LAMP_BLOCK.get());
            this.add(MetalBlocks.BLASPHEMITE_ORE.get(), block -> createOreDrop(MetalBlocks.BLASPHEMITE_ORE.get(), BlasphemiteItems.RAW_BLASPHEMITE.get()));



            ore(MetalBlocks.SANGUINITE_ORE, SanguiniteItems.RAW_SANGUINITE);





            LootItemCondition.Builder lootitemcondition$builder2 = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(PlantBlocks.EYEBALL_CROP.get())
                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(EyeballCropBlock.AGE, 6))
                    .or(LootItemBlockStatePropertyCondition
                            .hasBlockStateProperties(PlantBlocks.EYEBALL_CROP.get())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(EyeballCropBlock.AGE, 6)));


            this.add(PlantBlocks.EYEBALL_CROP.get(), createCropDrops(PlantBlocks.EYEBALL_CROP.get(), FoodItems.EYEBALL.get(),
                    PlaceableItems.EYEBALL_SEED.get(), lootitemcondition$builder2));

            this.add(WoodBlocks.BLOOD_LEAVES.get(), block ->
                    createLeavesDrops(block, PlantBlocks.BLOOD_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
            this.add(WoodBlocks.SOUL_LEAVES.get(), block ->
                    createLeavesDrops(block, PlantBlocks.BLOOD_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));



            this.createGrassDrops(PlantBlocks.BLOOD_GRASS, PlaceableItems.EYEBALL_SEED.get());

            this.createPlantsDrops(PlantBlocks.BLOOD_PETALS, PlantBlocks.BLOOD_PETALS.get().asItem());
            this.createSilkTouchOrShearsDispatchTable(PlantBlocks.LIGHT_MUSHROOM_BLOCK.get(), FoodItems.GLOW_MUSHROOM.get());
            this.createPlantsDrops(MiscBlocks.SMALL_ROCKS, MiscBlocks.SMALL_ROCKS.get().asItem());
            this.createPlantsDrops(PlantBlocks.BLOOD_FLOWER, PlantBlocks.BLOOD_FLOWER.get().asItem());
            this.createPlantsDrops(PlantBlocks.BLOOD_BUSH, PlantBlocks.BLOOD_BUSH.get().asItem());


            this.add(PlantBlocks.POTTED_BLOOD_FLOWER.get(), createPotFlowerItemTable(PlantBlocks.BLOOD_FLOWER.get()));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
        }
    }

    private static LootTable.Builder droopvine(Block block) {
        return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.GLOW_FRUIT.get())).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(Droopvine.GLOWY, true))));
    }
    public static class Entities extends EntityLootSubProvider {

        public Entities() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            // --- BOSSES & ELITES ---
            this.add(ModEntityTypes.UNKNOWN_LANTERN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            this.add(ModEntityTypes.SELIORA.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(DropItems.SELIORA_ESSENCE.get()))));

            this.add(ModEntityTypes.RITEKEEPER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(4))
                            .add(LootItem.lootTableItem(DropItems.RITEKEEPER_HEART.get()))));

            this.add(ModEntityTypes.BLASPHEMOUS_MALFORMATION.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            // --- HOSTILE MONSTERS ---
            this.add(ModEntityTypes.BLOODTHIRSTYBEAST.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(DropItems.AUREAL_REVENANT_DAGGER.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

            this.add(ModEntityTypes.OMEN_GAZER_ENTITY.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(DropItems.AUREAL_REVENANT_DAGGER.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

            this.add(ModEntityTypes.VEINRAVER_ENTITY.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(DropItems.VEINREAVER_HORN.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

            this.add(ModEntityTypes.HORNED_WORM.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            this.add(ModEntityTypes.VEIL_STALKER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            this.add(ModEntityTypes.ONI.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(MiscItems.SANGUINE_CRUCIBLE_CORE.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

            this.add(ModEntityTypes.VESPER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            this.add(ModEntityTypes.CINDER_ACOLYTE.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(DropItems.CINDER_ACOLYTE_FAINTED_EMBER.get())
                                    .when(LootItemRandomChanceCondition.randomChance(0.45F))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

            this.add(ModEntityTypes.FAILED_REMNANT.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(DropItems.FAILED_REMNANT_ASHES.get())
                                    .when(LootItemRandomChanceCondition.randomChance(0.45F))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

            // --- PASSIVE & NEUTRAL CREATURES ---
            this.add(ModEntityTypes.BLOODPIG.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.GOREHOG_RAW_STEAK.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))))
                    .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));

            this.add(ModEntityTypes.BLOOD_SEEKER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DropItems.VEINREAVER_HORN.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));

            this.add(ModEntityTypes.CRIMSON_RAVEN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(FoodItems.SCARLET_RAW_CHICKEN.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))
                            .add(LootItem.lootTableItem(DropItems.SCARLET_FEATHER.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));

            this.add(ModEntityTypes.EYESHELL_SNAIL.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DropItems.CRIMSON_SHELL.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));

            this.add(ModEntityTypes.SCARLETSPECKLED_FISH.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DropItems.CRIMSON_SHELL.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));

            this.add(ModEntityTypes.BLOODY_SOUL_ENTITY.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            this.add(ModEntityTypes.CORRUPTED_BLOODY_SOUL_ENTITY.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            this.add(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            // --- MINIONS & SUMMONS ---
            this.add(ModEntityTypes.FAILED_SON_OF_THE_UNKNOWN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

            this.add(ModEntityTypes.WEEPING_OCULAR.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));
            this.add(ModEntityTypes.GRAVE_WALKER_ENTITY.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));
            this.add(ModEntityTypes.BURDEN_OF_THE_UNKNOWN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));
            this.add(ModEntityTypes.BASTION_OF_THE_UNKNOWN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));


            // --- OTHER ENTITIES ---
            this.add(ModEntityTypes.CRAWLING_DELUSION.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))));

        }


        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return ModEntityTypes.ENTITY_TYPES.getEntries().stream().map(Supplier::get);
        }
    }

}
