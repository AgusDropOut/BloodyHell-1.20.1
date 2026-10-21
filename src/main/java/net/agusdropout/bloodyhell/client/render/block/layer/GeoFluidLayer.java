package net.agusdropout.bloodyhell.client.render.block.layer;

import net.agusdropout.bloodyhell.client.render.block.FluidBoxRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.block.entity.base.IGeoFluidBlock;
import net.agusdropout.bloodyhell.block.entity.base.IFluidBlockHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class GeoFluidLayer <T extends BaseGeckoBlockEntity & IFluidBlockHolder & IGeoFluidBlock> extends GeoRenderLayer<T> {

    public GeoFluidLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        GeoBone bloodBone = bakedModel.getBone(animatable.getFluidBoneName()).orElse(null);
        if (bloodBone == null) return;

        FluidStack fluidStack = animatable.getInputTank().getFluid();
        if (fluidStack.isEmpty()) return;

        float fillPercentage = (float) animatable.getInputTank().getFluidAmount() / animatable.getInputTank().getCapacity();

        IClientFluidTypeExtensions fluidProps = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = FluidBoxRenderer.blockSprite(fluidProps.getStillTexture(fluidStack));
        int color = fluidProps.getTintColor(fluidStack);

        poseStack.pushPose();


        RenderUtils.prepMatrixForBone(poseStack, bloodBone);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());

        poseStack.translate(0,0.9,0);


        float radius = animatable.getFluidRadius();
        float maxHeight = animatable.getFluidHeight() / 16.0f;
        float yOffset = animatable.getFluidHeightOffset();

        float xMin = -radius;
        float xMax = radius;
        float zMin = -radius;
        float zMax = radius;


        float yMin = yOffset;
        float yMax = yOffset + (maxHeight * fillPercentage);

        FluidBoxRenderer.drawBox(builder, poseStack.last(), sprite, color,
                xMin, yMin, zMin, xMax, yMax, zMax, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
