package net.agusdropout.bloodyhell.client.render.item.custom.renderer;


import net.agusdropout.bloodyhell.client.render.item.generic.GenericGeckoItemModel;
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
        super(new GenericGeckoItemModel<>());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {


        GeoModel<BlasphemousTwinDaggerItem> model = this.getGeoModel();

        GeoBone leftDaggerBone = model.getBone("left_dagger").orElse(null);

        if (leftDaggerBone != null) {

            if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                leftDaggerBone.setHidden(false);
            }

            else if (transformType == ItemDisplayContext.GUI || transformType == ItemDisplayContext.GROUND || transformType == ItemDisplayContext.FIXED) {
                leftDaggerBone.setHidden(false);
            }

            else {
                leftDaggerBone.setHidden(true);
            }
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}