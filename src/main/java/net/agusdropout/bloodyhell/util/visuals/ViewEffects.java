package net.agusdropout.bloodyhell.util.visuals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3f;
import net.agusdropout.bloodyhell.client.shader.ShaderUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class ViewEffects {
    private ViewEffects() {}

    public static void applyCameraView(PoseStack poseStack, Camera camera) {
        if (ShaderUtils.areShadersActive()) {
            applyViewEffects(poseStack, Minecraft.getInstance().getFrameTime());
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
    }

    private static void applyViewEffects(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        bobHurt(poseStack, partialTick, minecraft);
        if (minecraft.options.bobView().get()) {
            bobView(poseStack, partialTick, minecraft);
        }
        spinningEffect(poseStack, partialTick, minecraft);
    }

    private static void spinningEffect(PoseStack poseStack, float partialTick, Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        float scale = minecraft.options.screenEffectScale().get().floatValue();
        float intensity = Mth.lerp(partialTick, player.oSpinningEffectIntensity, player.spinningEffectIntensity) * scale * scale;
        if (intensity <= 0.0F) return;

        int speed = player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
        float squash = 5.0F / (intensity * intensity + 5.0F) - intensity * 0.04F;
        squash *= squash;
        Axis axis = Axis.of(new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F));
        float angle = ((float) minecraft.gameRenderer.tick + partialTick) * (float) speed;
        poseStack.mulPose(axis.rotationDegrees(angle));
        poseStack.scale(1.0F / squash, 1.0F, 1.0F);
        poseStack.mulPose(axis.rotationDegrees(-angle));
    }

    private static void bobHurt(PoseStack poseStack, float partialTick, Minecraft minecraft) {
        if (!(minecraft.getCameraEntity() instanceof LivingEntity entity)) return;

        float hurt = (float) entity.hurtTime - partialTick;
        if (entity.isDeadOrDying()) {
            float death = Math.min((float) entity.deathTime + partialTick, 20.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (death + 200.0F)));
        }
        if (hurt < 0.0F) return;

        hurt /= (float) entity.hurtDuration;
        hurt = Mth.sin(hurt * hurt * hurt * hurt * (float) Math.PI);
        float hurtDir = entity.getHurtDir();
        poseStack.mulPose(Axis.YP.rotationDegrees(-hurtDir));
        float tilt = (float) ((double) (-hurt) * 14.0D * minecraft.options.damageTiltStrength().get());
        poseStack.mulPose(Axis.ZP.rotationDegrees(tilt));
        poseStack.mulPose(Axis.YP.rotationDegrees(hurtDir));
    }

    private static void bobView(PoseStack poseStack, float partialTick, Minecraft minecraft) {
        if (!(minecraft.getCameraEntity() instanceof Player player)) return;

        float walkDelta = player.walkDist - player.walkDistO;
        float walk = -(player.walkDist + walkDelta * partialTick);
        float bob = Mth.lerp(partialTick, player.oBob, player.bob);
        poseStack.translate(Mth.sin(walk * (float) Math.PI) * bob * 0.5F, -Math.abs(Mth.cos(walk * (float) Math.PI) * bob), 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(walk * (float) Math.PI) * bob * 3.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(walk * (float) Math.PI - 0.2F) * bob) * 5.0F));
    }
}
