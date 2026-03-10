package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CondenserRenderer<T extends AbstractCondenserBlockEntity> implements BlockEntityRenderer<T> {

    private static final float FLUID_RADIUS = 0.18f;
    private static final float FLUID_MAX_HEIGHT = 0.65f;
    private static final float FLUID_Y_OFFSET = 1.0f;

    public CondenserRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidStack fluidStack = blockEntity.fluidTank.getFluid();
        if (fluidStack.isEmpty()) return;

        float fillPercentage = (float) blockEntity.fluidTank.getFluidAmount() / blockEntity.fluidTank.getCapacity();

        IClientFluidTypeExtensions fluidProps = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation fluidStill = fluidProps.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);

        int color = fluidProps.getTintColor(fluidStack);
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        poseStack.pushPose();
        poseStack.translate(0.5D, FLUID_Y_OFFSET, 0.5D);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float xMin = -FLUID_RADIUS;
        float xMax = FLUID_RADIUS;
        float zMin = -FLUID_RADIUS;
        float zMax = FLUID_RADIUS;
        float yMin = 0;
        float yMax = (FLUID_MAX_HEIGHT * fillPercentage);

        vertex(builder, matrix, normal, sprite, xMin, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 0, 0);
        vertex(builder, matrix, normal, sprite, xMin, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 0, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 1, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 1, 0);

        vertex(builder, matrix, normal, sprite, xMin, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 0, 0);
        vertex(builder, matrix, normal, sprite, xMin, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 0, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 1, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 1, 0);

        vertex(builder, matrix, normal, sprite, xMax, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 0, 0);
        vertex(builder, matrix, normal, sprite, xMax, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 0, 1);
        vertex(builder, matrix, normal, sprite, xMin, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 1, 1);
        vertex(builder, matrix, normal, sprite, xMin, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 1, 0);

        vertex(builder, matrix, normal, sprite, xMin, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 0, 0);
        vertex(builder, matrix, normal, sprite, xMin, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 0, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 1, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 1, 0);

        vertex(builder, matrix, normal, sprite, xMin, yMax, zMin, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 0, 0);
        vertex(builder, matrix, normal, sprite, xMin, yMin, zMin, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 0, 1);
        vertex(builder, matrix, normal, sprite, xMin, yMin, zMax, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 1, 1);
        vertex(builder, matrix, normal, sprite, xMin, yMax, zMax, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 1, 0);

        vertex(builder, matrix, normal, sprite, xMax, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 0, 0);
        vertex(builder, matrix, normal, sprite, xMax, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 0, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 1, 1);
        vertex(builder, matrix, normal, sprite, xMax, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 1, 0);

        poseStack.popPose();
    }

    private void vertex(VertexConsumer builder, Matrix4f matrix, Matrix3f normal, TextureAtlasSprite sprite,
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
                .normal(normal, nx, ny, nz)
                .endVertex();
    }
}