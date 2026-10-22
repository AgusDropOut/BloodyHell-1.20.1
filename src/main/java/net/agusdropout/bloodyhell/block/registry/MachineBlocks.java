package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.base.*;
import net.agusdropout.bloodyhell.block.custom.*;
import net.agusdropout.bloodyhell.block.custom.altar.BlasphemousBloodAltarBlock;
import net.agusdropout.bloodyhell.block.custom.altar.BloodAltarBlock;
import net.agusdropout.bloodyhell.block.custom.altar.MainBlasphemousBloodAltarBlock;
import net.agusdropout.bloodyhell.block.custom.altar.MainBloodAltarBlock;
import net.agusdropout.bloodyhell.block.custom.mechanism.*;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguiniteCondenserBlock;
import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class MachineBlocks {
    public static final RegistryObject<Block> BLASPHEMOUS_BLOOD_ALTAR = BLOCKS.register("blasphemous_blood_altar", ()-> new BlasphemousBloodAltarBlock(
            BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES).noOcclusion().noParticlesOnBreak()));
    public static final RegistryObject<Block> MAIN_BLASPHEMOUS_BLOOD_ALTAR = BLOCKS.register("main_blasphemous_blood_altar", ()-> new MainBlasphemousBloodAltarBlock(
            BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES).noOcclusion().noParticlesOnBreak()));
    public static final RegistryObject<Block> BLOOD_ALTAR = registerBlock("blood_altar",
            () -> new BloodAltarBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));
    public static final RegistryObject<Block> MAIN_BLOOD_ALTAR = registerBlock("main_blood_altar",
            () -> new MainBloodAltarBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));
    public static final RegistryObject<Block> UNKNOWN_PORTAL_BLOCK = registerBlockWithoutBlockItem("unknown_portal_block",
            () -> new UnknownPortalBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion()));
    public static final RegistryObject<Block> SANGUINE_CRUCIBLE = registerBlock("sanguine_crucible", ()-> new BloodWorkbenchBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> RHNULL_BLOOD_ENGINE_BLOCK = BLOCKS.register("rhnull_blood_engine_block",
            () -> new RhnullBloodEngineBlock(BlockBehaviour.Properties.copy(Blocks.REDSTONE_BLOCK)
                    .noOcclusion()));
    public static final RegistryObject<Block> RHNULL_BLOOD_ENGINE_PILLAR_BLOCK = BLOCKS.register("rhnull_blood_engine_pillar_block",
            () -> new RhnullBloodEnginePillarBlock(BlockBehaviour.Properties.copy(Blocks.REDSTONE_BLOCK)
                    .noOcclusion()));
    public static final RegistryObject<Block> SANGUINITE_PIPE = registerBlockWithoutBlockItem("sanguinite_pipe",
            () -> new SanguinitePipeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RHNULL_PIPE = registerBlockWithoutBlockItem("rhnull_pipe",
            () -> new RhnullPipeBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_TANK = registerBlock("sanguinite_tank",
            () -> new SanguiniteTankBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RHNULL_TANK = registerBlock("rhnull_tank",
            () -> new RhnullTankBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_BLOOD_HARVESTER = registerBlockWithoutBlockItem("sanguinite_blood_harvester",
            () -> new SanguiniteBloodHarvesterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_INFUSOR = registerBlock("sanguinite_infusor",
            () -> new SanguiniteInfusorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_CONDENSER = registerBlockWithoutBlockItem("sanguinite_condenser",
            () -> new SanguiniteCondenserBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> RHNULL_CONDENSER = registerBlockWithoutBlockItem("rhnull_condenser",
            () -> new RhnullCondenserBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> SANGUINE_LAPIDARY = registerBlock("sanguine_lapidary",
            () -> new SanguineLapidaryBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOOD_GEM_SPROUT = registerBlockWithoutBlockItem("blood_gem_sprout",
            () -> new BloodGemSproutBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().noLootTable()));

    private MachineBlocks() {}

    public static void init() {}
}
