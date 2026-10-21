package net.agusdropout.bloodyhell.client.render.entity.generic;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GenericGlowingGeckoEntityRenderer<T extends Entity & GeoAnimatable>
        extends GenericGeckoEntityRenderer<T> {

    public GenericGlowingGeckoEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    public GenericGlowingGeckoEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model,
                                             float shadowRadius) {
        this(context, model);
        this.shadowRadius = shadowRadius;
    }
}
