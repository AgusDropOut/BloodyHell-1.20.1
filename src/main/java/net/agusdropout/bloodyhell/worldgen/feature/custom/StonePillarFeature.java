package net.agusdropout.bloodyhell.worldgen.feature.custom;

import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import com.mojang.serialization.Codec;
import net.agusdropout.bloodyhell.block.custom.Droopvine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class StonePillarFeature extends Feature<NoneFeatureConfiguration>  {

    public StonePillarFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        LevelAccessor level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        if (!level.isEmptyBlock(pos) || !level.getBlockState(pos.below()).is(SoilBlocks.BLOOD_GRASS_BLOCK.get().defaultBlockState().getBlock())){
            return false;
        } else {
            level.setBlock(pos, StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
            level.setBlock(pos.above(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
            level.setBlock(pos.above().above(), StoneBlocks.BLOODY_STONE_TILES_BLOCK.get().defaultBlockState(), 2);
            if(random.nextFloat() < 0.25){
                level.setBlock(pos.north(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.west(), StoneBlocks.BLOODY_STONE_SLAB.get().defaultBlockState(), 2);
                level.setBlock(pos.below().north(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.below().south(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
            } else if(random.nextFloat() < 0.50){
                level.setBlock(pos.south(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.east(), StoneBlocks.BLOODY_STONE_SLAB.get().defaultBlockState(), 2);
                level.setBlock(pos.below().south(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.below().north(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
            } else if (random.nextFloat() < 0.75){
                level.setBlock(pos.east(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.south(), StoneBlocks.BLOODY_STONE_SLAB.get().defaultBlockState(), 2);
                level.setBlock(pos.below().east(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.below().west(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
            } else if (random.nextFloat() < 1.00){
                level.setBlock(pos.west(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.north(), StoneBlocks.BLOODY_STONE_SLAB.get().defaultBlockState(), 2);
                level.setBlock(pos.below().west(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
                level.setBlock(pos.below().east(), StoneBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(), 2);
            }

            return true;
        }
    }


}
