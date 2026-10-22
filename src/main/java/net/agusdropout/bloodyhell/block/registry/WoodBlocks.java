package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.base.*;
import net.agusdropout.bloodyhell.block.custom.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class WoodBlocks {
    public static final RegistryObject<Block> BLOOD_LOG = registerBlock("blood_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> BLOOD_WOOD = registerBlock("blood_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_BLOOD_LOG = registerBlock("stripped_blood_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_BLOOD_WOOD = registerBlock("stripped_blood_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> BLOOD_PLANKS = registerBlock("blood_planks", ()-> new FlammablePlanksBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOOD_PLANKS_STAIRS = registerBlock("blood_planks_stairs", () -> new StairBlock(() -> WoodBlocks.BLOOD_PLANKS.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOOD_PLANKS_SLAB = registerBlock("blood_planks_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOOD_PLANKS_FENCE = registerBlock("blood_planks_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOOD_PLANKS_FENCE_GATE = registerBlock("blood_planks_fence_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), SoundEvents.WOOD_PLACE, SoundEvents.WOOD_BREAK));
    public static final RegistryObject<Block> BLOOD_LEAVES = registerBlock("blood_leaves", ()-> new LeavesBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).lightLevel((state)->15)));
    public static final RegistryObject<Block> SOUL_LOG = registerBlock("soul_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> SOUL_WOOD = registerBlock("soul_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_SOUL_LOG = registerBlock("stripped_soul_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_SOUL_WOOD = registerBlock("stripped_soul_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> SOUL_PLANKS = registerBlock("soul_planks", ()-> new FlammablePlanksBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> SOUL_LEAVES = registerBlock("soul_leaves", ()-> new LeavesBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));

    private WoodBlocks() {}

    public static void init() {}
}
