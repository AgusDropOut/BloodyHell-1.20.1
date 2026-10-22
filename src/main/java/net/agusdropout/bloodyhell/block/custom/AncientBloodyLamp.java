package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class AncientBloodyLamp extends LanternBlock {
    public AncientBloodyLamp(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D;
        double z = pos.getZ() + 0.5D;

        double y = state.getValue(HANGING) ? pos.getY() + 0.40D : pos.getY() + 0.35D;

        if(random.nextDouble() < 0.05){
            level.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                    1.0F + random.nextFloat() * 0.5F,
                    random.nextFloat() * 0.7F + 0.3F, false);
        }

        for(int i = 0; i < 2; i++) {
            double offsetRatio = random.nextDouble() * 0.15D;
            double angle = random.nextDouble() * Math.PI * 2;

            double offsetX = Math.cos(angle) * offsetRatio;
            double offsetZ = Math.sin(angle) * offsetRatio;

            float t = (float) (offsetRatio / 0.15D);

            float r = 1.0F - (t * 0.7F);
            float g = 0.2F - (t * 0.2F);
            float b = 0.5F - (t * 0.5F);

            Vector3f color = new Vector3f(r, g, b);

            float size = 0.25F + (t * 0.2F);
            double speedY = 0.02D - (t * 0.015D);

            ParticleHelper.spawn(level, new MagicParticleOptions(
                            color,
                            size,
                            false,
                            30),
                    x + offsetX, y, z + offsetZ,
                    0.0D, speedY, 0.0D);
        }

        if(random.nextDouble() < 0.2) {
            ParticleHelper.spawn(level, ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                    x, y + 0.1D, z,
                    (random.nextDouble() - 0.5) * 0.05, 0.03D, (random.nextDouble() - 0.5) * 0.05);
        }

        if(random.nextDouble() < 0.1) {
            ParticleHelper.spawn(level, ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5D) * 0.1D,
                    y + 0.3D,
                    z + (random.nextDouble() - 0.5D) * 0.1D,
                    0.0D, 0.02D, 0.0D);
        }
    }
}