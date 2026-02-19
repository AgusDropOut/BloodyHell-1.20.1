package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.PainThroneRegistry;
import net.agusdropout.bloodyhell.util.bones.BoneManipulator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PainThroneLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    public PainThroneLayer(GeoRenderer<T> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        if (!(animatable instanceof Entity entity)) return;

        float intensity = PainThroneRegistry.getIntensity(entity.getUUID());




        if (intensity > 0) {

            PainThroneRegistry.printActiveVictims();
            float time = entity.tickCount + partialTick;
            var processor = this.renderer.getGeoModel().getAnimationProcessor();

            for (CoreGeoBone coreBone : processor.getRegisteredBones()) {
                GeoBone bone = (GeoBone) coreBone;



                BoneManipulator.applyGeckoTwitch(bone, time, intensity * 0.15f);


            }
        } else {
            var processor = this.renderer.getGeoModel().getAnimationProcessor();
            for (CoreGeoBone coreBone : processor.getRegisteredBones()) {
                GeoBone bone = (GeoBone) coreBone;

                bone.setPosX(bone.getInitialSnapshot().getOffsetX());
                bone.setPosY(bone.getInitialSnapshot().getOffsetY());
                bone.setRotX(bone.getInitialSnapshot().getRotX());
                bone.setRotZ(bone.getInitialSnapshot().getRotZ());
            }
        }
    }
}