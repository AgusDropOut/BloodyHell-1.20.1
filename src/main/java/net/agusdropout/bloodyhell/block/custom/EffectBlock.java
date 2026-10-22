package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;

import java.util.List;

public class EffectBlock extends Block {

        public EffectBlock(Properties properties) {
            super(properties);
        }
        @Override
        public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {

                    double range = 5.0D;
                    AABB effectArea = (new AABB(blockPos)).inflate(range);
                    List<Player> players = level.getEntitiesOfClass(Player.class, effectArea);
                    for (Player player : players) {
                        player.removeEffect(ModEffects.BLOOD_LUST.get());
                    }

        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return true;
        }
    }

