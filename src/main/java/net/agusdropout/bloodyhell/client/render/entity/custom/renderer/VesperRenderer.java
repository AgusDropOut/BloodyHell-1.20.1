package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.entity.layer.ModModelLayers;
import net.agusdropout.bloodyhell.client.render.entity.layer.VesperEyesLayer;
import net.agusdropout.bloodyhell.entity.custom.CrystalPillar;
import net.agusdropout.bloodyhell.entity.custom.VesperEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VesperRenderer extends MobRenderer<VesperEntity, VesperModel<VesperEntity>> {
    private static final ResourceLocation TEXTURE = BloodyHell.prefix("textures/entity/vesper.png");


    public VesperRenderer(EntityRendererProvider.Context context) {
        super(context, new VesperModel<>(context.bakeLayer(ModModelLayers.VESPER)), 1f);
        this.addLayer(new VesperEyesLayer<>(this));
    }

    @Override
    public void render(VesperEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.scale(1.2f, 1.2f, 1.2f);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(VesperEntity entity) {
        return TEXTURE;
    }
}
