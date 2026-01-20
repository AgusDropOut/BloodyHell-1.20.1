package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class RitekeeperHeartLayer extends GeoRenderLayer<RitekeeperEntity> {

    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation("textures/particle/flash.png");
    private static final float PHI = 1.618034f;

    public RitekeeperHeartLayer(GeoRenderer<RitekeeperEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, RitekeeperEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        bakedModel.getBone("heart").ifPresent(heartBone -> {
            poseStack.pushPose();

            // 1. Position Map
            Vector3d worldPos = heartBone.getWorldPosition();
            double lerpX = Mth.lerp(partialTick, animatable.xo, animatable.getX());
            double lerpY = Mth.lerp(partialTick, animatable.yo, animatable.getY());
            double lerpZ = Mth.lerp(partialTick, animatable.zo, animatable.getZ());
            poseStack.translate(worldPos.x() - lerpX, worldPos.y() - lerpY, worldPos.z() - lerpZ);

            // 2. Heartbeat & Spin
            float age = animatable.tickCount + partialTick;
            float beat = (Mth.sin(age * 0.2f) + 1.0f) * 0.5f;
            float pulseScale = 0.8f + (beat * 0.1f);

            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(age * 1.5f));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(age * 0.5f));
            poseStack.scale(pulseScale, pulseScale, pulseScale);

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(BLANK_TEXTURE));

            // 3. Render

            // A. Inner Dense Core (Small, Fully Opaque)
            poseStack.pushPose();
            float coreScale = 0.08f;
            poseStack.scale(coreScale, coreScale, coreScale);
            // Alpha changed from 230 to 255 (100% Opacity)
            renderIcosahedron(poseStack, consumer, 15728880, 200, 10, 10, 255);
            poseStack.popPose();

            // B. Outer Shell (Ghostly)
            poseStack.pushPose();
            float outerScale = 0.15f;
            poseStack.scale(outerScale, outerScale, outerScale);
            renderIcosahedron(poseStack, consumer, 15728880, 255, 60, 60, 80);
            poseStack.popPose();

            poseStack.popPose();
        });
    }

    private void renderIcosahedron(PoseStack stack, VertexConsumer consumer, int light, int r, int g, int b, int a) {
        Matrix4f p = stack.last().pose();
        Matrix3f n = stack.last().normal();

        float[][] v = new float[][] {
                {-1,  PHI, 0}, { 1,  PHI, 0}, {-1, -PHI, 0}, { 1, -PHI, 0},
                { 0, -1,  PHI}, { 0,  1,  PHI}, { 0, -1, -PHI}, { 0,  1, -PHI},
                { PHI, 0, -1}, { PHI, 0,  1}, {-PHI, 0, -1}, {-PHI, 0,  1}
        };

        int[][] indices = {
                {0,11,5}, {0,5,1}, {0,1,7}, {0,7,10}, {0,10,11},
                {1,5,9}, {5,11,4}, {11,10,2}, {10,7,6}, {7,1,8},
                {3,9,4}, {3,4,2}, {3,2,6}, {3,6,8}, {3,8,9},
                {4,9,5}, {2,4,11}, {6,2,10}, {8,6,7}, {9,8,1}
        };

        for (int[] face : indices) {
            float[] v1 = v[face[0]];
            float[] v2 = v[face[1]];
            float[] v3 = v[face[2]];
            drawTri(consumer, p, n, light, r, g, b, a, v1[0], v1[1], v1[2], v2[0], v2[1], v2[2], v3[0], v3[1], v3[2]);
        }
    }

    private void drawTri(VertexConsumer c, Matrix4f p, Matrix3f n, int light, int r, int g, int b, int a,
                         float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        float nx = (x1 + x2 + x3) / 3.0f;
        float ny = (y1 + y2 + y3) / 3.0f;
        float nz = (z1 + z2 + z3) / 3.0f;

        c.vertex(p, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
        c.vertex(p, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
        c.vertex(p, x3, y3, z3).color(r, g, b, a).uv(0.5f, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();

        c.vertex(p, x3, y3, z3).color(r, g, b, a).uv(0.5f, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
        c.vertex(p, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
        c.vertex(p, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, nx, ny, nz).endVertex();
    }
}