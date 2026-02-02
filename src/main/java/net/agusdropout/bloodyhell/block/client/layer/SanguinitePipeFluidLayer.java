package net.agusdropout.bloodyhell.block.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguinitePipeBlock;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguinitePipeBlockEntity;
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

public class SanguinitePipeFluidLayer extends GeoRenderLayer<SanguinitePipeBlockEntity> {

    public SanguinitePipeFluidLayer(GeoRenderer<SanguinitePipeBlockEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, SanguinitePipeBlockEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        // --- 1. GET STATE ---
        BlockState state = animatable.getBlockState();
        boolean n = state.getValue(SanguinitePipeBlock.NORTH);
        boolean s = state.getValue(SanguinitePipeBlock.SOUTH);
        boolean e = state.getValue(SanguinitePipeBlock.EAST);
        boolean w = state.getValue(SanguinitePipeBlock.WEST);
        boolean u = state.getValue(SanguinitePipeBlock.UP);
        boolean d = state.getValue(SanguinitePipeBlock.DOWN);

        // --- 2. ANALYZE SHAPE ---
        // Is it a simple straight line?
        boolean isVerticalLine = (u || d) && !n && !s && !e && !w; // Vertical (Up/Down)
        boolean isZLine = (n || s) && !u && !d && !e && !w;        // North/South
        boolean isXLine = (e || w) && !u && !d && !n && !s;        // East/West

        // Guard: If it is NOT a simple line, it is a Complex Connector.
        // Connectors have their own model structure and generally hide the internal fluid.
        if (!isVerticalLine && !isZLine && !isXLine) {
            return; // SKIP RENDERING
        }

        // Also Guard: If more than 2 connections (T-Junction/Cross), it's a Connector.
        int connections = (n ? 1 : 0) + (s ? 1 : 0) + (e ? 1 : 0) + (w ? 1 : 0) + (u ? 1 : 0) + (d ? 1 : 0);
        if (connections > 2) {
            return; // SKIP RENDERING
        }

        // --- 3. RENDER FLUID ---
        // If we reached here, it is a simple pipe. Render the fluid!

        GeoBone fluidBone = bakedModel.getBone("fluid_inner").orElse(null);
        if (fluidBone == null) return;

        FluidStack fluidStack = animatable.getFluidInTank();
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

        // --- COORDINATES ---

        float radius = 3.95f / 16.0f;
        float length = 8.0f / 16.0f;

        // Offsets
        float horizontalPipeOffset = 8.0f / 16.0f;
        float verticalPipeOffset = 8.0f / 16.0f;

        // --- REPLACED AXIS LOGIC ---
        // Instead of asking for AXIS (which no longer exists), we use the boolean logic from above.

        float xMin, xMax, yMin, yMax, zMin, zMax;

        if (isVerticalLine) {
            // === VERTICAL CASE ===
            xMin = -radius;
            xMax = radius;
            zMin = -radius;
            zMax = radius;

            yMin = -length + verticalPipeOffset;
            yMax = length + verticalPipeOffset;
        } else {
            // === HORIZONTAL CASE (X or Z) ===
            xMin = -radius;
            xMax = radius;

            yMin = -radius + horizontalPipeOffset;
            yMax = radius + horizontalPipeOffset;

            zMin = -length;
            zMax = length;
        }

        // --- DRAWING FACES ---

        // TOP (Y+)
        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 1, 0);

        // BOTTOM (Y-)
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 1, 0);

        // NORTH (Z-)
        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 0, 0);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 0, 1);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 1, 1);
        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 1, 0);

        // SOUTH (Z+)
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 1, 0);

        // WEST (X-)
        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 0, 1);
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 1, 1);
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 1, 0);

        // EAST (X+)
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