package net.agusdropout.bloodyhell.entity.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.VisceralProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
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
    public RenderType getRenderType(VisceralProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(VisceralProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);


    }


}

