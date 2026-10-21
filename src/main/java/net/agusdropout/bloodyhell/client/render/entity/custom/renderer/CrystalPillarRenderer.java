package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.entity.layer.CrystalPillarEyesLayer;
import net.agusdropout.bloodyhell.client.render.entity.layer.ModModelLayers;
import net.agusdropout.bloodyhell.entity.custom.CrystalPillar;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrystalPillarRenderer extends EntityRenderer<CrystalPillar> {
    private static final ResourceLocation TEXTURE = BloodyHell.prefix("textures/entity/crystal_pillar.png");
    private final CrystalPillarModel<CrystalPillar> model;

    public CrystalPillarRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CrystalPillarModel<>(context.bakeLayer(ModModelLayers.CRYSTAL_PILLAR));

    }

    @Override
    public void render(CrystalPillar entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (!entity.isActive()) {
            return;
        }
        poseStack.scale(1.5F, 1.5F, 1.5F);
        float ticks = entity.tickCount + g;
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);
        this.model.setupAnim(entity, 0.0F, 0.0f, ticks, entity.getYRot(), entity.getXRot());
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        super.render(entity, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(CrystalPillar entity) {
        return TEXTURE;
    }
}
