package net.agusdropout.bloodyhell.item.client.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.util.visuals.manager.BloodOrbRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BloodOrbLayer extends GeoRenderLayer<BaseStaffItem> {

    public BloodOrbLayer(GeoRenderer<BaseStaffItem> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, BaseStaffItem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.options.getCameraType().isFirstPerson() || mc.options.getCameraType().isMirrored()) {
            bakedModel.getBone("orb").ifPresent(orbBone -> {
                poseStack.pushPose();


                Matrix4f m = orbBone.getModelSpaceMatrix();
                poseStack.last().pose().mul(m);


                float pivotX = orbBone.getPivotX() / 16.0f;
                float pivotY = orbBone.getPivotY() / 16.0f;
                float pivotZ = orbBone.getPivotZ() / 16.0f;

                poseStack.translate(pivotX, pivotY, pivotZ);



                Matrix4f boneMat = new Matrix4f(poseStack.last().pose());
                Matrix4f handProj = new Matrix4f(RenderSystem.getProjectionMatrix());

                org.joml.Matrix3f boneRotation = new org.joml.Matrix3f();
                boneMat.get3x3(boneRotation);

                BloodOrbRenderManager.addOrbData(boneMat, handProj, boneRotation);

                poseStack.popPose();
            });
        }
    }
}