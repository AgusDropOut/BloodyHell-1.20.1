package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.entity.client.layer.RitekeeperHeartLayer;
import net.agusdropout.bloodyhell.particle.ModParticles; // IMPORT YOUR PARTICLES
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

public class RitekeeperRenderer extends GeoEntityRenderer<RitekeeperEntity> {

    public RitekeeperRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RitekeeperModel());
        this.addRenderLayer(new RitekeeperHeartLayer(this));
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(RitekeeperEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        GeoModel<RitekeeperEntity> model = this.getGeoModel();

        if (model != null) {
            Optional<GeoBone> steamBone = model.getBone("steam");

            if (steamBone.isPresent()) {
                Vector3d pos = steamBone.get().getWorldPosition();
                RandomSource rand = entity.level().getRandom();

                // 20% chance per frame (Subtle drip)
                if (rand.nextFloat() < 0.2f) {

                    double offsetX = (rand.nextDouble() - 0.5) * 0.2;
                    double offsetZ = (rand.nextDouble() - 0.5) * 0.2;

                    // BLOOD PULSAR PARTICLES
                    entity.level().addParticle(
                            ModParticles.BLOOD_PARTICLES.get(),
                            pos.x() + offsetX,
                            pos.y()-0.5,
                            pos.z() + offsetZ,
                            0,      // X Vel
                            -0.1,   // Y Vel (Consistent Downwards)
                            0       // Z Vel
                    );
                }
            }
        }
    }
}