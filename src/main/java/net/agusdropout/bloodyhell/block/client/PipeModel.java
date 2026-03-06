package net.agusdropout.bloodyhell.block.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.AbstractPipeBlock;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class PipeModel<T extends AbstractPipeBlockEntity> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T animatable) {
        BlockState state = animatable.getBlockState();
        String pipeId = ((AbstractPipeBlock) state.getBlock()).getPipeId();

        boolean n = state.getValue(AbstractPipeBlock.NORTH);
        boolean s = state.getValue(AbstractPipeBlock.SOUTH);
        boolean e = state.getValue(AbstractPipeBlock.EAST);
        boolean w = state.getValue(AbstractPipeBlock.WEST);
        boolean u = state.getValue(AbstractPipeBlock.UP);
        boolean d = state.getValue(AbstractPipeBlock.DOWN);

        int connections = (n ? 1 : 0) + (s ? 1 : 0) + (e ? 1 : 0) + (w ? 1 : 0) + (u ? 1 : 0) + (d ? 1 : 0);
        boolean isVerticalLine = (u || d) && !n && !s && !e && !w;
        boolean isZLine = (n || s) && !u && !d && !e && !w;
        boolean isXLine = (e || w) && !u && !d && !n && !s;

        if (isVerticalLine && connections <= 2) {
            return new ResourceLocation(BloodyHell.MODID, "geo/" + pipeId + "_vertical.geo.json");
        }
        if ((isZLine || isXLine) && connections <= 2) {
            return new ResourceLocation(BloodyHell.MODID, "geo/" + pipeId + ".geo.json");
        }

        boolean verticalBase = (u && d) || (!n && !s && !e && !w && (u || d));
        if (verticalBase) {
            return new ResourceLocation(BloodyHell.MODID, "geo/" + pipeId + "_vertical_con.geo.json");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "geo/" + pipeId + "_horizontal_con.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        String pipeId = ((AbstractPipeBlock) animatable.getBlockState().getBlock()).getPipeId();
        String modelPath = getModelResource(animatable).getPath();

        if (modelPath.contains("_con")) {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/" + pipeId + "_con.png");
        }
        return new ResourceLocation(BloodyHell.MODID, "textures/block/" + pipeId + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        String pipeId = ((AbstractPipeBlock) animatable.getBlockState().getBlock()).getPipeId();
        return new ResourceLocation(BloodyHell.MODID, "animations/" + pipeId + ".animation.json");
    }
}