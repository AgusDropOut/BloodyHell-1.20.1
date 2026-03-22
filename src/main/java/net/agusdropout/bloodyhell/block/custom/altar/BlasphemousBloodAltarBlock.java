package net.agusdropout.bloodyhell.block.custom.altar;

import net.agusdropout.bloodyhell.block.base.AbstractAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BlasphemousBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BlasphemousBloodAltarBlock extends AbstractAltarBlock {

    public static final BooleanProperty RITUAL_ACTIVE = BooleanProperty.create("ritual_active");

    public BlasphemousBloodAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlasphemousBloodAltarBlockEntity(pos, state);
    }


    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(MAINCHARGED)) return;

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.1;
        double z = pos.getZ() + 0.5;

        if (random.nextFloat() < 0.4f) {
            Vector3f bloodColor = new Vector3f(0.9f , 0f, 0f);
            MagicParticleOptions options = new MagicParticleOptions(bloodColor, 0.6f, false, 30, true);

            double offX = (random.nextDouble() - 0.5) * 0.4;
            double offZ = (random.nextDouble() - 0.5) * 0.4;

            ParticleHelper.spawn(level, options, x + offX, y, z + offZ, 0, 0.03, 0);
        }
    }


}