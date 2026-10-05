package net.agusdropout.bloodyhell.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.screen.ModLabelTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

public class ClientModLabelTooltip implements ClientTooltipComponent {

    private static final ResourceLocation ICON =
            new ResourceLocation(BloodyHell.MODID, "textures/item/ritekeeper_heart.png");

    private final ModLabelTooltipData data;

    public ClientModLabelTooltip(ModLabelTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public int getWidth(Font font) {

        return 10 + 4 + font.width("BloodyHell!");
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {

        graphics.blit(ICON,
                x, y + 1,
                10, 10,
                0, 0,
                16, 16,
                16, 16
        );

        graphics.drawString(font, "BloodyHell!", x + 14, y + 2, 0xFFAA0000, false);
    }
}