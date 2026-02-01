package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.BloodAltarItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericGeckoBlockItemModel extends GeoModel<BaseGeckoBlockItem> {
    @Override
    public ResourceLocation getModelResource (BaseGeckoBlockItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "geo/" +animatable.getId()+".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource (BaseGeckoBlockItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "textures/block/" +animatable.getId()+".png");
    }

    @Override
    public ResourceLocation getAnimationResource (BaseGeckoBlockItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "animations/" +animatable.getId()+".animation.json");
    }
}