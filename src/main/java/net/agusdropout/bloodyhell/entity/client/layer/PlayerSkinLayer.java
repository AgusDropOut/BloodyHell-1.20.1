package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.custom.BlasphemousTwinDaggersCloneEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;


public class PlayerSkinLayer extends GeoRenderLayer<BlasphemousTwinDaggersCloneEntity> {

    public PlayerSkinLayer(GeoEntityRenderer<BlasphemousTwinDaggersCloneEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, BlasphemousTwinDaggersCloneEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        Player owner = animatable.getOwner();
        if (owner instanceof AbstractClientPlayer clientPlayer) {

            ResourceLocation skinLocation = clientPlayer.getSkinTextureLocation();
            RenderType skinRenderType = RenderType.entityTranslucent(skinLocation);


            for (GeoBone bone : bakedModel.topLevelBones()) {
                recursiveHide(bone);
            }


            getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, skinRenderType, bufferSource.getBuffer(skinRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }


    private void recursiveHide(GeoBone bone) {
        String name = bone.getName();



        if (name.contains("dagger") || name.contains("weapon")) {
            bone.setHidden(true);
        }

        if (name.contains("body") || name.contains("arm") || name.contains("head") || name.contains("entity")) {
            bone.setHidden(false);
        }


        for (GeoBone child : bone.getChildBones()) {
            recursiveHide(child);
        }
    }

}