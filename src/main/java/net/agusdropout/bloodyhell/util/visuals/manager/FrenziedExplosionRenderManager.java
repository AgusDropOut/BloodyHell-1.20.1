package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class FrenziedExplosionRenderManager {
    private static final List<ExplosionData> ACTIVE_EXPLOSIONS = new ArrayList<>();
    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void addExplosion(Matrix4f pose, float size, float lifeProgress, float time) {
        if (ACTIVE_EXPLOSIONS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_EXPLOSIONS.add(new ExplosionData(pose, size, lifeProgress, time));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_EXPLOSIONS.isEmpty()) return;

        //RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShader(() -> ModShaders.FRENZIED_EXPLOSION_SHADER);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float[][] localCoords = {
                {-1.0f, -1.0f}, {-1.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, -1.0f}
        };

        for (ExplosionData data : ACTIVE_EXPLOSIONS) {
            if (ModShaders.FRENZIED_EXPLOSION_SHADER.getUniform("AnimTime") != null) {
                ModShaders.FRENZIED_EXPLOSION_SHADER.getUniform("AnimTime").set(data.time);
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
                        .uv(localCoords[i][0], localCoords[i][1])
                        .color(1.0f, 1.0f, 1.0f, data.lifeProgress)
                        .endVertex();
            }

            tess.end();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
       // RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        ACTIVE_EXPLOSIONS.clear();
    }

    private static class ExplosionData {
        Matrix4f pose;
        float size, lifeProgress, time;

        ExplosionData(Matrix4f pose, float size, float lifeProgress, float time) {
            this.pose = pose;
            this.size = size;
            this.lifeProgress = lifeProgress;
            this.time = time;
        }
    }
}