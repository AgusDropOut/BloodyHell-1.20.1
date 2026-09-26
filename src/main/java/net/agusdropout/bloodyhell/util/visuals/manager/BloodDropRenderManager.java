package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BloodDropRenderManager extends BaseShaderRenderManager<BloodDropRenderManager.BloodData> {

    public static final BloodDropRenderManager INSTANCE = new BloodDropRenderManager();

    public static void addDrop(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
        INSTANCE.addParticle(new BloodData(pose, size, r, g, b, alpha, time));
    }

    public static void renderAllAndClear() {
        INSTANCE.executeRenderAndClear();
    }

    public static class BloodData extends BaseShaderRenderManager.ParticleData {
        public final float time;

        public BloodData(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
            super(pose, size, r, g, b, alpha);
            this.time = time;
        }
    }

    @Override
    protected void setupBlendFunc() {
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
    }

    @Override
    protected void bindShader() {
        RenderSystem.setShader(() -> ModShaders.BLOOD_DROP_SHADER);
    }

    @Override
    protected void renderGeometry() {
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float[][] localCoords = {{-1.0f, -1.0f}, {-1.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, -1.0f}};

        float globalTime = (float)(net.minecraft.Util.getMillis() % 100000L) / 1000.0f;
        if (ModShaders.BLOOD_DROP_SHADER.getUniform("AnimTime") != null) {
            ModShaders.BLOOD_DROP_SHADER.getUniform("AnimTime").set(globalTime);
        }

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (BloodData data : ACTIVE_PARTICLES) {
            Vector3f[] corners = {
                    new Vector3f(-data.size, -data.size, 0),
                    new Vector3f(-data.size, data.size, 0),
                    new Vector3f(data.size, data.size, 0),
                    new Vector3f(data.size, -data.size, 0)
            };

            for (int i = 0; i < 4; i++) {
                Vector4f finalPos = new Vector4f(corners[i].x(), corners[i].y(), corners[i].z(), 1.0f);
                finalPos.mul(data.pose);

                buffer.vertex(finalPos.x(), finalPos.y(), finalPos.z())
                        .uv(localCoords[i][0], localCoords[i][1])
                        .color(data.r, data.g, data.b, data.alpha)
                        .endVertex();
            }
        }
        tess.end();
    }
}