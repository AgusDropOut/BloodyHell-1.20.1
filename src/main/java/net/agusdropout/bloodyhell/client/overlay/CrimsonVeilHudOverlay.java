package net.agusdropout.bloodyhell.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.client.data.ClientCrimsonVeilData;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CrimsonVeilHudOverlay  {

    private static final ResourceLocation SPHERE_EMPTY = new ResourceLocation(BloodyHell.MODID, "textures/gui/mana_sphere_empty.png");
    private static final ResourceLocation SPHERE_FILLED = new ResourceLocation(BloodyHell.MODID, "textures/gui/mana_sphere_filled.png");

    private static final int SPHERE_SIZE = 38;

    public CrimsonVeilHudOverlay() {

    }







    private int getMaxMana(Player player) {
        return player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL)
                .map(cap -> cap.getMAX_CRIMSOMVEIL())
                .orElse(0);
    }

    public static final IGuiOverlay OVERLAY = CrimsonVeilHudOverlay::renderOverlay;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        Player player = minecraft.player;
        ItemStack heldItem = player.getMainHandItem();
        return heldItem.is(ModTags.Items.CRIMSONVEIL_CONSUMER);
    }

    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float pt, int width, int height) {
        if (!shouldDisplayBar())
            return;
        PoseStack ms = guiGraphics.pose();
        int mana = ClientCrimsonVeilData.getPlayerCrimsonVeil();

        int maxMana = 100;

        int offsetLeft = 40;//+right -left
        int yOffset = minecraft.getWindow().getGuiScaledHeight() - 40;


        guiGraphics.blit(SPHERE_EMPTY, offsetLeft, yOffset, 0, 0, SPHERE_SIZE, SPHERE_SIZE, 38, 38);

        float manaPercentage = (float) mana / (float) maxMana;

        int filledHeight = (int) (SPHERE_SIZE * manaPercentage);
        int startY = SPHERE_SIZE - filledHeight;
        guiGraphics.blit(SPHERE_FILLED, offsetLeft, yOffset + startY, 0, startY, SPHERE_SIZE, filledHeight, 38, 38);
    }

    static boolean stillBar = true;


}