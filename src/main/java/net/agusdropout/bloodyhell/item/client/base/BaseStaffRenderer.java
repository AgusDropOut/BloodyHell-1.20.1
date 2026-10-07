package net.agusdropout.bloodyhell.item.client.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class BaseStaffRenderer extends GeoItemRenderer<BaseStaffItem> {

    // Weapon sway :L
    private static final float SWAY_RECOVERY_SPEED = 0.55f;
    private static final float SWAY_MAX_ANGLE = 25f;
    private static final float SWAY_MULT_HORIZONTAL = -1.1f;
    private static final float SWAY_MULT_VERTICAL = 0.1f;
    private static final float SWAY_MULT_ROLL = -0.4f;

    private ItemDisplayContext currentTransform = ItemDisplayContext.NONE;

    private float swayX = 0f;
    private float swayY = 0f;
    private float lastCameraYaw = 0f;
    private float lastCameraPitch = 0f;
    private boolean isInitialized = false;

    public BaseStaffRenderer() {
        super(new BaseStaffModel());
    }

    public BaseStaffRenderer(GeoModel<BaseStaffItem> modelProvider) {
        super(modelProvider);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.currentTransform = transformType;

        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.pushPose();

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && !mc.isPaused()) {

                float partialTick = mc.getFrameTime();
                float currentYaw = mc.player.getViewYRot(partialTick);
                float currentPitch = mc.player.getViewXRot(partialTick);

                if (!isInitialized) {
                    lastCameraYaw = currentYaw;
                    lastCameraPitch = currentPitch;
                    isInitialized = true;
                }

                float deltaYaw = Mth.wrapDegrees(currentYaw - lastCameraYaw);
                float deltaPitch = currentPitch - lastCameraPitch;

                lastCameraYaw = currentYaw;
                lastCameraPitch = currentPitch;

                swayX += deltaPitch;
                swayY += deltaYaw;

                swayX = Mth.lerp(SWAY_RECOVERY_SPEED, swayX, 0f);
                swayY = Mth.lerp(SWAY_RECOVERY_SPEED, swayY, 0f);

                float clampedX = Mth.clamp(swayX, -SWAY_MAX_ANGLE, SWAY_MAX_ANGLE);
                float clampedY = Mth.clamp(swayY, -SWAY_MAX_ANGLE, SWAY_MAX_ANGLE);

                poseStack.translate(0.15f, -0.6f, -0.4f);

                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(clampedY * SWAY_MULT_HORIZONTAL));
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(clampedX * SWAY_MULT_VERTICAL));
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(clampedY * SWAY_MULT_ROLL));

                poseStack.translate(0.0f, 0.4f, 0.0f);
            } else {
                poseStack.translate(0.15f, -0.2f, -0.4f);
            }

            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    @Override
    public void renderRecursively(PoseStack poseStack, BaseStaffItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (bone.getName().equals("arm")) {
            if (this.currentTransform != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                return;
            }

            if (Minecraft.getInstance().player != null) {
                poseStack.pushPose();
                RenderUtils.prepMatrixForBone(poseStack, bone);

                ResourceLocation skinTexture = Minecraft.getInstance().player.getSkinTextureLocation();
                RenderType customRenderType = RenderType.entityCutoutNoCull(skinTexture);
                VertexConsumer skinBuffer = bufferSource.getBuffer(customRenderType);

                this.renderCubesOfBone(poseStack, bone, skinBuffer, packedLight, packedOverlay, red, green, blue, alpha);

                VertexConsumer originalBuffer = bufferSource.getBuffer(renderType);
                this.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, originalBuffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

                poseStack.popPose();
                return;
            }
        }

        VertexConsumer safeBuffer = bufferSource.getBuffer(renderType);
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, safeBuffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}