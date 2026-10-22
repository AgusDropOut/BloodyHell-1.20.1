package net.agusdropout.bloodyhell.util.visuals.manager;

import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.client.shader.ShaderUtils;
import net.agusdropout.bloodyhell.util.visuals.ViewBobbing;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.Direction;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import com.mojang.math.Axis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BloodStainDecalRenderManager {
    private static final Map<Integer, DecalData> ACTIVE_DECALS = new ConcurrentHashMap<>();

    public static void addDecal(int id, double x, double y, double z, Direction face, float rot, float size, float r, float g, float b, float a) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        ACTIVE_DECALS.put(id, new DecalData(id, x, y, z, face, rot, size, r, g, b, a));
    }

    public static void renderAllAndClear(RenderLevelStageEvent event) {
        if (ACTIVE_DECALS.isEmpty()) return;

        Camera camera = event.getCamera();
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );

        ShaderInstance shader = ModShaders.BLOOD_STAIN_SHADER;
        if (shader != null) {
            if (shader.safeGetUniform("Time") != null) {
                shader.safeGetUniform("Time").set(ShaderTime.seconds());
            }
            if (shader.safeGetUniform("Intensity") != null) {
                shader.safeGetUniform("Intensity").set(1.0f);
            }
            if (shader.safeGetUniform("RealProjMat") != null) {
                shader.safeGetUniform("RealProjMat").set(event.getProjectionMatrix());
            }

            RenderSystem.setShader(() -> shader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

            PoseStack poseStack = new PoseStack();
            if (ShaderUtils.areShadersActive()) {
                ViewBobbing.apply(poseStack, event.getPartialTick());
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));

            for (DecalData data : ACTIVE_DECALS.values()) {
                poseStack.pushPose();

                poseStack.translate(data.x - camX, data.y - camY, data.z - camZ);

                float baseOffset = 0.01f;
                float microZ = baseOffset + (0.001f * (data.id % 50));

                switch (data.face) {
                    case UP:    poseStack.translate(0, microZ, 0); poseStack.mulPose(Axis.XP.rotationDegrees(-90)); break;
                    case DOWN:  poseStack.translate(0, 1.0f - microZ, 0); poseStack.mulPose(Axis.XP.rotationDegrees(90)); break;
                    case NORTH: poseStack.translate(0, 0.5, 1.0f - microZ); poseStack.mulPose(Axis.YP.rotationDegrees(180)); break;
                    case SOUTH: poseStack.translate(0, 0.5, microZ); break;
                    case WEST:  poseStack.translate(1.0f - microZ, 0.5, 0); poseStack.mulPose(Axis.YP.rotationDegrees(-90)); break;
                    case EAST:  poseStack.translate(microZ, 0.5, 0); poseStack.mulPose(Axis.YP.rotationDegrees(90)); break;
                }

                poseStack.mulPose(Axis.ZP.rotationDegrees(data.rot));
                poseStack.scale(data.size, data.size, data.size);

                Matrix4f pose = poseStack.last().pose();
                float s = 0.5f;
                buffer.vertex(pose, -s, -s, 0).color(data.r, data.g, data.b, data.a).uv(0, 1).endVertex();
                buffer.vertex(pose, -s, s, 0).color(data.r, data.g, data.b, data.a).uv(0, 0).endVertex();
                buffer.vertex(pose, s, s, 0).color(data.r, data.g, data.b, data.a).uv(1, 0).endVertex();
                buffer.vertex(pose, s, -s, 0).color(data.r, data.g, data.b, data.a).uv(1, 1).endVertex();

                poseStack.popPose();
            }

            tesselator.end();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();

        ACTIVE_DECALS.clear();
    }

    private static class DecalData {
        int id;
        double x, y, z;
        Direction face;
        float rot, size, r, g, b, a;

        DecalData(int id, double x, double y, double z, Direction face, float rot, float size, float r, float g, float b, float a) {
            this.id = id; this.x = x; this.y = y; this.z = z;
            this.face = face; this.rot = rot; this.size = size;
            this.r = r; this.g = g; this.b = b; this.a = a;
        }
    }
}