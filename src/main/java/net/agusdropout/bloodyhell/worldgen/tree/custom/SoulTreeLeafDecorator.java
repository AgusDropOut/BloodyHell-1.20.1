package net.agusdropout.bloodyhell.worldgen.tree.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import com.mojang.serialization.Codec;
import net.agusdropout.bloodyhell.worldgen.tree.ModTreeDecoratorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class SoulTreeLeafDecorator extends TreeDecorator {
    public static final SoulTreeLeafDecorator INSTANCE = new SoulTreeLeafDecorator();
    public static final Codec<SoulTreeLeafDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.SOUL_TREE_LEAF_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();

        context.leaves().forEach((pos -> {
            if (random.nextInt(3) == 0) {
                BlockPos downPos = pos.below();
                BlockPos down2Pos = downPos.below();
                if (context.isAir(downPos) && context.isAir(down2Pos)) {
                    context.setBlock(downPos, PlantBlocks.HANGING_SOUL_TREE_LEAVES.get().defaultBlockState());
                    context.setBlock(down2Pos, PlantBlocks.HANGING_SOUL_TREE_LEAVES.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
                }
            }
        }));
    }

}
