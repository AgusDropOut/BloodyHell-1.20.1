package net.agusdropout.bloodyhell.util.visuals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class ViewBobbing {
    private ViewBobbing() {}

    public static void apply(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        bobHurt(poseStack, partialTick, minecraft);
        if (minecraft.options.bobView().get()) {
            bobView(poseStack, partialTick, minecraft);
        }
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
