package net.agusdropout.bloodyhell.client.render.effect;

import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class FrenziedFlameRenderManager {
    private static final List<FlameData> ACTIVE_FLAMES = new ArrayList<>();

    private static final DeferredMatrices MATRICES = new DeferredMatrices();

    public static void addFlame(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (ACTIVE_FLAMES.isEmpty()) {
            MATRICES.capture();
        }
        ACTIVE_FLAMES.add(new FlameData(pose, size, r, g, b, alpha, time));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_FLAMES.isEmpty()) return;

        //RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();


        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );

        MATRICES.apply();

        RenderSystem.setShader(() -> ModShaders.FRENZIED_FLAME_SHADER);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();


        float[][] localCoords = {
                {-1.0f, -1.0f}, {-1.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, -1.0f}
        };

        for (FlameData data : ACTIVE_FLAMES) {
            if (ModShaders.FRENZIED_FLAME_SHADER.getUniform("AnimTime") != null) {
                ModShaders.FRENZIED_FLAME_SHADER.getUniform("AnimTime").set(data.time);
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
                        .color(data.r, data.g, data.b, data.alpha)
                        .endVertex();
            }

            tess.end();
        }

        MATRICES.restore();

        RenderSystem.depthMask(true);
       // RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        ACTIVE_FLAMES.clear();
    }

    private static class FlameData {
        Matrix4f pose;
        float size, r, g, b, alpha, time;

        FlameData(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
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