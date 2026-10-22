package net.agusdropout.bloodyhell.worldgen.feature.custom;

import net.agusdropout.bloodyhell.block.registry.LightBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GlowingCrystalFeature extends Feature<NoneFeatureConfiguration> {
    public GlowingCrystalFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        LevelAccessor level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        if (!level.isEmptyBlock(pos)) {
            return false;
        } else {
            BlockState blockstate_up = level.getBlockState(pos.above());
            BlockState blockstate_north = level.getBlockState(pos.north());
            BlockState blockstate_south = level.getBlockState(pos.south());
            BlockState blockstate_east = level.getBlockState(pos.east());
            BlockState blockstate_west = level.getBlockState(pos.west());
            if (    (blockstate_up.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock()) || (blockstate_north.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())) ||
                    (blockstate_south.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())) || (blockstate_east.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())) ||
                    (blockstate_west.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())))){
                Direction direction = null;
                if(blockstate_north.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())){
                    direction = Direction.SOUTH;
                } else if(blockstate_south.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())){
                    direction = Direction.NORTH;
                } else if (blockstate_east.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())){
                    direction = Direction.WEST;
                } else if(blockstate_west.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())){
                    direction = Direction.EAST;
                } else if(blockstate_up.is(StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState().getBlock())){
                    direction = Direction.DOWN;
                }
                level.setBlock(pos, LightBlocks.GLOWING_CRYSTAL.get().defaultBlockState().setValue(BlockStateProperties.FACING, direction), 0);
                return true;
            } else {
                return false;
            }
        }
    }
}
