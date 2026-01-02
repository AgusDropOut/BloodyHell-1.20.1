package net.agusdropout.bloodyhell.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlasphemousTwinDaggersRenderer extends GeoItemRenderer<BlasphemousTwinDaggerItem> {
    public BlasphemousTwinDaggersRenderer() {
        super(new BlasphemousTwinDaggerModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        // Obtenemos el modelo actual
        GeoModel<BlasphemousTwinDaggerItem> model = this.getGeoModel();

        // Buscamos el hueso "left_dagger" tal como aparece en tu imagen de Blockbench
        GeoBone leftDaggerBone = model.getBone("left_dagger").orElse(null);

        if (leftDaggerBone != null) {
            // LÓGICA:
            // Si es PRIMERA PERSONA (Mano derecha o izquierda) -> MOSTRAR TODO
            if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                leftDaggerBone.setHidden(false);
            }
            // Si es GUI (Inventario) -> MOSTRAR TODO (para que se vea bonito en el slot)
            else if (transformType == ItemDisplayContext.GUI || transformType == ItemDisplayContext.GROUND || transformType == ItemDisplayContext.FIXED) {
                leftDaggerBone.setHidden(false);
            }
            // Si es TERCERA PERSONA -> OCULTAR LA IZQUIERDA
            // (Porque la renderizaremos manualmente pegada al brazo izquierdo del jugador)
            else {
                leftDaggerBone.setHidden(true);
            }
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}