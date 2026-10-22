package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.deferred.base.QueuedRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.data.BlackHoleData;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.List;

public class BlackHoleEntityRenderManager extends QueuedRenderManager<BlackHoleData> {

    public BlackHoleEntityRenderManager() {
        super("BlackHoleEntity", EffectStates.BLACK_HOLE_ENTITY);
    }

    public void addBlackHole(Matrix4f pose, float radius, float time, float alpha, int color) {
        enqueue(new BlackHoleData(new Matrix4f(pose), radius, time, alpha, color));
    }

    @Override
    protected void draw(FrameContext frame, List<BlackHoleData> items) {
        ShaderInstance shader = ModShaders.BLACK_HOLE_SHADER;
        if (shader != null) {
            RenderSystem.setShader(() -> ModShaders.BLACK_HOLE_SHADER);
            RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/misc/white.png"));

            Tesselator tess = Tesselator.getInstance();
            BufferBuilder buffer = tess.getBuilder();

            for (BlackHoleData data : items) {
                float r = ((data.color() >> 16) & 0xFF) / 255.0f;
                float g = ((data.color() >> 8) & 0xFF) / 255.0f;
                float b = (data.color() & 0xFF) / 255.0f;

                if (shader.safeGetUniform("u_color") != null) shader.safeGetUniform("u_color").set(r, g, b);
                if (shader.safeGetUniform("u_time") != null) shader.safeGetUniform("u_time").set(data.time() / 3.0f);
                if (shader.safeGetUniform("u_alpha") != null) shader.safeGetUniform("u_alpha").set(data.alpha());

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                buffer.vertex(data.pose(), -data.radius(), 0.01f, -data.radius()).uv(0.0f, 0.0f).endVertex();
                buffer.vertex(data.pose(), data.radius(), 0.01f, -data.radius()).uv(1.0f, 0.0f).endVertex();
                buffer.vertex(data.pose(), data.radius(), 0.01f, data.radius()).uv(1.0f, 1.0f).endVertex();
                buffer.vertex(data.pose(), -data.radius(), 0.01f, data.radius()).uv(0.0f, 1.0f).endVertex();
                tess.end();
            }
        }
    }
}
