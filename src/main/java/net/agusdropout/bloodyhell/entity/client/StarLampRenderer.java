package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.block.entity.custom.StarLampBlockEntity;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class StarLampRenderer implements BlockEntityRenderer<StarLampBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("bloodyhell:textures/block/star_lamp.png");
    private static final float ROTATION_SPEED = 0.02f;
    private static final float PEAK_PULSE_AMOUNT = 0.1f;
    private static final float PEAK_PULSE_SPEED = 0.5f;

    public StarLampRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(StarLampBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float time = (Minecraft.getInstance().level.getGameTime() + partialTick) * ROTATION_SPEED;


        float baseScale = entity.getStarPoints();



        float pulse = (float)Math.abs(Math.sin(time * PEAK_PULSE_SPEED)) * PEAK_PULSE_AMOUNT;
        float tipScale = baseScale + pulse;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotation(time));
        poseStack.mulPose(Axis.XP.rotation(time * 0.7f));
        poseStack.scale(entity.getScale(), entity.getScale(), entity.getScale());

        VertexConsumer vertex = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));


        RenderHelper.renderStarLampIcosahedron(vertex, poseStack.last().pose(), poseStack.last().normal(),
                1.0f,
                tipScale,
                1.0f, 1.0f, 0.7f, 0.8f,
                15728880);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(StarLampBlockEntity entity) {
        return true;
    }
}