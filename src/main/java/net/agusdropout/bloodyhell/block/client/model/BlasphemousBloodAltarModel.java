package net.agusdropout.bloodyhell.block.client.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BlasphemousBloodAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousBloodAltarModel extends GeoModel<BlasphemousBloodAltarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(BlasphemousBloodAltarBlockEntity blockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blasphemous_blood_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousBloodAltarBlockEntity blockEntity) {
        if (blockEntity.isSomethingInside()) {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/blasphemous_blood_altar_iteminside.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/blasphemous_blood_altar.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousBloodAltarBlockEntity blockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blasphemous_blood_altar.animation.json");
    }
}