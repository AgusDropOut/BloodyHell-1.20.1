package net.agusdropout.bloodyhell.block.registry;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class MetalBlocks {
    public static final RegistryObject<Block> SANGUINITE_BLOCK = registerBlock("sanguinite_block", ()-> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RHNULL_BLOCK = registerBlock("rhnull_block", ()-> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLASPHEMITE_BLOCK = registerBlock("blasphemite_block", ()-> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_ORE = registerBlock("sanguinite_ore", ()-> new DropExperienceBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_ORE).strength(6f).requiresCorrectToolForDrops(), UniformInt.of(3,7)));
    public static final RegistryObject<Block> BLASPHEMITE_ORE = registerBlock("blasphemite_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(5f)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)));

    private MetalBlocks() {}

    public static void init() {}
}
