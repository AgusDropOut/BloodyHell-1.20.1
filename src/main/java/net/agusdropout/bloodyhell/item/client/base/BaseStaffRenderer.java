package net.agusdropout.bloodyhell.item.client.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.item.client.layer.BloodOrbLayer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class BaseStaffRenderer extends GeoItemRenderer<BaseStaffItem> {

    private ItemDisplayContext currentTransform = ItemDisplayContext.NONE;

    public BaseStaffRenderer() {
        super(new BaseStaffModel());
        this.addRenderLayer(new BloodOrbLayer(this));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        this.currentTransform = transformType;

        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.pushPose();
            poseStack.translate(0.15f, -0.2f, -0.4f);
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