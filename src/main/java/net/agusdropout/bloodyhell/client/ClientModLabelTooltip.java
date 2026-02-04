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
        return 12; // Height of the row
    }

    @Override
    public int getWidth(Font font) {
        // Icon (10px) + Spacing (4px) + Text Width
        return 10 + 4 + font.width("BloodyHell!");
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        // You generally don't need setShaderTexture in 1.20+ when using graphics.blit with a ResourceLocation,
        // the method handles the binding internally.

        // CORRECT BLIT FOR SCALING:
        // blit(texture, x, y, destWidth, destHeight, u, v, srcWidth, srcHeight, texWidth, texHeight)
        graphics.blit(ICON,
                x, y + 1,      // Destination X, Y
                10, 10,        // Destination Width, Height (Draw it small)
                0, 0,          // Source U, V (Start at top-left of texture)
                16, 16,        // Source Width, Height (Use the FULL 16x16 image)
                16, 16         // Texture File Dimensions (Total size of the png)
        );

        graphics.drawString(font, "BloodyHell!", x + 14, y + 2, 0xFFAA0000, false);
    }
}