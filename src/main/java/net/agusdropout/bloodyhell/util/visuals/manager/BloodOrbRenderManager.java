package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BloodOrbRenderManager {
    private static final List<OrbData> ACTIVE_ORBS = new ArrayList<>();



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

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.setShaderTexture(0, mainTarget.getColorTextureId());
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


            Vector3f userOffset = new Vector3f(0.0f, 0.0f, 0.0f);
            center.add(userOffset);


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