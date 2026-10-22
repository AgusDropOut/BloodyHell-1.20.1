package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.base.BaseLiquidBlock;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static net.agusdropout.bloodyhell.block.ModBlocks.BLOCKS;

public final class FluidBlocks {
    private static final Vector3f GOLD = new Vector3f(1.0f, 0.88f, 0.07f);

    public static final RegistryObject<LiquidBlock> BLOOD_FLUID_BLOCK = liquid("blood_fluid_block", ModFluids.BLOOD_SOURCE,
            BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable(), ModParticles.BLOOD_PARTICLES, 0.05f);
    public static final RegistryObject<LiquidBlock> CORRUPTED_BLOOD_BLOCK = liquid("corrupted_blood_block", ModFluids.CORRUPTED_BLOOD_SOURCE,
            BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable(), () -> ParticleTypes.ASH, 0.1f);
    public static final RegistryObject<LiquidBlock> VISCOUS_BLASPHEMY_BLOCK = liquid("viscous_blasphemy_block", ModFluids.VISCOUS_BLASPHEMY_SOURCE,
            glowingLava(), FluidBlocks::goldMagic, 0.02f);
    public static final RegistryObject<LiquidBlock> VISCERAL_BLOOD_BLOCK = liquid("visceral_fluid_block", ModFluids.VISCERAL_BLOOD_SOURCE,
            glowingLava(), FluidBlocks::goldMagic, 0.02f);

    private FluidBlocks() {}

    public static void init() {}

    private static BlockBehaviour.Properties glowingLava() {
        return BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel(state -> 15);
    }

    private static ParticleOptions goldMagic() {
        return new MagicParticleOptions(GOLD, 1.2f, true, 40);
    }

    private static RegistryObject<LiquidBlock> liquid(String name, Supplier<? extends FlowingFluid> fluid, BlockBehaviour.Properties properties,
                                                      Supplier<? extends ParticleOptions> particle, float particleChance) {
        return BLOCKS.register(name, () -> new BaseLiquidBlock(fluid, properties, particle, particleChance));
    }
}
