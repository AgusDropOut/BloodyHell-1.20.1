package net.agusdropout.bloodyhell.block.registry;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class SandstoneBlocks {
    public static final RegistryObject<Block> BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ERODED_BLASPHEMOUS_SANDSTONE = registerBlock("eroded_blasphemous_sandstone", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).lightLevel((state)->15).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> FULLY_ERODED_BLASPHEMOUS_SANDSTONE = registerBlock("fully_eroded_blasphemous_sandstone", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).lightLevel((state)->15).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CRACKED_BLASPHEMOUS_SANDSTONE = registerBlock("cracked_blasphemous_sandstone", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).lightLevel((state)->15).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("chiseled_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("chiseled_detailed_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CUT_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("cut_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("smooth_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS = registerBlock("smooth_blasphemous_sandstone_stairs", () -> new StairBlock(() -> SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB = registerBlock("smooth_blasphemous_sandstone_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));

    private SandstoneBlocks() {}

    public static void init() {}
}
