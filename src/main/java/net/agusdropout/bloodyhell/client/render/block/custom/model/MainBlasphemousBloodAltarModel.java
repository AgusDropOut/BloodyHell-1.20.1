package net.agusdropout.bloodyhell.client.render.block.custom.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBlasphemousBloodAltarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MainBlasphemousBloodAltarModel extends GeoModel<MainBlasphemousBloodAltarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MainBlasphemousBloodAltarBlockEntity blockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "geo/main_blasphemous_blood_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MainBlasphemousBloodAltarBlockEntity blockEntity) {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/main_blasphemous_blood_altar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MainBlasphemousBloodAltarBlockEntity blockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "animations/main_blasphemous_blood_altar.animation.json");
    }

    @Override
    public void setCustomAnimations(MainBlasphemousBloodAltarBlockEntity animatable, long instanceId, AnimationState<MainBlasphemousBloodAltarBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);


        GeoBone eye = (GeoBone) getAnimationProcessor().getBone("eye");
        if( eye != null && animatable.isActive()) {
            Minecraft mc = Minecraft.getInstance();



            double playerX = mc.player.getX();
            double playerZ = mc.player.getZ();
            double playerY = mc.player.getY() + mc.player.getEyeHeight();
            double boneX = eye.getWorldPosition().x;
            double boneY = eye.getWorldPosition().y;
            double boneZ = eye.getWorldPosition().z;

            double deltaX = playerX - boneX;
            double deltaY = playerY - boneY;
            double deltaZ = playerZ - boneZ;

            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if( distance < 10) {


                double yaw = Math.atan2(deltaX, deltaZ) - Math.PI;
                double pitch = Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) - Math.PI / 5;


                eye.setRotY((float) yaw);
                eye.setRotX((float) pitch);
            }


        }
    }
}