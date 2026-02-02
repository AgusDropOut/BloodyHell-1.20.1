package net.agusdropout.bloodyhell.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteInfusorBlockEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class SanguiniteInfusorRenderer implements BlockEntityRenderer<SanguiniteInfusorBlockEntity> {

    public SanguiniteInfusorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SanguiniteInfusorBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ItemStack itemStack = be.getRenderStack();

        // 1. RENDER ITEM
        if (!itemStack.isEmpty()) {
            poseStack.pushPose();
            // Center the item floating above
            poseStack.translate(0.5f, 1.25f, 0.5f);
            poseStack.scale(0.7f, 0.7f, 0.7f);

            // Spin item slowly
            long time = be.getLevel().getGameTime();
            float rotation = (time + partialTick) * 2.0f;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight,
                    OverlayTexture.NO_OVERLAY, poseStack, buffer, be.getLevel(), 1);
            poseStack.popPose();
        }

        // 2. RENDER ATLAS HEART (If Active)
        // 2. RENDER ATLAS HEART (If Active)
        if (be.isWorking()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 1.35f, 0.5f);

            VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());

            // GET DYNAMIC COLOR
            Vector3f c = be.getHeartColor();

            // Pass color to RenderHelper
            RenderHelper.renderAtlasHeart(consumer, poseStack.last().pose(), null,
                    be.getLevel().getGameTime(), partialTick,
                    c.x(), c.y(), c.z(), // RGB
                    0.8f, // Alpha
                    15728880 // Light
            );

            poseStack.popPose();
        }
    }
}