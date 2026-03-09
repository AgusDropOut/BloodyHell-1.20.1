package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.base.BaseWallPlantBlock;

import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AncientTorchBlock extends BaseWallPlantBlock {

    public AncientTorchBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction direction = state.getValue(FACING);
        double x = (double)pos.getX() + 0.5D;
        double y = (double)pos.getY() + 0.7D;
        double z = (double)pos.getZ() + 0.5D;


        double offset = 0.27D;
        x += (double)direction.getOpposite().getStepX() * offset;
        y += 0.22D;
        z += (double)direction.getOpposite().getStepZ() * offset;

        Vec3 center = new Vec3(x, y, z);
        Vec3 upwardMotion = new Vec3(0.0, 0.02, 0.0);


        ParticleHelper.spawnSphereGradient(level, center, 0.2, 3, upwardMotion, (ratio) -> {


            Vector3f color = ParticleHelper.gradient3(ratio,
                    new Vector3f(1.0f, 0.6f, 0.8f),
                    new Vector3f(0.9f, 0.1f, 0.1f),
                    new Vector3f(0.2f, 0.0f, 0.0f)
            );

            float size = 0.50f - (ratio * 0.25f);


            return new MagicParticleOptions(color, size, false, 60);
        });


        if (random.nextDouble() < 0.1) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y + 0.1, z, 0.0, 0.03, 0.0);
        }
    }
}