package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Random;

public class StarLampBlockEntity extends BlockEntity {
    private final float scale;
    private final int starPoints;    // número de puntas n
    private final float innerRatio;  // radio relativo de vértices internos

    public StarLampBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STAR_LAMP.get(), pos, state);

        // Tamaños posibles
        float[] sizes = {0.2f, 0.4f, 0.6f};
        this.scale = sizes[new Random(pos.asLong()).nextInt(sizes.length)];

        // Variante aleatoria de estrella
        Random r = new Random(pos.asLong());
        this.starPoints = 1 + r.nextInt(4);            // de 5 a 8 puntas
        this.innerRatio = 0.4f + r.nextFloat() * 0.4f; // ratio de vértices internos 0.4-0.8
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
