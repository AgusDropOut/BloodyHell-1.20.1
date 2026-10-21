package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.SanguineSacrificeEntity;
import net.agusdropout.bloodyhell.entity.custom.UnknownEntityArms;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

@OnlyIn(Dist.CLIENT)
public class SanguineSacrificeEntityRenderer extends GeoEntityRenderer<SanguineSacrificeEntity> {
    private final float randomScale;
    public SanguineSacrificeEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SanguineSacrificeEntityModel());
        this.shadowRadius = 0.8f;
        this.randomScale = (float) (0.8 + (1.4 - 0.8) * Math.random());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    public float getRandomScale() {
        return randomScale;
    }

    @Override
    public ResourceLocation getTextureLocation(SanguineSacrificeEntity instance) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/sanguine_sacrifice_entity.png");
    }

    @Override
    public RenderType getRenderType(SanguineSacrificeEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(SanguineSacrificeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float scale = this.getRandomScale();


        poseStack.scale(1.0f, scale, 1.0f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
