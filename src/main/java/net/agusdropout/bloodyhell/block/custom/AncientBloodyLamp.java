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
        // Tweaked Y: Hanging usually needs to be lower than standing to look centered in the glass
        double y = state.getValue(HANGING) ? pos.getY() + 0.40D : pos.getY() + 0.35D;

        // --- 1. Sound (Unchanged, maybe slightly rarer) ---
        if(random.nextDouble() < 0.05){
            level.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                    1.0F + random.nextFloat() * 0.5F,
                    random.nextFloat() * 0.7F + 0.3F, false);
        }

        // --- 2. The Gradient Blood Fire ---
        // We run this loop twice per tick to ensure the fire looks "dense"
        for(int i = 0; i < 2; i++) {
            // A. Calculate Position (Gaussian creates a natural "cone" shape)
            // Values closer to 0.0 are common (Center), values closer to 1.0 are rare (Edge)
            double offsetRatio = random.nextDouble() * 0.15D; // Max radius ~0.15 blocks
            double angle = random.nextDouble() * Math.PI * 2;

            double offsetX = Math.cos(angle) * offsetRatio;
            double offsetZ = Math.sin(angle) * offsetRatio;

            // B. Calculate Color based on Position (The Gradient)
            // If ratio is low (Center) -> Pink/Bright. If ratio is high (Edge) -> Dark Red.
            // We normalize the ratio (0.0 to 0.15) to a 0.0 to 1.0 scale for interpolation
            float t = (float) (offsetRatio / 0.15D);

            // Center Color (Hot Pink/Bright Red): 1.0, 0.2, 0.5
            // Edge Color (Dark Blood): 0.3, 0.0, 0.0
            float r = 1.0F - (t * 0.7F); // 1.0 -> 0.3
            float g = 0.2F - (t * 0.2F); // 0.2 -> 0.0
            float b = 0.5F - (t * 0.5F); // 0.5 -> 0.0

            Vector3f color = new Vector3f(r, g, b);

            // C. Calculate Size & Speed
            // Center particles are smaller and faster (heat). Edge particles are larger and lazy.
            float size = 0.25F + (t * 0.2F);
            double speedY = 0.02D - (t * 0.015D); // Center = 0.02, Edge = 0.005

            ParticleHelper.spawn(level, new MagicParticleOptions(
                            color,
                            size,
                            false, // Emissive/Glowing
                            30), // Lifetime
                    x + offsetX, y, z + offsetZ,
                    0.0D, speedY, 0.0D);
        }

        // --- 3. Occasional "Sparks" (Your ModParticles) ---
        // These act as the "embers" flying off the main fire
        if(random.nextDouble() < 0.2) {
            ParticleHelper.spawn(level, ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                    x, y + 0.1D, z,
                    (random.nextDouble() - 0.5) * 0.05, 0.03D, (random.nextDouble() - 0.5) * 0.05);
        }

        // --- 4. Smoke (Dark top) ---
        // Only spawn smoke at the very tip of the flame
        if(random.nextDouble() < 0.1) {
            ParticleHelper.spawn(level, ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5D) * 0.1D,
                    y + 0.3D, // Higher up
                    z + (random.nextDouble() - 0.5D) * 0.1D,
                    0.0D, 0.02D, 0.0D);
        }
    }
}