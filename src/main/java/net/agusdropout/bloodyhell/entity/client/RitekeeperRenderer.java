package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.entity.client.layer.RitekeeperHeartLayer;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions; // Import custom options
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import org.joml.Vector3d;
import org.joml.Vector3f; // Import Vector3f for colors
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
        // 1. Hide if Dashing
        if (entity.isEvading()) {
            return;
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        // 2. Particle Logic
        GeoModel<RitekeeperEntity> model = this.getGeoModel();
        if (model != null) {

            // A. Steam Bone (Passive)
            model.getBone("steam").ifPresent(bone -> {
                Vector3d pos = bone.getWorldPosition();
                if (entity.getRandom().nextFloat() < 0.2f) {
                    ParticleHelper.spawn(entity.level(), ParticleTypes.SMOKE,
                            pos.x, pos.y, pos.z,
                            (entity.getRandom().nextDouble() - 0.5) * 0.1, -0.1, (entity.getRandom().nextDouble() - 0.5) * 0.1);
                }
            });

            // B. Casting Hands (Only when casting)
            if (entity.isCasting()) {
                spawnHandMagic(entity, model.getBone("rightCastParticle"));
                spawnHandMagic(entity, model.getBone("leftCastParticle"));
            }
        }
    }

    private void spawnHandMagic(RitekeeperEntity entity, Optional<GeoBone> boneOpt) {
        boneOpt.ifPresent(bone -> {
            Vector3d pos = bone.getWorldPosition();
            RandomSource rand = entity.getRandom();

            // Spawn 2 particles per tick per hand for density
            for(int i=0; i<2; i++) {

                // Jitter: Random offset around the hand (0.15 blocks)
                double jx = (rand.nextDouble() - 0.5) * 0.3;
                double jy = (rand.nextDouble() - 0.5) * 0.3;
                double jz = (rand.nextDouble() - 0.5) * 0.3;

                // Color Selection (Red / Yellow / Dark Red)
                Vector3f color;
                float chance = rand.nextFloat();

                if (chance < 0.4f) {
                    color = new Vector3f(1.0f, 0.1f, 0.1f); // Bright Red
                } else if (chance < 0.5f) {
                    color = new Vector3f(1.0f, 1.0f, 1.0f); // Yellow/Gold
                } else {
                    color = new Vector3f(0.5f, 0.0f, 0.0f); // Dark Blood Red
                }

                // Create and Spawn
                // Life: 10-20 ticks, Size: 0.4, Glowing: false
                ParticleHelper.spawn(entity.level(),
                        new MagicParticleOptions(color, 0.4f, false, 10 + rand.nextInt(10)),
                        pos.x + jx, pos.y + jy, pos.z + jz,
                        jx * 0.2, 0.05, jz * 0.2 // Slight outward expansion velocity
                );
            }
        });
    }
}