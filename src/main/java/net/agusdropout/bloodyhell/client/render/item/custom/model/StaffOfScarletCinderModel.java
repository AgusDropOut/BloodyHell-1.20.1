package net.agusdropout.bloodyhell.client.render.item.custom.model;

import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffModel;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimationState;

public class StaffOfScarletCinderModel extends GenericStaffModel {

    private static final float CAM_SENSITIVITY = 0.025f;


    private static final float MAX_CLOTH_ROT_X = 0.8f;
    private static final float MAX_CLOTH_ROT_Z = 0.6f;


    private static final float CLOTH_BIAS_X = -0.15f;
    private static final float CLOTH_BIAS_Z = 0.25f;

    private static final float MAX_BRANCH_ROT = 0.25f;
    private static final float MAX_BRANCH_BOUNCE = 0.15f;

    private float lastCameraYaw = 0.0f;
    private float lastCameraPitch = 0.0f;
    private float smoothedSwayYaw = 0.0f;
    private float smoothedSwayPitch = 0.0f;

    private float smoothedForwardVel = 0.0f;
    private float smoothedStrafeVel = 0.0f;

    private float smoothedVerticalVel = 0.0f;
    private float lastVerticalVel = 0.0f;
    private float impactBop = 0.0f;

    private float smoothedWalkBob = 0.0f;

    private boolean initialized = false;

    @Override
    public void setCustomAnimations(BaseStaffItem animatable, long instanceId, AnimationState<BaseStaffItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.isPaused()) return;

        float partialTick = animationState.getPartialTick();
        float currentYaw = player.getViewYRot(partialTick);
        float currentPitch = player.getViewXRot(partialTick);

        if (!initialized) {
            lastCameraYaw = currentYaw;
            lastCameraPitch = currentPitch;
            initialized = true;
        }

        float deltaYaw = Mth.wrapDegrees(currentYaw - lastCameraYaw);
        float deltaPitch = currentPitch - lastCameraPitch;
        lastCameraYaw = currentYaw;
        lastCameraPitch = currentPitch;

        smoothedSwayYaw = Mth.lerp(0.25f, smoothedSwayYaw, deltaYaw);
        smoothedSwayPitch = Mth.lerp(0.25f, smoothedSwayPitch, deltaPitch);

        float rotRadYaw = smoothedSwayYaw * CAM_SENSITIVITY;
        float rotRadPitch = smoothedSwayPitch * CAM_SENSITIVITY;

        Vec3 velocity = player.getDeltaMovement();
        double playerYawRad = Math.toRadians(player.getYRot());

        float forwardVel = (float) (velocity.x * -Math.sin(playerYawRad) + velocity.z * Math.cos(playerYawRad));
        float strafeVel = (float) (velocity.x * Math.cos(playerYawRad) - velocity.z * -Math.sin(playerYawRad));

        float verticalVel = (float) velocity.y;
        float verticalAccel = verticalVel - lastVerticalVel;
        lastVerticalVel = verticalVel;

        smoothedForwardVel = Mth.lerp(0.1f, smoothedForwardVel, forwardVel);
        smoothedStrafeVel = Mth.lerp(0.1f, smoothedStrafeVel, strafeVel);


        smoothedVerticalVel = Mth.lerp(0.06f, smoothedVerticalVel, verticalVel);


        if (Math.abs(verticalAccel) > 0.12f) {
            impactBop += verticalAccel * 1.6f;
        }

        impactBop = Mth.lerp(0.25f, impactBop, 0.0f);

        float walkSpeed = player.walkDist - player.walkDistO;
        float currentWalkDist = player.walkDistO + walkSpeed * partialTick;
        float isMoving = Math.min(walkSpeed * 8.0f, 1.0f);

        float walkBob = Mth.sin(currentWalkDist * 2.0f) * isMoving;
        smoothedWalkBob = Mth.lerp(0.15f, smoothedWalkBob, walkBob);

        float time = (mc.level.getGameTime() + partialTick) * 0.1f;
        float windBase = 0.03f + Math.abs(smoothedForwardVel) * 0.08f;

        float windX = Mth.sin(time * 2.5f) * windBase;
        float windZ = Mth.cos(time * 1.8f) * (windBase * 0.5f);

        applyClothMotion("3", 1.0f, rotRadYaw, rotRadPitch, windX, windZ);

        for (int i = 1; i <= 6; i++) {
            float offset = i * 0.73f;
            float branchWeight = 1.0f - (i * 0.05f);

            float localWindX = Mth.sin(time * 3.0f + offset) * (windBase * 0.4f);
            float localWindZ = Mth.cos(time * 2.2f + offset) * (windBase * 0.3f);

            applyBranchMotion("branches" + i, branchWeight, rotRadYaw, rotRadPitch, localWindX, localWindZ, smoothedWalkBob);
        }
    }

    private void applyClothMotion(String boneName, float weight, float rotYaw, float rotPitch, float windX, float windZ) {
        getBone(boneName).ifPresent(bone -> {


            float targetRotX = CLOTH_BIAS_X + (-rotPitch * 0.6f + smoothedForwardVel * 1.4f - smoothedVerticalVel * 0.4f - impactBop * 0.5f + windX) * weight;
            float targetRotZ = CLOTH_BIAS_Z + (-rotYaw * 0.4f + smoothedStrafeVel * 0.5f + windZ) * weight;

            targetRotX = Mth.clamp(targetRotX, -MAX_CLOTH_ROT_X, MAX_CLOTH_ROT_X);
            targetRotZ = Mth.clamp(targetRotZ, -MAX_CLOTH_ROT_Z, MAX_CLOTH_ROT_Z);

            float fluidX = Mth.lerp(0.12f, bone.getRotX(), -targetRotX);
            float fluidZ = Mth.lerp(0.12f, bone.getRotZ(), targetRotZ);

            bone.setRotX(fluidX);
            bone.setRotZ(-fluidZ);
        });
    }

    private void applyBranchMotion(String boneName, float weight, float rotYaw, float rotPitch, float windX, float windZ, float walkBob) {
        getBone(boneName).ifPresent(bone -> {


            float targetRotX = (-rotPitch * 0.25f + smoothedVerticalVel * 0.15f - impactBop * 0.8f + windX) * weight;
            float targetRotZ = (-rotYaw * 0.15f + smoothedStrafeVel * 0.2f + windZ) * weight;


            float targetPosY = (walkBob * 0.15f - smoothedVerticalVel * 0.2f - impactBop * 1.2f) * weight;

            targetRotX = Mth.clamp(targetRotX, -MAX_BRANCH_ROT, MAX_BRANCH_ROT);
            targetRotZ = Mth.clamp(targetRotZ, -MAX_BRANCH_ROT, MAX_BRANCH_ROT);
            targetPosY = Mth.clamp(targetPosY, -MAX_BRANCH_BOUNCE, MAX_BRANCH_BOUNCE);

            float rigidRotX = Mth.lerp(0.35f, bone.getRotX(), targetRotX);
            float rigidRotZ = Mth.lerp(0.35f, bone.getRotZ(), targetRotZ);
            float rigidPosY = Mth.lerp(0.40f, bone.getPosY(), targetPosY);

            bone.setRotX(rigidRotX);
            bone.setRotZ(rigidRotZ);
            bone.setPosY(rigidPosY);
        });
    }
}