package net.agusdropout.bloodyhell.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class FloatingItemRenderer {

    private FloatingItemRenderer() {
    }

    /**
     * @param x,y,z         offset from the block origin
     * @param scale         uniform scale (negative flips the model)
     * @param spinSpeed     degrees per tick around Y
     * @param bobAmplitude  vertical sway in blocks, 0 disables bobbing
     * @param bobSpeed      radians per tick fed to the sway sine
     */
    public record Style(double x, double y, double z, float scale, float spinSpeed,
                        float bobAmplitude, float bobSpeed, ItemDisplayContext context) {
    }

    public static void render(Style style, ItemStack stack, Level level, float time,
                              PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(style.x(), style.y(), style.z());
        poseStack.translate(0.0, Mth.sin(time * style.bobSpeed()) * style.bobAmplitude(), 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(time * style.spinSpeed()));
        poseStack.scale(style.scale(), style.scale(), style.scale());

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, style.context(), light, overlay, poseStack, bufferSource, level, 0);

        poseStack.popPose();
    }
}
