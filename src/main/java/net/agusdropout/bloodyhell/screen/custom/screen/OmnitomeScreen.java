package net.agusdropout.bloodyhell.screen.custom.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.screen.custom.menu.OmnitomeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OmnitomeScreen extends AbstractContainerScreen<OmnitomeMenu> {

    private static final ResourceLocation GUI_BASE = new ResourceLocation(BloodyHell.MODID, "textures/gui/omnitome_gui_base.png");
    private static final ResourceLocation GUI_TIER_1 = new ResourceLocation(BloodyHell.MODID, "textures/gui/omnitome_gui_tier_1.png");
    private static final ResourceLocation GUI_TIER_2 = new ResourceLocation(BloodyHell.MODID, "textures/gui/omnitome_gui_tier_2.png");

    public OmnitomeScreen(OmnitomeMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 169;
    }



    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int currentTier = this.menu.getUpgradeTier();
        ResourceLocation activeTexture = switch (currentTier) {
            case 1 -> GUI_TIER_1;
            case 2 -> GUI_TIER_2;
            default -> GUI_BASE;
        };

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(activeTexture, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}