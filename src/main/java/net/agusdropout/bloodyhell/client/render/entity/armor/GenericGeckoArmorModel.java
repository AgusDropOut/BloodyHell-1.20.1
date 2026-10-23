package net.agusdropout.bloodyhell.client.render.entity.armor;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericGeckoArmorModel extends GeoModel<BaseGeckoArmorItem> {

    @Override
    public ResourceLocation getModelResource(BaseGeckoArmorItem animatable) {
        return new ResourceLocation(BloodyHell.MODID, "geo/" + animatable.getId() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BaseGeckoArmorItem animatable) {
        return new ResourceLocation(BloodyHell.MODID, "textures/armor/" + animatable.getId() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(BaseGeckoArmorItem animatable) {
        if (!animatable.getLook().animated()) return null;
        return new ResourceLocation(BloodyHell.MODID, "animations/" + animatable.getId() + ".animation.json");
    }
}
