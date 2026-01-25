package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.CinderAcolyteEntity;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CinderAcolyteRenderer extends GeoEntityRenderer<CinderAcolyteEntity> {

    public CinderAcolyteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CinderAcolyteModel());
    }

    @Override
    public void render(CinderAcolyteEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        // PARTICLE LOGIC (Run only on client main thread)
        // We use the BakedGeoModel to find the current position of the bones
        if (!entity.isRemoved()) {
            spawnBoneParticles(entity, partialTick);
        }
    }

    private void spawnBoneParticles(CinderAcolyteEntity entity, float partialTick) {
        // Get the model
        GeoModel<CinderAcolyteEntity> model = this.getGeoModel();

        RandomSource random = entity.getRandom();

        if (model == null) return;


        //for(int i = 1; i < 10; i++) {
        //    model.getBone("faceParticle" + i).ifPresent(bone -> {
//
        //        if (entity.getRandom().nextFloat() < 0.03f) {
        //            Vector3d pos = bone.getWorldPosition();
//
        //            // 1. Obtenemos el vector normalizado de hacia dónde mira el mob
        //            Vec3 look = entity.getLookAngle();
//
        //            // 2. Definimos la "Leve Velocidad" (0.05 a 0.1 es sutil, como humo flotando)
        //            double speed = 0.00005 + (entity.getRandom().nextDouble() * 0.0005);
//
        //            // 3. Multiplicamos la dirección por la velocidad
        //            ParticleHelper.spawn(entity.level(), ModParticles.CHILL_BLACK_PARTICLE.get(),
        //                    pos.x, pos.y, pos.z,
        //                    look.x * speed,  // Velocidad en X
        //                    look.y * speed,  // Velocidad en Y
        //                    look.z * speed   // Velocidad en Z
        //            );
        //        }
        //    });
        //}

        // 2. JET PARTICLE (Levitation) - Constant
        model.getBone("jetParticle").ifPresent(bone -> {
            Vector3d pos = bone.getWorldPosition();
            // Spawn smoke/fire downwards to simulate lift
            ParticleHelper.spawn(entity.level(), ParticleTypes.SMOKE,
                    pos.x, pos.y, pos.z,
                    (entity.getRandom().nextDouble() - 0.5) * 0.1, -0.1, (entity.getRandom().nextDouble() - 0.5) * 0.1);
        });


        model.getBone("staffParticle").ifPresent(bone -> {
            Vector3d pos = bone.getWorldPosition();
            RandomSource rand = entity.getRandom();

            // 1. PASSIVE STANCE (Idle Magic)
            // We increase the frequency slightly (0.3f) and mix colors.
            if (rand.nextFloat() < 0.3f) {

                // Color Palette Logic: Dark Red -> Vibrant Red -> Pink/White Core
                Vector3f color;
                float hueRoll = rand.nextFloat();

                if (hueRoll < 0.4f) {
                    color = new Vector3f(0.4f, 0.0f, 0.0f); // Deep Dark Red (Void-like)
                } else if (hueRoll < 0.8f) {
                    color = new Vector3f(0.9f, 0.1f, 0.1f); // Standard Blood Red
                } else {
                    color = new Vector3f(1.0f, 0.7f, 0.8f); // Pink/White (Hot Core energy)
                }

                // Spawn Magic Particle (Slow rising)
                ParticleHelper.spawn(entity.level(),
                        new MagicParticleOptions(color, 0.3f, false, 25), // Size 0.6, Life 25
                        pos.x, pos.y, pos.z,
                        (rand.nextDouble() - 0.5) * 0.05, 0.02, (rand.nextDouble() - 0.5) * 0.05); // Gentle float up

                // Occasional Small Flame for consistency
                if (rand.nextBoolean()) {
                    ParticleHelper.spawn(entity.level(), ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                            pos.x, pos.y, pos.z, 0, 0.02, 0);
                }
            }


            // 2. ACTIVE ATTACK (The Flamethrower)
            if (entity.isFlameAttacking()) {
                Vec3 dir = entity.getLookAngle();
                double speed = 0.6;
                double spread = 0.15;

                // A. THE CORE STREAM (Dense Small Fire)
                for (int i = 0; i < 4; i++) {
                    double vx = (dir.x * speed) + (rand.nextGaussian() * spread);
                    double vy = (dir.y * speed) + (rand.nextGaussian() * spread);
                    double vz = (dir.z * speed) + (rand.nextGaussian() * spread);

                    ParticleHelper.spawn(entity.level(), ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                            pos.x, pos.y, pos.z, vx, vy, vz);
                }

                // B. HEAVY BLOOD FIRE (New Layer)
                // Adds "chunkier" fire particles to make the stream look more dangerous
                for (int i = 0; i < 2; i++) {
                    double vx = (dir.x * speed * 0.8) + (rand.nextGaussian() * spread);
                    double vy = (dir.y * speed * 0.8) + (rand.nextGaussian() * spread);
                    double vz = (dir.z * speed * 0.8) + (rand.nextGaussian() * spread);

                    ParticleHelper.spawn(entity.level(), ModParticles.BLOOD_FLAME.get(),
                            pos.x, pos.y, pos.z, vx, vy, vz);
                }

                // C. ASH & DEBRIS (New Layer)
                // Ash floats around wildly, simulating burnt material
                for (int i = 0; i < 3; i++) {
                    ParticleHelper.spawn(entity.level(), ParticleTypes.ASH,
                            pos.x + (rand.nextDouble() - 0.5) * 0.5,
                            pos.y + (rand.nextDouble() - 0.5) * 0.5,
                            pos.z + (rand.nextDouble() - 0.5) * 0.5,
                            dir.x * 0.2 + (rand.nextDouble() - 0.5) * 0.1,
                            0.1, // Ash tends to rise
                            dir.z * 0.2 + (rand.nextDouble() - 0.5) * 0.1);
                }

                // D. MAGIC PARTICLES (Red/White Mix)
                if (rand.nextFloat() < 0.4f) {
                    Vector3f color = rand.nextBoolean() ?
                            new Vector3f(0.8f, 0.0f, 0.0f) : // Deep Red
                            new Vector3f(1.0f, 0.9f, 0.9f);  // White/Pale

                    ParticleHelper.spawn(entity.level(),
                            new MagicParticleOptions(color, 1.0f, false, 20),
                            pos.x, pos.y, pos.z,
                            dir.x * 0.4, dir.y * 0.4, dir.z * 0.4);
                }

                // E. DARK SMOKE (Contrast)
                if (rand.nextFloat() < 0.5f) {
                    ParticleHelper.spawn(entity.level(), ParticleTypes.LARGE_SMOKE,
                            pos.x, pos.y, pos.z,
                            dir.x * 0.2, 0.1, dir.z * 0.2);
                }

                // F. DRIPPING OIL
                if (rand.nextFloat() < 0.3f) {
                    ParticleHelper.spawn(entity.level(), ParticleTypes.DRIPPING_LAVA,
                            pos.x, pos.y, pos.z,
                            dir.x * 0.3, dir.y * 0.3, dir.z * 0.3);
                }
            }
        });
    }


}