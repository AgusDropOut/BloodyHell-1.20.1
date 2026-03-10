package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BlackHoleEntityRenderManager {
    private static final List<BlackHoleData> ACTIVE_BLACK_HOLES = new ArrayList<>();
    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void addBlackHole(Matrix4f pose, float radius, float time, float alpha, int color) {
        if (ACTIVE_BLACK_HOLES.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_BLACK_HOLES.add(new BlackHoleData(new Matrix4f(pose), radius, time, alpha, color));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_BLACK_HOLES.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

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

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

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