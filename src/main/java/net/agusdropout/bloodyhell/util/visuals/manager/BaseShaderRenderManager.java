package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseShaderRenderManager<T extends BaseShaderRenderManager.ParticleData> {
    protected final List<T> ACTIVE_PARTICLES = new ArrayList<>();
    protected final Matrix4f savedProjection = new Matrix4f();
    protected final Matrix4f savedModelView = new Matrix4f();

    public void addParticle(T data) {
        if (ACTIVE_PARTICLES.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_PARTICLES.add(data);
    }

    public void executeRenderAndClear() {
        if (ACTIVE_PARTICLES.isEmpty()) return;

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        setupBlendFunc();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

        bindShader();
        renderGeometry();

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        ACTIVE_PARTICLES.clear();
    }

    protected void setupBlendFunc() {
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
    }

    protected abstract void bindShader();
    protected abstract void renderGeometry();

    public static class ParticleData {
        public final Matrix4f pose;
        public final float size, r, g, b, alpha;

        public ParticleData(Matrix4f pose, float size, float r, float g, float b, float alpha) {
            this.pose = pose;
            this.size = size;
            this.r = r;
            this.g = g;
            this.b = b;
            this.alpha = alpha;
        }
    }
}