package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.types.MiniBlob;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MiniBlobRenderManager {

    private static final int MAX_BLOBS_PER_CLUSTER = 8;
    private static final double MERGE_RADIUS = 1.5;
    private static final List<MiniBlob> ACTIVE_MINI_BLOBS = new ArrayList<>();

    private static int copiedColorTexture = -1;
    private static int lastWidth = 0;
    private static int lastHeight = 0;

    public static void addMiniBlob(MiniBlob blob) {
        ACTIVE_MINI_BLOBS.add(blob);
    }

    public static void renderAll(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (ACTIVE_MINI_BLOBS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        Camera camera = event.getCamera();
        ShaderInstance shader = ModShaders.MINI_BLOOD_BLOB_SHADER;

        if (shader == null) {
            ACTIVE_MINI_BLOBS.clear();
            return;
        }

        RenderTarget mainTarget = mc.getMainRenderTarget();

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

        int currentTex = RenderSystem.getShaderTexture(0);
        RenderSystem.setShaderTexture(0, copiedColorTexture);
        RenderSystem.setShaderTexture(1, mainTarget.getDepthTextureId());
        RenderSystem.setShader(() -> shader);

        Matrix4f projMat = new Matrix4f(event.getProjectionMatrix());
        Matrix4f invProjMat = new Matrix4f(projMat).invert();

        PoseStack cleanStack = new PoseStack();
        cleanStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.getXRot()));
        cleanStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
        Matrix4f cleanInvViewMat = new Matrix4f(cleanStack.last().pose()).invert();

        if (shader.safeGetUniform("u_CleanInvViewMat") != null) shader.safeGetUniform("u_CleanInvViewMat").set(cleanInvViewMat);
        if (shader.safeGetUniform("u_ProjMat") != null) shader.safeGetUniform("u_ProjMat").set(projMat);
        if (shader.safeGetUniform("u_InvProjMat") != null) shader.safeGetUniform("u_InvProjMat").set(invProjMat);
        if (shader.safeGetUniform("u_Time") != null) shader.safeGetUniform("u_Time").set((float)(System.currentTimeMillis() % 100000L) / 1000.0f);

        Vec3 camPos = camera.getPosition();
        List<MiniBlob> visibleBlobs = new ArrayList<>();

        for (MiniBlob blob : ACTIVE_MINI_BLOBS) {
            if (camPos.distanceToSqr(blob.getPosition()) < 400.0) {
                visibleBlobs.add(blob);
            }
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        float camXRot = camera.getXRot();
        float camYRot = camera.getYRot();

        while (!visibleBlobs.isEmpty()) {
            MiniBlob centerBlob = visibleBlobs.remove(0);
            List<MiniBlob> cluster = new ArrayList<>();
            cluster.add(centerBlob);

            Iterator<MiniBlob> it = visibleBlobs.iterator();
            while (it.hasNext() && cluster.size() < MAX_BLOBS_PER_CLUSTER) {
                MiniBlob neighbor = it.next();
                if (centerBlob.getPosition().distanceTo(neighbor.getPosition()) < MERGE_RADIUS) {
                    cluster.add(neighbor);
                    it.remove();
                }
            }

            renderClusterQuad(cluster, centerBlob.getPosition(), camPos, camXRot, camYRot, shader, buffer, tesselator);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShaderTexture(0, currentTex);

        ACTIVE_MINI_BLOBS.clear();
    }

    private static void renderClusterQuad(List<MiniBlob> cluster, Vec3 center, Vec3 camPos, float camXRot, float camYRot, ShaderInstance shader, BufferBuilder buffer, Tesselator tesselator) {
        if (shader.safeGetUniform("u_BlobCount") != null) {
            shader.safeGetUniform("u_BlobCount").set((float) cluster.size());
        }

        for (int i = 0; i < MAX_BLOBS_PER_CLUSTER; i++) {
            String posUniform = "u_Blob" + i;
            String sizeUniform = "u_Size" + i;
            String colorUniform = "u_Color" + i;

            if (shader.safeGetUniform(posUniform) != null && shader.safeGetUniform(sizeUniform) != null && shader.safeGetUniform(colorUniform) != null) {
                if (i < cluster.size()) {
                    MiniBlob blob = cluster.get(i);
                    Vec3 pos = blob.getPosition();
                    float relX = (float) (pos.x - center.x);
                    float relY = (float) (pos.y - center.y);
                    float relZ = (float) (pos.z - center.z);

                    shader.safeGetUniform(posUniform).set(relX, relY, relZ);
                    shader.safeGetUniform(sizeUniform).set(blob.getSize());
                    shader.safeGetUniform(colorUniform).set(blob.getR(), blob.getG(), blob.getB());
                } else {
                    shader.safeGetUniform(posUniform).set(0.0f, 0.0f, 0.0f);
                    shader.safeGetUniform(sizeUniform).set(0.15f);
                    shader.safeGetUniform(colorUniform).set(0.5f, 0.0f, 0.05f);
                }
            }
        }

        float relCenterX = (float) (center.x - camPos.x);
        float relCenterY = (float) (center.y - camPos.y);
        float relCenterZ = (float) (center.z - camPos.z);

        if (shader.safeGetUniform("relativeClusterCenter") != null) {
            shader.safeGetUniform("relativeClusterCenter").set(relCenterX, relCenterY, relCenterZ);
        }

        PoseStack viewStack = new PoseStack();
        viewStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camXRot));
        viewStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(camYRot + 180.0F));
        Matrix4f viewMat = viewStack.last().pose();

        org.joml.Vector3f viewCenter = new org.joml.Vector3f(relCenterX, relCenterY, relCenterZ);
        viewCenter.mulPosition(viewMat);

        float size = 3.0f;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(viewCenter.x() - size, viewCenter.y() - size, viewCenter.z()).endVertex();
        buffer.vertex(viewCenter.x() + size, viewCenter.y() - size, viewCenter.z()).endVertex();
        buffer.vertex(viewCenter.x() + size, viewCenter.y() + size, viewCenter.z()).endVertex();
        buffer.vertex(viewCenter.x() - size, viewCenter.y() + size, viewCenter.z()).endVertex();
        tesselator.end();
    }
}