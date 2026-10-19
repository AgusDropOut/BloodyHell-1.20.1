package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class BloodStainDecalRenderManager {
    private static final List<DecalData> ACTIVE_DECALS = new ArrayList<>();
    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void addDecal(Matrix4f pose, float r, float g, float b, float a) {
        if (ACTIVE_DECALS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_DECALS.add(new DecalData(new Matrix4f(pose), r, g, b, a));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_DECALS.isEmpty()) return;

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

        ShaderInstance shader = ModShaders.BLOOD_BURST_DECAL_SHADER;
        if (shader != null) {
            RenderSystem.setShader(() -> shader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

            for (DecalData data : ACTIVE_DECALS) {
                float s = 0.5f;
                buffer.vertex(data.pose, -s, -s, 0).color(data.r, data.g, data.b, data.a).uv(0, 1).endVertex();
                buffer.vertex(data.pose, -s, s, 0).color(data.r, data.g, data.b, data.a).uv(0, 0).endVertex();
                buffer.vertex(data.pose, s, s, 0).color(data.r, data.g, data.b, data.a).uv(1, 0).endVertex();
                buffer.vertex(data.pose, s, -s, 0).color(data.r, data.g, data.b, data.a).uv(1, 1).endVertex();
            }

            tesselator.end();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        ACTIVE_DECALS.clear();
    }

    private static class DecalData {
        Matrix4f pose;
        float r, g, b, a;

        DecalData(Matrix4f pose, float r, float g, float b, float a) {
            this.pose = pose;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }
}