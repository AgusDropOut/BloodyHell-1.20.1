package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.client.layer.PlayerSkinLayer;
import net.agusdropout.bloodyhell.entity.custom.BlasphemousTwinDaggersCloneEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlasphemousTwinDaggersCloneRenderer extends GeoEntityRenderer<BlasphemousTwinDaggersCloneEntity> {

    public BlasphemousTwinDaggersCloneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlasphemousTwinDaggersCloneModel());

        addRenderLayer(new PlayerSkinLayer(this));
    }

    @Override
    public void renderRecursively(PoseStack stack, BlasphemousTwinDaggersCloneEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {


        if (bone.getName().contains("dagger")) {
            stack.pushPose();


            stack.translate(0, 0, 0.5);

            super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            stack.popPose();
            return;
        }

        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}