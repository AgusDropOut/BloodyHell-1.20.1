package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.base.*;
import net.agusdropout.bloodyhell.block.custom.*;
import net.agusdropout.bloodyhell.block.custom.mechanism.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class SoilBlocks {
    public static final RegistryObject<Block> BLOOD_GRASS_BLOCK = registerBlock("blood_grass_block", ()-> new BloodGrassBlock(
            BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(1f).randomTicks().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> BLOODDIRT_FARMLAND = registerBlock("blooddirt_farmland", () -> new BloodDirtFarmlandBlock(
            BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> BLOOD_DIRT_BLOCK = registerBlock("blood_dirt_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.DIRT).strength(1f)));
    public static final RegistryObject<Block> BLASPHEMOUS_SAND_BLOCK = registerBlock("blasphemous_sand_block", () -> new SandBlock(1,
            BlockBehaviour.Properties.copy(Blocks.SAND).strength(1F)));

    private SoilBlocks() {}

    public static void init() {}
}
