package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.SpecialSlashEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpecialSlashRenderer extends EntityRenderer<SpecialSlashEntity> {

    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blank.png");

    public SpecialSlashRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SpecialSlashEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        float age = entity.tickCount + partialTicks;
        float rawScale = Mth.clamp(age / 3.5f, 0.0f, 1.0f);
        float scale = 0.1f + 0.9f * (float) Math.pow(rawScale, 0.5);
        float alpha = (age > 16.0f) ? 1.0f - Mth.clamp((age - 16.0f) / 4.0f, 0.0f, 1.0f) : 1.0f;

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        for (int i = 0; i < 5; i++) {
            poseStack.pushPose();
            poseStack.translate(-i * 0.3f, 0, 0);
            float ghostScale = scale * (1.0f - (i * 0.1f));
            float ghostAlpha = alpha * (1.0f - i / 5.0f);

            if (ghostAlpha > 0.05f) {
                float rad = 3.5f * ghostScale;
                float width = 0.8f * ghostScale;

                // DEFINE COLORS
                // Inner: Black
                float[] colInner = {0.0f, 0.0f, 0.0f, 0.9f * ghostAlpha};
                // Outer: Yellow
                float[] colOuter = {1.0f, 1.0f, 0.0f, 0.6f * ghostAlpha};

                buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

                // Slash 1
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.mulPose(Axis.XN.rotationDegrees(180));
                poseStack.mulPose(Axis.YN.rotationDegrees(45));

                // Use Gradient Helper
                RenderHelper.renderCrescentGradient(buffer, poseStack.last().pose(), null,
                        rad, width, (float)Math.PI/1.2f,
                        colInner, colOuter, 15728880);

                poseStack.popPose();

                // Slash 2
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.mulPose(Axis.YN.rotationDegrees(-45));

                // Use Gradient Helper
                RenderHelper.renderCrescentGradient(buffer, poseStack.last().pose(), null,
                        rad, width, (float)Math.PI/1.2f,
                        colInner, colOuter, 15728880);

                poseStack.popPose();

                tess.end();
            }
            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpecialSlashEntity entity) {
        return BLANK_TEXTURE;
    }
}