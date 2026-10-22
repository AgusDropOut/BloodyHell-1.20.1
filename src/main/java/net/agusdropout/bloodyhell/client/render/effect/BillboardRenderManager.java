package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.effect.api.BillboardEffect;
import net.agusdropout.bloodyhell.client.render.effect.data.BillboardData;
import net.agusdropout.bloodyhell.client.render.view.ViewEffects;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class BillboardRenderManager implements DeferredRenderManager {
    private final BillboardEffect effect;
    private final List<BillboardData> queue = new ArrayList<>();
    private final DeferredMatrices matrices = new DeferredMatrices();

    public BillboardRenderManager(BillboardEffect effect) {
        this.effect = effect;
    }

    @Override
    public String name() {
        return effect.name();
    }

    public void submit(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (queue.isEmpty()) {
            matrices.capture();
        }
        queue.add(new BillboardData(pose, size, r, g, b, alpha, time));
    }

    public void submit(Matrix4f pose, float size, float r, float g, float b, float alpha) {
        submit(pose, size, r, g, b, alpha, 0.0f);
    }

    public void submitWhite(Matrix4f pose, float size, float alpha, float time) {
        submit(pose, size, 1.0f, 1.0f, 1.0f, alpha, time);
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (queue.isEmpty()) return;

        effect.state().begin().run();

        if (effect.cameraView()) {
            matrices.applyProjection();
            PoseStack rsStack = RenderSystem.getModelViewStack();
            rsStack.setIdentity();
            ViewEffects.applyCameraView(rsStack, frame.camera());
            RenderSystem.applyModelViewMatrix();
        } else {
            matrices.apply();
        }

        RenderSystem.setShader(effect.shader());
        effect.afterBind().run();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float min = effect.uvMin();
        float max = effect.uvMax();
        float[][] uvs = {{min, min}, {min, max}, {max, max}, {max, min}};

        if (effect.timeMode() == BillboardEffect.TimeMode.PER_ITEM) {
            for (BillboardData data : queue) {
                setTime(data.time());
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                addQuad(buffer, data, uvs);
                tess.end();
            }
        } else {
            if (effect.timeMode() == BillboardEffect.TimeMode.GLOBAL) {
                setTime(ShaderTime.seconds());
            }
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            for (BillboardData data : queue) {
                addQuad(buffer, data, uvs);
            }
            tess.end();
        }

        matrices.restore();

        effect.state().end().run();

        queue.clear();
    }

    private void setTime(float value) {
        Uniform uniform = effect.shader().get().getUniform(effect.timeUniform());
        if (uniform != null) {
            uniform.set(value);
        }
    }

    private static void addQuad(BufferBuilder buffer, BillboardData data, float[][] uvs) {
        Vector3f[] corners = {
                new Vector3f(-data.size(), -data.size(), 0),
                new Vector3f(-data.size(), data.size(), 0),
                new Vector3f(data.size(), data.size(), 0),
                new Vector3f(data.size(), -data.size(), 0)
        };

        for (int i = 0; i < 4; i++) {
            Vector4f finalPos = new Vector4f(corners[i].x(), corners[i].y(), corners[i].z(), 1.0f);
            finalPos.mul(data.pose());

            buffer.vertex(finalPos.x(), finalPos.y(), finalPos.z())
                    .uv(uvs[i][0], uvs[i][1])
                    .color(data.r(), data.g(), data.b(), data.alpha())
                    .endVertex();
        }
    }
}
