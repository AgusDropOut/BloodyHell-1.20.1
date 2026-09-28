package net.agusdropout.bloodyhell.item.client.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class BaseStaffRenderer extends GeoItemRenderer<BaseStaffItem> {


    private Matrix4f capturedArmMatrix = null;

    public BaseStaffRenderer() {
        super(new BaseStaffModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.capturedArmMatrix = null;


        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);

        if (this.capturedArmMatrix != null && (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                poseStack.pushPose();


                poseStack.last().pose().set(this.capturedArmMatrix);


                boolean isRightHand = transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
                float offsetX = 1.0f / 16.0f ;
                float offsetZ = 2.0f / 16.0f ;
                float offsetY = 5f / 16.0f;

                poseStack.translate(offsetX, offsetY, offsetZ);



                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));

                renderPlayerArm(poseStack, bufferSource, packedLight, mc.player, isRightHand);

                poseStack.popPose();
            }
        }
    }


    @Override
    public void renderRecursively(PoseStack poseStack, BaseStaffItem animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource,
                                  VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, float red, float green, float blue, float alpha) {

        if (bone.getName().equals("right_arm_anchor")) {

            poseStack.pushPose();
            RenderUtils.prepMatrixForBone(poseStack, bone);
            this.capturedArmMatrix = new Matrix4f(poseStack.last().pose());
            poseStack.popPose();

            return;
        }


        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }


    private void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, boolean rightArm) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super AbstractClientPlayer> renderer = dispatcher.getRenderer(player);

        if (renderer instanceof PlayerRenderer playerRenderer) {
            if (rightArm) {
                playerRenderer.renderRightHand(poseStack, buffer, packedLight, player);
            } else {
                playerRenderer.renderLeftHand(poseStack, buffer, packedLight, player);
            }
        }
    }
}