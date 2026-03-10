package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.client.model.BlasphemousBloodAltarModel;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BlasphemousBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BlasphemousBloodAltarRenderer extends GeoBlockRenderer<BlasphemousBloodAltarBlockEntity> {
    public BlasphemousBloodAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new BlasphemousBloodAltarModel());
    }

    @Override
    public void actuallyRender(PoseStack pPoseStack, BlasphemousBloodAltarBlockEntity pBlockEntity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(pPoseStack, pBlockEntity, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        pPoseStack.pushPose();

        int items = pBlockEntity.getItemHandler().getSlots();
        if (items <= 0) return;

        float anglePer = 360F / items;
        double time = ClientTickHandler.ticksInGame + partialTick;

        for (int i = 0; i < items; i++) {
            ItemStack stack = pBlockEntity.getItemHandler().getStackInSlot(i);

            if (!stack.isEmpty()) {
                pPoseStack.pushPose();

                pPoseStack.translate(0, 1.5, 0);

                float currentAngle = (anglePer * i) + (float) (time % 360);
                pPoseStack.mulPose(Axis.YP.rotationDegrees(currentAngle));

                pPoseStack.translate(0.65F, 0F, 0F);

                pPoseStack.mulPose(Axis.YP.rotationDegrees(45F + (float) (time % 360)));

                float levitationOffset = (float) Math.sin((time % 360) / 10F) * 0.05F;
                pPoseStack.translate(0F, levitationOffset, 0F);

                pPoseStack.scale(0.5F, 0.5F, 0.5F);

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        stack,
                        ItemDisplayContext.FIXED,
                        packedLight,
                        packedOverlay,
                        pPoseStack,
                        bufferSource,
                        pBlockEntity.getLevel(),
                        0
                );

                pPoseStack.popPose();
            }
        }

        pPoseStack.popPose();
    }
}