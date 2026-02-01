package net.agusdropout.bloodyhell.block.client.generic;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BaseGeckoBlockModel<T extends BaseGeckoBlockEntity> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "geo/" + animatable.getAssetPathName() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "textures/block/" + animatable.getAssetPathName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/" + animatable.getAssetPathName() + ".animation.json");
    }
}