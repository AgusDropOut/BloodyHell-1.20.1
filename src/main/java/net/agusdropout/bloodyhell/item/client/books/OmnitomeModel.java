package net.agusdropout.bloodyhell.item.client.books;

import net.agusdropout.bloodyhell.BloodyHell;


import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OmnitomeModel extends GeoModel<OmnitomeItem> {
    @Override
    public ResourceLocation getModelResource (OmnitomeItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "geo/the_omnitome.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource (OmnitomeItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "textures/item/omnitome.png");
    }

    @Override
    public ResourceLocation getAnimationResource (OmnitomeItem animatable){
        return new ResourceLocation(BloodyHell.MODID, "animations/the_omnitome.animation.json");
    }
}
