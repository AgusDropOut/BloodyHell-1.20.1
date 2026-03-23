package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.client.layer.GenericEmissiveLayer;
import net.agusdropout.bloodyhell.block.client.model.BlasphemousBloodAltarModel;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BlasphemousBloodAltarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlasphemousBloodAltarRenderer extends GeoBlockRenderer<BlasphemousBloodAltarBlockEntity> {

    public BlasphemousBloodAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new BlasphemousBloodAltarModel());
        addRenderLayer(new GenericEmissiveLayer<>(this));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BlasphemousBloodAltarBlockEntity blockEntity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, blockEntity, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        AltarRenderHelper.renderExtras(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}