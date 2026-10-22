package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.RenderHelper;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.deferred.base.QueuedRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.data.RingData;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.List;

public class MagicRingRenderManager extends QueuedRenderManager<RingData> {
    private static final ResourceLocation AURORA_NOISE = new ResourceLocation(BloodyHell.MODID, "textures/particle/aurora_noise.png");
    private static final ResourceLocation AURORA_NOISE_ALT = new ResourceLocation(BloodyHell.MODID, "textures/particle/aurora_noise2.png");

    public MagicRingRenderManager() {
        super("MagicRing", EffectStates.MAGIC_RING);
    }

    public void addRing(float px, float py, float pz, float radius, float ringHeight, float time, float r, float g, float b, float alpha) {
        enqueue(new RingData(px, py, pz, radius, ringHeight, time, r, g, b, alpha));
    }

    @Override
    protected void draw(FrameContext frame, List<RingData> items) {
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        for (RingData data : items) {
            PoseStack poseStack = new PoseStack();
            poseStack.translate(data.px(), data.py(), data.pz());
            Matrix4f pose = poseStack.last().pose();

            RenderSystem.setShaderTexture(0, AURORA_NOISE);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            RenderHelper.renderTexturedAuroraRing(buffer, pose, data.radius(), data.ringHeight(), 200, data.time() * 0.5f, data.r(), data.g(), data.b(), data.alpha());
            tess.end();

            RenderSystem.setShaderTexture(0, AURORA_NOISE_ALT);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            RenderHelper.renderTexturedAuroraRing(buffer, pose, data.radius(), data.ringHeight(), 200, -data.time(), data.r(), data.g(), data.b(), data.alpha());
            tess.end();
        }
    }
}
