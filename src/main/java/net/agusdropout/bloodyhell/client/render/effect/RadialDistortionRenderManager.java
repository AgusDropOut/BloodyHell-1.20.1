package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.deferred.base.QueuedRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.data.DistortionData;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class RadialDistortionRenderManager extends QueuedRenderManager<DistortionData> {

    public RadialDistortionRenderManager() {
        super("RadialDistortion", EffectStates.SCREEN_OVERLAY);
    }

    public void addDistortion(Matrix4f pose, float size, Vector3f color, float alpha, float time, Quaternionf customRotation) {
        enqueue(new DistortionData(pose, size, color, alpha, time, customRotation));
    }

    @Override
    protected void draw(FrameContext frame, List<DistortionData> items) {
        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();
        int screenW = mc.getWindow().getWidth();
        int screenH = mc.getWindow().getHeight();

        RenderSystem.setShader(() -> ModShaders.RADIAL_DISTORTION_SHADER);

        if (ModShaders.RADIAL_DISTORTION_SHADER.getUniform("ScreenSize") != null) {
            ModShaders.RADIAL_DISTORTION_SHADER.getUniform("ScreenSize").set((float) screenW, (float) screenH);
        }

        RenderSystem.setShaderTexture(0, mainTarget.getColorTextureId());

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float[][] uvs = {
                {0.0f, 0.0f}, {0.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, 0.0f}
        };

        for (DistortionData data : items) {
            if (ModShaders.RADIAL_DISTORTION_SHADER.getUniform("GameTime") != null) {
                ModShaders.RADIAL_DISTORTION_SHADER.getUniform("GameTime").set(data.time());
            }

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            Quaternionf rotationToUse = (data.camRot() == null) ? mc.gameRenderer.getMainCamera().rotation() : data.camRot();

            Vector3f[] corners = {
                    new Vector3f(-data.size(), -data.size(), 0),
                    new Vector3f(-data.size(), data.size(), 0),
                    new Vector3f(data.size(), data.size(), 0),
                    new Vector3f(data.size(), -data.size(), 0)
            };

            for (int i = 0; i < 4; i++) {
                Vector3f posVec = new Vector3f(corners[i]);
                posVec.rotate(rotationToUse);

                Vector4f finalPos = new Vector4f(posVec.x(), posVec.y(), posVec.z(), 1.0f);
                finalPos.mul(data.pose());

                buffer.vertex(finalPos.x(), finalPos.y(), finalPos.z())
                        .uv(uvs[i][0], uvs[i][1])
                        .color(data.color().x(), data.color().y(), data.color().z(), data.alpha())
                        .endVertex();
            }
            tess.end();
        }
    }
}
