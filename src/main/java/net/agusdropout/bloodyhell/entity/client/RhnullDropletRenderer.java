package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullDropletEntity;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class RhnullDropletRenderer extends EntityRenderer<RhnullDropletEntity> {

    private static int captureTextureId = -1;

    public RhnullDropletRenderer(EntityRendererProvider.Context context) {
        super(context);

        if (captureTextureId == -1) {
            captureTextureId = GL11.glGenTextures();
        }
    }

    @Override
    public void render(RhnullDropletEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {



        float time = entity.getLifeTicks() + partialTicks;
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch();
        }

        poseStack.pushPose();


        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());


        poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));



        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Quaternionf projectileRotation = Axis.YP.rotationDegrees(yRot - 90.0F).mul(Axis.ZP.rotationDegrees(xRot));

        //ShaderUtils.renderDistortionPlane(poseStack, captureTextureId, 2.0f, new Vector3f(1, 1, 1), 1.0f, time, projectileRotation, ModShaders.RADIAL_DISTORTION_SHADER);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder builder = tess.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f pose = poseStack.last().pose();


        float length = 1.2f;
        float width = 0.15f;


        float hR = 1.0f, hG = 0.6f, hB = 0.7f, hA = 1.0f;


        float tR = 0.6f, tG = 0.0f, tB = 0.0f, tA = 0.0f;



        builder.vertex(pose, -length, 0, -width).color(tR, tG, tB, tA).endVertex();
        builder.vertex(pose, 0.2f, 0, -width / 2f).color(hR, hG, hB, hA).endVertex();
        builder.vertex(pose, 0.2f, 0, width / 2f).color(hR, hG, hB, hA).endVertex();
        builder.vertex(pose, -length, 0, width).color(tR, tG, tB, tA).endVertex();

        builder.vertex(pose, -length, -width, 0).color(tR, tG, tB, tA).endVertex();

        builder.vertex(pose, 0.2f, -width / 2f, 0).color(hR, hG, hB, hA).endVertex();
        builder.vertex(pose, 0.2f, width / 2f, 0).color(hR, hG, hB, hA).endVertex();
        builder.vertex(pose, -length, width, 0).color(tR, tG, tB, tA).endVertex();

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RhnullDropletEntity entity) {
        return null;
    }
}