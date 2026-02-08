package net.agusdropout.bloodyhell.block.custom.plant;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.base.BaseGemSproutBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class BloodGemSproutBlock extends BaseGemSproutBlock {

    public BloodGemSproutBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos posBelow = pos.below();
        BlockState stateBelow = level.getBlockState(posBelow);
        boolean isValidSoil = stateBelow.is(BlockTags.DIRT) || stateBelow.is(ModBlocks.BLOOD_GRASS.get()) || stateBelow.is(ModBlocks.BLOOD_DIRT_BLOCK.get());
        return isValidSoil;
    }


}