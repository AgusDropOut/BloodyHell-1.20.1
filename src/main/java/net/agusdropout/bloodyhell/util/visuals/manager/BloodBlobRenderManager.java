package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.block.entity.custom.engine.RhnullBloodEngineBlockEntity;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.types.IBloodBlobEmitter;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BloodBlobRenderManager {

    public static void renderAll(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (RhnullBloodEngineBlockEntity.ACTIVE_ENGINES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        Camera camera = event.getCamera();
        ShaderInstance shader = ModShaders.BLOOD_BLOB_SHADER;
        if (shader == null) return;

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        int currentTex = RenderSystem.getShaderTexture(0);
        RenderSystem.setShaderTexture(0, mc.getMainRenderTarget().getDepthTextureId());
        RenderSystem.setShader(() -> shader);


        Matrix4f vanillaViewMat = new Matrix4f(event.getPoseStack().last().pose());
        Matrix4f projMat = new Matrix4f(event.getProjectionMatrix());
        Matrix4f invProjMat = new Matrix4f(projMat).invert();


        PoseStack cleanStack = new PoseStack();
        cleanStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.getXRot()));
        cleanStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
        Matrix4f cleanViewMat = new Matrix4f(cleanStack.last().pose());
        Matrix4f cleanInvViewMat = new Matrix4f(cleanViewMat).invert();


        float partialTicks = mc.getFrameTime();
        double bobX = 0.0;
        double bobY = 0.0;

        if (mc.options.bobView().get() && mc.getCameraEntity() instanceof net.minecraft.world.entity.player.Player player) {
            float f = player.walkDist - player.walkDistO;
            float walkTime = -(player.walkDist + f * partialTicks);
            float bobIntensity = net.minecraft.util.Mth.lerp(partialTicks, player.oBob, player.bob);

            bobX = (double)(net.minecraft.util.Mth.sin(walkTime * (float)Math.PI) * bobIntensity * 0.5F);
            bobY = (double)(-Math.abs(net.minecraft.util.Mth.cos(walkTime * (float)Math.PI) * bobIntensity));
        }

        Vector4f bobOffsetVec = new Vector4f((float)-bobX, (float)-bobY, 0.0f, 0.0f);
        bobOffsetVec.mul(cleanInvViewMat);

        Vec3 rawCamPos = camera.getPosition();
        double compCamX = rawCamPos.x + bobOffsetVec.x();
        double compCamY = rawCamPos.y + bobOffsetVec.y();
        double compCamZ = rawCamPos.z + bobOffsetVec.z();

        if(shader.safeGetUniform("u_CleanInvViewMat") != null) shader.safeGetUniform("u_CleanInvViewMat").set(cleanInvViewMat);
        if(shader.safeGetUniform("u_VanillaModelViewMat") != null) shader.safeGetUniform("u_VanillaModelViewMat").set(vanillaViewMat);
        if(shader.safeGetUniform("u_ProjMat") != null) shader.safeGetUniform("u_ProjMat").set(projMat);
        if(shader.safeGetUniform("u_InvProjMat") != null) shader.safeGetUniform("u_InvProjMat").set(invProjMat);
        if(shader.safeGetUniform("u_BobbingOffset") != null) shader.safeGetUniform("u_BobbingOffset").set(bobOffsetVec.x(), bobOffsetVec.y(), bobOffsetVec.z());
        if(shader.safeGetUniform("u_Time") != null) shader.safeGetUniform("u_Time").set((float)(Util.getMillis() % 100000L) / 1000.0f);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        for (IBloodBlobEmitter emitter : RhnullBloodEngineBlockEntity.ACTIVE_ENGINES) {
            if (!emitter.isActive()) continue;

            Vector3f absCenter = emitter.getBlobCenter();


            float relX = (float) (absCenter.x() - compCamX);
            float relY = (float) (absCenter.y() - compCamY);
            float relZ = (float) (absCenter.z() - compCamZ);

            if(shader.safeGetUniform("relativeBlobCenter") != null) {
                shader.safeGetUniform("relativeBlobCenter").set(relX, relY, relZ);
            }

            Vector3f baseColor = emitter.getBloodBaseColor();
            Vector3f glowColor = emitter.getBloodGlowColor();
            if(shader.safeGetUniform("bloodBaseColor") != null) shader.safeGetUniform("bloodBaseColor").set(baseColor.x(), baseColor.y(), baseColor.z());
            if(shader.safeGetUniform("bloodGlowColor") != null) shader.safeGetUniform("bloodGlowColor").set(glowColor.x(), glowColor.y(), glowColor.z());
            if(shader.safeGetUniform("chargeLevel") != null) shader.safeGetUniform("chargeLevel").set(emitter.getChargeLevel());
            if(shader.safeGetUniform("stabilization") != null) shader.safeGetUniform("stabilization").set(emitter.getStabilizationLevel());

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            buffer.vertex(-1, -1, 0).endVertex();
            buffer.vertex( 1, -1, 0).endVertex();
            buffer.vertex( 1,  1, 0).endVertex();
            buffer.vertex(-1,  1, 0).endVertex();
            tesselator.end();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, currentTex);
    }
}