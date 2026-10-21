package net.agusdropout.bloodyhell.client.render.block.layer;

import net.agusdropout.bloodyhell.client.render.block.FluidBoxRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.base.AbstractPipeBlock;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class PipeFluidLayer<T extends AbstractPipeBlockEntity> extends GeoRenderLayer<T> {

    public PipeFluidLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        BlockState state = animatable.getBlockState();
        boolean n = state.getValue(AbstractPipeBlock.NORTH);
        boolean s = state.getValue(AbstractPipeBlock.SOUTH);
        boolean e = state.getValue(AbstractPipeBlock.EAST);
        boolean w = state.getValue(AbstractPipeBlock.WEST);
        boolean u = state.getValue(AbstractPipeBlock.UP);
        boolean d = state.getValue(AbstractPipeBlock.DOWN);

        boolean isVerticalLine = (u || d) && !n && !s && !e && !w;
        boolean isZLine = (n || s) && !u && !d && !e && !w;
        boolean isXLine = (e || w) && !u && !d && !n && !s;

        if (!isVerticalLine && !isZLine && !isXLine) return;

        int connections = (n ? 1 : 0) + (s ? 1 : 0) + (e ? 1 : 0) + (w ? 1 : 0) + (u ? 1 : 0) + (d ? 1 : 0);
        if (connections > 2) return;

        GeoBone fluidBone = bakedModel.getBone("fluid_inner").orElse(null);
        if (fluidBone == null) return;

        FluidStack fluidStack = animatable.getFluidTank().getFluid();
        if (fluidStack.isEmpty()) return;

        IClientFluidTypeExtensions fluidProps = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = FluidBoxRenderer.blockSprite(fluidProps.getStillTexture(fluidStack));
        int color = fluidProps.getTintColor(fluidStack);

        poseStack.pushPose();
        RenderUtils.prepMatrixForBone(poseStack, fluidBone);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());

        float radius = 3.95f / 16.0f;
        float length = 8.0f / 16.0f;
        float horizontalPipeOffset = 8.0f / 16.0f;
        float verticalPipeOffset = 8.0f / 16.0f;

        float xMin, xMax, yMin, yMax, zMin, zMax;

        if (isVerticalLine) {
            xMin = -radius;
            xMax = radius;
            zMin = -radius;
            zMax = radius;
            yMin = -length + verticalPipeOffset;
            yMax = length + verticalPipeOffset;
        } else {
            xMin = -radius;
            xMax = radius;
            yMin = -radius + horizontalPipeOffset;
            yMax = radius + horizontalPipeOffset;
            zMin = -length;
            zMax = length;
        }

        FluidBoxRenderer.drawBox(builder, poseStack.last(), sprite, color,
                xMin, yMin, zMin, xMax, yMax, zMax, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
