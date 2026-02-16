package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

// No more <T extends ...>
public class GenericSpellBookModel extends GeoModel<BaseSpellBookItem<?>> {

    @Override
    public ResourceLocation getModelResource(BaseSpellBookItem<?> animatable) {
        return new ResourceLocation(BloodyHell.MODID, "geo/" + animatable.getSpellBookId() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BaseSpellBookItem<?> animatable) {
        return new ResourceLocation(BloodyHell.MODID, "textures/item/" + animatable.getSpellBookId() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(BaseSpellBookItem<?> animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/" + animatable.getSpellBookId() + ".animation.json");
    }
}