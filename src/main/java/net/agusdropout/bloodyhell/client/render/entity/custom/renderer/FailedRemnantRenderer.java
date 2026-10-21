package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.custom.FailedRemnantEntity;
import net.agusdropout.bloodyhell.client.render.entity.layer.BloodFireGeoLayer;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.agusdropout.bloodyhell.client.render.entity.generic.ConfigurableGeoModel;

public class FailedRemnantRenderer extends GeoEntityRenderer<FailedRemnantEntity> {

    public FailedRemnantRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ConfigurableGeoModel<>("failed_remnant"));
        addRenderLayer(new BloodFireGeoLayer<>(this));
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

            if (entity.getRandom().nextFloat() < 0.10f) {

                Quaternionf boneRotation = new Quaternionf();
                bone.getWorldSpaceMatrix().getUnnormalizedRotation(boneRotation);

                float width = 0.25f;
                float height = 0.25f;

                float lx = (entity.getRandom().nextFloat() - 0.5f) * width;
                float ly = (entity.getRandom().nextFloat() - 0.5f) * height;
                float lz = 1f;

                Vector3f spawnOffset = new Vector3f(lx, ly, lz);
                spawnOffset.rotate(boneRotation);


                Vector3d bonePos = bone.getWorldPosition();



            }
        });
    }
}
