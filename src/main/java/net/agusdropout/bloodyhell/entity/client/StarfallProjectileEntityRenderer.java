package net.agusdropout.bloodyhell.entity.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.StarfallProjectile;
import net.agusdropout.bloodyhell.entity.projectile.VirulentAnchorProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class StarfallProjectileEntityRenderer extends GeoEntityRenderer<StarfallProjectile> {
    public StarfallProjectileEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StarfallProjectileEntityModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.01f;

    }

    @Override
    public ResourceLocation getTextureLocation(StarfallProjectile instance) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/starfall_projectile_texture.png");
    }

    @Override
    public RenderType getRenderType(StarfallProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(StarfallProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();


        float maxLife = 200f;
        float scale = 1.0f + (1.0f - ((float) entity.getLifeTicks() / maxLife)) * 1.5f;


        poseStack.scale(scale, scale, scale);


        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
