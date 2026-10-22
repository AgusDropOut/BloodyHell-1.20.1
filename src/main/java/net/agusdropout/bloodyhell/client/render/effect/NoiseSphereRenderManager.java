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
import net.agusdropout.bloodyhell.client.render.effect.data.SphereData;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.List;

public class NoiseSphereRenderManager extends QueuedRenderManager<SphereData> {
    private static final ResourceLocation OUTER_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/particle/sphere_noise_alt.png");
    private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/particle/sphere_noise_abs.png");

    public NoiseSphereRenderManager() {
        super("NoiseSphere", EffectStates.NOISE_SPHERE);
    }

    public void addSphere(float px, float py, float pz, float radius, Vector3f color, float alpha, float uOffset, float vOffset) {
        enqueue(new SphereData(px, py, pz, radius, color, alpha, uOffset, vOffset));
    }

    @Override
    protected void draw(FrameContext frame, List<SphereData> items) {
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        for (SphereData data : items) {
            int r = (int) (data.color().x() * 255);
            int g = (int) (data.color().y() * 255);
            int b = (int) (data.color().z() * 255);
            int a = (int) (data.alpha() * 255);

            PoseStack poseStack = new PoseStack();
            poseStack.translate(data.px(), data.py(), data.pz());

            RenderSystem.setShaderTexture(0, OUTER_TEXTURE);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            poseStack.pushPose();
            RenderHelper.renderTexturedSphereNoLight(buffer, poseStack, data.radius(), 24, 24, r, g, b, a, data.uOffset(), data.vOffset());
            poseStack.popPose();
            tess.end();

            RenderSystem.setShaderTexture(0, INNER_TEXTURE);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            poseStack.pushPose();
            RenderHelper.renderTexturedSphereNoLight(buffer, poseStack, data.radius() * 0.95f, 24, 24, r, g, b, a, -data.uOffset(), -data.vOffset());
            poseStack.popPose();
            tess.end();
        }
    }
}
