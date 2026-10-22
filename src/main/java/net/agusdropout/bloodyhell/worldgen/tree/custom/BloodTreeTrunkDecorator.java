package net.agusdropout.bloodyhell.worldgen.tree.custom;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import com.mojang.serialization.Codec;
import net.agusdropout.bloodyhell.block.base.BaseWallPlantBlock;
import net.agusdropout.bloodyhell.worldgen.tree.ModTreeDecoratorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class BloodTreeTrunkDecorator extends TreeDecorator {
    public static final BloodTreeTrunkDecorator INSTANCE = new BloodTreeTrunkDecorator();
    public static final Codec<BloodTreeTrunkDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.BLOOD_TREE_TRUNK_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();

        context.logs().forEach((pos) -> {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos newPos = pos.offset(direction.getStepX(), 0, direction.getStepZ());
                if (random.nextInt(25) == 0) {
                    if (context.isAir(newPos)) {
                        placeMushroom(context, newPos, direction);
                    }
                }
            }
        });
    }

    private void placeMushroom(TreeDecorator.Context context, BlockPos pos, Direction direction) {
        context.setBlock(pos, PlantBlocks.BLOOD_WALL_MUSHROOM_BLOCK.get().defaultBlockState().setValue(BaseWallPlantBlock.FACING, direction));
    }
}
