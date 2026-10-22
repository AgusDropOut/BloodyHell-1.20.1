package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Random;

public class StarLampBlockEntity extends BlockEntity {
    private final float scale;
    private final int starPoints;
    private final float innerRatio;

    public StarLampBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STAR_LAMP.get(), pos, state);


        float[] sizes = {0.2f, 0.4f, 0.6f};
        this.scale = sizes[new Random(pos.asLong()).nextInt(sizes.length)];

        Random r = new Random(pos.asLong());
        this.starPoints = 1 + r.nextInt(4);
        this.innerRatio = 0.4f + r.nextFloat() * 0.4f;
    }

    public float getScale() {
        return scale;
    }

    public int getStarPoints() {
        return starPoints;
    }

    public float getInnerRatio() {
        return innerRatio;
    }
}
