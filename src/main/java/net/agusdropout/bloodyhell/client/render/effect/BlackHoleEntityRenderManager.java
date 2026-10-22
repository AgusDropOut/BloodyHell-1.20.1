package net.agusdropout.bloodyhell.client.render.effect;

import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BlackHoleEntityRenderManager implements DeferredRenderManager {
    private final List<BlackHoleData> ACTIVE_BLACK_HOLES = new ArrayList<>();
    private final DeferredMatrices matrices = new DeferredMatrices();

    public void addBlackHole(Matrix4f pose, float radius, float time, float alpha, int color) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (ACTIVE_BLACK_HOLES.isEmpty()) {
            matrices.capture();
        }
        ACTIVE_BLACK_HOLES.add(new BlackHoleData(new Matrix4f(pose), radius, time, alpha, color));
    }

    @Override
    public String name() {
        return "BlackHoleEntity";
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (ACTIVE_BLACK_HOLES.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        matrices.apply();

        ShaderInstance shader = ModShaders.BLACK_HOLE_SHADER;
        if (shader != null) {
            RenderSystem.setShader(() -> ModShaders.BLACK_HOLE_SHADER);
            RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/misc/white.png"));

            Tesselator tess = Tesselator.getInstance();
            BufferBuilder buffer = tess.getBuilder();

            for (BlackHoleData data : ACTIVE_BLACK_HOLES) {
                float r = ((data.color >> 16) & 0xFF) / 255.0f;
                float g = ((data.color >> 8) & 0xFF) / 255.0f;
                float b = (data.color & 0xFF) / 255.0f;

                if (shader.safeGetUniform("u_color") != null) shader.safeGetUniform("u_color").set(r, g, b);
                if (shader.safeGetUniform("u_time") != null) shader.safeGetUniform("u_time").set(data.time / 3.0f);
                if (shader.safeGetUniform("u_alpha") != null) shader.safeGetUniform("u_alpha").set(data.alpha);

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                buffer.vertex(data.pose, -data.radius, 0.01f, -data.radius).uv(0.0f, 0.0f).endVertex();
                buffer.vertex(data.pose, data.radius, 0.01f, -data.radius).uv(1.0f, 0.0f).endVertex();
                buffer.vertex(data.pose, data.radius, 0.01f, data.radius).uv(1.0f, 1.0f).endVertex();
                buffer.vertex(data.pose, -data.radius, 0.01f, data.radius).uv(0.0f, 1.0f).endVertex();
                tess.end();
            }
        }

        matrices.restore();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        ACTIVE_BLACK_HOLES.clear();
    }

    private static class BlackHoleData {
        Matrix4f pose;
        float radius, time, alpha;
        int color;

        BlackHoleData(Matrix4f pose, float radius, float time, float alpha, int color) {
            this.pose = pose;
            this.radius = radius;
            this.time = time;
            this.alpha = alpha;
            this.color = color;
        }
    }
}