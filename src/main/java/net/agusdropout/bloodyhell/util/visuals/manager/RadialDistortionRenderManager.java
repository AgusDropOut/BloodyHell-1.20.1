package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class RadialDistortionRenderManager {
    private static final List<DistortionData> ACTIVE_DISTORTIONS = new ArrayList<>();
    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void addDistortion(Matrix4f pose, float size, Vector3f color, float alpha, float time, Quaternionf customRotation) {
        if (ACTIVE_DISTORTIONS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_DISTORTIONS.add(new DistortionData(pose, size, color, alpha, time, customRotation));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_DISTORTIONS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();
        int screenW = mc.getWindow().getWidth();
        int screenH = mc.getWindow().getHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShader(() -> ModShaders.RADIAL_DISTORTION_SHADER);

        if (ModShaders.RADIAL_DISTORTION_SHADER.getUniform("ScreenSize") != null) {
            ModShaders.RADIAL_DISTORTION_SHADER.getUniform("ScreenSize").set((float) screenW, (float) screenH);
        }


        RenderSystem.setShaderTexture(0, mainTarget.getColorTextureId());

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float[][] uvs = {
                {0.0f, 0.0f}, {0.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, 0.0f}
        };

        for (DistortionData data : ACTIVE_DISTORTIONS) {
            if (ModShaders.RADIAL_DISTORTION_SHADER.getUniform("GameTime") != null) {
                ModShaders.RADIAL_DISTORTION_SHADER.getUniform("GameTime").set(data.time);
            }

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);


            Quaternionf rotationToUse = (data.camRot == null) ? mc.gameRenderer.getMainCamera().rotation() : data.camRot;

            Vector3f[] corners = {
                    new Vector3f(-data.size, -data.size, 0),
                    new Vector3f(-data.size, data.size, 0),
                    new Vector3f(data.size, data.size, 0),
                    new Vector3f(data.size, -data.size, 0)
            };

            for (int i = 0; i < 4; i++) {
                Vector3f posVec = new Vector3f(corners[i]);
                posVec.rotate(rotationToUse);

                Vector4f finalPos = new Vector4f(posVec.x(), posVec.y(), posVec.z(), 1.0f);
                finalPos.mul(data.pose);

                buffer.vertex(finalPos.x(), finalPos.y(), finalPos.z())
                        .uv(uvs[i][0], uvs[i][1])
                        .color(data.color.x(), data.color.y(), data.color.z(), data.alpha)
                        .endVertex();
            }
            tess.end();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        ACTIVE_DISTORTIONS.clear();
    }

    private static class DistortionData {
        Matrix4f pose;
        float size, alpha, time;
        Vector3f color;
        Quaternionf camRot;

        DistortionData(Matrix4f pose, float size, Vector3f color, float alpha, float time, Quaternionf camRot) {
            this.pose = pose;
            this.size = size;
            this.color = color;
            this.alpha = alpha;
            this.time = time;
            this.camRot = (camRot != null) ? new Quaternionf(camRot) : null;
        }
    }
}