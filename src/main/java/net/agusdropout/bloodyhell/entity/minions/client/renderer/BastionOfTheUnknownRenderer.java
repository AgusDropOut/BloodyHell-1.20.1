package net.agusdropout.bloodyhell.entity.minions.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.minions.client.base.GenericMinionRenderer;
import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class BastionOfTheUnknownRenderer extends GenericMinionRenderer<BastionOfTheUnknownEntity> {
    public BastionOfTheUnknownRenderer(EntityRendererProvider.Context renderManager, GeoModel model) {
        super(renderManager, model);
    }
    @Override
    public void render(BastionOfTheUnknownEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

       if (entity.triggerStepParticles) {
           spawnStepParticles(entity, "rightFootParticle", "leftFootParticle");
           entity.triggerStepParticles = false;
       }
       if (entity.triggerShieldParticles) {
           spawnShieldParticles(entity, "shieldParticle");
           entity.triggerShieldParticles = false;
       }
    }

    private void spawnStepParticles(BastionOfTheUnknownEntity entity, String frontBoneName, String backBoneName) {
        BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity));
        if (model == null) return;

        spawnParticlesAtBone(entity, model, frontBoneName);
        spawnParticlesAtBone(entity, model, backBoneName);
    }

    private void spawnShieldParticles(BastionOfTheUnknownEntity entity, String shieldBoneName) {
        BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity));
        if (model == null) return;
        spawnParticlesAtBone(entity, model, shieldBoneName);
    }


    private void spawnParticlesAtBone(BastionOfTheUnknownEntity entity, BakedGeoModel model, String boneName) {
        int particleCount = 30;
        double splashRadius = 0.25;
        double upwardSpeed = 0.12;
        double outwardSpeed = 0.25;

        model.getBone(boneName).ifPresent(bone -> {
            Vector3d bonePos = bone.getWorldPosition();

            double absoluteX = bonePos.x;
            double absoluteY = bonePos.y;
            double absoluteZ = bonePos.z;


            BlockPos posBelow = BlockPos.containing(absoluteX, absoluteY - 0.2D, absoluteZ);
            BlockState state = entity.level().getBlockState(posBelow);

            if (!state.isAir() && state.getFluidState().isEmpty()) {
                BlockParticleOption blockParticle = new BlockParticleOption(ParticleTypes.BLOCK, state);

                Vec3 centerPos = new Vec3(bonePos.x, bonePos.y + 0.1, bonePos.z);
                ParticleHelper.spawnCrownSplash(
                        entity.level(),
                        blockParticle,
                        centerPos,
                        particleCount,
                        splashRadius,
                        upwardSpeed,
                        outwardSpeed
                );
            }
        });
    }
}
