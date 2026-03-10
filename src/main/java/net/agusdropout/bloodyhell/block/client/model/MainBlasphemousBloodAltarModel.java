package net.agusdropout.bloodyhell.block.client.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBlasphemousBloodAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MainBlasphemousBloodAltarModel extends GeoModel<MainBlasphemousBloodAltarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MainBlasphemousBloodAltarBlockEntity blockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "geo/main_blasphemous_blood_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MainBlasphemousBloodAltarBlockEntity blockEntity) {
        if (blockEntity.isActive()) {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/main_blasphemous_blood_altar_active.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/main_blasphemous_blood_altar.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(MainBlasphemousBloodAltarBlockEntity blockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "animations/main_blasphemous_blood_altar.animation.json");
    }
}