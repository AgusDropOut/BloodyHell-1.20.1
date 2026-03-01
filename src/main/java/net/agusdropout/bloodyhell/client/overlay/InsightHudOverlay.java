package net.agusdropout.bloodyhell.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class InsightHudOverlay {

    private static final ResourceLocation LOW_INSIGHT = new ResourceLocation(BloodyHell.MODID, "textures/gui/low_insight.png");
    private static final ResourceLocation MEDIUM_INSIGHT = new ResourceLocation(BloodyHell.MODID, "textures/gui/medium_insight.png");
    private static final ResourceLocation HIGH_INSIGHT = new ResourceLocation(BloodyHell.MODID, "textures/gui/high_insight.png");
    private static final int IMAGE_SIZE = 32;
    public static final IGuiOverlay OVERLAY = InsightHudOverlay::renderOverlay;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        return ClientInsightData.getPlayerInsight() > 0;
    }

    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float pt, int width, int height) {
        if (!shouldDisplayBar()) return;

        int insight = ClientInsightData.getPlayerInsight();
        float time = (minecraft.level.getGameTime() + pt) * 0.1f;

        int offsetLeft = (width / 2) + 91 + 15;
        int yOffset = height - IMAGE_SIZE - 4;

        if (insight >= 70) {
            offsetLeft += (int) (Math.random() * 3 - 1);
            yOffset += (int) (Math.random() * 3 - 1);
        }

        float scale = 1.0f;
        if (insight >= 30) {
            scale = 1.0f + (Mth.sin(time) * 0.03f * (insight / 100.0f));
        }

        PoseStack ms = guiGraphics.pose();
        ms.pushPose();

        ms.translate(offsetLeft + (IMAGE_SIZE / 2.0f), yOffset + (IMAGE_SIZE / 2.0f), 0);
        ms.scale(scale, scale, 1.0f);
        ms.translate(-(IMAGE_SIZE / 2.0f), -(IMAGE_SIZE / 2.0f), 0);

        float alpha = Mth.clamp(insight / 20.0f, 0.3f, 1.0f);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        ResourceLocation insightTexture = getInsightTexture(insight);
        guiGraphics.blit(insightTexture, 0, 0, 0, 0, IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        ms.popPose();

        String insightText = String.valueOf(insight);
        int textX = offsetLeft + (IMAGE_SIZE / 2) - (minecraft.font.width(insightText) / 2);
        int textY = height - 10;
        guiGraphics.drawString(minecraft.font, insightText, textX, textY, 0xA0A0A0, true);
    }

    private static ResourceLocation getInsightTexture(int insight) {
        if (insight < 30) {
            return LOW_INSIGHT;
        } else if (insight < 70) {
            return MEDIUM_INSIGHT;
        } else {
            return HIGH_INSIGHT;
        }
    }
}