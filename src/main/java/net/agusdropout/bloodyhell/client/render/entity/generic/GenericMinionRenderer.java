package net.agusdropout.bloodyhell.client.render.entity.generic;


import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.FailedSonOfTheUnknown;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class GenericMinionRenderer<T extends AbstractMinionEntity> extends AbstractMinionRenderer<T> {
    private static final String[] LOCATORS = {"particle", "particle1", "particle2"};
    public GenericMinionRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }


    protected void spawnLocatorParticles(T entity) {
        if (entity.getRandom().nextFloat() > 0.005f || !ClientInsightData.canSee(entity)) {
            return;
        }

        BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity));
        if (model == null) return;

        int color = entity.getStripeColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        Vector3f particleColor = new Vector3f(r, g, b);

        ChillFallingParticleOptions ocularParticle = new ChillFallingParticleOptions(
                particleColor,
                0.03f,
                40,
                20
        );


        for (String locatorName : LOCATORS) {
            if(entity.getRandom().nextFloat() > 0.7f) continue;
            model.getBone(locatorName).ifPresent(bone -> {
                Vector3d bonePos = bone.getWorldPosition();

                double absoluteX = bonePos.x;
                double absoluteY = bonePos.y;
                double absoluteZ = bonePos.z;

                ParticleHelper.spawn(entity.level(), ocularParticle, absoluteX, absoluteY, absoluteZ, 0, 0, 0);
            });
        }
    }
}
