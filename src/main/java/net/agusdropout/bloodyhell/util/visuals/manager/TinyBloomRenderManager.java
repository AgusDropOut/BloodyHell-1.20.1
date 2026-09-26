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

public class TinyBloomRenderManager extends BaseShaderRenderManager<TinyBloomRenderManager.BloomData> {

    public static final TinyBloomRenderManager INSTANCE = new TinyBloomRenderManager();

    public static void addBloom(Matrix4f pose, float size, float r, float g, float b, float alpha) {
        INSTANCE.addParticle(new BloomData(pose, size, r, g, b, alpha));
    }

    public static void renderAllAndClear() {
        INSTANCE.executeRenderAndClear();
    }

    public static class BloomData extends BaseShaderRenderManager.ParticleData {
        public BloomData(Matrix4f pose, float size, float r, float g, float b, float alpha) {
            super(pose, size, r, g, b, alpha);
        }
    }

    @Override
    protected void bindShader() {
        RenderSystem.setShader(() -> ModShaders.TINY_BLOOM_SHADER);
    }

    @Override
    protected void renderGeometry() {
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        float[][] localCoords = {{-1.0f, -1.0f}, {-1.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, -1.0f}};

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (BloomData data : ACTIVE_PARTICLES) {
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