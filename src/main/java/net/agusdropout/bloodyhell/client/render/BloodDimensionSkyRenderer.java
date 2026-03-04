package net.agusdropout.bloodyhell.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

public class BloodDimensionSkyRenderer {


    private static VertexBuffer starBuffer;
    /*Twilight Forest Mod Code*/
    // [VanillaCopy] LevelRenderer.renderSky's overworld branch, without sun/moon/sunrise/sunset, using our own stars at full brightness, and lowering void horizon threshold height from getHorizonHeight (63) to 0
    private static final ResourceLocation SKY_TEXTURE =
            new ResourceLocation(BloodyHell.MODID, "textures/environment/bloodsky.png");
    private static final ResourceLocation FOG_OVERLAY_1 =
            new ResourceLocation(BloodyHell.MODID, "textures/environment/fog_overlay_1.png");
    private static final ResourceLocation FOG_OVERLAY_2 =
            new ResourceLocation(BloodyHell.MODID, "textures/environment/fog_overlay_2.png");
    private static final ResourceLocation BLOOD_MOON = new ResourceLocation(BloodyHell.MODID, "textures/environment/blood_moon.png");

    public static boolean renderSky(ClientLevel level, float partialTicks, PoseStack poseStack,
                                    Camera camera, Matrix4f projectionMatrix, Runnable setupFog) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SKY_TEXTURE);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        PoseStack.Pose matrix = poseStack.last();
        Matrix4f m = matrix.pose();

        float radius = 500f;
        int segments = 32;

        for (int i = 0; i < segments; i++) {
            double theta1 = 2 * Math.PI * i / segments;
            double theta2 = 2 * Math.PI * (i + 1) / segments;

            for (int j = 0; j < segments / 2; j++) {
                double phi1 = Math.PI * j / (segments / 2);
                double phi2 = Math.PI * (j + 1) / (segments / 2);

                float u1 = (float) i / segments;
                float u2 = (float) (i + 1) / segments;
                float v1 = (float) j / (segments / 2);
                float v2 = (float) (j + 1) / (segments / 2);

                float x1 = (float) (radius * Math.sin(phi1) * Math.cos(theta1));
                float y1 = (float) (radius * Math.cos(phi1));
                float z1 = (float) (radius * Math.sin(phi1) * Math.sin(theta1));

                float x2 = (float) (radius * Math.sin(phi2) * Math.cos(theta1));
                float y2 = (float) (radius * Math.cos(phi2));
                float z2 = (float) (radius * Math.sin(phi2) * Math.sin(theta1));

                float x3 = (float) (radius * Math.sin(phi2) * Math.cos(theta2));
                float y3 = (float) (radius * Math.cos(phi2));
                float z3 = (float) (radius * Math.sin(phi2) * Math.sin(theta2));

                float x4 = (float) (radius * Math.sin(phi1) * Math.cos(theta2));
                float y4 = (float) (radius * Math.cos(phi1));
                float z4 = (float) (radius * Math.sin(phi1) * Math.sin(theta2));

                buffer.vertex(m, x1, y1, z1).uv(u1, v1).endVertex();
                buffer.vertex(m, x2, y2, z2).uv(u1, v2).endVertex();
                buffer.vertex(m, x3, y3, z3).uv(u2, v2).endVertex();
                buffer.vertex(m, x4, y4, z4).uv(u2, v1).endVertex();
            }
        }

        BufferUploader.drawWithShader(buffer.end());
       // renderFogOverlay(poseStack, projectionMatrix, partialTicks, FOG_OVERLAY_1, 0f, 0.3f );

        renderFogOverlay(poseStack, projectionMatrix, partialTicks, FOG_OVERLAY_2, 0.8f, -0.3f);
        renderBloodMoon(poseStack, projectionMatrix, partialTicks);
        renderFogOverlay(poseStack, projectionMatrix, partialTicks, FOG_OVERLAY_2, 0.8f, 0.5f);

        return true;
    }



    private static void renderFogOverlay(PoseStack poseStack, Matrix4f projectionMatrix, float partialTicks, ResourceLocation overlayTexture, float alphaMult, float velocityMult) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, overlayTexture);

        final float R = 520f;
        final int SEG = 48;
        final int RINGS = 24;

        RenderSystem.setShaderColor(1f, 0f, 0f, alphaMult);

        BufferBuilder buf = Tesselator.getInstance().getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        Minecraft mc = Minecraft.getInstance();
        float time = (mc.level != null ? (mc.level.getGameTime() + partialTicks) : partialTicks);
        float rot = time * velocityMult;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        for (int i = 0; i < RINGS; i++) {
            double phi1 = Math.PI * (i / (double) RINGS);
            double phi2 = Math.PI * ((i + 1) / (double) RINGS);

            float y1 = (float)(R * Math.cos(phi1));
            float y2 = (float)(R * Math.cos(phi2));

            float r1 = (float)(R * Math.sin(phi1));
            float r2 = (float)(R * Math.sin(phi2));

            float v1 = i / (float) RINGS;
            float v2 = (i + 1) / (float) RINGS;

            for (int j = 0; j < SEG; j++) {
                double theta1 = (2 * Math.PI) * (j / (double) SEG);
                double theta2 = (2 * Math.PI) * ((j + 1) / (double) SEG);

                float x1_1 = (float)(r1 * Math.cos(theta1));
                float z1_1 = (float)(r1 * Math.sin(theta1));

                float x2_1 = (float)(r1 * Math.cos(theta2));
                float z2_1 = (float)(r1 * Math.sin(theta2));

                float x1_2 = (float)(r2 * Math.cos(theta1));
                float z1_2 = (float)(r2 * Math.sin(theta1));

                float x2_2 = (float)(r2 * Math.cos(theta2));
                float z2_2 = (float)(r2 * Math.sin(theta2));

                float u1 = j / (float) SEG;
                float u2 = (j + 1) / (float) SEG;

                Matrix4f m = poseStack.last().pose();
                buf.vertex(m, x1_1, y1, z1_1).uv(u1, v1).endVertex();
                buf.vertex(m, x1_2, y2, z1_2).uv(u1, v2).endVertex();
                buf.vertex(m, x2_2, y2, z2_2).uv(u2, v2).endVertex();
                buf.vertex(m, x2_1, y1, z2_1).uv(u2, v1).endVertex();
            }
        }

        BufferUploader.drawWithShader(buf.end());
        poseStack.popPose();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }



    private static void renderBloodMoon(PoseStack poseStack, Matrix4f projectionMatrix, float partialTicks) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BLOOD_MOON);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float size = 40.0F;   // más razonable
        float y = -100.0F;    // siempre negativo en el sistema de cielo

        poseStack.pushPose();

        //  rotación: inclina la luna hacia el horizonte que quieras
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F)); // este ángulo la mueve horizontalmente
        poseStack.mulPose(Axis.XP.rotationDegrees(130.0F)); // este la sube/baja en la cúpula

        Matrix4f matrix = poseStack.last().pose();

        buffer.vertex(matrix, -size, y, -size).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix, -size, y,  size).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(matrix,  size, y,  size).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix,  size, y, -size).uv(1.0F, 0.0F).endVertex();

        BufferUploader.drawWithShader(buffer.end());
        poseStack.popPose();

        RenderSystem.disableBlend();
    }





    }



