package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import net.agusdropout.bloodyhell.client.render.block.generic.BaseGeckoBlockModel;
import net.agusdropout.bloodyhell.client.render.block.layer.GenericEmissiveLayer;
import net.agusdropout.bloodyhell.client.render.block.layer.GeoFluidLayer;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.UnknownPortalBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class UnknownPortalRenderer extends GeoBlockRenderer<UnknownPortalBlockEntity> {
    public UnknownPortalRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
        this.addRenderLayer(new GeoFluidLayer<>(this));
        this.addRenderLayer(new GenericEmissiveLayer<>(this));
    }
}