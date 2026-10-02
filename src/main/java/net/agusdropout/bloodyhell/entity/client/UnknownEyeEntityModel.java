package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.client.animations.CrystalPillarAnimations;
import net.agusdropout.bloodyhell.entity.client.animations.UnknownEyeEntityAnimations;
import net.agusdropout.bloodyhell.entity.custom.CrimsonRavenEntity;
import net.agusdropout.bloodyhell.entity.custom.CrystalPillar;
import net.agusdropout.bloodyhell.entity.custom.UnknownEyeEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class UnknownEyeEntityModel extends GeoModel<UnknownEyeEntity> {
    @Override
    public ResourceLocation getModelResource(UnknownEyeEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/unknown_eye_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(UnknownEyeEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/unknown_eye_entity.png");
    }

    @Override
    public ResourceLocation getAnimationResource(UnknownEyeEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/unknown_eye_entity.animation.json");
    }
    @Override
    public void setCustomAnimations(UnknownEyeEntity animatable, long instanceId, AnimationState<UnknownEyeEntity> animationState) {

        CoreGeoBone head = getAnimationProcessor().getBone("eye");
        if (head != null) {

            float lookPitch = animatable.getLookPitch();
            float lookYaw = animatable.getLookYaw();

            head.setRotX(-lookPitch * Mth.DEG_TO_RAD);
            head.setRotY(-lookYaw * Mth.DEG_TO_RAD);



        }
    }
}
