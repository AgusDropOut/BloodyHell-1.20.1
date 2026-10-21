package net.agusdropout.bloodyhell.client.render.entity.generic;



import net.agusdropout.bloodyhell.client.render.entity.generic.InsightCreatureRenderer;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.client.render.entity.layer.MinionStripeLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;

public abstract class AbstractMinionRenderer<T extends AbstractMinionEntity> extends InsightCreatureRenderer<T> {

    public AbstractMinionRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model, false);
        this.addRenderLayer(new MinionStripeLayer<>(this));
    }
}