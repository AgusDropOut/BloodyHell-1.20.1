package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.client.TentacleEntityModel;
import net.agusdropout.bloodyhell.entity.custom.TentacleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

// Extiende de RenderLayer
public class TentacleGlowLayer extends RenderLayer<TentacleEntity, TentacleEntityModel> {

    // Ubicación de tu textura de brillo (Fondo negro, partes brillantes con color)
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/entity_tentacles_glowmask.png");

    public TentacleGlowLayer(RenderLayerParent<TentacleEntity, TentacleEntityModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TentacleEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        // Solo renderizamos si la entidad está viva (opcional, pero se ve mejor)
        if (!entity.isInvisible()) {

            // RenderType.eyes() hace que la textura brille en la oscuridad (ignora packedLight)
            // y usa mezcla aditiva sobre el fondo negro de la textura.
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(GLOW_TEXTURE));

            // Usamos el modelo del padre (el tentáculo ya posicionado)
            // IMPORTANTE: 'packedLight' aquí se ignora por el shader 'eyes', pero 'packedOverlay' no.
            // Usamos OverlayTexture.NO_OVERLAY para que no se ponga roja al recibir daño (opcional)
            // O usa 15728880 (luz máxima) si usaras otro render type.

            this.getParentModel().renderToBuffer(
                    poseStack,
                    vertexConsumer,
                    15728880, // Luz Máxima (Full Bright)
                    OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F // Color RGBA (Blanco = color original de la textura)
            );
        }
    }
}