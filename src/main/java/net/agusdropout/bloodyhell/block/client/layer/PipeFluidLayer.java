package net.agusdropout.bloodyhell.block.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.base.AbstractPipeBlock;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;
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
        ResourceLocation fluidStill = fluidProps.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidStill);

        int color = fluidProps.getTintColor(fluidStack);
        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;

        poseStack.pushPose();
        RenderUtils.prepMatrixForBone(poseStack, fluidBone);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

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

        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 1, 0);

        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 1, 0);

        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 0, 0);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 0, 1);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 1, 1);
        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 1, 0);

        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 1, 0);

        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 0, 1);
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 1, 1);
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 1, 0);

        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 0, 0);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 1, 0);

        poseStack.popPose();
    }

    private void vertex(VertexConsumer builder, Matrix4f matrix, TextureAtlasSprite sprite,
                        float x, float y, float z,
                        float r, float g, float b, float a,
                        int light, int overlay,
                        float nx, float ny, float nz,
                        float uOffset, float vOffset) {

        float u = (uOffset > 0.5f) ? sprite.getU1() : sprite.getU0();
        float v = (vOffset > 0.5f) ? sprite.getV1() : sprite.getV0();

        builder.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }
}