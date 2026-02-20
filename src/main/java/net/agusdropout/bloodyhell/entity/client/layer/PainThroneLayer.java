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
import net.minecraft.util.RandomSource;


import java.util.List;


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
        var processor = this.renderer.getGeoModel().getAnimationProcessor();
        List<CoreGeoBone> allBones = processor.getRegisteredBones().stream().toList();

        if (intensity > 0) {
            float time = entity.tickCount + partialTick;
            for (CoreGeoBone coreBone : allBones) {
                BoneManipulator.applyGeckoTwitch((GeoBone) coreBone, time, intensity * 0.15f);
            }
        } else {
            resetBones(allBones);
        }


        List<PainThroneRegistry.BrokenBoneInfo> activeBreaks = PainThroneRegistry.getActiveBreaks(entity.getUUID());

        if (!activeBreaks.isEmpty() && !allBones.isEmpty()) {
            for (PainThroneRegistry.BrokenBoneInfo info : activeBreaks) {
                RandomSource randomSource = RandomSource.create(info.seed);
                int targetIndex = randomSource.nextInt(allBones.size());
                GeoBone victimBone = (GeoBone) allBones.get(targetIndex);

                BoneManipulator.applyExorcismTwist(victimBone, info.progress);
            }
        }
    }

    private void resetBones(List<CoreGeoBone> bones) {
        for (CoreGeoBone coreBone : bones) {
            GeoBone bone = (GeoBone) coreBone;
            var snapshot = bone.getInitialSnapshot();
            bone.setPosX(snapshot.getOffsetX());
            bone.setPosY(snapshot.getOffsetY());
            bone.setRotX(snapshot.getRotX());
            bone.setRotY(snapshot.getRotY());
            bone.setRotZ(snapshot.getRotZ());
        }
    }
}