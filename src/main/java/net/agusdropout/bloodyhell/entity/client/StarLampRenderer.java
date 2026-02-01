package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.block.entity.custom.StarLampBlockEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
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

        // Original logic: Base is static from block config, Tips pulse
        float baseScale = entity.getStarPoints(); // In old code this was 'PEAK_BASE_SCALE' used for tips

        // Wait, looking at old code:
        // tips[i] = baseVerts[i].scale(scale); where scale = PEAK_BASE_SCALE + pulse.
        // baseVerts were unit vector size (approx 1.0).
        // So actually: Inner radius is 1.0 (Unit Icosahedron), Outer radius is entity.getStarPoints() + Pulse.

        float pulse = (float)Math.abs(Math.sin(time * PEAK_PULSE_SPEED)) * PEAK_PULSE_AMOUNT;
        float tipScale = baseScale + pulse;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotation(time));
        poseStack.mulPose(Axis.XP.rotation(time * 0.7f));
        poseStack.scale(entity.getScale(), entity.getScale(), entity.getScale());

        VertexConsumer vertex = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        // Use the specialized helper method
        // Colors from old code: 255, 255, 180, 200 -> 1.0, 1.0, 0.7, 0.8
        // Light: 0xF000F0 -> 15728880 (Fullbright)

        RenderHelper.renderStarLampIcosahedron(vertex, poseStack.last().pose(), poseStack.last().normal(),
                1.0f,       // Base Scale (Inner)
                tipScale,   // Tip Scale (Outer + Pulse)
                1.0f, 1.0f, 0.7f, 0.8f, // Color (Pale Yellow)
                15728880);  // Full Brightness

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(StarLampBlockEntity entity) {
        return true;
    }
}