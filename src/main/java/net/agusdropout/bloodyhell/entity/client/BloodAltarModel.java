package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.custom.BloodAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodAltarModel extends GeoModel<BloodAltarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(BloodAltarBlockEntity bloodAltarBlockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blood_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodAltarBlockEntity bloodAltarBlockEntity) {
        if (bloodAltarBlockEntity.isSomethingInside()) {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/blood_altar_iteminside.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/blood_altar.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(BloodAltarBlockEntity bloodAltarBlockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blood_altar.animation.json");
    }
}
