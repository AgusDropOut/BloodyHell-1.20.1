package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.entity.projectile.BloodSphereEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BloodSphereRenderer extends EntityRenderer<BloodSphereEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/misc/white.png");

    public BloodSphereRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodSphereEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        float time = entity.tickCount + partialTick;
        float scale = 1.8f * (1.0f + (float)Math.sin(time * 0.3f) * 0.05f);
        poseStack.scale(scale, scale, scale);

        Matrix4f p = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();

        // 1. SINGULARITY
        VertexConsumer portal = buffer.getBuffer(RenderType.endPortal());
        RenderHelper.renderSphere(portal, p, n, 0.24f, 12, 12, 1f, 1f, 1f, 1f, 255);

        // 2. BLOODY CORE (Translucent)
        VertexConsumer core = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        // Hex: 440000 -> 0.26, 0, 0
        RenderHelper.renderSphere(core, p, n, 0.3f, 12, 12, 0.26f, 0f, 0f, 0.78f, packedLight);

        // 3. OUTER MANTLE
        VertexConsumer halo = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        // Hex: BB0000 -> 0.73, 0, 0 | Alpha 120 -> 0.47
        RenderHelper.renderSphere(halo, p, n, 0.85f, 12, 12, 0.73f, 0f, 0f, 0.47f, packedLight);

        // 4. DARK AURA
        RenderHelper.renderSphere(halo, p, n, 0.9f, 12, 12, 0f, 0f, 0f, 0.15f, packedLight);

        poseStack.popPose();

        // 5. GROUND WAVE
        renderGroundEffect(entity, partialTick, poseStack, buffer);

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderGroundEffect(BloodSphereEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        Level level = entity.level();
        Vec3 pos = entity.getPosition(partialTick);
        BlockPos blockPos = BlockPos.containing(pos);

        double groundY = -999;
        for (int i = 0; i <= 6; i++) {
            BlockPos check = blockPos.below(i);
            if (!level.getBlockState(check).isAir() && level.getBlockState(check).isSolidRender(level, check)) {
                groundY = check.getY() + 1.01; break;
            }
        }

        if (groundY != -999) {
            double dist = pos.y - groundY;
            if (dist <= 6.0) {
                float intensity = 1.0f - ((float)dist / 6.0f);
                float time = entity.tickCount + partialTick;

                poseStack.pushPose();
                poseStack.translate(0, -dist, 0);

                // --- FIX: TRANSFORMATIONS ---
                // RenderHelper.renderDisk draws flat on XZ plane by default.
                // 1. Removed Axis.XP.rotationDegrees(90.0F) -> No need to flip it anymore.
                // 2. Changed ZP to YP -> Spin animation must be around the Y (Up) axis now.
                poseStack.mulPose(Axis.YP.rotationDegrees(time * 5.0f));

                float wScale = 3.5f * (0.8f + 0.2f * (float)Math.sin(time * 0.2f));
                poseStack.scale(wScale, wScale, wScale);

                VertexConsumer c = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));

                // Color: FF0000 -> 1, 0, 0
                float alpha = 0.58f * intensity;
                float[] innerCol = {1f, 0f, 0f, alpha};
                float[] outerCol = {1f, 0f, 0f, 0f};

                // REFACTORED CALL
                RenderHelper.renderDisk(c, poseStack.last().pose(), poseStack.last().normal(),
                        0.4f, 1.0f, 16, 0f, innerCol, outerCol, 15728880);

                poseStack.popPose();
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BloodSphereEntity entity) {
        return TEXTURE;
    }
}