package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import net.agusdropout.bloodyhell.client.render.block.FloatingItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteInfusorBlockEntity;
import net.agusdropout.bloodyhell.client.render.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class SanguiniteInfusorRenderer implements BlockEntityRenderer<SanguiniteInfusorBlockEntity> {

    private static final FloatingItemRenderer.Style ITEM_STYLE = new FloatingItemRenderer.Style(
            0.5, 1.25, 0.5, 0.7f, 2.0f, 0.0f, 0.0f, ItemDisplayContext.GROUND);

    public SanguiniteInfusorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SanguiniteInfusorBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ItemStack itemStack = be.getRenderStack();

        if (!itemStack.isEmpty()) {
            float time = be.getLevel().getGameTime() + partialTick;
            FloatingItemRenderer.render(ITEM_STYLE, itemStack, be.getLevel(), time, poseStack, buffer, packedLight, packedOverlay);
        }

        if (be.isWorking()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 1.35f, 0.5f);

            VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());

            Vector3f c = be.getHeartColor();

            RenderHelper.renderAtlasHeart(consumer, poseStack.last().pose(), null,
                    be.getLevel().getGameTime(), partialTick,
                    c.x(), c.y(), c.z(),
                    0.8f,
                    15728880
            );

            poseStack.popPose();
        }
    }
}