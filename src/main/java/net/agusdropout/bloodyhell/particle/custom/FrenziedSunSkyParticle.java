package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.util.visuals.manager.FrenziedFlameRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.entity.Entity;

public class FrenziedSunSkyParticle extends Particle {

    private final float baseScale;

    public FrenziedSunSkyParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.hasPhysics = false;
        this.lifetime = 100;

        /* Scaled up massively because we are pushing it 400 blocks into the background */
        this.baseScale = 160.0F;
        this.alpha = 1.0F;
    }

    @Override
    public void tick() {
        if (ClientInsightData.getPlayerInsight() <= 50.0F) {
            this.remove();
            return;
        }

        this.age = 0;


        Entity cameraEntity = Minecraft.getInstance().cameraEntity;
        if (cameraEntity != null) {
            this.setPos(cameraEntity.getX(), cameraEntity.getY(), cameraEntity.getZ());
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {

        PoseStack poseStack = new PoseStack();


        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(130.0F));

        poseStack.translate(0.0D, -400.0D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F)); // Lay it flat

        float time = (System.currentTimeMillis() % 100000L) / 1000.0F;
        float rCol = 1.0F;
        float gCol = 0.8F;
        float bCol = 0.2F;


        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 20.0D);
        FrenziedFlameRenderManager.addFlame(poseStack.last().pose(), this.baseScale * 1.15F, rCol, gCol, bCol, this.alpha * 0.6F, time + 25.0F);
        poseStack.popPose();

        FrenziedFlameRenderManager.addFlame(poseStack.last().pose(), this.baseScale, rCol, gCol, bCol, this.alpha, time);

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, -20.0D);
        FrenziedFlameRenderManager.addFlame(poseStack.last().pose(), this.baseScale * 0.85F, rCol, gCol, bCol, this.alpha * 0.8F, time - 15.0F);
        poseStack.popPose();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}