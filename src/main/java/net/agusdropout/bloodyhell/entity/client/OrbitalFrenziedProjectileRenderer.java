package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.projectile.OrbitalFrenziedProjectile;
import net.agusdropout.bloodyhell.util.visuals.manager.FrenziedTrailRenderManager;
import net.agusdropout.bloodyhell.util.visuals.manager.RadiantEnergyRenderManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class OrbitalFrenziedProjectileRenderer extends EntityRenderer<OrbitalFrenziedProjectile> {

    public OrbitalFrenziedProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(OrbitalFrenziedProjectile entity, Frustum frustum, double camX, double camY, double camZ) {
        /* Bypasses frustum culling so the trail remains visible even if the entity's core leaves the screen */
        return true;
    }

    @Override
    public void render(OrbitalFrenziedProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.historyCore.isEmpty()) return;

        float time = (System.currentTimeMillis() % 100000L) / 1000.0F;
        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();

        double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        Vec3 currentCore = new Vec3(lerpX, lerpY, lerpZ);

        /* 2. Extract the pure Camera View Matrix from the PoseStack. */
        float relX = (float) (lerpX - cameraPos.x());
        float relY = (float) (lerpY - cameraPos.y());
        float relZ = (float) (lerpZ - cameraPos.z());

        PoseStack viewStack = new PoseStack();
        viewStack.last().pose().set(poseStack.last().pose());
        viewStack.translate(-relX, -relY, -relZ);

        /* 3. Manipulate the RenderSystem ModelView stack directly */
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(viewStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        /* 4. Inject the active frame coordinates at the head of the trails */
        List<Vec3> smoothCore = new ArrayList<>();
        smoothCore.add(currentCore);
        smoothCore.addAll(entity.historyCore);

        double interpolatedAngle = (entity.tickCount + partialTicks) * 0.4D;
        double offsetX = Math.cos(interpolatedAngle) * 0.35D;
        double offsetZ = Math.sin(interpolatedAngle) * 0.35D;

        List<Vec3> smoothOrbit1 = new ArrayList<>();
        smoothOrbit1.add(new Vec3(lerpX + offsetX, lerpY, lerpZ + offsetZ));
        smoothOrbit1.addAll(entity.historyOrbit1);

        List<Vec3> smoothOrbit2 = new ArrayList<>();
        smoothOrbit2.add(new Vec3(lerpX - offsetX, lerpY, lerpZ - offsetZ));
        smoothOrbit2.addAll(entity.historyOrbit2);

        float alpha = 1.0F;

        FrenziedTrailRenderManager.addTrail(
                smoothCore,
                cameraPos,
                entity.coreWidth,
                1.0F, 1.0F, 1.0F,
                alpha,
                time
        );

        RadiantEnergyRenderManager.addTrail(
                smoothOrbit1,
                cameraPos,
                entity.orbitWidth,
                1.0F, 1.0F, 1.0F,
                alpha,
                time
        );

        RadiantEnergyRenderManager.addTrail(
                smoothOrbit2,
                cameraPos,
                entity.orbitWidth,
                1.0F, 1.0F, 1.0F,
                alpha,
                time
        );

        /* 5. Restore the RenderSystem stack */
        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(OrbitalFrenziedProjectile entity) {
        return null;
    }
}