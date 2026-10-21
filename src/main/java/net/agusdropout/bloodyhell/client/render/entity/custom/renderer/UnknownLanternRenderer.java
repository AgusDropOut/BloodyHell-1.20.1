package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.generic.ConfigurableGeoModel;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import net.agusdropout.bloodyhell.client.render.entity.generic.InsightCreatureRenderer;

import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

public class UnknownLanternRenderer extends InsightCreatureRenderer<UnknownLanternEntity> {

    public UnknownLanternRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ConfigurableGeoModel<>("unknown_lantern", "head", 1.0f, 1.0f), true);
    }

    public void spawnRadialStepParticles(UnknownLanternEntity entity, boolean isRightLeg) {

        int particleCount = 30;
        double splashRadius = 0.25;
        double upwardSpeed = 0.12;
        double outwardSpeed = 0.25;


        String boneName = isRightLeg ? "rightLegParticle" : "leftLegParticle";
        GeoBone locatorBone = (GeoBone)this.getGeoModel().getAnimationProcessor().getBone(boneName);

        if (locatorBone != null) {
            Vector3d truePos = locatorBone.getWorldPosition();


            BlockPos stepPos = BlockPos.containing(truePos.x, truePos.y - 1, truePos.z);
            BlockState blockState = entity.level().getBlockState(stepPos);

            if (!blockState.isAir() && blockState.getFluidState().isEmpty()) {
                BlockParticleOption blockParticle = new BlockParticleOption(ParticleTypes.BLOCK, blockState);

                Vec3 centerPos = new Vec3(truePos.x, truePos.y + 0.1, truePos.z);
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
        }
    }
}
