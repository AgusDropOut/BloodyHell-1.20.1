package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.base.BaseWallPlantBlock;

import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.property.Properties;
import org.joml.Vector3f;

public class AncientTorchBlock extends BaseWallPlantBlock {

    public AncientTorchBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // 1. Calculate Base Position
        // Vanilla wall torches offset the flame based on direction.
        // Since you extend BaseWallPlantBlock, we check the FACING property.
        Direction direction = state.getValue(FACING);
        double x = (double)pos.getX() + 0.5D;
        double y = (double)pos.getY() + 0.7D; // Flame is slightly above center
        double z = (double)pos.getZ() + 0.5D;

        // Adjust position to sit on the "head" of the torch based on wall facing
        double offset = 0.27D;
        x += (double)direction.getOpposite().getStepX() * offset;
        y += 0.22D; // Move up slightly higher for wall torches
        z += (double)direction.getOpposite().getStepZ() * offset;

        Vec3 center = new Vec3(x, y, z);
        Vec3 upwardMotion = new Vec3(0.0, 0.02, 0.0); // Slight rise due to heat

        // 2. Spawn Gradient Fire
        // We use your helper to spawn 2 particles per tick with calculated colors
        ParticleHelper.spawnSphereGradient(level, center, 0.2, 3, upwardMotion, (ratio) -> {

            // --- The Gradient Logic (0.0 = Center, 1.0 = Edge) ---
            Vector3f color = ParticleHelper.gradient3(ratio,
                    new Vector3f(1.0f, 0.6f, 0.8f), // START: Hot/White-Pink (Core)
                    new Vector3f(0.9f, 0.1f, 0.1f), // MID:   Blood Red
                    new Vector3f(0.2f, 0.0f, 0.0f)  // END:   Black/Dried Blood (Edge)
            );

            // Particles are smaller at the edges to look like fading smoke
            float size = 0.50f - (ratio * 0.25f);

            // Return your custom particle options
            return new MagicParticleOptions(color, size, false, 60);
        });

        // 3. Optional: Occasional Smoke (Standard Vanilla feel)
        if (random.nextDouble() < 0.1) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y + 0.1, z, 0.0, 0.03, 0.0);
        }
    }
}