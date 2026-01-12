package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.base.TallPlantBlock;
import net.agusdropout.bloodyhell.block.custom.HangingSoulTreeLeavesBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, BloodyHell.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.BLOOD_FLOWER.get(), models().cross(blockTexture(ModBlocks.BLOOD_FLOWER.get()).getPath(),
                blockTexture(ModBlocks.BLOOD_FLOWER.get())).renderType("cutout"));
       simpleBlockWithItem(ModBlocks.BLOOD_GRASS.get(), models().cross(blockTexture(ModBlocks.BLOOD_GRASS.get()).getPath(),
                blockTexture(ModBlocks.BLOOD_GRASS.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_BLOOD_FLOWER.get(), models().singleTexture("potted_blood_flower", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.BLOOD_FLOWER.get())).renderType("cutout"));
       blockWithItem(ModBlocks.BLEEDING_BLOCK);
       getVariantBuilder(ModBlocks.HANGING_SOUL_TREE_LEAVES.get()).forAllStates(state -> {
           String name = name(ModBlocks.HANGING_SOUL_TREE_LEAVES) + (state.getValue(HangingSoulTreeLeavesBlock.HALF) == DoubleBlockHalf.UPPER ? "_top" : "");
           return ConfiguredModel.builder().modelFile(models().cross(name, texture(name)).renderType("minecraft:cutout")).build();
       });
        getVariantBuilder(ModBlocks.HANGING_BLOOD_TREE_LEAVES.get()).forAllStates(state -> {
            String name = name(ModBlocks.HANGING_BLOOD_TREE_LEAVES) + (state.getValue(HangingSoulTreeLeavesBlock.HALF) == DoubleBlockHalf.UPPER ? "_top" : "");
            return ConfiguredModel.builder().modelFile(models().cross(name, texture(name)).renderType("minecraft:cutout")).build();
        });
        blockWithItem(ModBlocks.BLOODY_STONE_BLOCK);
        blockWithItem(ModBlocks.BLOODY_STONE_TILES_BLOCK);
        blockWithItem(ModBlocks.POLISHED_BLOODY_STONE_BLOCK);
        blockWithItem(ModBlocks.BLOOD_GLOW_STONE);
        blockWithItem(ModBlocks.BLOODY_STONE_BRICKS);
        blockWithItem(ModBlocks.RHNULL_BLOCK);

        blockTranslucent(ModBlocks.GLOWING_CRYSTAL_GLASS_BLOCK);

        crossBlock(ModBlocks.BLOOD_GLOWING_CHAINS_BLOCK);

        //Bloody Stone
        stairsBlock(((StairBlock) ModBlocks.BLOODY_STONE_STAIRS.get()), blockTexture(ModBlocks.BLOODY_STONE_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.BLOODY_STONE_SLAB.get()), blockTexture(ModBlocks.BLOODY_STONE_BLOCK.get()), blockTexture(ModBlocks.BLOODY_STONE_BLOCK.get()));
        fenceBlock(((FenceBlock) ModBlocks.BLOODY_STONE_FENCE.get()), blockTexture(ModBlocks.BLOODY_STONE_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.BLOODY_STONE_FENCE_GATE.get()), blockTexture(ModBlocks.BLOODY_STONE_BLOCK.get()));
        wallBlock(((WallBlock) ModBlocks.BLOODY_STONE_WALL.get()), blockTexture(ModBlocks.BLOODY_STONE_BLOCK.get()));
        //Bloody Stone Tiles
        stairsBlock(((StairBlock) ModBlocks.BLOODY_STONE_TILES_STAIRS.get()), blockTexture(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.BLOODY_STONE_TILES_SLAB.get()), blockTexture(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()), blockTexture(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        fenceBlock(((FenceBlock) ModBlocks.BLOODY_STONE_TILES_FENCE.get()), blockTexture(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.BLOODY_STONE_FENCE_TILES_GATE.get()), blockTexture(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        wallBlock(((WallBlock) ModBlocks.BLOODY_STONE_TILES_WALL.get()), blockTexture(ModBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        //Polished Bloody Stone
        stairsBlock(((StairBlock) ModBlocks.POLISHED_BLOODY_STONE_STAIRS.get()), blockTexture(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.POLISHED_BLOODY_STONE_SLAB.get()), blockTexture(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()), blockTexture(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        fenceBlock(((FenceBlock) ModBlocks.POLISHED_BLOODY_STONE_FENCE.get()), blockTexture(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get()), blockTexture(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        wallBlock(((WallBlock) ModBlocks.POLISHED_BLOODY_STONE_WALL.get()), blockTexture(ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        //Bloody Stone Bricks
        stairsBlock(((StairBlock) ModBlocks.BLOODY_STONE_BRICKS_STAIRS.get()), blockTexture(ModBlocks.BLOODY_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) ModBlocks.BLOODY_STONE_BRICKS_SLAB.get()), blockTexture(ModBlocks.BLOODY_STONE_BRICKS.get()), blockTexture(ModBlocks.BLOODY_STONE_BRICKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.BLOODY_STONE_BRICKS_FENCE.get()), blockTexture(ModBlocks.BLOODY_STONE_BRICKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.BLOODY_STONE_FENCE_BRICKS_GATE.get()), blockTexture(ModBlocks.BLOODY_STONE_BRICKS.get()));
        wallBlock(((WallBlock) ModBlocks.BLOODY_STONE_BRICKS_WALL.get()), blockTexture(ModBlocks.BLOODY_STONE_BRICKS.get()));
        //Blood Planks
        stairsBlock(((StairBlock) ModBlocks.BLOOD_PLANKS_STAIRS.get()), blockTexture(ModBlocks.BLOOD_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.BLOOD_PLANKS_SLAB.get()), blockTexture(ModBlocks.BLOOD_PLANKS.get()), blockTexture(ModBlocks.BLOOD_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.BLOOD_PLANKS_FENCE.get()), blockTexture(ModBlocks.BLOOD_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.BLOOD_PLANKS_FENCE_GATE.get()), blockTexture(ModBlocks.BLOOD_PLANKS.get()));

        //Blasphemous Biome
        rotatedCubeBlock(ModBlocks.BLASPHEMOUS_SAND_BLOCK);
        blockWithItem(ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(ModBlocks.BLASPHEMITE_ORE);
        blockWithItem(ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(ModBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK);
        stairsBlock(((StairBlock) ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get()), blockTexture(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get()), blockTexture(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), blockTexture(ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()));
        blockWithItem(ModBlocks.ERODED_BLASPHEMOUS_SANDSTONE);
        blockWithItem(ModBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE);
        blockWithItem(ModBlocks.CRACKED_BLASPHEMOUS_SANDSTONE);
        simpleBlockWithItem(ModBlocks.SPIKY_GRASS.get(), models().cross(blockTexture(ModBlocks.SPIKY_GRASS.get()).getPath(),
                blockTexture(ModBlocks.SPIKY_GRASS.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.ROUNDED_GRASS.get(), models().cross(blockTexture(ModBlocks.ROUNDED_GRASS.get()).getPath(),
                blockTexture(ModBlocks.ROUNDED_GRASS.get())).renderType("cutout"));
        tallPlantBlock(ModBlocks.STING_FLOWER);


    }
    private String name(Block block) {
        return key(block).getPath();
    }
    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(BloodyHell.MODID +
                ":block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    protected ResourceLocation texture(String name) {
        return modLoc("block/" + name);
    }

    protected String name(Supplier<? extends Block> block) {
        return BuiltInRegistries.BLOCK.getKey(block.get()).getPath();
    }

    public void block(Supplier<? extends Block> block) {
        simpleBlock(block.get());
    }

    public void blockTranslucent(Supplier<? extends Block> block) {
        simpleBlock(block.get(), models().cubeAll(name(block), blockTexture(block.get())).renderType("translucent"));
    }

    public void log(Supplier<? extends RotatedPillarBlock> block, String name) {
        axisBlock(block.get(), texture(name));
    }

    private void crossBlock(Supplier<? extends Block> block, ModelFile model) {
        getVariantBuilder(block.get()).forAllStates(state ->
                ConfiguredModel.builder()
                        .modelFile(model)
                        .build());
    }

    public void torchBlock(Supplier<? extends Block> block, Supplier<? extends Block> wall) {
        ModelFile torch = models().torch(name(block), texture(name(block))).renderType("cutout");
        ModelFile torchwall = models().torchWall(name(wall), texture(name(block))).renderType("cutout");
        simpleBlock(block.get(), torch);
        getVariantBuilder(wall.get()).forAllStates(state ->
                ConfiguredModel.builder()
                        .modelFile(torchwall)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360)
                        .build());
    }

    public void crossBlock(Supplier<? extends Block> block) {
        crossBlock(block, models().cross(name(block), texture(name(block))).renderType("cutout"));
    }

    public void tintedCrossBlock(Supplier<? extends Block> block) {
        crossBlock(block, models().withExistingParent(name(block), mcLoc("block/tinted_cross")).texture("cross", texture(name(block))).renderType("cutout"));
    }

    public void stairs(Supplier<? extends StairBlock> block, Supplier<? extends Block> fullBlock) {
        stairsBlock(block.get(), texture(name(fullBlock)));
    }

    public void slab(Supplier<? extends SlabBlock> block, Supplier<? extends Block> fullBlock) {
        slabBlock(block.get(), texture(name(fullBlock)), texture(name(fullBlock)));
    }

    public void wall(Supplier<? extends WallBlock> wall, Supplier<? extends Block> fullBlock) {
        wallBlock(wall.get(), texture(name(fullBlock)));
    }

    public void fence(Supplier<? extends FenceBlock> block, Supplier<? extends Block> fullBlock) {
        fenceBlock(block.get(), texture(name(fullBlock)));
        fenceColumn(block, name(fullBlock));

    }

    private void fenceColumn(Supplier<? extends FenceBlock> block, String name) {
        String baseName = name(block);
        fourWayBlock(block.get(),
                models().fencePost(baseName + "_post", texture(name)),
                models().fenceSide(baseName + "_side", texture(name)));
    }

    public void fenceGate(Supplier<? extends FenceGateBlock> block, Supplier<? extends Block> fullBlock) {
        fenceGateBlock(block.get(), texture(name(fullBlock)));
    }

    public void door(Supplier<? extends DoorBlock> block, String name) {
        doorBlockWithRenderType(block.get(), name(block), texture(name + "_door_bottom"), texture(name + "_door_top"), "cutout");
    }

    public void trapdoor(Supplier<? extends TrapDoorBlock> block, String name) {
        trapdoorBlockWithRenderType(block.get(), texture(name + "_trapdoor"), true, "cutout");
    }

    public void carpet(Supplier<? extends WoolCarpetBlock> block) {
        simpleBlock(block.get(), models().carpet(name(block), texture(name(block))));
    }

    public void button(Supplier<? extends ButtonBlock> block, Supplier<? extends Block> fullBlock) {
        buttonBlock(block.get(), texture(name(fullBlock)));
    }

    public void pressurePlate(Supplier<? extends PressurePlateBlock> block, Supplier<? extends Block> fullBlock) {
        pressurePlateBlock(block.get(), texture(name(fullBlock)));
    }

    public void sign(Supplier<? extends StandingSignBlock> standingBlock, Supplier<? extends WallSignBlock> wallBlock, String name) {
        signBlock(standingBlock.get(), wallBlock.get(), modLoc("block/" + name));
    }



    public void hangingSign(Supplier<? extends CeilingHangingSignBlock> standingBlock, Supplier<? extends WallHangingSignBlock> wallBlock, String name) {
        ModelFile model = models().getBuilder(name(standingBlock)).texture("particle", modLoc("block/" + name));
        simpleBlock(standingBlock.get(), model);
        simpleBlock(wallBlock.get(), model);
    }

    /**
     * Generate a blockstate with 0°, 90°, 180°, 270° Y-axis rotations
     * for any simple cube block.
     */
    public void rotatedCubeBlock(Supplier<? extends Block> blockSupplier) {
        Block block = blockSupplier.get();
        String blockName = name(block);

        // Create the base cube model if it doesn't exist yet
        ModelFile modelFile = models().cubeAll(blockName, blockTexture(block));

        // Generate the 4 rotation variants
        getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {
                new ConfiguredModel(modelFile, 0, 0, false),    // 0°
                new ConfiguredModel(modelFile, 0, 90, false),   // 90°
                new ConfiguredModel(modelFile, 0, 180, false),  // 180°
                new ConfiguredModel(modelFile, 0, 270, false)   // 270°
        });

        // Generate item model automatically (like blockWithItem)
        simpleBlockItem(block, modelFile);
    }
    public void tallPlantBlock(Supplier<? extends Block> blockSupplier) {
        Block block = blockSupplier.get();
        String baseName = name(block); // 🔥 obtiene el nombre del bloque
        ResourceLocation baseTexture = texture(baseName);

        // Modelos para las tres partes
        ModelFile root = models().cross(baseName + "_root", modLoc("block/" + baseName + "_root")).renderType("cutout");
        ModelFile stem = models().cross(baseName + "_stem", modLoc("block/" + baseName + "_stem")).renderType("cutout");
        ModelFile top  = models().cross(baseName + "_top",  modLoc("block/" + baseName + "_top")).renderType("cutout");

        getVariantBuilder(block).forAllStates(state -> {
            String part = state.getValue(TallPlantBlock.PART).getSerializedName(); // <-- clave
            ModelFile chosen = switch (part) {
                case "root" -> root;
                case "stem" -> stem;
                case "top"  -> top;
                default     -> stem;
            };
            return ConfiguredModel.builder().modelFile(chosen).build();
        });
    }

}