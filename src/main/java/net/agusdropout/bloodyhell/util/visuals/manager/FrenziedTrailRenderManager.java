package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.SplineHelper;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class FrenziedTrailRenderManager {
    private static final List<TrailData> ACTIVE_TRAILS = new ArrayList<>();
    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void addTrail(List<Vec3> history, Vec3 cameraPos, float width, float r, float g, float b, float alpha, float time) {
        if (history.size() < 2) return;

        if (ACTIVE_TRAILS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_TRAILS.add(new TrailData(new ArrayList<>(history), cameraPos, width, r, g, b, alpha, time));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_TRAILS.isEmpty()) return;


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

        RenderSystem.setShader(() -> ModShaders.LINEAR_FRENZIED_FLAME_SHADER);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);


        for (TrailData data : ACTIVE_TRAILS) {
            if (ModShaders.RADIANT_ENERGY_SHADER.getUniform("AnimTime") != null) {
                ModShaders.RADIANT_ENERGY_SHADER.getUniform("AnimTime").set(data.time);
            }


            List<Vec3> rawHistory = data.history;
            List<Vec3> smoothHistory = new ArrayList<>();
            int subdivisions = 3;

            if (rawHistory.size() >= 2) {
                List<Vec3> paddedPts = new ArrayList<>();
                paddedPts.add(rawHistory.get(0));
                paddedPts.addAll(rawHistory);
                paddedPts.add(rawHistory.get(rawHistory.size() - 1));

                for (int i = 1; i < paddedPts.size() - 2; i++) {
                    Vec3 p0 = paddedPts.get(i - 1);
                    Vec3 p1 = paddedPts.get(i);
                    Vec3 p2 = paddedPts.get(i + 1);
                    Vec3 p3 = paddedPts.get(i + 2);

                    smoothHistory.add(p1);
                    for (int j = 1; j < subdivisions; j++) {
                        double t = (double) j / subdivisions;
                        smoothHistory.add(SplineHelper.catmullRom(p0, p1, p2, p3, t));
                    }
                }
                smoothHistory.add(paddedPts.get(paddedPts.size() - 2));
            } else {
                smoothHistory = rawHistory;
            }

            int size = smoothHistory.size();
            float vStep = 2.0F / (size - 1);


            for (int i = 0; i < size - 1; i++) {
                Vec3 current = smoothHistory.get(i);
                Vec3 next = smoothHistory.get(i + 1);

                Vector3f dir = new Vector3f((float) (next.x - current.x), (float) (next.y - current.y), (float) (next.z - current.z)).normalize();
                Vector3f toCameraCurrent = new Vector3f((float) (data.cameraPos.x - current.x), (float) (data.cameraPos.y - current.y), (float) (data.cameraPos.z - current.z)).normalize();

                Vector3f rightCurrent = new Vector3f();
                dir.cross(toCameraCurrent, rightCurrent);
                rightCurrent.normalize().mul(data.width);

                Vector3f toCameraNext = new Vector3f((float) (data.cameraPos.x - next.x), (float) (data.cameraPos.y - next.y), (float) (data.cameraPos.z - next.z)).normalize();
                Vector3f rightNext = new Vector3f();
                dir.cross(toCameraNext, rightNext);
                rightNext.normalize().mul(data.width);

                float v1 = -1.0F + (i * vStep);
                float v2 = -1.0F + ((i + 1) * vStep);

                float cx = (float) (current.x - data.cameraPos.x);
                float cy = (float) (current.y - data.cameraPos.y);
                float cz = (float) (current.z - data.cameraPos.z);

                float nx = (float) (next.x - data.cameraPos.x);
                float ny = (float) (next.y - data.cameraPos.y);
                float nz = (float) (next.z - data.cameraPos.z);

                float tAlpha = data.alpha * (1.0F - ((float) i / size));

                buffer.vertex(cx - rightCurrent.x, cy - rightCurrent.y, cz - rightCurrent.z).uv(-1.0F, v1).color(data.r, data.g, data.b, tAlpha).endVertex();
                buffer.vertex(cx + rightCurrent.x, cy + rightCurrent.y, cz + rightCurrent.z).uv(1.0F, v1).color(data.r, data.g, data.b, tAlpha).endVertex();
                buffer.vertex(nx + rightNext.x, ny + rightNext.y, nz + rightNext.z).uv(1.0F, v2).color(data.r, data.g, data.b, tAlpha).endVertex();
                buffer.vertex(nx - rightNext.x, ny - rightNext.y, nz - rightNext.z).uv(-1.0F, v2).color(data.r, data.g, data.b, tAlpha).endVertex();
            }
        }

        tess.end();

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        ACTIVE_TRAILS.clear();
    }

    private static class TrailData {
        List<Vec3> history;
        Vec3 cameraPos;
        float width, r, g, b, alpha, time;

        TrailData(List<Vec3> history, Vec3 cameraPos, float width, float r, float g, float b, float alpha, float time) {
            this.history = history;
            this.cameraPos = cameraPos;
            this.width = width;
            this.r = r;
            this.g = g;
            this.b = b;
            this.alpha = alpha;
            this.time = time;
        }
    }
}