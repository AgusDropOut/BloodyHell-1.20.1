package net.agusdropout.bloodyhell.entity.minions.client.base;



import net.agusdropout.bloodyhell.entity.client.base.InsightCreatureRenderer;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.entity.minions.client.layer.MinionStripeLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;

public abstract class AbstractMinionRenderer<T extends AbstractMinionEntity> extends InsightCreatureRenderer<T> {

    public AbstractMinionRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model, false);
        this.addRenderLayer(new MinionStripeLayer<>(this));
    }
}