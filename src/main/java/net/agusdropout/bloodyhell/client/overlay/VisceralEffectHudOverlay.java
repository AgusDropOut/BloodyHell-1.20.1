package net.agusdropout.bloodyhell.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class VisceralEffectHudOverlay {

    private static final ResourceLocation EFFECT_OVERLAY = new ResourceLocation(BloodyHell.MODID, "textures/gui/visceral_effect_overlay.png");
    private static final Minecraft minecraft = Minecraft.getInstance();
    public static final IGuiOverlay OVERLAY = VisceralEffectHudOverlay::renderOverlay;

    public static boolean shouldDisplayOverlay() {
        Player player = minecraft.player;
        return player != null && player.hasEffect(ModEffects.VISCERAL_EFFECT.get());
    }

    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float pt, int width, int height) {
        if (!shouldDisplayOverlay())
            return;

        PoseStack poseStack = guiGraphics.pose();

        int guiWidth = minecraft.getWindow().getGuiScaledWidth();
        int guiHeight = minecraft.getWindow().getGuiScaledHeight();

        int textureWidth = 1280;
        int textureHeight = 720;

        float scaleX = (float) guiWidth / textureWidth;
        float scaleY = (float) guiHeight / textureHeight;
        float scale = Math.max(scaleX, scaleY);

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        guiGraphics.blit(EFFECT_OVERLAY, 0, 0, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        poseStack.popPose();
    }
}