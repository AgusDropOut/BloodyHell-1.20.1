package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.base.AbstractFireBlock;
import net.agusdropout.bloodyhell.block.entity.custom.BloodFireBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.FrenziedFireBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.RhnullBloodEngineBlockEntity;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class RhnullBloodEngineBlock extends Block implements EntityBlock {

    public RhnullBloodEngineBlock(Properties properties) {
        super(properties);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RhnullBloodEngineBlockEntity(blockPos, blockState);
    }
}