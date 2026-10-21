package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.custom.CinderAcolyteEntity;
import net.agusdropout.bloodyhell.client.render.entity.layer.BloodFireGeoLayer;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.agusdropout.bloodyhell.client.render.entity.generic.ConfigurableGeoModel;

public class CinderAcolyteRenderer extends GeoEntityRenderer<CinderAcolyteEntity> {

    public CinderAcolyteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ConfigurableGeoModel<>("cinder_acolyte"));
        addRenderLayer(new BloodFireGeoLayer<>(this));
    }

    @Override
    public void render(CinderAcolyteEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (!entity.isRemoved()) {
            spawnBoneParticles(entity, partialTick);
        }
    }

    private void spawnBoneParticles(CinderAcolyteEntity entity, float partialTick) {
        GeoModel<CinderAcolyteEntity> model = this.getGeoModel();

        RandomSource random = entity.getRandom();

        if (model == null) return;



        model.getBone("jetParticle").ifPresent(bone -> {
            Vector3d pos = bone.getWorldPosition();
            ParticleHelper.spawn(entity.level(), ParticleTypes.SMOKE,
                    pos.x, pos.y, pos.z,
                    (entity.getRandom().nextDouble() - 0.5) * 0.1, -0.1, (entity.getRandom().nextDouble() - 0.5) * 0.1);
        });


        model.getBone("staffParticle").ifPresent(bone -> {
            Vector3d pos = bone.getWorldPosition();
            RandomSource rand = entity.getRandom();

            if (rand.nextFloat() < 0.3f) {

                Vector3f color;
                float hueRoll = rand.nextFloat();

                if (hueRoll < 0.4f) {
                    color = new Vector3f(0.4f, 0.0f, 0.0f);
                } else if (hueRoll < 0.8f) {
                    color = new Vector3f(0.9f, 0.1f, 0.1f);
                } else {
                    color = new Vector3f(1.0f, 0.7f, 0.8f);
                }

                ParticleHelper.spawn(entity.level(),
                        new MagicParticleOptions(color, 0.3f, false, 25),
                        pos.x, pos.y, pos.z,
                        (rand.nextDouble() - 0.5) * 0.05, 0.02, (rand.nextDouble() - 0.5) * 0.05);

                if (rand.nextBoolean()) {
                    ParticleHelper.spawn(entity.level(), ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                            pos.x, pos.y, pos.z, 0, 0.02, 0);
                }
            }


            if (entity.isFlameAttacking()) {
                Vec3 dir = entity.getLookAngle();
                double speed = 0.6;
                double spread = 0.15;

                for (int i = 0; i < 4; i++) {
                    double vx = (dir.x * speed) + (rand.nextGaussian() * spread);
                    double vy = (dir.y * speed) + (rand.nextGaussian() * spread);
                    double vz = (dir.z * speed) + (rand.nextGaussian() * spread);

                    ParticleHelper.spawn(entity.level(), ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                            pos.x, pos.y, pos.z, vx, vy, vz);
                }

                for (int i = 0; i < 2; i++) {
                    double vx = (dir.x * speed * 0.8) + (rand.nextGaussian() * spread);
                    double vy = (dir.y * speed * 0.8) + (rand.nextGaussian() * spread);
                    double vz = (dir.z * speed * 0.8) + (rand.nextGaussian() * spread);

                    ParticleHelper.spawn(entity.level(), ModParticles.BLOOD_FLAME.get(),
                            pos.x, pos.y, pos.z, vx, vy, vz);
                }

                for (int i = 0; i < 3; i++) {
                    ParticleHelper.spawn(entity.level(), ParticleTypes.ASH,
                            pos.x + (rand.nextDouble() - 0.5) * 0.5,
                            pos.y + (rand.nextDouble() - 0.5) * 0.5,
                            pos.z + (rand.nextDouble() - 0.5) * 0.5,
                            dir.x * 0.2 + (rand.nextDouble() - 0.5) * 0.1,
                            0.1,
                            dir.z * 0.2 + (rand.nextDouble() - 0.5) * 0.1);
                }

                if (rand.nextFloat() < 0.4f) {
                    Vector3f color = rand.nextBoolean() ?
                            new Vector3f(0.8f, 0.0f, 0.0f) :
                            new Vector3f(1.0f, 0.9f, 0.9f);

                    ParticleHelper.spawn(entity.level(),
                            new MagicParticleOptions(color, 1.0f, false, 20),
                            pos.x, pos.y, pos.z,
                            dir.x * 0.4, dir.y * 0.4, dir.z * 0.4);
                }

                if (rand.nextFloat() < 0.5f) {
                    ParticleHelper.spawn(entity.level(), ParticleTypes.LARGE_SMOKE,
                            pos.x, pos.y, pos.z,
                            dir.x * 0.2, 0.1, dir.z * 0.2);
                }

                if (rand.nextFloat() < 0.3f) {
                    ParticleHelper.spawn(entity.level(), ParticleTypes.DRIPPING_LAVA,
                            pos.x, pos.y, pos.z,
                            dir.x * 0.3, dir.y * 0.3, dir.z * 0.3);
                }
            }
        });
    }


}
