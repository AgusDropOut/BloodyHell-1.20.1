package net.agusdropout.bloodyhell.entity.unknown.client.renderer;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.entity.client.base.InsightCreatureRenderer;
import net.agusdropout.bloodyhell.entity.unknown.client.layer.EchoGlowLayer;
import net.agusdropout.bloodyhell.entity.unknown.client.model.EchoOfTheNamelessModel;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.agusdropout.bloodyhell.util.visuals.manager.SphericalShieldRenderManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.joml.Matrix4f;

public class EchoOfTheNamelessRenderer extends InsightCreatureRenderer<EchoOfTheNamelessEntity> {

    public EchoOfTheNamelessRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EchoOfTheNamelessModel());
        this.addRenderLayer(new EchoGlowLayer(this));
    }

    @Override
    public void render(EchoOfTheNamelessEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        float energyRatio = entity.getEnergy() / 100.0F;

        if (energyRatio <= 0.0F || entity.getEntityState() == EchoOfTheNamelessEntity.STATE_BURROWING) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() / 2.0D, 0.0D);
        Matrix4f capturedPose = new Matrix4f(poseStack.last().pose());
        poseStack.popPose();

        float rCol = 1.0F;
        float gCol = 0.84F;
        float bCol = 0.0F;
        float radius = 10.0F;

        float baseAlpha = 0.3F * energyRatio;

        SphericalShieldRenderManager.queueRender(() -> {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();

            RenderSystem.setShader(() -> ModShaders.SHAPE_UNKNOWN_FIRE_SHADER);
            if (ModShaders.SHAPE_UNKNOWN_FIRE_SHADER != null) {
                Uniform timeUniform = ModShaders.SHAPE_UNKNOWN_FIRE_SHADER.getUniform("AnimTime");
                if (timeUniform != null) {
                    timeUniform.set((System.currentTimeMillis() % 100000L) / 1000.0F);
                }
            }
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderHelper.renderColorSphere(builder, capturedPose, radius, 32, 32, rCol, gCol, bCol, Math.max(0.0F, baseAlpha - 0.15F));
            tesselator.end();

            RenderSystem.setShader(() -> ModShaders.SHAPE_GLITTER_SHADER);
            if (ModShaders.SHAPE_GLITTER_SHADER != null) {
                Uniform timeUniform = ModShaders.SHAPE_GLITTER_SHADER.getUniform("GlitterTime");
                if (timeUniform != null) {
                    timeUniform.set(entity.tickCount + partialTick);
                }
            }
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderHelper.renderColorSphere(builder, capturedPose, radius * 0.99f, 32, 32, rCol, gCol, bCol, baseAlpha * 0.5f);
            tesselator.end();

            RenderSystem.setShader(() -> ModShaders.SHAPE_SPHERICAL_RIM_SHADER);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderHelper.renderColorSphere(builder, capturedPose, radius * 1.01f, 32, 32, rCol * 0.05f, gCol * 0.05f, bCol * 0.05f, baseAlpha);
            tesselator.end();
        });
    }
}