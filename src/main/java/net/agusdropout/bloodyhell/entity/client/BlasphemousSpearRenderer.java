package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpearEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlasphemousSpearRenderer extends GeoEntityRenderer<BlasphemousSpearEntity> {
    public BlasphemousSpearRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlasphemousSpearModel());

        this.shadowRadius = 0.3f;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void render(BlasphemousSpearEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // Mantenemos solo la escala aleatoria (Efecto visual bonito)
        float randomVar = (entity.getId() % 5) * 0.05f;
        float scaleXZ = 0.8f + randomVar;
        float scaleY = 0.8f + ((entity.getId() % 3) * 0.15f);
        poseStack.scale(scaleXZ, scaleY, scaleXZ);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }


}