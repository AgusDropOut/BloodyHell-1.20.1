package net.agusdropout.bloodyhell.entity.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.GraveWalkerEntity;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GraveWalkerEntityRenderer extends GeoEntityRenderer<GraveWalkerEntity> {
    public GraveWalkerEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GraveWalkerEntityModel());
        this.shadowRadius = 0.8f;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));


    }

    @Override
    public ResourceLocation getTextureLocation(GraveWalkerEntity instance) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/grave_walker.png");
    }

    @Override
    public RenderType getRenderType(GraveWalkerEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(GraveWalkerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(0.6f, 0.6f, 0.6f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    }
}
