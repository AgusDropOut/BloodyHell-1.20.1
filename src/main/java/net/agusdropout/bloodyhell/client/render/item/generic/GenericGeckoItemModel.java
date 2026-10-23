package net.agusdropout.bloodyhell.client.render.item.generic;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.GeckoAsset;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericGeckoItemModel<T extends Item & GeoItem & GeckoAsset> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource (T animatable){
        return new ResourceLocation(BloodyHell.MODID, "geo/" +animatable.getId()+".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource (T animatable){
        return new ResourceLocation(BloodyHell.MODID, "textures/item/" +animatable.getId()+".png");
    }

    @Override
    public ResourceLocation getAnimationResource (T animatable){
        return new ResourceLocation(BloodyHell.MODID, "animations/" +animatable.getId()+".animation.json");
    }
}