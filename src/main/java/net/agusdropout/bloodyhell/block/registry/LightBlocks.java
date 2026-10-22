package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.base.*;
import net.agusdropout.bloodyhell.block.custom.*;
import net.agusdropout.bloodyhell.block.custom.mechanism.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class LightBlocks {
    public static final RegistryObject<Block> SOUL_LAMP = registerBlock("soul_lamp", ()-> new Soullampblock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).lightLevel(state -> state.getValue(Soullampblock.LIT) ? 15 : 0).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOOD_GLOW_STONE = registerBlock("blood_glow_stone",
            ()-> new EffectBlock(BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).lightLevel((state)->15)));
    public static final RegistryObject<Block> GLOWING_CRYSTAL = registerBlock("glowing_crystal", () -> new GlowingCrystalBlock(
            BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK).strength(1F).lightLevel((state)-> 20).explosionResistance(20f).noOcclusion()));
    public static final RegistryObject<Block> GLOWING_CRYSTAL_GLASS_BLOCK = registerBlock("glowing_crystal_glass_block", () -> new GlassBlock(
            BlockBehaviour.Properties.copy(Blocks.GLASS).strength(1F).lightLevel((state)-> 20).noOcclusion()));
    public static final RegistryObject<Block> GLOWING_CRYSTAL_LANTERN = registerBlock("glowing_crystal_lantern", () -> new LanternBlock(
            BlockBehaviour.Properties.copy(Blocks.GLASS).strength(1F).lightLevel((state)-> 14).noOcclusion()));
    public static final RegistryObject<Block> BLOOD_GLOWING_CHAINS_BLOCK = registerBlock("blood_glowing_chains_block", () -> new ChainBlock(
            BlockBehaviour.Properties.copy(Blocks.CHAIN).strength(1F).lightLevel((state)-> 7).noOcclusion()));
    public static final RegistryObject<Block> STAR_LAMP_BLOCK = registerBlock("star_lamp_block", () -> new StarLampBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).lightLevel((state)->15).noOcclusion()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_LAMP = registerBlock("ancient_bloody_lamp", () -> new AncientBloodyLamp(
            BlockBehaviour.Properties.copy(Blocks.LANTERN).strength(1F).lightLevel((state)-> 14).noOcclusion()));
    public static final RegistryObject<Block> ANCIENT_TORCH_BLOCK = registerBlockWithoutBlockItem("ancient_torch_block", () -> new AncientTorchBlock(
                    BlockBehaviour.Properties.copy(Blocks.TORCH).strength(1F).lightLevel((state) -> 14).noOcclusion()) {
            });

    private LightBlocks() {}

    public static void init() {}
}
