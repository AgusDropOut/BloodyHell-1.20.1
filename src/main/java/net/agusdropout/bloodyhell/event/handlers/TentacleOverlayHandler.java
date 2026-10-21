package net.agusdropout.bloodyhell.event.handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
public class TentacleOverlayHandler {

    private static final ResourceLocation VIGNETTE_TEX = new ResourceLocation("textures/misc/vignette.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.VIGNETTE.type()) return;

        float intensity = LanternAmbienceHandler.currentIntensity;
        if (intensity <= 0.01f) return;

        Minecraft mc = Minecraft.getInstance();
        int width = event.getGuiGraphics().guiWidth();
        int height = event.getGuiGraphics().guiHeight();
        PoseStack poseStack = event.getGuiGraphics().pose();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        Matrix4f matrix4f = poseStack.last().pose();


        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, intensity * 0.35f);
        RenderSystem.setShaderTexture(0, VIGNETTE_TEX);


        float marginX = width * 0.15f;
        float marginY = height * 0.15f;

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, -marginX, height + marginY, 0).uv(0, 1).endVertex();
        bufferbuilder.vertex(matrix4f, width + marginX, height + marginY, 0).uv(1, 1).endVertex();
        bufferbuilder.vertex(matrix4f, width + marginX, -marginY, 0).uv(1, 0).endVertex();
        bufferbuilder.vertex(matrix4f, -marginX, -marginY, 0).uv(0, 0).endVertex();
        tesselator.end();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);



        var shader = ModShaders.FRENZY_TENTACLES;
        if (shader == null) {
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            return;
        }

        RenderSystem.setShader(() -> shader);

        if (shader.getUniform("Intensity") != null) {
            shader.getUniform("Intensity").set(intensity);
        }

        float time = (mc.level.getGameTime() + event.getPartialTick()) / 20.0f;
        if (shader.getUniform("Time") != null) {
            shader.getUniform("Time").set(time);
        }

        int numTentacles = 8;
        float radius = Math.max(width, height) / 1.5f;
        float thickness = radius * 0.8f;

        if (shader.getUniform("Resolution") != null) {
            shader.getUniform("Resolution").set(radius, thickness);
        }

        for (int i = 0; i < numTentacles; i++) {
            poseStack.pushPose();


            float seed = i * 2.1415f;
            if (shader.getUniform("Seed") != null) {
                shader.getUniform("Seed").set(seed);
            }

            poseStack.translate(width / 2f, height / 2f, 0);

            float angle = (360f / numTentacles) * i;
            angle += (seed % 20f) - 10f;
            angle += (float) Math.sin(time * 0.5f + seed) * 8f;

            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-radius, 0, 0);

            Matrix4f tentacleMatrix = poseStack.last().pose();

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(tentacleMatrix, 0, thickness / 2f, 0).uv(0, 1).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex(tentacleMatrix, radius, thickness / 2f, 0).uv(1, 1).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex(tentacleMatrix, radius, -thickness / 2f, 0).uv(1, 0).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex(tentacleMatrix, 0, -thickness / 2f, 0).uv(0, 0).color(1f, 1f, 1f, 1f).endVertex();
            tesselator.end();

            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}