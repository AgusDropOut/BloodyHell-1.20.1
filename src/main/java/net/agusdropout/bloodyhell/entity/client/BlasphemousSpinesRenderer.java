package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.entity.client.BlasphemousSpinesModel;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpinesEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlasphemousSpinesRenderer extends GeoEntityRenderer<BlasphemousSpinesEntity> {
    public BlasphemousSpinesRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlasphemousSpinesModel());
    }
    @Override
    public void render(BlasphemousSpinesEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // --- IDEA EXTRA: VARIACIÓN ALEATORIA ---
        // Usamos la ID de la entidad como semilla "pseudo-random"
        // Esto hace que cada espina tenga un tamaño único y constante.
        float randomVar = (entity.getId() % 5) * 0.05f; // Variación entre 0.0 y 0.25

        // Base scale: 0.6 (Más chicas como pediste)
        // Rango final: Entre 0.6 y 0.85 de tamaño original
        float scale = 0.6f + randomVar;

        // También podemos variar un poco la altura (eje Y) independientemente
        float scaleY = 0.7f + ((entity.getId() % 3) * 0.1f);

        poseStack.scale(scale, scaleY, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }


}