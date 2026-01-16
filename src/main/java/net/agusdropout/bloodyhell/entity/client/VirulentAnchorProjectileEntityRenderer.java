package net.agusdropout.bloodyhell.entity.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
 
import net.agusdropout.bloodyhell.entity.projectile.VirulentAnchorProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class VirulentAnchorProjectileEntityRenderer extends GeoEntityRenderer<VirulentAnchorProjectileEntity> {
    public VirulentAnchorProjectileEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VirulentAnchorProjectileEntityModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.01f;

    }

    @Override
    public ResourceLocation getTextureLocation(VirulentAnchorProjectileEntity instance) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/virulent_anchor_projectile.png");
    }

    @Override
    public RenderType getRenderType(VirulentAnchorProjectileEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(VirulentAnchorProjectileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
