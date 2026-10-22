package net.agusdropout.bloodyhell.worldgen.tree.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.worldgen.tree.ModTrunkPlacerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;


public class GiantBloodTrunkPlacer extends TrunkPlacer {
        public static final Codec<GiantBloodTrunkPlacer> CODEC = RecordCodecBuilder.create(giantBloodTrunkPlacerInstance ->
                trunkPlacerParts(giantBloodTrunkPlacerInstance).apply(giantBloodTrunkPlacerInstance, GiantBloodTrunkPlacer::new));
        public GiantBloodTrunkPlacer(int p_70268_, int p_70269_, int p_70270_) {
            super(p_70268_, p_70269_, p_70270_);

        }
    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.GIANT_BLOOD_TRUNK_PLACER.get();
    }
        @Override
        public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos,
                        BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
            List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
            OptionalInt optionalInt = OptionalInt.empty();


            int minHeight = 10;
            int height = minHeight + pRandom.nextInt(8);





            return list;
        }
    }

