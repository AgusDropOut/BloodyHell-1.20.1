package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericSpellBookModel<T extends BaseSpellBookItem<T>> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "geo/" + animatable.getSpellBookId() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "textures/item/" + animatable.getSpellBookId() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/" + animatable.getSpellBookId() + ".animation.json");
    }
}