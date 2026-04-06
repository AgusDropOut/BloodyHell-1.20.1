package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.effects.FrenziedFireEntity;
import net.agusdropout.bloodyhell.util.visuals.manager.LinearFrenziedFlameRenderManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FrenziedFireRenderer extends EntityRenderer<FrenziedFireEntity> {

    public FrenziedFireRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FrenziedFireEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        /* Billboard transformation mathematics */
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        //poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));

        float time = (System.currentTimeMillis() % 100000L) / 1000.0F;
        float progress = entity.getLifeProgress();

        /* Smooth scale-in and fade-out calculations */
        float scale = Mth.sin(progress * (float) Math.PI) * 1.5F;
        float alpha = 1.0F - (float) Math.pow(progress, 3.0);

        LinearFrenziedFlameRenderManager.addFlame(
                poseStack.last().pose(),
                scale,
                1.0F, 1.0F, 1.0F,
                alpha,
                time
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FrenziedFireEntity entity) {
        /* Texture binding is handled by the shader render manager */
        return null;
    }
}