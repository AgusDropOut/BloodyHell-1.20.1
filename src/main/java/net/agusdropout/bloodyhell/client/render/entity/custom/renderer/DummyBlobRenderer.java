package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.agusdropout.bloodyhell.block.entity.custom.engine.RhnullBloodEngineBlockEntity;
import net.agusdropout.bloodyhell.client.render.ModRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix4f;

public class DummyBlobRenderer implements BlockEntityRenderer<RhnullBloodEngineBlockEntity> {

    public DummyBlobRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(RhnullBloodEngineBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.getRaymarchedBlob());

        poseStack.pushPose();

        poseStack.translate(0.5, 1.5, 0.5);

        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        Matrix4f pose = poseStack.last().pose();

        float s = 1.0f;
        consumer.vertex(pose, -s, -s, 0).uv(0, 1).endVertex();
        consumer.vertex(pose,  s, -s, 0).uv(1, 1).endVertex();
        consumer.vertex(pose,  s,  s, 0).uv(1, 0).endVertex();
        consumer.vertex(pose, -s,  s, 0).uv(0, 0).endVertex();

        poseStack.popPose();
    }
}
