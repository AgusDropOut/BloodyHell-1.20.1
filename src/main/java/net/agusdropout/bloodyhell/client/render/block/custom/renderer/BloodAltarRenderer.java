package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BloodAltarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class BloodAltarRenderer implements BlockEntityRenderer<BloodAltarBlockEntity> {

    public BloodAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BloodAltarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        AltarRenderHelper.renderExtras(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}