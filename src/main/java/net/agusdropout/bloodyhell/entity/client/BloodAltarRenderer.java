package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.entity.BloodAltarBlockEntity;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BloodAltarRenderer extends GeoBlockRenderer<BloodAltarBlockEntity> {
    public BloodAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new BloodAltarModel());
    }

    @Override
    public void actuallyRender(PoseStack pPoseStack, BloodAltarBlockEntity pBlockEntity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Renders the altar model first
        super.actuallyRender(pPoseStack, pBlockEntity, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        pPoseStack.pushPose();

        // 1. Initial configuration
        int items = 3;
        float anglePer = 360F / items;
        double time = ClientTickHandler.ticksInGame + partialTick;

        // 2. Iterate over items
        for (int i = 0; i < items; i++) {
            ItemStack stack = pBlockEntity.getItemHandler().getStackInSlot(i);

            if (!stack.isEmpty()) {
                pPoseStack.pushPose();

                // A. Move to block center (Rotation pivot)
                // In 1.20.1 GeckoLib, (0,0,0) is usually the corner. (0.5, Y, 0.5) is the center.
                pPoseStack.translate(0, 1.5, 0);

                // B. Orbital rotation (Rotate around Y center)
                // Uses native Axis.YP (Y Positive)
                float currentAngle = (anglePer * i) + (float) (time % 360);
                pPoseStack.mulPose(Axis.YP.rotationDegrees(currentAngle));

                // C. Move outwards (Circle radius)
                // Only moves in X. Moving X and Z simultaneously without rotation creates an ellipse.
                // 0.65F determines the distance from the center.
                pPoseStack.translate(0.65F, 0F, 0F);

                // D. Item rotation around its own axis
                // This makes the item spin while orbiting.
                pPoseStack.mulPose(Axis.YP.rotationDegrees(45F + (float) (time % 360)));

                // E. Levitation Effect (Bobbing)
                float levitationOffset = (float) Math.sin((time % 360) / 10F) * 0.05F;
                pPoseStack.translate(0F, levitationOffset, 0F);

                // F. Scale and Rendering
                // FIXED is used instead of GROUND because GROUND applies an offset intended for floor items.
                // FIXED centers the item at (0,0,0).
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