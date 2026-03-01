package net.agusdropout.bloodyhell.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
// Assuming your effects are here
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Matrix4f;


public class BloodFireOverlay {

    // Make sure this texture is in assets/bloodyhell/textures/block/blood_fire_0.png
    // It MUST have a .mcmeta file for animation and be registered in the block atlas.
    private static final ResourceLocation BLOOD_FIRE_TEXTURE = new ResourceLocation("bloodyhell", "block/blood_fire");

    public static final IGuiOverlay HUD_BLOOD_FIRE = BloodFireOverlay::renderOverlay;


    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float pt, int width, int height) {
        if (Minecraft.getInstance().player == null || !Minecraft.getInstance().player.hasEffect(ModEffects.BLOOD_FIRE_EFFECT.get())) {
            return;
        }

        PoseStack posestack = guiGraphics.pose();
        posestack.pushPose();

        // --- CRITICAL FIX: Match Vanilla Projection ---
        // 1. Move to Center of Screen
        posestack.translate(width / 2.0F, height / 2.0F, 0.0F);

        // 2. Scale up so the 1x1 unit quad covers the whole screen.
        // We use the negative Y scale because GUI coordinate systems are flipped compared to World systems.
        // We use Math.max(width, height) to ensure it covers the screen even in wide/tall aspect ratios.
        float scale = Math.max(width, height);
        posestack.scale(scale, -scale, 1.0F); // Note the -scale for Y to flip it upright
        // ----------------------------------------------

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.depthFunc(519); // GL_ALWAYS
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Use your custom texture here
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(BLOOD_FIRE_TEXTURE);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());

        float f = sprite.getU0();
        float f1 = sprite.getU1();
        float f2 = (f + f1) / 2.0F;
        float f3 = sprite.getV0();
        float f4 = sprite.getV1();
        float f5 = (f3 + f4) / 2.0F;
        float f6 = sprite.uvShrinkRatio();
        float f7 = Mth.lerp(f6, f, f2);
        float f8 = Mth.lerp(f6, f1, f2);
        float f9 = Mth.lerp(f6, f3, f5);
        float f10 = Mth.lerp(f6, f4, f5);

        for(int i = 0; i < 2; ++i) {
            posestack.pushPose();
            // Original vanilla offsets
            posestack.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            posestack.mulPose(Axis.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));

            Matrix4f matrix4f = posestack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f10).endVertex();
            bufferbuilder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f10).endVertex();
            bufferbuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f9).endVertex();
            bufferbuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f9).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            posestack.popPose();
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515); // GL_LEQUAL

        posestack.popPose();
    }
}