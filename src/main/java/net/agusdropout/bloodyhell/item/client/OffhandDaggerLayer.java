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
        // 1. Obtenemos el ítem de la mano principal
        ItemStack mainHandItem = player.getMainHandItem();

        // 2. Verificamos si es nuestra daga doble
        if (mainHandItem.getItem() instanceof BlasphemousTwinDaggerItem) {

            matrixStack.pushPose();

            // 3. Pegamos la renderización al hueso del BRAZO IZQUIERDO
            // Esto es crucial: Si Player Animator mueve el brazo, la daga se mueve con él.
            this.getParentModel().leftArm.translateAndRotate(matrixStack);

            // 4. Ajustes de Posición y Rotación
            // Estos valores son "a ojo", tendrás que ajustarlos ligeramente si la daga queda flotando o chueca.
            matrixStack.translate(-0.04, 0.65, 0.1); // Mueve la daga hacia la palma
            matrixStack.mulPose(Axis.XP.rotationDegrees(-90f)); // Rota para que apunte adelante
            matrixStack.mulPose(Axis.YP.rotationDegrees(180f)); // Rota para que el filo mire afuera (si es necesario)

            // 5. Renderizamos el ítem como si estuviera en la mano izquierda
            // Nota: Usamos THIRD_PERSON_LEFT_HAND para que el juego sepa cómo tratarlo
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