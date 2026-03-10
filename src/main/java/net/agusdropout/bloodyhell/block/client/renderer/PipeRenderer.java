package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.base.AbstractPipeBlock;

import net.agusdropout.bloodyhell.block.client.model.PipeModel;
import net.agusdropout.bloodyhell.block.client.layer.PipeFluidLayer;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PipeRenderer<T extends AbstractPipeBlockEntity> extends GeoBlockRenderer<T> {

    public PipeRenderer(BlockEntityRendererProvider.Context context) {
        super(new PipeModel<>());
        addRenderLayer(new PipeFluidLayer<>(this));
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        BlockState state = animatable.getBlockState();
        boolean n = state.getValue(AbstractPipeBlock.NORTH);
        boolean s = state.getValue(AbstractPipeBlock.SOUTH);
        boolean e = state.getValue(AbstractPipeBlock.EAST);
        boolean w = state.getValue(AbstractPipeBlock.WEST);
        boolean u = state.getValue(AbstractPipeBlock.UP);
        boolean d = state.getValue(AbstractPipeBlock.DOWN);

        model.getBone("northCon").ifPresent(b -> b.setHidden(!n));
        model.getBone("southCon").ifPresent(b -> b.setHidden(!s));
        model.getBone("eastCon").ifPresent(b -> b.setHidden(!e));
        model.getBone("westCon").ifPresent(b -> b.setHidden(!w));

        boolean isXAxis = (e || w) && !n && !s && !u && !d;

        if (isXAxis) {
            model.getBone("upCon").ifPresent(b -> b.setHidden(!u));
            model.getBone("downCon").ifPresent(b -> b.setHidden(!d));
            model.getBone("westCon").ifPresent(b -> b.setHidden(!n));
            model.getBone("eastCon").ifPresent(b -> b.setHidden(!s));
        } else {
            model.getBone("upCon").ifPresent(b -> b.setHidden(!u));
            model.getBone("downCon").ifPresent(b -> b.setHidden(!d));
            model.getBone("eastCon").ifPresent(b -> b.setHidden(!e));
            model.getBone("westCon").ifPresent(b -> b.setHidden(!w));
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        BlockState state = getAnimatable().getBlockState();
        boolean n = state.getValue(AbstractPipeBlock.NORTH);
        boolean s = state.getValue(AbstractPipeBlock.SOUTH);
        boolean e = state.getValue(AbstractPipeBlock.EAST);
        boolean w = state.getValue(AbstractPipeBlock.WEST);
        boolean u = state.getValue(AbstractPipeBlock.UP);
        boolean d = state.getValue(AbstractPipeBlock.DOWN);

        boolean isXLine = (e || w) && !u && !d && !n && !s;

        if (isXLine) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }
    }
}