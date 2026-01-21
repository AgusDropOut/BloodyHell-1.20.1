package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BloodNovaEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BloodNovaRenderer extends EntityRenderer<BloodNovaEntity> {

    private static final ResourceLocation BLANK = new ResourceLocation(BloodyHell.MODID, "textures/misc/white.png");

    private static final Vector3f COL_CORE = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Vector3f COL_RIM = new Vector3f(0.5f, 0.0f, 0.05f);
    private static final Vector3f COL_DISK_IN = new Vector3f(0.9f, 0.05f, 0.1f);
    private static final Vector3f COL_DISK_OUT = new Vector3f(0.1f, 0.0f, 0.0f);

    public BloodNovaRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodNovaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTicks;
        float scale = 2.5f + 0.1f * Mth.sin(age * 0.1f);
        if (entity.tickCount > 150) scale *= Math.max(0, (180 - age) / 30.0f);

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 1.5f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(age * 0.03f) * 15f));

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = poseStack.last().pose();

        // 1. JETS (Restored Fidelity)
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float jetRot = age * 0.2f;
        // Use Flared Cylinder (Power 2.0 = Quadratic Trumpet shape)
        // Rotation = jetRot * 2.0 (Spinning)
        // Twist = 2.0 (Static structural twist)
        RenderHelper.renderFlaredCylinder(buffer, matrix, null, 4.0f, 0.2f, 1.2f, jetRot * 2.0f, 2.0f, 10, 12, 2.0f, 0.8f, 0f, 0.1f, 0.7f, 0f, 15728880);

        poseStack.pushPose();
        poseStack.scale(1, -1, 1);
        RenderHelper.renderFlaredCylinder(buffer, poseStack.last().pose(), null, 4.0f, 0.2f, 1.2f, jetRot * 2.0f, 2.0f, 10, 12, 2.0f, 0.8f, 0f, 0.1f, 0.7f, 0f, 15728880);
        poseStack.popPose();

        tess.end();

        // 2. CORE
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderHelper.renderSphere(buffer, matrix, null, 0.4f, 8, 12, COL_CORE.x, COL_CORE.y, COL_CORE.z, 1.0f, 15728880);
        RenderHelper.renderSphere(buffer, matrix, null, 0.45f, 8, 12, COL_RIM.x, COL_RIM.y, COL_RIM.z, 0.6f, 15728880);
        tess.end();

        // 3. SPARKLES (Procedural logic kept local as it depends on entity UUID seed)
        // (Assuming you kept renderProceduralSparkles from previous refactor if you want them back)

        // 4. DISK
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float[] cIn = {COL_DISK_IN.x, COL_DISK_IN.y, COL_DISK_IN.z, 0.9f};
        float[] cOut = {COL_DISK_OUT.x, COL_DISK_OUT.y, COL_DISK_OUT.z, 0.0f};

        RenderHelper.renderDisk(buffer, matrix, null, 0.5f, 1.3f, 24, age * 0.05f, cIn, cOut, 15728880);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(65));
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * 2.0f));
        RenderHelper.renderDisk(buffer, poseStack.last().pose(), null, 0.6f, 1.1f, 24, 0, cIn, cOut, 15728880);
        poseStack.popPose();

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodNovaEntity entity) {
        return BLANK;
    }
}