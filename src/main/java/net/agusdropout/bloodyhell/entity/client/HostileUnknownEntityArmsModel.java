package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.HostileUnknownEntityArms;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncGrabBonePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class HostileUnknownEntityArmsModel extends GeoModel<HostileUnknownEntityArms> {

    @Override
    public ResourceLocation getModelResource(HostileUnknownEntityArms animatable) {
        return new ResourceLocation(BloodyHell.MODID, "geo/hostile_unknown_entity_arms.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HostileUnknownEntityArms animatable) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/hostile_unknown_entity_arms.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HostileUnknownEntityArms animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/hostile_unknown_entity_arms.animation.json");
    }

    @Override
    public void setCustomAnimations(HostileUnknownEntityArms animatable, long instanceId, AnimationState<HostileUnknownEntityArms> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone rootBone = getAnimationProcessor().getBone("v1");
        GeoBone armBaseBone = (GeoBone) getAnimationProcessor().getBone("v17");

        if (rootBone == null || armBaseBone == null) return;

        LivingEntity target = animatable.getTarget();
        if (target != null && (animatable.getTentacleState() == 2 || animatable.getTentacleState() == 3)) {

            Vec3 origin = animatable.position();
            Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2.0, 0);

            double dx = targetPos.x - origin.x;
            double dy = targetPos.y - origin.y;
            double dz = targetPos.z - origin.z;
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            double fullDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if(animatable.getTentacleState() == HostileUnknownEntityArms.STATE_GRAB) {

                float targetYaw = (float) Math.atan2(dz, dx) + 1.5707F;
                float targetPitch = (float) -(Math.atan2(dy, horizontalDistance));
                rootBone.setRotY(targetYaw);
                rootBone.setRotX(targetPitch);
            }

            float baseModelLength = 3.0F;
            float scaleFactor = (float) (fullDistance / baseModelLength);
            //rootBone.setScaleZ(scaleFactor);

            if (animatable.level().isClientSide()) {
                float boneX = (float) (armBaseBone.getWorldPosition().x);
                float boneY = (float) (armBaseBone.getWorldPosition().y);
                float boneZ = (float) (armBaseBone.getWorldPosition().z);

                ModMessages.sendToServer(new SyncGrabBonePacket(animatable.getId(), boneX, boneY, boneZ));
            }
        }
    }
}