package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
    public void executeRenderAndClear() {
        if (ACTIVE_PARTICLES.isEmpty()) return;

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        setupBlendFunc();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);


        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        rsStack.setIdentity();
        rsStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.getXRot()));
        rsStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));

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


        float[][] localCoords = {
                {-0.5f, -0.5f},
                {-0.5f,  0.5f},
                { 0.5f,  0.5f},
                { 0.5f, -0.5f}
        };

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