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

public class BloodTreeLeafDecorator extends TreeDecorator {
    public static final BloodTreeLeafDecorator INSTANCE = new BloodTreeLeafDecorator();
    public static final Codec<BloodTreeLeafDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.BLOOD_TREE_LEAF_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        context.leaves().forEach((pos -> {
            if (random.nextFloat() < 0.5F) {
                if(context.isAir(pos.below())) {
                    context.setBlock(pos.below(), PlantBlocks.HANGING_BLOOD_TREE_LEAVES.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
                    context.setBlock(pos.below().below(), PlantBlocks.HANGING_BLOOD_TREE_LEAVES.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
                }
            }
        }));
    }

}
