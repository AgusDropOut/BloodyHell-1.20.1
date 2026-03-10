package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericGeckoItemModel extends GeoModel<BaseGeckoItem> {
    @Override
    public ResourceLocation getModelResource (BaseGeckoItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "geo/" +animatable.getId()+".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource (BaseGeckoItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "textures/item/" +animatable.getId()+".png");
    }

    @Override
    public ResourceLocation getAnimationResource (BaseGeckoItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "animations/" +animatable.getId()+".animation.json");
    }
}