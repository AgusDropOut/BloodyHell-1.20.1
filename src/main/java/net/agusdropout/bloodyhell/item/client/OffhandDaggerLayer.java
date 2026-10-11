package net.agusdropout.bloodyhell.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis; // En versiones anteriores puede ser Vector3f
import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class OffhandDaggerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public OffhandDaggerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        ItemStack mainHandItem = player.getMainHandItem();


        if (mainHandItem.getItem() instanceof BlasphemousTwinDaggerItem) {

            matrixStack.pushPose();


            this.getParentModel().leftArm.translateAndRotate(matrixStack);


            matrixStack.translate(
                    0.10,
                    0.50,
                    -0.05
            );


            matrixStack.mulPose(Axis.XP.rotationDegrees(-115f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(180f));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(10f));



            Minecraft.getInstance().getItemRenderer().renderStatic(
                    mainHandItem,
                    ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    matrixStack,
                    buffer,
                    player.level(),
                    0
            );

            matrixStack.popPose();
        }
    }
}