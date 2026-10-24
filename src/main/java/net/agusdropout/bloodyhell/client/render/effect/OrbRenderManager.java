package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.render.deferred.SceneColorCopy;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.effect.api.OrbEffect;
import net.agusdropout.bloodyhell.client.render.effect.data.OrbCharge;
import net.agusdropout.bloodyhell.client.render.effect.data.OrbData;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OrbRenderManager implements DeferredRenderManager {
    private final OrbEffect effect;
    private final List<OrbData> activeOrbs = new ArrayList<>();

    public OrbRenderManager(OrbEffect effect) {
        this.effect = effect;
    }

    @Override
    public String name() {
        return effect.name();
    }

    public void submit(Matrix4f pose, Matrix4f projection, Matrix3f rotation, ItemDisplayContext transform, OrbCharge charge) {
        if (RenderPassGuard.shouldSkipDeferred(transform)) return;
        float scale = rotation.getColumn(0, new Vector3f()).length();
        if (scale < 1.0e-4f) return;
        activeOrbs.add(new OrbData(pose, projection, rotation, scale, charge));
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (activeOrbs.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();

        ShaderInstance shader = effect.shader().get();

        if (shader == null) {
            activeOrbs.clear();
            return;
        }

        int copiedColorTexture = SceneColorCopy.acquire(mainTarget);

        effect.state().begin().run();

        RenderSystem.setShaderTexture(0, copiedColorTexture);
        RenderSystem.setShaderTexture(1, mainTarget.getDepthTextureId());

        RenderSystem.setShader(() -> shader);

        Matrix4f worldProjMat = new Matrix4f(frame.projectionMatrix());
        Matrix4f worldInvProjMat = new Matrix4f(worldProjMat).invert();
        VertexSorting worldSorting = RenderSystem.getVertexSorting();

        if(shader.safeGetUniform("u_InvProjMat") != null) shader.safeGetUniform("u_InvProjMat").set(worldInvProjMat);
        if(shader.safeGetUniform("u_Time") != null) shader.safeGetUniform("u_Time").set(ShaderTime.seconds());

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        for (OrbData data : activeOrbs) {
            RenderSystem.setProjectionMatrix(data.projection(), VertexSorting.DISTANCE_TO_ORIGIN);
            if(shader.safeGetUniform("u_ProjMat") != null) shader.safeGetUniform("u_ProjMat").set(data.projection());

            Vector3f center = new Vector3f(data.pose().m30(), data.pose().m31(), data.pose().m32());
            float size = effect.quadSize() * data.scale();
            Matrix3f shaderRotation = new Matrix3f(data.rotation()).scale(1.0f / (data.scale() * data.scale()));

            if(shader.safeGetUniform("u_OrbViewPos") != null) {
                shader.safeGetUniform("u_OrbViewPos").set(center.x(), center.y(), center.z());
            }

            if(shader.safeGetUniform("u_OrbRotation") != null) {
                shader.safeGetUniform("u_OrbRotation").set(shaderRotation);
            }

            setCharge(shader, data.charge());
            effect.perOrbUniforms().accept(shader);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            buffer.vertex(center.x() - size, center.y() - size, center.z()).endVertex();
            buffer.vertex(center.x() + size, center.y() - size, center.z()).endVertex();
            buffer.vertex(center.x() + size, center.y() + size, center.z()).endVertex();
            buffer.vertex(center.x() - size, center.y() + size, center.z()).endVertex();
            tess.end();
        }

        RenderSystem.setProjectionMatrix(worldProjMat, worldSorting);
        effect.state().end().run();

        activeOrbs.clear();
    }

    private static void setCharge(ShaderInstance shader, OrbCharge charge) {
        if (shader.safeGetUniform("u_Charge") != null) shader.safeGetUniform("u_Charge").set(charge.charge());
        if (shader.safeGetUniform("u_ChargeTime") != null) shader.safeGetUniform("u_ChargeTime").set(charge.clock());
        if (shader.safeGetUniform("u_Flash") != null) shader.safeGetUniform("u_Flash").set(charge.flash());
        if (shader.safeGetUniform("u_Pulse") != null) shader.safeGetUniform("u_Pulse").set(charge.pulse());
    }
}
