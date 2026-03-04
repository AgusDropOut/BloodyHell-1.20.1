package net.agusdropout.bloodyhell.entity.minions.client.base;


import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;

public class GenericMinionRenderer<T extends AbstractMinionEntity> extends AbstractMinionRenderer<T> {

    public GenericMinionRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }
}
