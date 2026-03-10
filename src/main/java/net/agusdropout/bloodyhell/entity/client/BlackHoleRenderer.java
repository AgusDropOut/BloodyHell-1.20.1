package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.effects.BlackHoleEntity;
import net.agusdropout.bloodyhell.util.visuals.manager.BlackHoleEntityRenderManager;
import net.agusdropout.bloodyhell.util.visuals.manager.BlackHoleRenderManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class BlackHoleRenderer extends EntityRenderer<BlackHoleEntity> {

    public BlackHoleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlackHoleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float radius = entity.getRadius();
        float time = (entity.tickCount + partialTicks) / 20.0f;
        float alpha = 1.0f;

        float lifeRatio = (float) entity.tickCount / entity.getMaxAge();
        if (lifeRatio > 0.8f) {
            alpha = 1.0f - ((lifeRatio - 0.8f) / 0.2f);
        } else if (lifeRatio < 0.1f) {
            alpha = lifeRatio / 0.1f;
        }

        Matrix4f pose = poseStack.last().pose();

        BlackHoleEntityRenderManager.addBlackHole(pose, radius, time, alpha, entity.getColor());

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BlackHoleEntity entity) {
        return null;
    }
}