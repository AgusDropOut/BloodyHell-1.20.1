package net.agusdropout.bloodyhell.item.client.staffs;

import net.agusdropout.bloodyhell.item.client.base.BaseStaffModel;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class StaffOfGoldenSorrowModel extends BaseStaffModel {


    private static final float CAM_SENSITIVITY = 0.025f;
    private static final float MAX_LEATHER_ROT_X = 0.45f;
    private static final float MAX_LEATHER_ROT_Z = 0.15f;


    private static final float DISK_BOUNCE_Y = 0.08f;
    private static final float DISK_TILT_X = 0.08f;
    private static final float DISK_TILT_Z = 0.05f;

    private float lastCameraYaw = 0.0f;
    private float lastCameraPitch = 0.0f;
    private float smoothedSwayYaw = 0.0f;
    private float smoothedSwayPitch = 0.0f;

    private float smoothedForwardVel = 0.0f;
    private float smoothedStrafeVel = 0.0f;
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


        smoothedForwardVel = Mth.lerp(0.1f, smoothedForwardVel, forwardVel);
        smoothedStrafeVel = Mth.lerp(0.1f, smoothedStrafeVel, strafeVel);


        float walkSpeed = player.walkDist - player.walkDistO;
        float currentWalkDist = player.walkDistO + walkSpeed * partialTick;
        float isMoving = Math.min(walkSpeed * 8.0f, 1.0f);

        float walkBob = Mth.sin(currentWalkDist * 2.0f) * isMoving;
        smoothedWalkBob = Mth.lerp(0.15f, smoothedWalkBob, walkBob);


        float time = (mc.level.getGameTime() + partialTick) * 0.1f;

        float windBase = 0.03f + Math.abs(smoothedForwardVel) * 0.08f;

        float windX1 = Mth.sin(time * 2.5f) * windBase;
        float windZ1 = Mth.cos(time * 1.8f) * (windBase * 0.5f);

        float windX2 = Mth.sin(time * 2.1f + 1.5f) * windBase;
        float windZ2 = Mth.cos(time * 2.3f + 0.8f) * (windBase * 0.5f);


        applyLeatherMotion("leather1", 1.0f, rotRadYaw, rotRadPitch, windX1, windZ1);
        applyLeatherMotion("leather2", 1.25f, rotRadYaw, rotRadPitch, windX2, windZ2);

        applyDiskMotion("disk1", 1.0f, rotRadPitch, smoothedWalkBob, windX1);
        applyDiskMotion("disk2", 1.35f, -rotRadPitch, -smoothedWalkBob, windX2);
    }

    private void applyLeatherMotion(String boneName, float weight, float rotYaw, float rotPitch, float windX, float windZ) {
        getBone(boneName).ifPresent(bone -> {

            float targetRotX = (-rotPitch * 0.5f + smoothedForwardVel * 1.2f + windX) * weight;
            float targetRotZ = (-rotYaw * 0.25f + smoothedStrafeVel * 0.35f + windZ) * weight;


            targetRotX = Mth.clamp(targetRotX, -MAX_LEATHER_ROT_X, MAX_LEATHER_ROT_X);
            targetRotZ = Mth.clamp(targetRotZ, -MAX_LEATHER_ROT_Z, MAX_LEATHER_ROT_Z);


            float fluidX = Mth.lerp(0.15f, bone.getRotX(), targetRotX);
            float fluidZ = Mth.lerp(0.15f, bone.getRotZ(), targetRotZ);

            bone.setRotX(fluidX);
            bone.setRotZ(fluidZ);
        });
    }

    private void applyDiskMotion(String boneName, float weight, float rotPitch, float walkBob, float windX) {
        getBone(boneName).ifPresent(bone -> {

            float targetBounceY = (walkBob * 0.6f - Math.abs(rotPitch) * 0.3f) * DISK_BOUNCE_Y * weight;
            targetBounceY = Mth.clamp(targetBounceY, -DISK_BOUNCE_Y, DISK_BOUNCE_Y);

            float fluidY = Mth.lerp(0.2f, bone.getPosY(), targetBounceY);
            bone.setPosY(fluidY);


            float targetRotX = (-rotPitch * 0.3f + Math.abs(smoothedForwardVel) * 0.2f + windX * 0.2f) * DISK_TILT_X * weight;
            float targetRotZ = (smoothedStrafeVel * 0.2f) * DISK_TILT_Z * weight;

            targetRotX = Mth.clamp(targetRotX, -DISK_TILT_X, DISK_TILT_X);
            targetRotZ = Mth.clamp(targetRotZ, -DISK_TILT_Z, DISK_TILT_Z);


            float fluidRotX = Mth.lerp(0.15f, bone.getRotX(), targetRotX);
            float fluidRotZ = Mth.lerp(0.15f, bone.getRotZ(), targetRotZ);

            bone.setRotX(fluidRotX);
            bone.setRotZ(fluidRotZ);
        });
    }
}