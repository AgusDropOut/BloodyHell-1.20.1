package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.entity.projectile.BloodClotProjectile;
import net.agusdropout.bloodyhell.util.RenderHelper; // Import Helper
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BloodClotRenderer extends EntityRenderer<BloodClotProjectile> {

    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation("textures/particle/flash.png");
    private static final int GRID_SIZE = 16;
    private static final float RADIUS = 7.5f;
    private static final int LAYERS = 6;

    public BloodClotRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodClotProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float time = entity.tickCount + partialTicks;

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(time * 5.0f));

        float pixelSize = 0.05f;
        float drawSize = pixelSize * 1.3f;
        float layerSpacing = 0.04f;

        float pulse = 1.0f + (Mth.sin(time * 0.4f) * 0.1f);
        poseStack.scale(pulse, pulse, pulse);

        float totalWidth = GRID_SIZE * pixelSize;
        float totalDepth = LAYERS * layerSpacing;
        poseStack.translate(-totalWidth / 2.0f, -totalWidth / 2.0f, -totalDepth / 2.0f);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BLANK_TEXTURE));
        Matrix4f p = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();

        for (int z = 0; z < LAYERS; z++) {
            float d = z * layerSpacing;
            float shiftX = Mth.sin(time * 0.5f + z * 0.6f) * 0.03f;
            float shiftY = Mth.cos(time * 0.5f + z * 0.6f) * 0.03f;

            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {
                    float dx = x - 7.5f;
                    float dy = y - 7.5f;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    if (dist > RADIUS) continue;

                    double angle = Math.atan2(dy, dx);
                    float ripple = Mth.sin((float) (time * 0.6f + angle * 6.0f)) * 0.04f * (dist / RADIUS);

                    float finalX = (x * pixelSize) + shiftX + (float)(Math.cos(angle) * ripple);
                    float finalY = (y * pixelSize) + shiftY + (float)(Math.sin(angle) * ripple);

                    int r, g, b;
                    if (dist > RADIUS - 2.5f) {
                        float darkness = (dist - (RADIUS - 2.5f)) / 2.5f;
                        r = (int) (140 * (1.0f - darkness));
                        g = 0; b = 0;
                    } else {
                        r = 210 + (int)(Mth.sin(time * 0.2f) * 20);
                        g = 20; b = 20;
                    }

                    // --- REFACTORED CALL ---
                    RenderHelper.renderPixel(consumer, p, n, finalX, finalY, d, drawSize,
                            r/255f, g/255f, b/255f, 1.0f, packedLight);
                }
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodClotProjectile entity) {
        return BLANK_TEXTURE;
    }
}