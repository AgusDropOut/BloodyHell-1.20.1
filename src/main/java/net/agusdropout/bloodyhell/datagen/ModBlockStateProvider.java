package net.agusdropout.bloodyhell.datagen;

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
import net.agusdropout.bloodyhell.block.base.TallPlantBlock;
import net.agusdropout.bloodyhell.block.custom.HangingSoulTreeLeavesBlock;
import net.minecraft.core.Direction;
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
        simpleBlockWithItem(PlantBlocks.BLOOD_FLOWER.get(), models().cross(blockTexture(PlantBlocks.BLOOD_FLOWER.get()).getPath(),
                blockTexture(PlantBlocks.BLOOD_FLOWER.get())).renderType("cutout"));
       simpleBlockWithItem(PlantBlocks.BLOOD_GRASS.get(), models().cross(blockTexture(PlantBlocks.BLOOD_GRASS.get()).getPath(),
                blockTexture(PlantBlocks.BLOOD_GRASS.get())).renderType("cutout"));
        simpleBlockWithItem(PlantBlocks.POTTED_BLOOD_FLOWER.get(), models().singleTexture("potted_blood_flower", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(PlantBlocks.BLOOD_FLOWER.get())).renderType("cutout"));
       blockWithItem(MiscBlocks.BLEEDING_BLOCK);
       getVariantBuilder(PlantBlocks.HANGING_SOUL_TREE_LEAVES.get()).forAllStates(state -> {
           String name = name(PlantBlocks.HANGING_SOUL_TREE_LEAVES) + (state.getValue(HangingSoulTreeLeavesBlock.HALF) == DoubleBlockHalf.UPPER ? "_top" : "");
           return ConfiguredModel.builder().modelFile(models().cross(name, texture(name)).renderType("minecraft:cutout")).build();
       });
        getVariantBuilder(PlantBlocks.HANGING_BLOOD_TREE_LEAVES.get()).forAllStates(state -> {
            String name = name(PlantBlocks.HANGING_BLOOD_TREE_LEAVES) + (state.getValue(HangingSoulTreeLeavesBlock.HALF) == DoubleBlockHalf.UPPER ? "_top" : "");
            return ConfiguredModel.builder().modelFile(models().cross(name, texture(name)).renderType("minecraft:cutout")).build();
        });
        blockWithItem(StoneBlocks.BLOODY_STONE_BLOCK);
        blockWithItem(StoneBlocks.BLOODY_STONE_TILES_BLOCK);
        blockWithItem(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK);
        blockWithItem(LightBlocks.BLOOD_GLOW_STONE);
        blockWithItem(StoneBlocks.BLOODY_STONE_BRICKS);
        blockWithItem(MetalBlocks.RHNULL_BLOCK);
        blockWithItem(MetalBlocks.BLASPHEMITE_BLOCK);

        fanBlock(PlantBlocks.BLOOD_PETALS);

        blockTranslucent(LightBlocks.GLOWING_CRYSTAL_GLASS_BLOCK);

        crossBlock(LightBlocks.BLOOD_GLOWING_CHAINS_BLOCK);
        crossBlock(MiscBlocks.BLOOD_FIRE);
        crossBlock(MiscBlocks.FRENZIED_FIRE_BLOCK);

        veinBlock(PlantBlocks.VISCERAL_INFECTED_VEIN);

        stairsBlock(((StairBlock) StoneBlocks.BLOODY_STONE_STAIRS.get()), blockTexture(StoneBlocks.BLOODY_STONE_BLOCK.get()));
        slabBlock(((SlabBlock) StoneBlocks.BLOODY_STONE_SLAB.get()), blockTexture(StoneBlocks.BLOODY_STONE_BLOCK.get()), blockTexture(StoneBlocks.BLOODY_STONE_BLOCK.get()));
        fenceBlock(((FenceBlock) StoneBlocks.BLOODY_STONE_FENCE.get()), blockTexture(StoneBlocks.BLOODY_STONE_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) StoneBlocks.BLOODY_STONE_FENCE_GATE.get()), blockTexture(StoneBlocks.BLOODY_STONE_BLOCK.get()));
        wallBlock(((WallBlock) StoneBlocks.BLOODY_STONE_WALL.get()), blockTexture(StoneBlocks.BLOODY_STONE_BLOCK.get()));

        stairsBlock(((StairBlock) StoneBlocks.BLOODY_STONE_TILES_STAIRS.get()), blockTexture(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        slabBlock(((SlabBlock) StoneBlocks.BLOODY_STONE_TILES_SLAB.get()), blockTexture(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get()), blockTexture(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        fenceBlock(((FenceBlock) StoneBlocks.BLOODY_STONE_TILES_FENCE.get()), blockTexture(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) StoneBlocks.BLOODY_STONE_FENCE_TILES_GATE.get()), blockTexture(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get()));
        wallBlock(((WallBlock) StoneBlocks.BLOODY_STONE_TILES_WALL.get()), blockTexture(StoneBlocks.BLOODY_STONE_TILES_BLOCK.get()));

        stairsBlock(((StairBlock) StoneBlocks.POLISHED_BLOODY_STONE_STAIRS.get()), blockTexture(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        slabBlock(((SlabBlock) StoneBlocks.POLISHED_BLOODY_STONE_SLAB.get()), blockTexture(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get()), blockTexture(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        fenceBlock(((FenceBlock) StoneBlocks.POLISHED_BLOODY_STONE_FENCE.get()), blockTexture(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) StoneBlocks.POLISHED_BLOODY_STONE_FENCE_GATE.get()), blockTexture(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));
        wallBlock(((WallBlock) StoneBlocks.POLISHED_BLOODY_STONE_WALL.get()), blockTexture(StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get()));

        stairsBlock(((StairBlock) StoneBlocks.BLOODY_STONE_BRICKS_STAIRS.get()), blockTexture(StoneBlocks.BLOODY_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) StoneBlocks.BLOODY_STONE_BRICKS_SLAB.get()), blockTexture(StoneBlocks.BLOODY_STONE_BRICKS.get()), blockTexture(StoneBlocks.BLOODY_STONE_BRICKS.get()));
        fenceBlock(((FenceBlock) StoneBlocks.BLOODY_STONE_BRICKS_FENCE.get()), blockTexture(StoneBlocks.BLOODY_STONE_BRICKS.get()));
        fenceGateBlock(((FenceGateBlock) StoneBlocks.BLOODY_STONE_FENCE_BRICKS_GATE.get()), blockTexture(StoneBlocks.BLOODY_STONE_BRICKS.get()));
        wallBlock(((WallBlock) StoneBlocks.BLOODY_STONE_BRICKS_WALL.get()), blockTexture(StoneBlocks.BLOODY_STONE_BRICKS.get()));

        stairsBlock(((StairBlock) WoodBlocks.BLOOD_PLANKS_STAIRS.get()), blockTexture(WoodBlocks.BLOOD_PLANKS.get()));
        slabBlock(((SlabBlock) WoodBlocks.BLOOD_PLANKS_SLAB.get()), blockTexture(WoodBlocks.BLOOD_PLANKS.get()), blockTexture(WoodBlocks.BLOOD_PLANKS.get()));
        fenceBlock(((FenceBlock) WoodBlocks.BLOOD_PLANKS_FENCE.get()), blockTexture(WoodBlocks.BLOOD_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) WoodBlocks.BLOOD_PLANKS_FENCE_GATE.get()), blockTexture(WoodBlocks.BLOOD_PLANKS.get()));

        blockWithItem(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS);
        stairsBlock(((StairBlock) StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS.get()), blockTexture(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB.get()), blockTexture(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()), blockTexture(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()));
        wallBlock(((WallBlock) StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL.get()), blockTexture(StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get()));
        log((StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN));
        blockWithItem(StoneBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS);
        blockWithItem(StoneBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS);
        crossBlock(LightBlocks.ANCIENT_BLOODY_LAMP);
        simpleHorizontalBlock(MiscBlocks.ANCIENT_BLOOD_CAPSULE);

        wallBlockHorizontal(LightBlocks.ANCIENT_TORCH_BLOCK);

        rotatedCubeBlock(SoilBlocks.BLASPHEMOUS_SAND_BLOCK);
        blockWithItem(SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(MetalBlocks.BLASPHEMITE_ORE);
        blockWithItem(SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(SandstoneBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK);
        blockWithItem(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK);
        stairsBlock(((StairBlock) SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS.get()), blockTexture(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()));
        slabBlock(((SlabBlock) SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB.get()), blockTexture(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()), blockTexture(SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get()));
        blockWithItem(SandstoneBlocks.ERODED_BLASPHEMOUS_SANDSTONE);
        blockWithItem(SandstoneBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE);
        blockWithItem(SandstoneBlocks.CRACKED_BLASPHEMOUS_SANDSTONE);
        simpleBlockWithItem(PlantBlocks.SPIKY_GRASS.get(), models().cross(blockTexture(PlantBlocks.SPIKY_GRASS.get()).getPath(),
                blockTexture(PlantBlocks.SPIKY_GRASS.get())).renderType("cutout"));
        simpleBlockWithItem(PlantBlocks.ROUNDED_GRASS.get(), models().cross(blockTexture(PlantBlocks.ROUNDED_GRASS.get()).getPath(),
                blockTexture(PlantBlocks.ROUNDED_GRASS.get())).renderType("cutout"));
        tallPlantBlock(PlantBlocks.STING_FLOWER);

        block(MachineBlocks.SANGUINITE_PIPE);
        block(MachineBlocks.RHNULL_PIPE);
        block(MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR);
        block(MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR);
        block(MachineBlocks.RHNULL_BLOOD_ENGINE_BLOCK);
        block(MachineBlocks.RHNULL_BLOOD_ENGINE_PILLAR_BLOCK);
        block(MachineBlocks.UNKNOWN_PORTAL_BLOCK);
        block(MachineBlocks.SANGUINITE_BLOOD_HARVESTER);

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

    public void log(Supplier<? extends Block> block) {
        Block blockInstance = block.get();
        if (blockInstance instanceof RotatedPillarBlock rotatedPillarBlock) {
            ResourceLocation texture = blockTexture(blockInstance);
            axisBlock(rotatedPillarBlock, texture);

        } else {
            simpleBlockWithItem(blockInstance, cubeAll(blockInstance));
        }
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

    public void rotatedCubeBlock(Supplier<? extends Block> blockSupplier) {
        Block block = blockSupplier.get();
        String blockName = name(block);

        ModelFile modelFile = models().cubeAll(blockName, blockTexture(block));
        getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {
                new ConfiguredModel(modelFile, 0, 0, false),
                new ConfiguredModel(modelFile, 0, 90, false),
                new ConfiguredModel(modelFile, 0, 180, false),
                new ConfiguredModel(modelFile, 0, 270, false)
        });
        simpleBlockItem(block, modelFile);
    }
    public void tallPlantBlock(Supplier<? extends Block> blockSupplier) {
        Block block = blockSupplier.get();
        String baseName = name(block);
        ModelFile root = models().cross(baseName + "_root", modLoc("block/" + baseName + "_root")).renderType("cutout");
        ModelFile stem = models().cross(baseName + "_stem", modLoc("block/" + baseName + "_stem")).renderType("cutout");
        ModelFile top  = models().cross(baseName + "_top",  modLoc("block/" + baseName + "_top")).renderType("cutout");

        getVariantBuilder(block).forAllStates(state -> {
            String part = state.getValue(TallPlantBlock.PART).getSerializedName();
            ModelFile chosen = switch (part) {
                case "root" -> root;
                case "stem" -> stem;
                case "top"  -> top;
                default     -> stem;
            };
            return ConfiguredModel.builder().modelFile(chosen).build();
        });
    }

    public void wallBlockHorizontal(Supplier<? extends Block> block) {
        String modelName = name(block);
        ModelFile model = models().getExistingFile(modLoc("block/" + modelName));

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)

                    .rotationY((int) dir.toYRot())
                    .build();
        });
    }

    public void simpleHorizontalBlock(Supplier<? extends Block> block) {
        ModelFile model = models().getExistingFile(modLoc("block/" + name(block)));

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            int rotY = ((int) dir.toYRot() + 180) % 360;

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(rotY)
                    .build();
        });
    }

    public void veinBlock(Supplier<? extends Block> blockSupplier) {
        Block block = blockSupplier.get();
        ResourceLocation texture = blockTexture(block);
        String name = name(block);

        ModelFile model = models().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", texture)
                .texture("vein", texture)
                .renderType("cutout")
                .element()
                .from(0.0f, 0.0f, 0.0f)
                .to(16.0f, 16.0f, 1.0f)
                .shade(false)
                .face(Direction.SOUTH).texture("#vein").uvs(0, 0, 16, 16).end()
                .face(Direction.NORTH).texture("#vein").uvs(16, 0, 0, 16).end()
                .end();

        var builder = getMultipartBuilder(block);

        Direction[] directions = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN };

        for (Direction dir : directions) {
            int rotX = 0;
            int rotY = 0;
            switch (dir) {
                case NORTH: rotX = 0;   rotY = 0;   break;
                case EAST:  rotX = 0;   rotY = 90;  break;
                case SOUTH: rotX = 0;   rotY = 180; break;
                case WEST:  rotX = 0;   rotY = 270; break;
                case UP:    rotX = 270; rotY = 0;   break;
                case DOWN:  rotX = 90;  rotY = 0;   break;
            }

            builder.part()
                    .modelFile(model)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .uvLock(true)
                    .addModel()
                    .condition(MultifaceBlock.getFaceProperty(dir), true);

            var fallbackPart = builder.part()
                    .modelFile(model)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .uvLock(true)
                    .addModel();

            fallbackPart.condition(PipeBlock.NORTH, false)
                    .condition(PipeBlock.EAST, false)
                    .condition(PipeBlock.SOUTH, false)
                    .condition(PipeBlock.WEST, false)
                    .condition(PipeBlock.UP, false)
                    .condition(PipeBlock.DOWN, false);
        }
    }

    public void fanBlock(Supplier<? extends Block> block) {
        ModelFile fanModel = models().withExistingParent(name(block), mcLoc("block/coral_fan"))
                .texture("fan", blockTexture(block.get()))
                .renderType("cutout");

        simpleBlock(block.get(), fanModel);
    }

    public void fanBlock(Supplier<? extends Block> block, String textureName) {
        ModelFile fanModel = models().withExistingParent(name(block), mcLoc("block/coral_fan"))
                .texture("fan", modLoc("block/" + textureName))
                .renderType("cutout");

        simpleBlock(block.get(), fanModel);
    }

}