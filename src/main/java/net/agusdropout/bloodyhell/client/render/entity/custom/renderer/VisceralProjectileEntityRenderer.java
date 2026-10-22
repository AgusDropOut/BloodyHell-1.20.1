package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.VisceralProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VisceralProjectileEntityRenderer extends GeoEntityRenderer<VisceralProjectile> {
    public VisceralProjectileEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VisceralProjectileEntityModel());
        this.shadowRadius = 0.001f;

    }

    @Override
    public ResourceLocation getTextureLocation(VisceralProjectile instance) {
        if(!instance.isAlternative()){
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/visceral_projectile_alternative.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/visceral_projectile.png");
        }
    }

    @Override
    public void render(VisceralProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);


    }


}
