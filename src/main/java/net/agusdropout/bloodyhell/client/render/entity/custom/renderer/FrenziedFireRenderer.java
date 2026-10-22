package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.effects.FrenziedFireEntity;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.client.render.effect.BillboardEffects;
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

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        float time = ShaderTime.seconds();
        float progress = entity.getLifeProgress();

        float scale = Mth.sin(progress * (float) Math.PI) * 1.5F;
        float alpha = 1.0F - (float) Math.pow(progress, 3.0);

        BillboardEffects.LINEAR_FRENZIED_FLAME.submit(
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
        return null;
    }
}
