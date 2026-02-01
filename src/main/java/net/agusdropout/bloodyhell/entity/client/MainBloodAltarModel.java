package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.custom.MainBloodAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MainBloodAltarModel extends GeoModel<MainBloodAltarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MainBloodAltarBlockEntity bloodAltarBlockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "geo/main_blood_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MainBloodAltarBlockEntity bloodAltarBlockEntity) {
        if (bloodAltarBlockEntity.isActive()) {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/main_blood_altar_active.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/main_blood_altar.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(MainBloodAltarBlockEntity bloodAltarBlockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "animations/main_blood_altar.animation.json");
    }
}
