package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.entity.projectile.BloodClotProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
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

        // 1. BILLBOARD + TUMBLE ROTATION
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        // Add a slow rotation around Z to make the mass look like it's rolling
        poseStack.mulPose(Axis.ZP.rotationDegrees(time * 5.0f));

        // 2. SETTINGS
        float pixelSize = 0.05f;   // Grid spacing
        float drawSize = pixelSize * 1.3f; // DRAW SIZE (130% of spacing = Overlap to fix gaps)
        float layerSpacing = 0.04f;

        float pulse = 1.0f + (Mth.sin(time * 0.4f) * 0.1f);
        poseStack.scale(pulse, pulse, pulse);

        // Center Volume
        float totalWidth = GRID_SIZE * pixelSize;
        float totalDepth = LAYERS * layerSpacing;
        poseStack.translate(-totalWidth / 2.0f, -totalWidth / 2.0f, -totalDepth / 2.0f);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BLANK_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f p = pose.pose();
        Matrix3f n = pose.normal();

        // 3. RENDER LOOP
        for (int z = 0; z < LAYERS; z++) {
            float d = z * layerSpacing;

            // Layer Shift (Viscous wobble)
            float shiftX = Mth.sin(time * 0.5f + z * 0.6f) * 0.03f;
            float shiftY = Mth.cos(time * 0.5f + z * 0.6f) * 0.03f;

            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {

                    float dx = x - 7.5f;
                    float dy = y - 7.5f;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    if (dist > RADIUS) continue;

                    // Wave Logic
                    double angle = Math.atan2(dy, dx);
                    // Higher frequency (angle * 6) makes more "ripples" on the edge
                    float ripple = Mth.sin((float) (time * 0.6f + angle * 6.0f)) * 0.04f * (dist / RADIUS);

                    float finalX = (x * pixelSize) + shiftX + (float)(Math.cos(angle) * ripple);
                    float finalY = (y * pixelSize) + shiftY + (float)(Math.sin(angle) * ripple);

                    // Colors
                    int r, g, b;
                    if (dist > RADIUS - 2.5f) {
                        // BORDER: Darker and Thicker
                        float darkness = (dist - (RADIUS - 2.5f)) / 2.5f;
                        r = (int) (140 * (1.0f - darkness)); // Fades to black
                        g = 0;
                        b = 0;
                    } else {
                        // CENTER: Bright
                        r = 210 + (int)(Mth.sin(time * 0.2f) * 20); // Pulse color slightly
                        g = 20;
                        b = 20;
                    }

                    drawPixel(consumer, p, n, packedLight, finalX, finalY, d, drawSize, r, g, b, 255);
                }
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void drawPixel(VertexConsumer consumer, Matrix4f p, Matrix3f n, int light, float x, float y, float z, float size, int r, int g, int b, int a) {
        // We draw the pixel slightly larger than the grid step (size passed in is 1.3x)
        consumer.vertex(p, x, y, z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        consumer.vertex(p, x, y + size, z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        consumer.vertex(p, x + size, y + size, z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        consumer.vertex(p, x + size, y, z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BloodClotProjectile entity) {
        return BLANK_TEXTURE;
    }
}