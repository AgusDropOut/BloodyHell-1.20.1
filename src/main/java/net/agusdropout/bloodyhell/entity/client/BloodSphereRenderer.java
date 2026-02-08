package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodSphereEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BloodSphereRenderer extends EntityRenderer<BloodSphereEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/misc/white.png");


    private static final float BASE_RADIUS = 6.0f;

    public BloodSphereRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodSphereEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float time = entity.tickCount + partialTick;
        float radius = entity.getRadius();

        // 1. Calculate Size Multiplier
        float radiusCorrection = radius / BASE_RADIUS;

        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        // 2. Apply Scale
        // Base scale 1.8f * Breathing Animation * Gem Multiplier
        float scale = 1.8f * (1.0f + (float)Math.sin(time * 0.3f) * 0.05f) * radiusCorrection;
        poseStack.scale(scale, scale, scale);

        Matrix4f p = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();

        // 3. Render Sphere Layers
        VertexConsumer portal = buffer.getBuffer(RenderType.endPortal());
        RenderHelper.renderSphere(portal, p, n, 0.24f, 12, 12, 1f, 1f, 1f, 1f, 255);

        VertexConsumer core = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        RenderHelper.renderSphere(core, p, n, 0.3f, 12, 12, 0.26f, 0f, 0f, 0.78f, packedLight);

        VertexConsumer halo = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        RenderHelper.renderSphere(halo, p, n, 0.85f, 12, 12, 0.73f, 0f, 0f, 0.47f, packedLight);
        RenderHelper.renderSphere(halo, p, n, 0.9f, 12, 12, 0f, 0f, 0f, 0.15f, packedLight);

        poseStack.popPose();

        // 4. Ground Effect (Also Scaled)
        renderGroundEffect(entity, partialTick, poseStack, buffer, radiusCorrection, radius);

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderGroundEffect(BloodSphereEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, float radiusCorrection, float currentRadius) {
        Level level = entity.level();
        Vec3 pos = entity.getPosition(partialTick);
        BlockPos blockPos = BlockPos.containing(pos);

        // Scan downwards for ground
        double groundY = -999;

        // Search distance scales slightly with size (bigger sphere = casts shadow further down)
        int searchDist = (int) Math.ceil(currentRadius * 1.5);

        for (int i = 0; i <= searchDist; i++) {
            BlockPos check = blockPos.below(i);
            if (!level.getBlockState(check).isAir() && level.getBlockState(check).isSolidRender(level, check)) {
                groundY = check.getY() + 1.01;
                break;
            }
        }

        if (groundY != -999) {
            double dist = pos.y - groundY;

            // Only render if close enough to ground (relative to size)
            if (dist <= currentRadius) {
                float intensity = 1.0f - ((float)dist / currentRadius);
                float time = entity.tickCount + partialTick;

                poseStack.pushPose();
                poseStack.translate(0, -dist, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(time * 5.0f));

                // Scale the wave effect
                float wScale = 3.5f * (0.8f + 0.2f * (float)Math.sin(time * 0.2f)) * radiusCorrection;
                poseStack.scale(wScale, wScale, wScale);

                VertexConsumer c = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));

                float alpha = 0.58f * intensity;
                float[] innerCol = {1f, 0f, 0f, alpha};
                float[] outerCol = {1f, 0f, 0f, 0f};

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