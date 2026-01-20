package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.joml.Vector3f;

public class BleedingBlock extends Block {


    public BleedingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState state, Entity entity) {
        if(entity instanceof LivingEntity LivingEntity){
            if(!entity.getType().is(ModTags.Entities.INMUNE_TO_BLEEDING_BLOCK)){
                LivingEntity.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 200));
            }
        }
        

        super.stepOn(level, blockPos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

        // --- 1. MAGICAL BLOOD AURA ---
        // Spawns with some frequency (30% chance per tick)
        if (random.nextFloat() < 0.3F) {
            // Position: Random spot on the top surface
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
            double y = pos.getY() + 1.1D; // Start slightly above the block
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;

            level.addParticle(
                    new MagicParticleOptions(
                            new Vector3f(0.9F, 0.0F, 0.1F), // Deep Red color
                            0.4F + random.nextFloat() * 0.4F, // Random Size (0.4 to 0.8)
                            false,                             // Jitter Enabled (Living Color)
                            40 + random.nextInt(20)           // Lifetime: 40-60 ticks
                    ),
                    x, y, z,
                    0.0, 0.015, 0.0 // Very slow gentle rise upwards
            );
        }

        // --- 2. EXISTING BLOOD DRIP PARTICLES ---
        // I kept your original logic for the messy dripping on the sides
        if (random.nextInt(10) == 0) {
            for (int i = 0; i < 4; ++i) {
                double x = (double) pos.getX() + random.nextDouble();
                double y = (double) pos.getY() + random.nextDouble();
                double z = (double) pos.getZ() + random.nextDouble();
                double xSpeed = ((double) random.nextFloat() - 0.5D) * 0.5D;
                double ySpeed = ((double) random.nextFloat() - 0.5D) * 0.5D;
                double zSpeed = ((double) random.nextFloat() - 0.5D) * 0.5D;
                int j = random.nextInt(2) * 2 - 1;

                if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
                    x = (double) pos.getX() + 0.5D + 0.25D * (double) j;
                    xSpeed = random.nextFloat() * 2.0F * (float) j;
                } else {
                    z = (double) pos.getZ() + 0.5D + 0.25D * (double) j;
                    zSpeed = random.nextFloat() * 1.0F * (float) j;
                }

                level.addParticle(ModParticles.BLOOD_PARTICLES.get(), x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }

        for(int i = 0; i < 3; i++) {
            // Start near the top center of the block
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.4D;
            double y = pos.getY() + 1.2D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.4D;

            // Random Velocity EVERYWHERE
            // Upwards (0.1 to 0.3) + Random Horizontal (-0.15 to 0.15)
            double vx = 0;
            double vy = 0.1D + random.nextDouble() * 0.2D;
            double vz = 0;

            level.addParticle(
                    new MagicFloorParticleOptions(
                            new Vector3f(0.6F, 0.0F, 0.0F), // Dark Red
                            0.5F,                           // Size
                            false,                          // Jitter (Off)
                            60                              // Max Lifetime (will die sooner on shrink)
                    ),
                    x, y, z,
                    vx, vy, vz // Pass the random velocity here
            );
        }

        super.animateTick(state, level, pos, random);
    }
}

