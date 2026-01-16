package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.client.layer.TentacleGlowLayer;
import net.agusdropout.bloodyhell.entity.custom.TentacleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TentacleEntityRenderer extends EntityRenderer<TentacleEntity> {

    private final TentacleEntityModel model;
    private static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/entity_tentacles.png");

    // Instancia de nuestra capa de brillo
    private final TentacleGlowLayer glowLayer;

    public TentacleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TentacleEntityModel(context.bakeLayer(TentacleEntityModel.LAYER_LOCATION));

        // INICIALIZACIÓN DE LA GLOW LAYER
        // Como TentacleEntity no es un LivingEntity, no tiene el sistema de layers automático.
        // Creamos un "Padre" anónimo para pasarle el modelo a la layer.
        this.glowLayer = new TentacleGlowLayer(new RenderLayerParent<TentacleEntity, TentacleEntityModel>() {
            @Override
            public TentacleEntityModel getModel() {
                return model;
            }

            @Override
            public ResourceLocation getTextureLocation(TentacleEntity entity) {
                return TEXTURE;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(TentacleEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(TentacleEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.getInitialDelay() > 0) {
            return;
        }
        poseStack.pushPose();

        float globalScale = 1.4f;
        poseStack.scale(globalScale, globalScale, globalScale);

        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(0.0D, -1.5D, 0.0D);

        // Interpolación para suavidad
        float smoothedLife = entity.getLifeTicks(partialTick);

        // Configuración de animación
        this.model.setupAnim(entity, 0, 0, smoothedLife, 0, 0);

        // 1. RENDER BASE (Sombras y luces normales)
        var vertexConsumer = buffer.getBuffer(this.model.renderType(getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // 2. RENDER GLOW MASK (Brillo en oscuridad)
        // Llamamos manualmente a la layer pasando los datos necesarios.
        this.glowLayer.render(poseStack, buffer, packedLight, entity, 0, 0, partialTick, smoothedLife, 0, 0);

        // 3. RENDER ITEMS EN MANO
        renderItemsInHand(entity, poseStack, buffer, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderItemsInHand(TentacleEntity entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        List<ItemStack> items = entity.getHeldVisualItems();

        if (!items.isEmpty()) {
            poseStack.pushPose();

            // --- CADENA CINEMÁTICA ---
            model.v1.translateAndRotate(poseStack);
            model.v2.translateAndRotate(poseStack);
            model.v3.translateAndRotate(poseStack);
            model.v4.translateAndRotate(poseStack);
            model.v5.translateAndRotate(poseStack);
            model.v6.translateAndRotate(poseStack);
            model.v7.translateAndRotate(poseStack);
            model.v8.translateAndRotate(poseStack);
            model.v9.translateAndRotate(poseStack);
            model.v10.translateAndRotate(poseStack);
            model.v11.translateAndRotate(poseStack);
            model.v12.translateAndRotate(poseStack);
            model.v13.translateAndRotate(poseStack);
            model.v14.translateAndRotate(poseStack);
            model.v15.translateAndRotate(poseStack);
            model.v16.translateAndRotate(poseStack);
            model.v17.translateAndRotate(poseStack); // La mano
            model.locator.translateAndRotate(poseStack);

            // --- ORGANIZACIÓN DE ITEMS ---
            poseStack.scale(0.35f, 0.35f, 0.35f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));

            for (int i = 0; i < items.size(); i++) {
                poseStack.pushPose();

                if (i == 0) {
                    poseStack.translate(0, 0, 0);
                } else if (i == 1) {
                    poseStack.translate(-0.25, 0, 0.15);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-20));
                } else if (i == 2) {
                    poseStack.translate(0.25, 0, 0.15);
                    poseStack.mulPose(Axis.YP.rotationDegrees(20));
                }

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        items.get(i),
                        ItemDisplayContext.FIXED,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        buffer,
                        entity.level(),
                        0
                );
                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}