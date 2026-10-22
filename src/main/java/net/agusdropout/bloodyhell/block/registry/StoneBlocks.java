package net.agusdropout.bloodyhell.block.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;
import static net.agusdropout.bloodyhell.block.BlockStrength.*;

public final class StoneBlocks {
    public static final RegistryObject<Block> BLOODY_STONE_BLOCK = registerBlock("bloody_stone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_STAIRS = registerBlock("bloody_stone_stairs", () -> new StairBlock(() -> StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_SLAB = registerBlock("bloody_stone_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_FENCE).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE = registerBlock("bloody_stone_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE_GATE = registerBlock("bloody_stone_fence_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops(), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> BLOODY_STONE_WALL = registerBlock("bloody_stone_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_BLOCK = registerBlock("polished_bloody_stone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.POLISHED_GRANITE).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_STAIRS = registerBlock("polished_bloody_stone_stairs", () -> new StairBlock(() -> StoneBlocks.POLISHED_BLOODY_STONE_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_SLAB = registerBlock("polished_bloody_stone_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_FENCE).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_FENCE = registerBlock("polished_bloody_stone_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_FENCE_GATE = registerBlock("polished_bloody_stone_fence_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops(), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_WALL = registerBlock("polished_bloody_stone_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_BLOCK = registerBlock("bloody_stone_tiles_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_STAIRS = registerBlock("bloody_stone_tiles_stairs", () -> new StairBlock(() -> StoneBlocks.BLOODY_STONE_TILES_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_STAIRS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_SLAB = registerBlock("bloody_stone_tiles_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_FENCE = registerBlock("bloody_stone_tiles_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE_TILES_GATE = registerBlock("bloody_stone_fence_tiles_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops(), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_WALL = registerBlock("bloody_stone_tiles_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS = registerBlock("bloody_stone_bricks", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_STAIRS = registerBlock("bloody_stone_bricks_stairs", () -> new StairBlock(() -> StoneBlocks.BLOODY_STONE_BRICKS.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_SLAB = registerBlock("bloody_stone_bricks_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_FENCE).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_FENCE = registerBlock("bloody_stone_bricks_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE_BRICKS_GATE = registerBlock("bloody_stone_fence_bricks_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops(), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_WALL = registerBlock("bloody_stone_bricks_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(STONE_HARDNESS, STONE_RESISTANCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS = registerBlock("ancient_bloody_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(ANCIENT_HARDNESS, ANCIENT_RESISTANCE)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_SLAB = registerBlock("ancient_bloody_stone_bricks_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(ANCIENT_HARDNESS, ANCIENT_RESISTANCE)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_STAIRS = registerBlock("ancient_bloody_stone_bricks_stairs",
            () -> new StairBlock(() -> StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(ANCIENT_HARDNESS, ANCIENT_RESISTANCE)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_WALL = registerBlock("ancient_bloody_stone_bricks_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(ANCIENT_HARDNESS, ANCIENT_RESISTANCE)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_COLUMN = registerBlock("ancient_bloody_stone_bricks_column",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(ANCIENT_HARDNESS, ANCIENT_RESISTANCE)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_DETAILED_BLOODY_STONE_BRICKS = registerBlock("ancient_detailed_bloody_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(ANCIENT_HARDNESS, ANCIENT_RESISTANCE)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_CHISELED_BLOODY_STONE_BRICKS= registerBlock("ancient_chiseled_bloody_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(ANCIENT_HARDNESS, ANCIENT_RESISTANCE)
                    .requiresCorrectToolForDrops()));

    private StoneBlocks() {}

    public static void init() {}
}
