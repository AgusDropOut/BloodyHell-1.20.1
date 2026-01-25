package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.FailedRemnantEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FailedRemnantRenderer extends GeoEntityRenderer<FailedRemnantEntity> {

    public FailedRemnantRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FailedRemnantModel());
    }

    @Override
    public void render(FailedRemnantEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float scale = 0.6f;
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (!entity.isRemoved()) {
            spawnFaceParticles(entity);
        }
    }

    private void spawnFaceParticles(FailedRemnantEntity entity) {
        BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity));
        if (model == null) return;

        model.getBone("faceParticle").ifPresent(bone -> {
            // Chance to drip (Disgusting drool effect)
            if (entity.getRandom().nextFloat() < 0.10f) {

                // 1. Get Rotation from World Matrix
                Quaternionf boneRotation = new Quaternionf();
                bone.getWorldSpaceMatrix().getUnnormalizedRotation(boneRotation);

                // 2. Define Face Size (Small crawling face)
                float width = 0.25f;
                float height = 0.25f;

                // 3. Random Point on Surface (Local)
                float lx = (entity.getRandom().nextFloat() - 0.5f) * width;
                float ly = (entity.getRandom().nextFloat() - 0.5f) * height;
                // Negative Z pushes it "Forward" out of the mesh
                float lz = -0.2f;

                // 4. Rotate Local Vector
                Vector3f spawnOffset = new Vector3f(lx, ly, lz);
                spawnOffset.rotate(boneRotation);

                // 5. Add World Position
                Vector3d bonePos = bone.getWorldPosition();

                // 6. Spawn Chill Black Particle
                // Drops down slowly, maybe slight forward momentum
                Vec3 look = entity.getLookAngle();
                ParticleHelper.spawn(entity.level(), ModParticles.CHILL_BLACK_PARTICLE.get(),
                        bonePos.x + spawnOffset.x,
                        bonePos.y + spawnOffset.y,
                        bonePos.z + spawnOffset.z,
                        look.x * 0.02, -0.05, look.z * 0.02);
            }
        });
    }
}