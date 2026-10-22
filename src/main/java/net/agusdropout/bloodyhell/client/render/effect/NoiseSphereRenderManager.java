package net.agusdropout.bloodyhell.client.render.effect;

import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.RenderHelper;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class NoiseSphereRenderManager implements DeferredRenderManager {
    private final List<SphereData> ACTIVE_SPHERES = new ArrayList<>();

    private static final ResourceLocation OUTER_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/particle/sphere_noise_alt.png");
    private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/particle/sphere_noise_abs.png");

    private final DeferredMatrices matrices = new DeferredMatrices();

    public void addSphere(float px, float py, float pz, float radius, Vector3f color, float alpha, float uOffset, float vOffset) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (ACTIVE_SPHERES.isEmpty()) {
            matrices.capture();
        }
        ACTIVE_SPHERES.add(new SphereData(px, py, pz, radius, color, alpha, uOffset, vOffset));
    }

    @Override
    public String name() {
        return "NoiseSphere";
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (ACTIVE_SPHERES.isEmpty()) return;

        matrices.apply();


        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();


        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        for (SphereData data : ACTIVE_SPHERES) {
            int r = (int) (data.color.x() * 255);
            int g = (int) (data.color.y() * 255);
            int b = (int) (data.color.z() * 255);
            int a = (int) (data.alpha * 255);

            PoseStack poseStack = new PoseStack();
            poseStack.translate(data.px, data.py, data.pz);


            RenderSystem.setShaderTexture(0, OUTER_TEXTURE);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            poseStack.pushPose();
            RenderHelper.renderTexturedSphereNoLight(buffer, poseStack, data.radius, 24, 24, r, g, b, a, data.uOffset, data.vOffset);
            poseStack.popPose();
            tess.end();

            RenderSystem.setShaderTexture(0, INNER_TEXTURE);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            poseStack.pushPose();
            RenderHelper.renderTexturedSphereNoLight(buffer, poseStack, data.radius * 0.95f, 24, 24, r, g, b, a, -data.uOffset, -data.vOffset);
            poseStack.popPose();
            tess.end();
        }


        matrices.restore();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        ACTIVE_SPHERES.clear();
    }

    private static class SphereData {
        float px, py, pz, radius, alpha, uOffset, vOffset;
        Vector3f color;

        SphereData(float px, float py, float pz, float radius, Vector3f color, float alpha, float uOffset, float vOffset) {
            this.px = px; this.py = py; this.pz = pz;
            this.radius = radius; this.color = color;
            this.alpha = alpha; this.uOffset = uOffset; this.vOffset = vOffset;
        }
    }
}