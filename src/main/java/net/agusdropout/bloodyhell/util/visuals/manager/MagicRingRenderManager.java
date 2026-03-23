package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class MagicRingRenderManager {
    private static final List<RingData> ACTIVE_RINGS = new ArrayList<>();

    private static final ResourceLocation AURORA_NOISE = new ResourceLocation(BloodyHell.MODID, "textures/particle/aurora_noise.png");
    private static final ResourceLocation AURORA_NOISE_ALT = new ResourceLocation(BloodyHell.MODID, "textures/particle/aurora_noise2.png");

    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void addRing(float px, float py, float pz, float radius, float ringHeight, float time, float r, float g, float b, float alpha) {
        if (ACTIVE_RINGS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        ACTIVE_RINGS.add(new RingData(px, py, pz, radius, ringHeight, time, r, g, b, alpha));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_RINGS.isEmpty()) return;

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();


        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        for (RingData data : ACTIVE_RINGS) {
            PoseStack poseStack = new PoseStack();
            poseStack.translate(data.px, data.py, data.pz);
            Matrix4f pose = poseStack.last().pose();


            RenderSystem.setShaderTexture(0, AURORA_NOISE);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            RenderHelper.renderTexturedAuroraRing(buffer, pose, data.radius, data.ringHeight, 200, data.time * 0.5f, data.r, data.g, data.b, data.alpha);
            tess.end();


            RenderSystem.setShaderTexture(0, AURORA_NOISE_ALT);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            RenderHelper.renderTexturedAuroraRing(buffer, pose, data.radius, data.ringHeight, 200, -data.time, data.r, data.g, data.b, data.alpha);
            tess.end();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        ACTIVE_RINGS.clear();
    }

    private static class RingData {
        float px, py, pz, radius, ringHeight, time, r, g, b, alpha;

        RingData(float px, float py, float pz, float radius, float ringHeight, float time, float r, float g, float b, float alpha) {
            this.px = px; this.py = py; this.pz = pz;
            this.radius = radius; this.ringHeight = ringHeight; this.time = time;
            this.r = r; this.g = g; this.b = b; this.alpha = alpha;
        }
    }
}