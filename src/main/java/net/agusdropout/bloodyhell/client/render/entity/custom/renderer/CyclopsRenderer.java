package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.custom.CyclopsEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.minecraft.util.RandomSource;

public class CyclopsRenderer extends EntityRenderer<CyclopsEntity> {

    private static final int LAT_SEGMENTS = 32;
    private static final int LON_SEGMENTS = 64;
    private static final float EYE_RADIUS = 0.5f;
    private static final float IRIS_SIZE = 0.2f;

    private static final ResourceLocation IRIS_TEXTURE = new ResourceLocation("bloodyhell", "textures/misc/iris.png");
    private final SimplexNoise colorNoise;

    public CyclopsRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.colorNoise = new SimplexNoise(RandomSource.create());
    }

    @Override
    public void render(CyclopsEntity entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
        pose.translate(0, entity.getBbHeight() / 2, 0);

        float interpolatedYaw = entity.getEyeYaw(partialTicks);
        float interpolatedPitch = entity.getEyePitch(partialTicks);

        float yawRad = (float) -Math.toRadians(interpolatedYaw) -110;
        float pitchRad = (float) Math.toRadians(interpolatedPitch);

        Quaternionf rotation = new Quaternionf()
                .rotateY(yawRad)
                .rotateX(pitchRad);

        pose.mulPose(rotation);

        Matrix4f matrix = pose.last().pose();
        int fullBright = LightTexture.FULL_BRIGHT;

        VertexConsumer sphereBuffer = buffer.getBuffer(RenderType.entitySolid(new ResourceLocation("textures/misc/white.png")));

        Vector3f colorYellow = new Vector3f(1.0f, 1.0f, 0.0f);
        Vector3f colorOrange = new Vector3f(1.0f, 0.6f, 0.0f);

        float time = (entity.tickCount + partialTicks) * 0.02f;

        for (int i = 0; i < LAT_SEGMENTS; i++) {
            for (int j = 0; j < LON_SEGMENTS; j++) {
                double theta1 = Math.PI * i / LAT_SEGMENTS - Math.PI / 2;
                double theta2 = Math.PI * (i + 1) / LAT_SEGMENTS - Math.PI / 2;
                double phi1 = 2 * Math.PI * j / LON_SEGMENTS;
                double phi2 = 2 * Math.PI * (j + 1) / LON_SEGMENTS;

                Vector3f v1 = new Vector3f((float)(Math.cos(theta1) * Math.cos(phi1)), (float)Math.sin(theta1), (float)(Math.cos(theta1) * Math.sin(phi1)));
                Vector3f v2 = new Vector3f((float)(Math.cos(theta2) * Math.cos(phi1)), (float)Math.sin(theta2), (float)(Math.cos(theta2) * Math.sin(phi1)));
                Vector3f v3 = new Vector3f((float)(Math.cos(theta2) * Math.cos(phi2)), (float)Math.sin(theta2), (float)(Math.cos(theta2) * Math.sin(phi2)));
                Vector3f v4 = new Vector3f((float)(Math.cos(theta1) * Math.cos(phi2)), (float)Math.sin(theta1), (float)(Math.cos(theta1) * Math.sin(phi2)));

                Vector3f color1 = getColorFromNoise(v1, time, colorYellow, colorOrange);
                Vector3f color2  = getColorFromNoise(v2, time, colorYellow, colorOrange);
                Vector3f color3 = getColorFromNoise(v3, time, colorYellow, colorOrange);
                Vector3f color4 = getColorFromNoise(v4, time, colorYellow, colorOrange);

                v1.mul(EYE_RADIUS);
                v2.mul(EYE_RADIUS);
                v3.mul(EYE_RADIUS);
                v4.mul(EYE_RADIUS);

                sphereBuffer.vertex(matrix, v1.x, v1.y, v1.z).color(color1.x, color1.y, color1.z, 1.0f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(1,0,0).endVertex();
                sphereBuffer.vertex(matrix, v2.x, v2.y, v2.z).color(color2.x, color2.y, color2.z, 1.0f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(1,0,0).endVertex();
                sphereBuffer.vertex(matrix, v3.x, v3.y, v3.z).color(color3.x, color3.y, color3.z, 1.0f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(1,0,0).endVertex();
                sphereBuffer.vertex(matrix, v4.x, v4.y, v4.z).color(color4.x, color4.y, color4.z, 1.0f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(1,0,0).endVertex();
            }
        }

        VertexConsumer irisBuffer = buffer.getBuffer(RenderType.entityTranslucent(IRIS_TEXTURE));
        float s = IRIS_SIZE;
        float irisOffset = -EYE_RADIUS - 0.001f;

        irisBuffer.vertex(matrix, -s, -s, irisOffset).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(0, 0, -1).endVertex();
        irisBuffer.vertex(matrix, s, -s, irisOffset).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(0, 0, -1).endVertex();
        irisBuffer.vertex(matrix, s, s, irisOffset).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(0, 0, -1).endVertex();
        irisBuffer.vertex(matrix, -s, s, irisOffset).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright).normal(0, 0, -1).endVertex();

        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    private Vector3f getColorFromNoise(Vector3f position, float time, Vector3f colorA, Vector3f colorB) {
        float noiseScale = 2.0f;
        double noiseValue = colorNoise.getValue(position.x * noiseScale + time, position.y * noiseScale, position.z * noiseScale);
        float t = (float)((noiseValue + 1.0) / 2.0);
        Vector3f finalColor = new Vector3f();
        colorA.lerp(colorB, t, finalColor);
        return finalColor;
    }

    @Override
    public ResourceLocation getTextureLocation(CyclopsEntity entity) {
        return null;
    }
}
