package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.entity.custom.BlasphemousTwinDaggersCloneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousTwinDaggersCloneModel extends GeoModel<BlasphemousTwinDaggersCloneEntity> {
    @Override
    public ResourceLocation getModelResource(BlasphemousTwinDaggersCloneEntity object) {
        // Archivo: assets/bloodyhell/geo/blasphemous_twin_daggers_clone.geo.json
        return new ResourceLocation("bloodyhell", "geo/blasphemous_twin_daggers_clone.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousTwinDaggersCloneEntity object) {
        // Textura base (Dagas): assets/bloodyhell/textures/entity/blasphemous_twin_daggers_clone.png
        return new ResourceLocation("bloodyhell", "textures/entity/blasphemous_twin_daggers_clone.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousTwinDaggersCloneEntity animatable) {
        // Animación: assets/bloodyhell/animations/blasphemous_twin_daggers_clone.animation.json
        return new ResourceLocation("bloodyhell", "animations/blasphemous_twin_daggers_clone.animation.json");
    }
}