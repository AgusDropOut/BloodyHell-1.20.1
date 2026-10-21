package net.agusdropout.bloodyhell.client.render.entity.generic;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GenericGeckoEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {

    public GenericGeckoEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }
}
