package net.agusdropout.bloodyhell.screen.custom.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.client.data.ClientCrimsonVeilData;
import net.agusdropout.bloodyhell.event.handlers.ClientInputHandler;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItemStackHandler;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SelectOmnitomeSlotC2SPacket;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class SpellWheelScreen extends Screen {

    private static final ResourceLocation CENTER_TEX = new ResourceLocation("bloodyhell", "textures/gui/crimson_veil_center.png");
    private static final ResourceLocation SLOT_TEX = new ResourceLocation("bloodyhell", "textures/gui/empty_spell_slot.png");
    private static final ResourceLocation BLOCKED_SLOT_TEX = new ResourceLocation("bloodyhell", "textures/gui/blocked_spell_slot.png");
    private static final ResourceLocation ORNAMENT_TEX = new ResourceLocation("bloodyhell", "textures/gui/spell_wheel_ornament.png");
    private static final ResourceLocation ORNAMENT_TEX_LESS = new ResourceLocation("bloodyhell", "textures/gui/spell_wheel_ornament_less.png");

    private static final int[] SLOT_REQUIRED_TIER = new int[]{0, 1, 0, 2, 0, 1, 0, 2};

    private static final int RADIUS = 95;

    private final int centerSize = 128;
    private final int ornamentSize = 260;
    private final int slotSize = 64;

    private final InteractionHand hand;
    private final ItemStack omnitomeStack;
    private final ResourceLocation[] slotIcons = new ResourceLocation[8];
    private final boolean[] slotUnlocked = new boolean[8];
    private int currentTier = 0;

    private int selectedSlot = -1;
    private boolean selectionCommitted = false;

    public SpellWheelScreen(InteractionHand hand, ItemStack omnitomeStack) {
        super(Component.literal("Spell Wheel"));
        this.hand = hand;
        this.omnitomeStack = omnitomeStack;
        loadOmnitomeContents();
    }

    private void loadOmnitomeContents() {
        this.omnitomeStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof OmnitomeItemStackHandler omnitomeHandler) {
                omnitomeHandler.loadFromStack();
            }

            ItemStack upgradeStack = handler.getStackInSlot(OmnitomeItemStackHandler.UPGRADE_SLOT);
            if (upgradeStack.is(ModItems.ANCIENT_OCULAR_LENSE.get())) {
                this.currentTier = 1;
            } else if (upgradeStack.is(ModItems.BLASPHEMOUS_OCULAR_LENSE.get())) {
                this.currentTier = 2;
            } else {
                this.currentTier = 0;
            }

            for (int i = 0; i < 8; i++) {
                this.slotUnlocked[i] = this.currentTier >= SLOT_REQUIRED_TIER[i];
                ItemStack bookStack = handler.getStackInSlot(i);
                if (!bookStack.isEmpty() && bookStack.getItem() instanceof BaseSpellBookItem<?> spellBook) {
                    this.slotIcons[i] = spellBook.getSpellIconLocation();
                } else {
                    this.slotIcons[i] = null;
                }
            }
        });
    }

    private void commitSelection() {
        if (this.selectionCommitted) return;
        this.selectionCommitted = true;

        if (this.selectedSlot == -1 || (this.selectedSlot >= 0 && this.selectedSlot < 8 && this.slotUnlocked[this.selectedSlot])) {
            ItemStack liveStack = (this.minecraft != null && this.minecraft.player != null)
                    ? this.minecraft.player.getItemInHand(this.hand)
                    : this.omnitomeStack;

            OmnitomeItem.selectSlot(liveStack, this.selectedSlot);
            if (this.minecraft != null && this.minecraft.level != null) {
                liveStack.getOrCreateTag().putLong(OmnitomeItem.NBT_LAST_MORPH_TIME, this.minecraft.level.getGameTime());
            }
            ModMessages.sendToServer(new SelectOmnitomeSlotC2SPacket(this.hand, this.selectedSlot));
        }
    }

    @Override
    protected void init() {
        super.init();
        long window = this.minecraft.getWindow().getWindow();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }

    @Override
    public void removed() {
        commitSelection();
        super.removed();
        long window = this.minecraft.getWindow().getWindow();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.selectedSlot >= -1) {
            if (this.selectedSlot == -1 || this.slotUnlocked[this.selectedSlot]) {
                commitSelection();
                this.onClose();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (ClientInputHandler.SPELL_WHEEL_KEY.matches(keyCode, scanCode)) {
            commitSelection();
            this.onClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double angleToMouse = Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
        angleToMouse = (angleToMouse + 90 + 360) % 360;

        double distanceToMouse = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));
        if (distanceToMouse > 20) {
            int candidate = (int) Math.round(angleToMouse / 45.0) % 8;
            this.selectedSlot = this.slotUnlocked[candidate] ? candidate : -1;
        } else {
            this.selectedSlot = -1;
        }

        renderSelectionWedge(guiGraphics, centerX, centerY);
        renderOrnament(guiGraphics, centerX, centerY);
        renderCenterOrb(guiGraphics, centerX, centerY);
        renderSlots(guiGraphics, centerX, centerY);
    }

    private void renderOrnament(GuiGraphics guiGraphics, int centerX, int centerY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int drawX = centerX - this.ornamentSize / 2;
        int drawY = centerY - this.ornamentSize / 2;

        ResourceLocation ornament = this.currentTier < 2 ? ORNAMENT_TEX_LESS : ORNAMENT_TEX;
        guiGraphics.blit(ornament, drawX, drawY, 0, 0, this.ornamentSize, this.ornamentSize, this.ornamentSize, this.ornamentSize);

        RenderSystem.disableBlend();
    }

    private void renderSelectionWedge(GuiGraphics guiGraphics, int centerX, int centerY) {
        if (this.selectedSlot == -1) return;

        int quadSize = 320;
        int drawX = centerX - quadSize / 2;
        int drawY = centerY - quadSize / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> ModShaders.SPELL_WHEEL_BG_SHADER);
        RenderSystem.setShaderTexture(0, SLOT_TEX);

        float time = (float) (Util.getMillis() % 100000L) / 1000.0f;
        if (ModShaders.SPELL_WHEEL_BG_SHADER.safeGetUniform("TimeAnim") != null) {
            ModShaders.SPELL_WHEEL_BG_SHADER.safeGetUniform("TimeAnim").set(time);
        }

        double baseAngle = Math.toRadians((this.selectedSlot * 45.0) - 90.0);
        while (baseAngle > Math.PI) baseAngle -= 2.0 * Math.PI;
        while (baseAngle < -Math.PI) baseAngle += 2.0 * Math.PI;

        if (ModShaders.SPELL_WHEEL_BG_SHADER.safeGetUniform("TargetAngle") != null) {
            ModShaders.SPELL_WHEEL_BG_SHADER.safeGetUniform("TargetAngle").set((float) baseAngle);
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        Matrix4f matrix4f = guiGraphics.pose().last().pose();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, drawX, drawY + quadSize, 0).uv(0.0f, 1.0f).endVertex();
        bufferBuilder.vertex(matrix4f, drawX + quadSize, drawY + quadSize, 0).uv(1.0f, 1.0f).endVertex();
        bufferBuilder.vertex(matrix4f, drawX + quadSize, drawY, 0).uv(1.0f, 0.0f).endVertex();
        bufferBuilder.vertex(matrix4f, drawX, drawY, 0).uv(0.0f, 0.0f).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
    }

    private void renderCenterOrb(GuiGraphics guiGraphics, int x, int y) {
        int drawX = x - this.centerSize / 2;
        int drawY = y - this.centerSize / 2;

        RenderSystem.setShader(() -> ModShaders.CRIMSON_VEIL_GUI_SHADER);
        RenderSystem.setShaderTexture(0, CENTER_TEX);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (ModShaders.CRIMSON_VEIL_GUI_SHADER.safeGetUniform("TimeAnim") != null) {
            float time = (float) (Util.getMillis() % 100000L) / 1000.0f;
            ModShaders.CRIMSON_VEIL_GUI_SHADER.safeGetUniform("TimeAnim").set(time);
        }

        if (ModShaders.CRIMSON_VEIL_GUI_SHADER.safeGetUniform("BloodLevel") != null && this.minecraft.player != null) {
            int currentVeil = ClientCrimsonVeilData.getPlayerCrimsonVeil();
            float fillPercentage = (float) currentVeil / (float) PlayerCrimsonVeil.MAX_CRIMSOMVEIL;
            float mappedFill = (fillPercentage * 0.8f) - 0.4f;
            ModShaders.CRIMSON_VEIL_GUI_SHADER.safeGetUniform("BloodLevel").set(mappedFill);
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        Matrix4f matrix4f = guiGraphics.pose().last().pose();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, drawX, drawY + this.centerSize, 0).uv(0.0f, 1.0f).endVertex();
        bufferBuilder.vertex(matrix4f, drawX + this.centerSize, drawY + this.centerSize, 0).uv(1.0f, 1.0f).endVertex();
        bufferBuilder.vertex(matrix4f, drawX + this.centerSize, drawY, 0).uv(1.0f, 0.0f).endVertex();
        bufferBuilder.vertex(matrix4f, drawX, drawY, 0).uv(0.0f, 0.0f).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
    }

    private void renderSlots(GuiGraphics guiGraphics, int centerX, int centerY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (int i = 0; i < 8; i++) {
            double angleRad = Math.toRadians((i * 45) - 90);
            int slotCenterX = centerX + (int) (Math.cos(angleRad) * RADIUS);
            int slotCenterY = centerY + (int) (Math.sin(angleRad) * RADIUS);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(slotCenterX, slotCenterY, 0);

            if (!this.slotUnlocked[i]) {

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                guiGraphics.pose().translate(-this.slotSize / 2.0F, -this.slotSize / 2.0F, 0);
                guiGraphics.blit(BLOCKED_SLOT_TEX, 0, 0, 0, 0, this.slotSize, this.slotSize, this.slotSize, this.slotSize);
            } else {

                if (i == this.selectedSlot) {
                    guiGraphics.pose().scale(1.25F, 1.25F, 1.25F);
                    RenderSystem.setShaderColor(1.2F, 0.8F, 0.4F, 1.0F);
                } else {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }

                guiGraphics.pose().translate(-this.slotSize / 2.0F, -this.slotSize / 2.0F, 0);
                guiGraphics.blit(SLOT_TEX, 0, 0, 0, 0, this.slotSize, this.slotSize, this.slotSize, this.slotSize);

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                if (this.slotIcons[i] != null) {
                    guiGraphics.blit(this.slotIcons[i], 0, 0, 0, 0, this.slotSize, this.slotSize, this.slotSize, this.slotSize);
                }
            }

            guiGraphics.pose().popPose();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}