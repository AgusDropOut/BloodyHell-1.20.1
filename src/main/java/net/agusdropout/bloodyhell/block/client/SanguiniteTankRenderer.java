package net.agusdropout.bloodyhell.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.custom.mechanism.ConnectionType;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguiniteTankBlock;
import net.agusdropout.bloodyhell.block.entity.custom.SanguiniteTankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class SanguiniteTankRenderer implements BlockEntityRenderer<SanguiniteTankBlockEntity> {

    public SanguiniteTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SanguiniteTankBlockEntity tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // 1. CHECK: Only render if this is the BOTTOM block of a stack
        if (tile.getLevel() != null) {
            if (tile.getLevel().getBlockState(tile.getBlockPos().below()).getBlock() instanceof SanguiniteTankBlock) {
                return;
            }
        }

        SanguiniteTankBlockEntity controller = tile.getController();

        if (controller == null) return;
        FluidStack fluidStack = controller.getFluid();

        if (fluidStack.isEmpty()) return;

        int totalCapacity = controller.getCapacity();
        if (totalCapacity <= 0) return;

        

        // 2. CALCULATE HEIGHT
        float fillPercentage = (float) fluidStack.getAmount() / (float) totalCapacity;
        int localHeight = tile.getLocalHeight();
        float maxPhysicalHeight = localHeight - (0.0625f * 2) - 0.002f;
        float renderHeight = fillPercentage * maxPhysicalHeight;

        // 3. GET SPRITE & COLOR
        IClientFluidTypeExtensions fluidType = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation fluidStill = fluidType.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);

        int color = fluidType.getTintColor(fluidStack);
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        // 4. DYNAMIC BOUNDS CALCULATION
        BlockState state = tile.getBlockState();

        // Define the "Glass Thickness" inset
        float inset = 0.0625f + 0.001f; // 1 pixel + safety margin

        // Check North Face
        // If NONE -> It's an internal connection -> Fluid touches edge (0)
        // If SINGLE/LEFT/RIGHT -> It's a glass wall -> Fluid insets (0.0625)
        float zMin = (state.getValue(SanguiniteTankBlock.NORTH) == ConnectionType.NONE) ? 0.0f : inset;

        // Check South Face
        float zMax = (state.getValue(SanguiniteTankBlock.SOUTH) == ConnectionType.NONE) ? 1.0f : (1.0f - inset);

        // Check West Face
        float xMin = (state.getValue(SanguiniteTankBlock.WEST) == ConnectionType.NONE) ? 0.0f : inset;

        // Check East Face
        float xMax = (state.getValue(SanguiniteTankBlock.EAST) == ConnectionType.NONE) ? 1.0f : (1.0f - inset);

        float yBase = 0.0625f + 0.001f;

        // 5. DRAWING (Standard Logic)
        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();

        float remainingHeight = renderHeight;
        float currentY = 0;

        while (remainingHeight > 0) {
            float segmentHeight = Math.min(1.0f, remainingHeight);
            float yStart = yBase + currentY;
            float yEnd = yBase + currentY + segmentHeight;

            float uMin = sprite.getU0();
            float uMax = sprite.getU1();
            float vMin = sprite.getV(0);
            float vMax = sprite.getV(segmentHeight * 16);

            // NORTH
            builder.vertex(matrix, xMax, yStart, zMin).color(r, g, b, a).uv(uMin, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMin, yStart, zMin).color(r, g, b, a).uv(uMax, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMin, yEnd, zMin).color(r, g, b, a).uv(uMax, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMax, yEnd, zMin).color(r, g, b, a).uv(uMin, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();

            // SOUTH
            builder.vertex(matrix, xMin, yStart, zMax).color(r, g, b, a).uv(uMin, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMax, yStart, zMax).color(r, g, b, a).uv(uMax, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMax, yEnd, zMax).color(r, g, b, a).uv(uMax, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMin, yEnd, zMax).color(r, g, b, a).uv(uMin, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();

            // WEST
            builder.vertex(matrix, xMin, yStart, zMin).color(r, g, b, a).uv(uMin, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMin, yStart, zMax).color(r, g, b, a).uv(uMax, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMin, yEnd, zMax).color(r, g, b, a).uv(uMax, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMin, yEnd, zMin).color(r, g, b, a).uv(uMin, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();

            // EAST
            builder.vertex(matrix, xMax, yStart, zMax).color(r, g, b, a).uv(uMin, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMax, yStart, zMin).color(r, g, b, a).uv(uMax, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMax, yEnd, zMin).color(r, g, b, a).uv(uMax, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, xMax, yEnd, zMax).color(r, g, b, a).uv(uMin, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();

            remainingHeight -= segmentHeight;
            currentY += segmentHeight;
        }

        // TOP CAP (Uses dynamic bounds)
        float yTop = yBase + renderHeight;
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV(0);
        float vMax = sprite.getV(16);

        builder.vertex(matrix, xMin, yTop, zMin).color(r, g, b, a).uv(uMin, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, xMin, yTop, zMax).color(r, g, b, a).uv(uMin, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, xMax, yTop, zMax).color(r, g, b, a).uv(uMax, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, xMax, yTop, zMin).color(r, g, b, a).uv(uMax, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();

        // BOTTOM CAP (Visible from underneath)
        float yBottom = yBase;
        builder.vertex(matrix, xMin, yBottom, zMin).color(r, g, b, a).uv(uMin, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, xMax, yBottom, zMin).color(r, g, b, a).uv(uMax, vMin).uv2(packedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, xMax, yBottom, zMax).color(r, g, b, a).uv(uMax, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, xMin, yBottom, zMax).color(r, g, b, a).uv(uMin, vMax).uv2(packedLight).normal(0, 1, 0).endVertex();

        poseStack.popPose();
    }
}