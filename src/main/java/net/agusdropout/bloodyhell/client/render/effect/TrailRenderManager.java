package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.deferred.base.QueuedRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.api.TrailEffect;
import net.agusdropout.bloodyhell.client.render.effect.data.TrailData;
import net.agusdropout.bloodyhell.util.visuals.SplineHelper;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TrailRenderManager extends QueuedRenderManager<TrailData> {
    private final TrailEffect effect;

    public TrailRenderManager(TrailEffect effect) {
        super(effect.name(), effect.state());
        this.effect = effect;
    }

    public void submit(List<Vec3> history, Vec3 cameraPos, float width, float r, float g, float b, float alpha, float time) {
        if (history.size() < 2) return;
        enqueue(new TrailData(new ArrayList<>(history), cameraPos, width, r, g, b, alpha, time));
    }

    @Override
    protected void draw(FrameContext frame, List<TrailData> items) {
        RenderSystem.setShader(effect.shader());

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (TrailData data : items) {
            Uniform timeUniform = effect.timeTarget().get().getUniform("AnimTime");
            if (timeUniform != null) {
                timeUniform.set(data.time());
            }

            List<Vec3> rawHistory = data.history();
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

            Vec3 cameraPos = data.cameraPos();

            for (int i = 0; i < size - 1; i++) {
                Vec3 current = smoothHistory.get(i);
                Vec3 next = smoothHistory.get(i + 1);

                Vector3f dir = new Vector3f((float) (next.x - current.x), (float) (next.y - current.y), (float) (next.z - current.z)).normalize();
                Vector3f toCameraCurrent = new Vector3f((float) (cameraPos.x - current.x), (float) (cameraPos.y - current.y), (float) (cameraPos.z - current.z)).normalize();

                Vector3f rightCurrent = new Vector3f();
                dir.cross(toCameraCurrent, rightCurrent);
                rightCurrent.normalize().mul(data.width());

                Vector3f toCameraNext = new Vector3f((float) (cameraPos.x - next.x), (float) (cameraPos.y - next.y), (float) (cameraPos.z - next.z)).normalize();
                Vector3f rightNext = new Vector3f();
                dir.cross(toCameraNext, rightNext);
                rightNext.normalize().mul(data.width());

                float v1 = -1.0F + (i * vStep);
                float v2 = -1.0F + ((i + 1) * vStep);

                float cx = (float) (current.x - cameraPos.x);
                float cy = (float) (current.y - cameraPos.y);
                float cz = (float) (current.z - cameraPos.z);

                float nx = (float) (next.x - cameraPos.x);
                float ny = (float) (next.y - cameraPos.y);
                float nz = (float) (next.z - cameraPos.z);

                float tAlpha = data.alpha() * (1.0F - ((float) i / size));

                buffer.vertex(cx - rightCurrent.x, cy - rightCurrent.y, cz - rightCurrent.z).uv(-1.0F, v1).color(data.r(), data.g(), data.b(), tAlpha).endVertex();
                buffer.vertex(cx + rightCurrent.x, cy + rightCurrent.y, cz + rightCurrent.z).uv(1.0F, v1).color(data.r(), data.g(), data.b(), tAlpha).endVertex();
                buffer.vertex(nx + rightNext.x, ny + rightNext.y, nz + rightNext.z).uv(1.0F, v2).color(data.r(), data.g(), data.b(), tAlpha).endVertex();
                buffer.vertex(nx - rightNext.x, ny - rightNext.y, nz - rightNext.z).uv(-1.0F, v2).color(data.r(), data.g(), data.b(), tAlpha).endVertex();
            }
        }

        tess.end();
    }
}
