package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.BlasphemousArmEntity;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousWhirlwindEntity;
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
public class BlasphemousWhirlwindEntityRenderer extends GeoEntityRenderer<BlasphemousWhirlwindEntity> {
    public BlasphemousWhirlwindEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlasphemousWhirlwindEntityModel());
        this.shadowRadius = 0.8f;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BlasphemousWhirlwindEntity instance) {
        if (!instance.isMinorRingActive()){
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_whirlwind_entity.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_whirlwind_entity_no_small_ring.png");
        }
    }

    @Override
    public RenderType getRenderType(BlasphemousWhirlwindEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void render(BlasphemousWhirlwindEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
