package net.agusdropout.bloodyhell.worldgen.feature.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import com.mojang.serialization.Codec;
import net.agusdropout.bloodyhell.block.base.BloodScrapperPlantLeaves;
import net.agusdropout.bloodyhell.block.base.TallGrowingPlant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class BloodScrapperPlantFeature extends Feature<ProbabilityFeatureConfiguration> {
    private static final BlockState BLOOD_TRUNK;
    private static final BlockState BLOOD_FINALE_LARGE;
    private static final BlockState BLOOD_TOP_LARGE;
    private static final BlockState BLOOD_TOP_SMALL;

    public BloodScrapperPlantFeature(Codec<ProbabilityFeatureConfiguration> p_65137_) {
        super(p_65137_);
    }

    public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> p_159438_) {
        int $$1 = 0;
        BlockPos $$2 = p_159438_.origin();
        WorldGenLevel $$3 = p_159438_.level();
        RandomSource $$4 = p_159438_.random();
        ProbabilityFeatureConfiguration $$5 = (ProbabilityFeatureConfiguration)p_159438_.config();
        BlockPos.MutableBlockPos $$6 = $$2.mutable();
        BlockPos.MutableBlockPos $$7 = $$2.mutable();
        if ($$3.isEmptyBlock($$6)) {
            if (PlantBlocks.BLOOD_SCRAPPER_PLANT.get().defaultBlockState().canSurvive($$3, $$6)) {
                int $$8 = $$4.nextInt(12) + 5;
                int $$9;
                if ($$4.nextFloat() < $$5.probability) {
                    $$9 = $$4.nextInt(4) + 1;

                    for(int $$10 = $$2.getX() - $$9; $$10 <= $$2.getX() + $$9; ++$$10) {
                        for(int $$11 = $$2.getZ() - $$9; $$11 <= $$2.getZ() + $$9; ++$$11) {
                            int $$12 = $$10 - $$2.getX();
                            int $$13 = $$11 - $$2.getZ();
                            if ($$12 * $$12 + $$13 * $$13 <= $$9 * $$9) {
                                $$7.set($$10, $$3.getHeight(Heightmap.Types.WORLD_SURFACE, $$10, $$11) - 1, $$11);
                                if (isDirt($$3.getBlockState($$7))) {
                                    $$3.setBlock($$7, Blocks.PODZOL.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }

                for($$9 = 0; $$9 < $$8 && $$3.isEmptyBlock($$6); ++$$9) {
                    $$3.setBlock($$6, BLOOD_TRUNK, 2);
                    $$6.move(Direction.UP, 1);
                }

                if ($$6.getY() - $$2.getY() >= 3) {
                    $$3.setBlock($$6, BLOOD_FINALE_LARGE, 2);
                    $$3.setBlock($$6.move(Direction.DOWN, 1), BLOOD_TOP_LARGE, 2);
                    $$3.setBlock($$6.move(Direction.DOWN, 1), BLOOD_TOP_SMALL, 2);
                }
            }

            ++$$1;
        }

        return $$1 > 0;
    }

    static {
        BLOOD_TRUNK = (BlockState)((BlockState)((BlockState)PlantBlocks.BLOOD_SCRAPPER_PLANT.get().defaultBlockState().setValue(TallGrowingPlant.AGE, 1)).setValue(TallGrowingPlant.LEAVES, BloodScrapperPlantLeaves.NONE)).setValue(TallGrowingPlant.STAGE, 0);
        BLOOD_FINALE_LARGE = (BlockState)((BlockState)BLOOD_TRUNK.setValue(TallGrowingPlant.LEAVES, BloodScrapperPlantLeaves.LARGE)).setValue(TallGrowingPlant.STAGE, 1);
        BLOOD_TOP_LARGE = (BlockState)BLOOD_TRUNK.setValue(TallGrowingPlant.LEAVES, BloodScrapperPlantLeaves.LARGE);
        BLOOD_TOP_SMALL = (BlockState)BLOOD_TRUNK.setValue(TallGrowingPlant.LEAVES, BloodScrapperPlantLeaves.SMALL);
    }
}
