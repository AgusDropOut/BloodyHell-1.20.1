package net.agusdropout.bloodyhell.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.BloodyHellBoss;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BossBarHudOverlay {

    private static final ResourceLocation BOSS_BAR_BASE = new ResourceLocation(BloodyHell.MODID, "textures/gui/seliora_boss_bar_base.png");
    private static final ResourceLocation BOSS_BAR_FILL = new ResourceLocation(BloodyHell.MODID, "textures/gui/seliora_boss_bar_fill.png");

    private static final int BAR_WIDTH = 256;
    private static final int BAR_HEIGHT = 16;

    public static final IGuiOverlay OVERLAY = BossBarHudOverlay::renderOverlay;

    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float pt, int width, int height) {
        PoseStack ms = guiGraphics.pose();

        if (ClientBossBarData.isDead() || !ClientBossBarData.isNear()) return;

        int x = width / 2 - BAR_WIDTH / 2;
        int y = 10;

        // Base
        guiGraphics.blit(BOSS_BAR_BASE, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

        // Progreso de vida
        float progress = (float) ClientBossBarData.getHealth() / ClientBossBarData.getMaxHealth();
        int fillWidth = (int)(BAR_WIDTH * progress);

        if (fillWidth > 0) {
            guiGraphics.blit(BOSS_BAR_FILL, x, y, 0, 0, fillWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        }

        // --- Render del nombre del jefe ---
        Minecraft mc = Minecraft.getInstance();
        String name = "Seliora, the first Archbishop";

        int nameWidth = mc.font.width(name);
        int nameX = width / 2 - nameWidth / 2;
        int nameY = y - 10; // un poco arriba de la barra

        guiGraphics.drawString(mc.font, name, nameX, nameY, 0xFFCB5C, true);

    }
}