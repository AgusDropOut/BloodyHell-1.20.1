package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import net.agusdropout.bloodyhell.client.render.block.FluidBoxRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.custom.altar.BloodAltarBlock;
import net.agusdropout.bloodyhell.block.entity.base.AbstractAltarPedestalBlockEntity;
import net.agusdropout.bloodyhell.block.entity.base.IChargableAltarEntity;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class AltarRenderHelper {

    public static <T extends AbstractAltarPedestalBlockEntity & IChargableAltarEntity> void renderExtras(
            T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        double time = ClientTickHandler.ticksInGame + partialTick;

        if (blockEntity.getBlockState().hasProperty(BloodAltarBlock.MAINCHARGED) &&
                blockEntity.getBlockState().getValue(BloodAltarBlock.MAINCHARGED)) {
            renderCorruptedBlood(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }

        int maxItems = blockEntity.getMaxItemCount();
        if (maxItems <= 0) return;

        float anglePer = 360F / maxItems;

        for (int i = 0; i < maxItems; i++) {
            ItemStack stack = blockEntity.getItemHandler().getStackInSlot(i);

            if (!stack.isEmpty()) {
                poseStack.pushPose();

                if(!blockEntity.isGeckoLib()) {
                    poseStack.translate(0.5D, 1.25D, 0.5D);
                } else {
                    poseStack.translate(0, 1.70D, 0);
                }



                if (maxItems == 1) {
                    poseStack.mulPose(Axis.YP.rotationDegrees((float) (time % 360)));
                    float levitationOffset = (float) Math.sin((time % 360) / 10F) * 0.05F;
                    poseStack.translate(0F, levitationOffset, 0F);
                } else {
                    float currentAngle = (anglePer * i) + (float) (time % 360);
                    poseStack.mulPose(Axis.YP.rotationDegrees(currentAngle));
                    poseStack.translate(0.3D, 0D, 0D);
                    poseStack.mulPose(Axis.YP.rotationDegrees( 45 + (float) (time % 360)));
                    float levitationOffset = (float) Math.sin(((time + (i * 10)) % 360) / 10F) * 0.05F;
                    poseStack.translate(0F, levitationOffset, 0F);
                }

                poseStack.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getInstance().getItemRenderer().renderStatic(
                        stack, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                        poseStack, bufferSource, blockEntity.getLevel(), 0
                );
                poseStack.popPose();
            }
        }
    }

    private static <T extends AbstractAltarPedestalBlockEntity & IChargableAltarEntity> void renderCorruptedBlood(
            T be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        IClientFluidTypeExtensions fluidProps = IClientFluidTypeExtensions.of(be.getFluidType());
        TextureAtlasSprite sprite = FluidBoxRenderer.blockSprite(fluidProps.getStillTexture());
        int color = fluidProps.getTintColor();

        poseStack.pushPose();

        if(!be.isGeckoLib()) {
            poseStack.translate(0.5D, be.getFluidYOffset(), 0.5D);
        } else {
            poseStack.translate(0, be.getFluidYOffset(), 0);
        }

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());

        float xMin = -be.getFluidRadius();
        float xMax = be.getFluidRadius();
        float zMin = -be.getFluidRadius();
        float zMax = be.getFluidRadius();
        float yMin = 0;
        float yMax = be.getFluidHeight() + ((float)Math.sin((ClientTickHandler.ticksInGame + partialTick) * 0.1f) * 0.02f);


        FluidBoxRenderer.drawBox(builder, poseStack.last(), sprite, color,
                xMin, yMin, zMin, xMax, yMax, zMax, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
