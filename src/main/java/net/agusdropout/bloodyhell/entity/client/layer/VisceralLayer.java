package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VisceralLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    // You need a texture. Ideally, a white "veiny" or "noisy" texture with transparency.
    // White pixels will become Green/Yellow. Transparent pixels will show the mob underneath.
    private static final ResourceLocation VISCERAL_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/visceral_overlay.png");

    public VisceralLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {


        MobEffectInstance instance = entity.getEffect(ModEffects.VISCERAL_EFFECT.get());


            // Instant removal if null, almost finished, or in water
            if (instance == null || instance.getDuration() <= 1 || entity.isInWater()) {
                return;
            }
            // 1. Get the model
            M model = this.getParentModel();

            // 2. Prepare the model (sync animation state)
            model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            // 3. Create the Vertex Consumer with a Translucent RenderType
            // 'entityTranslucent' allows us to use Alpha (transparency)
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(VISCERAL_TEXTURE));

            // 4. Render with Tint
            // R: 0.6, G: 1.0, B: 0.2 (Sickly Green-Yellow)
            // A: 0.6 (60% Opacity) - Change this to make it more or less visible

            // Optional: Pulsing Alpha
            float pulse = (float) (Math.sin(ageInTicks * 0.1) * 0.2 + 0.6); // Oscillates between 0.4 and 0.8 opacity

            model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0.6F, 1.0F, 0.2F, pulse);

    }
}