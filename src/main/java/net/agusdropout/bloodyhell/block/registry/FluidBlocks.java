package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.base.*;
import net.agusdropout.bloodyhell.block.custom.*;
import net.agusdropout.bloodyhell.block.custom.mechanism.*;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class FluidBlocks {
    public static final RegistryObject<LiquidBlock> BLOOD_FLUID_BLOCK = BLOCKS.register("blood_fluid_block",
            () -> new BaseLiquidBlock(
                    ModFluids.BLOOD_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable(),
                    ModParticles.BLOOD_PARTICLES,
                    0.05f
            ));
    public static final RegistryObject<LiquidBlock> CORRUPTED_BLOOD_BLOCK = BLOCKS.register("corrupted_blood_block",
            () -> new BaseLiquidBlock(
                    ModFluids.CORRUPTED_BLOOD_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable(),
                    () -> net.minecraft.core.particles.ParticleTypes.ASH,
                    0.1f
            ));
    public static final RegistryObject<LiquidBlock> VISCOUS_BLASPHEMY_BLOCK = BLOCKS.register("viscous_blasphemy_block",
            () -> new BaseLiquidBlock(
                    ModFluids.VISCOUS_BLASPHEMY_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel((state) -> 15),
                    () -> new net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions(
                            new org.joml.Vector3f(1.0f, 0.88f, 0.07f),
                            1.2f,
                            true,
                            40
                    ),
                    0.02f
            ));
    public static final RegistryObject<LiquidBlock> VISCERAL_BLOOD_BLOCK = BLOCKS.register("visceral_fluid_block",
            () -> new BaseLiquidBlock(
                    ModFluids.VISCERAL_BLOOD_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel((state) -> 15),
                    () -> new net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions(
                            new org.joml.Vector3f(1.0f, 0.88f, 0.07f),
                            1.2f,
                            true,
                            40
                    ),
                    0.02f
            ));

    private FluidBlocks() {}

    public static void init() {}
}
