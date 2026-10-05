package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.TextureUtil;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BloodOrbRenderManager {
    private static final List<OrbData> ACTIVE_ORBS = new ArrayList<>();


    private static int copiedColorTexture = -1;
    private static int lastWidth = 0;
    private static int lastHeight = 0;

    public static void renderAll(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (ACTIVE_ORBS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();
        ShaderInstance shader = ModShaders.BLOOD_ORB_SHADER;

        if (shader == null) {
            ACTIVE_ORBS.clear();
            return;
        }


        if (copiedColorTexture == -1 || lastWidth != mainTarget.width || lastHeight != mainTarget.height) {
            if (copiedColorTexture != -1) {
                TextureUtil.releaseTextureId(copiedColorTexture);
            }
            copiedColorTexture = TextureUtil.generateTextureId();
            lastWidth = mainTarget.width;
            lastHeight = mainTarget.height;

            RenderSystem.bindTexture(copiedColorTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }


        RenderSystem.bindTexture(copiedColorTexture);
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, mainTarget.width, mainTarget.height, 0);


        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);


        RenderSystem.setShaderTexture(0, copiedColorTexture);
        RenderSystem.setShaderTexture(1, mainTarget.getDepthTextureId());

        RenderSystem.setShader(() -> shader);

        Matrix4f worldProjMat = new Matrix4f(event.getProjectionMatrix());
        Matrix4f worldInvProjMat = new Matrix4f(worldProjMat).invert();

        if(shader.safeGetUniform("u_InvProjMat") != null) shader.safeGetUniform("u_InvProjMat").set(worldInvProjMat);
        if(shader.safeGetUniform("u_Time") != null) shader.safeGetUniform("u_Time").set((float)(Util.getMillis() % 100000L) / 1000.0f);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        float size = 0.5f;

        for (OrbData data : ACTIVE_ORBS) {
            RenderSystem.setProjectionMatrix(data.projection, VertexSorting.DISTANCE_TO_ORIGIN);
            if(shader.safeGetUniform("u_ProjMat") != null) shader.safeGetUniform("u_ProjMat").set(data.projection);

            Vector3f center = new Vector3f(data.pose.m30(), data.pose.m31(), data.pose.m32());

            if(shader.safeGetUniform("u_OrbViewPos") != null) {
                shader.safeGetUniform("u_OrbViewPos").set(center.x(), center.y(), center.z());
            }

            if(shader.safeGetUniform("u_OrbRotation") != null) {
                shader.safeGetUniform("u_OrbRotation").set(data.rotation);
            }

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            buffer.vertex(center.x() - size, center.y() - size, center.z()).endVertex();
            buffer.vertex(center.x() + size, center.y() - size, center.z()).endVertex();
            buffer.vertex(center.x() + size, center.y() + size, center.z()).endVertex();
            buffer.vertex(center.x() - size, center.y() + size, center.z()).endVertex();
            tess.end();
        }

        RenderSystem.setProjectionMatrix(worldProjMat, VertexSorting.ORTHOGRAPHIC_Z);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        ACTIVE_ORBS.clear();
    }

    public static void addOrbData(Matrix4f pose, Matrix4f projection, org.joml.Matrix3f rotation) {
        ACTIVE_ORBS.add(new OrbData(pose, projection, rotation));
    }

    private static class OrbData {
        Matrix4f pose;
        Matrix4f projection;
        org.joml.Matrix3f rotation;

        OrbData(Matrix4f pose, Matrix4f projection, org.joml.Matrix3f rotation) {
            this.pose = pose;
            this.projection = projection;
            this.rotation = rotation;
        }
    }
}