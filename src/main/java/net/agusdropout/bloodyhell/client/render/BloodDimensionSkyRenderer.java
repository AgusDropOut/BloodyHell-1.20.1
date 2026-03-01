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
    private static final ResourceLocation FOG_OVERLAY =
            new ResourceLocation(BloodyHell.MODID, "textures/environment/blood_fog_overlay.png");
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
        renderFogOverlay(poseStack, projectionMatrix, partialTicks);
        renderBloodMoon(poseStack, projectionMatrix, partialTicks);
        return true; // evita dibujar el cielo vanilla
    }



    private static void renderFogOverlay(PoseStack poseStack, Matrix4f projectionMatrix, float partialTicks) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, FOG_OVERLAY);

        final float R = 520f; // radio un poco mayor que el skybox
        final int SEG = 48;   // suavidad
        final float OPACITY_MULT = 0.9f;
        RenderSystem.setShaderColor(1f, 1f, 1f, OPACITY_MULT);

        BufferBuilder buf = Tesselator.getInstance().getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // Animación: rotación lenta
        Minecraft mc = Minecraft.getInstance();
        float time = (mc.level != null ? (mc.level.getGameTime() + partialTicks) : partialTicks);
        float rot = time * 0.06f; // velocidad

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(rot)); // aplicar la rotación

        // Media esfera superior
        for (int i = 0; i < SEG; i++) {
            double t1 = (2 * Math.PI) * (i / (double) SEG);
            double t2 = (2 * Math.PI) * ((i + 1) / (double) SEG);

            float x1 = (float)(R * Math.cos(t1));
            float z1 = (float)(R * Math.sin(t1));
            float x2 = (float)(R * Math.cos(t2));
            float z2 = (float)(R * Math.sin(t2));

            float yTop = 220f;  // altura de la cúpula
            float yMid = -30f;   // borde cercano al horizonte

            // UV polar (para evitar costuras duras)
            float u1 = (float)(0.5 + 0.5 * Math.cos(t1));
            float v1 = (float)(0.5 + 0.5 * Math.sin(t1));
            float u2 = (float)(0.5 + 0.5 * Math.cos(t2));
            float v2 = (float)(0.5 + 0.5 * Math.sin(t2));

            // Quad del segmento
            Matrix4f m = poseStack.last().pose(); //  ahora sí usamos la matriz con la rotación aplicada
            buf.vertex(m, x1, yMid, z1).uv(u1, v1).endVertex();
            buf.vertex(m, x2, yMid, z2).uv(u2, v2).endVertex();
            buf.vertex(m, 0,  yTop,  0).uv(0.5f, 0.5f).endVertex();
            buf.vertex(m, 0,  yTop,  0).uv(0.5f, 0.5f).endVertex();
        }

        BufferUploader.drawWithShader(buf.end());
        poseStack.popPose(); // restaurar transformaciones

        // Limpieza
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



