package net.agusdropout.bloodyhell.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BossBarHudOverlay {

    private static  ResourceLocation  BOSS_BAR_BASE;
    private static  ResourceLocation BOSS_BAR_FILL;



    private static final int BAR_WIDTH = 256;
    private static final int BAR_HEIGHT = 16;

    public static final IGuiOverlay OVERLAY = BossBarHudOverlay::renderOverlay;

    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float pt, int width, int height) {
        int bossID = ClientBossBarData.getBossID();
        switch (bossID) {
            case 2 :
                BOSS_BAR_BASE = new ResourceLocation(BloodyHell.MODID, "textures/gui/seliora_boss_bar_base.png");
                BOSS_BAR_FILL = new ResourceLocation(BloodyHell.MODID, "textures/gui/seliora_boss_bar_fill.png");
                break;
            case 1 :
                BOSS_BAR_BASE = new ResourceLocation(BloodyHell.MODID, "textures/gui/ritekeeper_boss_bar_base.png");
                BOSS_BAR_FILL = new ResourceLocation(BloodyHell.MODID, "textures/gui/ritekeeper_boss_bar_fill.png");
                break;
        }

        PoseStack ms = guiGraphics.pose();

        if (ClientBossBarData.isDead() || !ClientBossBarData.isNear()) return;

        int x = width / 2 - BAR_WIDTH / 2;
        int y = 10;


        guiGraphics.blit(BOSS_BAR_BASE, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);


        float progress = (float) ClientBossBarData.getHealth() / ClientBossBarData.getMaxHealth();
        int fillWidth = (int)(BAR_WIDTH * progress);

        if (fillWidth > 0) {
            guiGraphics.blit(BOSS_BAR_FILL, x, y, 0, 0, fillWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        }


        Minecraft mc = Minecraft.getInstance();
        switch (bossID){
            case 1 :
                String nameRitekeeper = "Ritekeeper the corrupted";
                int nameWidthRitekeeper = mc.font.width(nameRitekeeper);
                int nameXRitekeeper = width / 2 - nameWidthRitekeeper / 2;
                int nameYRitekeeper = y - 10;

                guiGraphics.drawString(mc.font, nameRitekeeper, nameXRitekeeper, nameYRitekeeper, 0xb50c00, true);
                break;
            case 2 :
                String name = "Seliora, the first Archbishop";

                int nameWidth = mc.font.width(name);
                int nameX = width / 2 - nameWidth / 2;
                int nameY = y - 10;

                guiGraphics.drawString(mc.font, name, nameX, nameY, 0xFFCB5C, true);
                break;

        }


    }
}