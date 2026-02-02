package net.agusdropout.bloodyhell.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.agusdropout.bloodyhell.block.entity.custom.plant.BloodGemSproutBlockEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class BloodGemSproutRenderer implements BlockEntityRenderer<BloodGemSproutBlockEntity> {

    // --- MANUAL OFFSETS ---
    private static final float OFF_X = 0.5f; // Center of block X
    private static final float OFF_Z = -0.00f; // Center of block Z
    private static final float OFF_Y = 0.9f; // Height (0.5 is middle, 1.0 is top)
    private static final float SCALE_W = 0.20f;
    private static final float SCALE_H = 0.25f;

    // We need a texture for solid rendering.
    // If you don't have a specific gem texture, using "snow" or "white concrete" is a good cheat
    // because it provides a clean white surface we can tint red/blue/green.
    private static final ResourceLocation GEM_TEXTURE = new ResourceLocation("textures/block/white_concrete.png");
    // OR use your existing overlay if it is a pure white square:
    // private static final ResourceLocation GEM_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/block/blood_gem_sprout_overlay.png");

    public BloodGemSproutRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BloodGemSproutBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        // 1. Decode Color
        int c = be.getGemColor();
        float r = ((c >> 16) & 0xFF) / 255f;
        float g = ((c >> 8) & 0xFF) / 255f;
        float b = (c & 0xFF) / 255f;

        // 2. Setup Render Buffer
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(GEM_TEXTURE));

        // 3. Animation Logic
        float time = be.getLevel().getGameTime() + partialTick;

        // LEVITATION MATH:
        // Speed: 0.1f (Lower = Slower)
        // Amplitude: 0.05f (Lower = Smaller movement range)
        float bobOffset = Mth.sin(time * 0.1f) * 0.01f;

        // ROTATION (Optional, uncomment if you want it to spin while floating)
         float rotation = time * 1.2f;

        // 4. Apply Transformations
        // Add 'bobOffset' to the Y position
        poseStack.translate(OFF_X, OFF_Y + bobOffset, OFF_Z);

         poseStack.mulPose(Axis.YP.rotationDegrees(rotation)); // Uncomment to spin

        // 5. Render Octahedron
        // Outer Shell
        RenderHelper.renderOctahedron(
                consumer,
                poseStack.last().pose(),
                poseStack.last().normal(),
                0.25f,
                r, g, b, 1.0f,
                packedLight
        );

        // Inner Core (Optional detail)
        RenderHelper.renderOctahedron(
                consumer,
                poseStack.last().pose(),
                poseStack.last().normal(),
                0.1f,
                r, g, b, 1.0f,
                packedLight
        );

        poseStack.popPose();
    }
}