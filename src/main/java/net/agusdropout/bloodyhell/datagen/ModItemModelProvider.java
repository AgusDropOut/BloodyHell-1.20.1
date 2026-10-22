package net.agusdropout.bloodyhell.datagen;

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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class ModItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BloodyHell.MODID, existingFileHelper);
    }
    @Override
    protected void registerModels() {

        normalItem(MiscItems.UNKNOWN_GUIDE_BOOK);

        toolItem(SanguiniteItems.SANGUINITE_SWORD);
        toolItem(SanguiniteItems.SANGUINITE_PICKAXE);
        toolItem(SanguiniteItems.SANGUINITE_AXE);
        toolItem(SanguiniteItems.SANGUINITE_SHOVEL);
        toolItem(SanguiniteItems.SANGUINITE_HOE);
        normalItem(BloodGearItems.BLOOD_HELMET);
        normalItem(BloodGearItems.BLOOD_CHESTPLATE);
        normalItem(BloodGearItems.BLOOD_LEGGINGS);
        normalItem(BloodGearItems.BLOOD_BOOTS);
        normalItem(SanguiniteItems.SANGUINITE);
        normalItem(SanguiniteItems.SANGUINITE_NUGGET);
        normalItem(SanguiniteItems.RAW_SANGUINITE);

        normalItem(GemItems.PURE_BLOOD_GEM);
        normalItem(GemItems.AVENTURINE_BLOOD_GEM);
        normalItem(GemItems.CITRINE_BLOOD_GEM);
        normalItem(GemItems.TANZARINE_BLOOD_GEM);

        normalItem(BucketItems.BLOOD_BUCKET);
        normalItem(BucketItems.CORRUPTED_BLOOD_BUCKET);
        normalItem(BucketItems.VISCOUS_BLASPHEMY_BUCKET);
        normalItem(BucketItems.VISCERAL_BLOOD_BUCKET);

        toolItem(RhnullItems.RHNULL_SWORD);
        toolItem(RhnullItems.RHNULL_PICKAXE);
        toolItem(RhnullItems.RHNULL_AXE);
        toolItem(RhnullItems.RHNULL_SHOVEL);
        toolItem(RhnullItems.RHNULL_HOE);
        normalItem(RhnullItems.RHNULL);
        normalItem(RhnullItems.RHNULL_NUGGET);
        normalItem(RhnullItems.RHNULL_HELMET);
        normalItem(RhnullItems.RHNULL_CHESTPLATE);
        normalItem(RhnullItems.RHNULL_LEGGINGS);
        normalItem(RhnullItems.RHNULL_BOOTS);

        normalItem(DropItems.AUREAL_REVENANT_DAGGER);
        normalItem(DropItems.VEINREAVER_HORN);
        normalItem(DropItems.CRIMSON_SHELL);
        normalItem(DropItems.SCARLET_FEATHER);
        normalItem(DropItems.RITEKEEPER_HEART);
        normalItem(DropItems.FAILED_REMNANT_ASHES);
        normalItem(DropItems.CINDER_ACOLYTE_FAINTED_EMBER);

        egg(SpawnEggItems.VEINRAVER_ENTITY_SPAWN_EGG);
        egg(SpawnEggItems.OFFSPRING_OF_THE_UNKNOWN_SPAWN_EGG);
        egg(SpawnEggItems.BLASPHEMOUS_MALFORMATION_SPAWN_EGG);
        egg(SpawnEggItems.SELIORA_SPAWN_EGG);
        egg(SpawnEggItems.HORNED_WORM_SPAWN_EGG);
        egg(SpawnEggItems.VEIL_STALKER_SPAWN_EGG);
        egg(SpawnEggItems.CYCLOPS_ENTITY_SPAWN_EGG);
        egg(SpawnEggItems.GRAVE_WALKER_SPAWN_EGG);
        egg(SpawnEggItems.CINDER_ACOLYTE_SPAWN_EGG);
        egg(SpawnEggItems.FAILED_REMNANT_SPAWN_EGG);

        normalItem(FoodItems.GOREHOG_RAW_STEAK);
        normalItem(FoodItems.GOREHOG_COOKED_STEAK);
        normalItem(FoodItems.SCARLET_RAW_CHICKEN);
        normalItem(FoodItems.SCARLET_COOKED_CHICKEN);
        normalItem(FoodItems.GLOW_FRUIT);
        normalItem(FoodItems.GLOW_MUSHROOM);

        normalItem(MiscItems.CHALICE_OF_THE_DAMMED);
        normalItem(MiscItems.CRIMSON_IDOL_COIN);
        normalItem(MiscItems.SANGUINE_CRUCIBLE_CORE);
        normalItem(PlaceableItems.VORACIOUS_MUSHROOM);
        normalItem(PlaceableItems.CRIMSON_LURE_MUSHROOM);
        normalItem(PlaceableItems.BLOOD_GEM_SPROUT_SEED);

        normalItem(MiscItems.ROOTLET_POWDER);

        normalItem(FlaskItems.BLOOD_FLASK);
        normalItem(FlaskItems.CORRUPTED_BLOOD_FLASK);
        normalItem(FlaskItems.FILLED_BLOOD_FLASK);
        normalItem(FlaskItems.FILLED_RHNULL_BLOOD_FLASK);
        normalItem(FlaskItems.FILLED_VISCOUS_BLASPHEMY_FLASK);
        normalItem(FlaskItems.BOUND_BLOOD_FLASK);

        toolItem(WeaponItems.SACRIFICIAL_DAGGER);
        toolItem(WeaponItems.HERETIC_SACRIFICIAL_DAGGER);

        normalItem(WeaponItems.UNKNOWN_ENTITY_FINGER);

        normalItem(MiscItems.GAZE_OF_THE_UNKNOWN);
        normalItem(MiscItems.NAMELESS_WHISPER);
        normalItem(DropItems.BLOOD_ECHO_SHARD);
        normalItem(DropItems.NAMELESS_ECHO_SHARD);

        normalItem(GemItems.AMULET_OF_ANCESTRAL_BLOOD);
        normalItem(GemItems.SANGUINITE_GEM_FRAME);
        normalItem(GemItems.SANGUINITE_GREAT_GEM_FRAME);
        normalItem(GemItems.RHNULL_GEM_FRAME);
        normalItem(GemItems.RHNULL_GREAT_GEM_FRAME);
        normalItem(GemItems.ANCIENT_GEM);
        normalItem(GemItems.ANCIENT_RHNULL_GEM);
        normalItem(GemItems.ANCIENT_BLASPHEMOUS_GEM);
        normalItem(GemItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD);
        normalItem(GemItems.GREAT_ANCIENT_GEM);
        normalItem(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM);
        normalItem(GemItems.GREAT_ANCIENT_RHNULL_GEM);
        normalItem(GemItems.CRIMSON_WARD_RING);
        normalItem(GemItems.BLASPHEMOUS_RING);

        normalItem(ReliquaryItems.ANCIENT_OCULAR_LENSE);
        normalItem(ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE);
        normalItem(ReliquaryItems.MARK_OF_THE_RESTLESS_SLUMBER);
        normalItem(ReliquaryItems.RUNE_OF_THE_RAVENOUS_GAZE);
        normalItem(ReliquaryItems.OMEN_OF_THE_CRUSHING_TOLL);
        normalItem(ReliquaryItems.SEAL_OF_THE_HOLLOW_BULWARK);

        normalItem(BlasphemiteItems.BLASPHEMOUS_EYE);

        block(MachineBlocks.SANGUINITE_PIPE);
        block(MachineBlocks.RHNULL_PIPE);
        block(MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR);
        block(MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR);
        block(MachineBlocks.RHNULL_BLOOD_ENGINE_BLOCK);
        block(MachineBlocks.RHNULL_BLOOD_ENGINE_PILLAR_BLOCK);
        block(MachineBlocks.UNKNOWN_PORTAL_BLOCK);
        block(MachineBlocks.SANGUINITE_BLOOD_HARVESTER);

        evenSimplerBlockItem(MachineBlocks.SANGUINE_CRUCIBLE);
        evenSimplerBlockItem(MetalBlocks.RHNULL_BLOCK);
        evenSimplerBlockItem(SoilBlocks.BLOODDIRT_FARMLAND);
        evenSimplerBlockItem(PlantBlocks.CRIMSON_LURE_MUSHROOM_BLOCK);
        simpleBlockItemBlockTexture(PlantBlocks.BLOOD_FLOWER);
        simpleBlockItemBlockTexture(PlantBlocks.BLOOD_GRASS);

        simpleBlockItemBlockTexture(PlantBlocks.VISCERAL_INFECTED_VEIN);

        evenSimplerBlockItem(LightBlocks.GLOWING_CRYSTAL_GLASS_BLOCK);
        evenSimplerBlockItem(LightBlocks.BLOOD_GLOWING_CHAINS_BLOCK);

        normalItem(PlaceableItems.BLOOD_LILY);
        blockFlatWithItemTexture(LightBlocks.STAR_LAMP_BLOCK, "star_lamp_block");

        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_BLOCK);

        simpleBlockItemBlockTexture(MiscBlocks.BLOOD_FIRE);
        simpleBlockItemBlockTexture(MiscBlocks.FRENZIED_FIRE_BLOCK);
        fenceItem(StoneBlocks.BLOODY_STONE_FENCE, StoneBlocks.BLOODY_STONE_BLOCK);
        wallItem(StoneBlocks.BLOODY_STONE_WALL, StoneBlocks.BLOODY_STONE_BLOCK);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_STAIRS);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_SLAB);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_FENCE_GATE);

        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_TILES_BLOCK);
        fenceItem(StoneBlocks.BLOODY_STONE_TILES_FENCE, StoneBlocks.BLOODY_STONE_TILES_BLOCK);
        wallItem(StoneBlocks.BLOODY_STONE_TILES_WALL, StoneBlocks.BLOODY_STONE_TILES_BLOCK);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_TILES_STAIRS);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_TILES_SLAB);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_FENCE_TILES_GATE);

        evenSimplerBlockItem(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK);
        fenceItem(StoneBlocks.POLISHED_BLOODY_STONE_FENCE, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK);
        wallItem(StoneBlocks.POLISHED_BLOODY_STONE_WALL, StoneBlocks.POLISHED_BLOODY_STONE_BLOCK);
        evenSimplerBlockItem(StoneBlocks.POLISHED_BLOODY_STONE_STAIRS);
        evenSimplerBlockItem(StoneBlocks.POLISHED_BLOODY_STONE_SLAB);
        evenSimplerBlockItem(StoneBlocks.POLISHED_BLOODY_STONE_FENCE_GATE);

        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_BRICKS);
        fenceItem(StoneBlocks.BLOODY_STONE_BRICKS_FENCE, StoneBlocks.BLOODY_STONE_BRICKS);
        wallItem(StoneBlocks.BLOODY_STONE_BRICKS_WALL, StoneBlocks.BLOODY_STONE_BRICKS);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_BRICKS_STAIRS);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_BRICKS_SLAB);
        evenSimplerBlockItem(StoneBlocks.BLOODY_STONE_FENCE_BRICKS_GATE);

        fenceItem(WoodBlocks.BLOOD_PLANKS_FENCE, WoodBlocks.BLOOD_PLANKS);
        evenSimplerBlockItem(WoodBlocks.BLOOD_PLANKS_STAIRS);
        evenSimplerBlockItem(WoodBlocks.BLOOD_PLANKS_SLAB);
        evenSimplerBlockItem(WoodBlocks.BLOOD_PLANKS_FENCE_GATE);

        evenSimplerBlockItem(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS);
        wallItem(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL, StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS);
        evenSimplerBlockItem(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS);
        evenSimplerBlockItem(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB);
        evenSimplerBlockItem(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN);
        evenSimplerBlockItem(StoneBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS);
        evenSimplerBlockItem(StoneBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS);
        evenSimplerBlockItem(LightBlocks.ANCIENT_BLOODY_LAMP);
        blockItem(PlaceableItems.ANCIENT_TORCH_ITEM, LightBlocks.ANCIENT_TORCH_BLOCK);

        evenSimplerBlockItem(SoilBlocks.BLASPHEMOUS_SAND_BLOCK);
        evenSimplerBlockItem(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK);
        evenSimplerBlockItem(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK);
        evenSimplerBlockItem(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS);
        evenSimplerBlockItem(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB);
        normalItem(DropItems.SELIORA_ESSENCE);
        evenSimplerBlockItem(SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK);
        evenSimplerBlockItem(SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK);
        evenSimplerBlockItem(SandstoneBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK);
        evenSimplerBlockItem(SandstoneBlocks.ERODED_BLASPHEMOUS_SANDSTONE);
        evenSimplerBlockItem(SandstoneBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE);
        evenSimplerBlockItem(SandstoneBlocks.CRACKED_BLASPHEMOUS_SANDSTONE);
        evenSimplerBlockItem(MetalBlocks.BLASPHEMITE_ORE);
        evenSimplerBlockItem(MetalBlocks.BLASPHEMITE_BLOCK);
        simpleBlockItemBlockTexture(PlantBlocks.SPIKY_GRASS);
        simpleBlockItemBlockTexture(PlantBlocks.ROUNDED_GRASS);
        simpleBlockItemBlockTexture(PlantBlocks.STING_FLOWER);

        normalItem(BloodGearItems.BLOOD_ARROW);
        normalItem(BlasphemiteItems.BLASPHEMITE);
        normalItem(BlasphemiteItems.BLASPHEMITE_NUGGET);
        normalItem(BlasphemiteItems.RAW_BLASPHEMITE);
        normalItem(BlasphemiteItems.BLASPHEMITE_BOOTS);
        normalItem(BlasphemiteItems.BLASPHEMITE_LEGGINGS);
        normalItem(BlasphemiteItems.BLASPHEMITE_CHESTPLATE);
        normalItem(BlasphemiteItems.BLASPHEMITE_HELMET);
        toolItem(BlasphemiteItems.BLASPHEMITE_SWORD);
        toolItem(BlasphemiteItems.BLASPHEMITE_SHOVEL);
        toolItem(BlasphemiteItems.BLASPHEMITE_HOE);
        toolItem(BlasphemiteItems.BLASPHEMITE_AXE);
        toolItem(BlasphemiteItems.BLASPHEMITE_PICKAXE);

        toolItem(SanguiniteItems.SANGUINITE_PICKAXE);
        toolItem(SanguiniteItems.SANGUINITE_AXE);
        toolItem(SanguiniteItems.SANGUINITE_SHOVEL);
        toolItem(SanguiniteItems.SANGUINITE_HOE);

        simpleBlockItemBlockTexture(PlantBlocks.HANGING_BLOOD_TREE_LEAVES);
        simpleBlockItemBlockTexture(LightBlocks.GLOWING_CRYSTAL);
        simpleBlockItemBlockTexture(PlantBlocks.DROOPVINE);
        simpleBlockItemBlockTexture(PlantBlocks.DROOPVINE_PLANT);
        fanItem(PlantBlocks.BLOOD_PETALS);

    }
    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(BloodyHell.MODID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }
    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(BloodyHell.MODID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItemBlockTexture(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(BloodyHell.MODID,"block/" + item.getId().getPath()));
    }
    private String blockName(Supplier<? extends Block> block) {
        return BuiltInRegistries.BLOCK.getKey(block.get()).getPath();
    }

    private ResourceLocation texture(String name) {
        return modLoc("block/" + name);
    }

    public void itemFence(Supplier<? extends Block> block, String name) {
        withExistingParent(blockName(block), mcLoc("block/fence_inventory"))
                .texture("texture", ("block/" + name));
    }

    public ItemModelBuilder block(Supplier<? extends Block> block) {
        return block(block, blockName(block));
    }

    public ItemModelBuilder block(Supplier<? extends Block> block, String name) {
        return withExistingParent(blockName(block), modLoc("block/" + name));
    }

    public void blockFlat(Supplier<? extends Block> block) {
        withExistingParent(blockName(block), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + blockName(block)));
    }

    public void blockFlatWithBlockTexture(Supplier<? extends Block> block, String name) {
        withExistingParent(blockName(block), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + name))
                .renderType("translucent");
    }

    public void blockFlatWithItemTexture(Supplier<? extends Block> block, String name) {
        withExistingParent(blockName(block), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name));
    }

    public void normalItem(Supplier<? extends Item> item) {
        withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }

    public void normalItemSpecifiedTexture(Supplier<? extends Item> item, String name) {
        withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name));
    }

    public void torchItem(Supplier<? extends Block> item) {
        withExistingParent(BuiltInRegistries.BLOCK.getKey(item.get()).getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + BuiltInRegistries.BLOCK.getKey(item.get()).getPath()));
    }

    public void toolItem(Supplier<? extends Item> item) {
        withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(), mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }

    public void rodItem(Supplier<? extends Item> item) {
        withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(), mcLoc("item/handheld_rod"))
                .texture("layer0", modLoc("item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }

    public void egg(Supplier<? extends Item> item) {
        withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(), mcLoc("item/template_spawn_egg"));
    }

    public void sign(Supplier<? extends SignBlock> sign) {
        withExistingParent(blockName(sign), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + blockName(sign)));
    }

    public ItemModelBuilder wall(Supplier<? extends WallBlock> wall, Supplier<? extends Block> fullBlock) {
        return wallInventory(BuiltInRegistries.BLOCK.getKey(wall.get()).getPath(), texture(blockName(fullBlock)));
    }

    public ItemModelBuilder button(Supplier<? extends ButtonBlock> button, Supplier<? extends Block> fullBlock) {
        return buttonInventory(BuiltInRegistries.BLOCK.getKey(button.get()).getPath(), texture(blockName(fullBlock)));
    }

    public void trapdoor(Supplier<? extends TrapDoorBlock> trapdoor) {
        withExistingParent(BuiltInRegistries.BLOCK.getKey(trapdoor.get()).getPath(), new ResourceLocation(BloodyHell.MODID, "block/" + blockName(trapdoor) + "_bottom"));
    }
    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(BloodyHell.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }
    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(BloodyHell.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void emissiveBlockItem(RegistryObject<Block> block) {
        String name = ForgeRegistries.BLOCKS.getKey(block.get()).getPath();

        this.withExistingParent(name, modLoc("block/" + name))
                .texture("layer1", new ResourceLocation(BloodyHell.MODID, "block/" + name + "_emissive"))
                .renderType("cutout");
    }

    public void blockItem(RegistryObject<Item> item, RegistryObject<Block> block) {

        ResourceLocation Itemid = ForgeRegistries.ITEMS.getKey(item.get());
        ResourceLocation Blockid = ForgeRegistries.BLOCKS.getKey(block.get());

        this.withExistingParent(Itemid.getPath(), modLoc("block/" + Blockid.getPath()));
    }

    public void fanItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/coral_fan"))
                .texture("fan", modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void fanItem(RegistryObject<Block> block, String textureName) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/coral_fan"))
                .texture("fan", modLoc("block/" + textureName));
    }

}
