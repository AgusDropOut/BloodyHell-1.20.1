package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import net.agusdropout.bloodyhell.client.render.block.generic.BaseGeckoBlockModel;
import net.agusdropout.bloodyhell.client.render.block.layer.GeoFluidLayer;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteBloodHarvesterBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.UnknownPortalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SanguiniteHarvesterRenderer extends GeoBlockRenderer<SanguiniteBloodHarvesterBlockEntity> {
    public SanguiniteHarvesterRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
        this.addRenderLayer(new GeoFluidLayer<>(this));
    }

    @Override
    public RenderType getRenderType(SanguiniteBloodHarvesterBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}