package net.agusdropout.bloodyhell.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class BaseLiquidBlock extends LiquidBlock {
    private final Supplier<? extends ParticleOptions> particleSupplier;
    private final float particleChance;

    public BaseLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties, Supplier<? extends ParticleOptions> particleSupplier, float particleChance) {
        super(fluid, properties);
        this.particleSupplier = particleSupplier;
        this.particleChance = particleChance;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        // Only spawn particles if the random check passes
        if (random.nextFloat() < this.particleChance) {
            // Only spawn if the block ABOVE is air (so it looks like it's on the surface)
            if (level.isEmptyBlock(pos.above())) {
                double x = pos.getX() + random.nextDouble();
                // Spawn slightly below the top of the block so it emerges from the liquid
                double y = pos.getY() + 1.0D - (random.nextDouble() * 0.1D);
                double z = pos.getZ() + random.nextDouble();

                level.addParticle(this.particleSupplier.get(), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}