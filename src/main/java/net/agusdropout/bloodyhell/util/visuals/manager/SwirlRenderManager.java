package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class SwirlRenderManager {
    private static final List<SwirlData> ACTIVE_SWIRLS = new ArrayList<>();


    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void addSwirl(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
        if (ACTIVE_SWIRLS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_SWIRLS.add(new SwirlData(pose, size, r, g, b, alpha, time));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_SWIRLS.isEmpty()) return;

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

        RenderSystem.setShader(() -> ModShaders.ETHEREAL_SWIRL_SHADER);

        if (ModShaders.ETHEREAL_SWIRL_SHADER.getUniform("ScreenSize") != null) {
            ModShaders.ETHEREAL_SWIRL_SHADER.getUniform("ScreenSize").set((float) screenW, (float) screenH);
        }

        RenderSystem.setShaderTexture(0, mainTarget.getColorTextureId());

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float[][] uvs = {
                {0.0f, 0.0f}, {0.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, 0.0f}
        };

        for (SwirlData data : ACTIVE_SWIRLS) {
            if (ModShaders.ETHEREAL_SWIRL_SHADER.getUniform("EtherealTime") != null) {
                ModShaders.ETHEREAL_SWIRL_SHADER.getUniform("EtherealTime").set(data.time);
            }

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            Vector3f[] corners = {
                    new Vector3f(-data.size, -data.size, 0),
                    new Vector3f(-data.size, data.size, 0),
                    new Vector3f(data.size, data.size, 0),
                    new Vector3f(data.size, -data.size, 0)
            };

            for (int i = 0; i < 4; i++) {
                Vector4f finalPos = new Vector4f(corners[i].x(), corners[i].y(), corners[i].z(), 1.0f);
                finalPos.mul(data.pose);

                buffer.vertex(finalPos.x(), finalPos.y(), finalPos.z())
                        .uv(uvs[i][0], uvs[i][1])
                        .color(data.r, data.g, data.b, data.alpha)
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

        ACTIVE_SWIRLS.clear();
    }

    private static class SwirlData {
        Matrix4f pose;
        float size, r, g, b, alpha, time;

        SwirlData(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
            this.pose = pose;
            this.size = size;
            this.r = r;
            this.g = g;
            this.b = b;
            this.alpha = alpha;
            this.time = time;
        }
    }
}