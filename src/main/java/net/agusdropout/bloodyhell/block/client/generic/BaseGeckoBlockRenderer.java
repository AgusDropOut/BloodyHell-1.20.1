package net.agusdropout.bloodyhell.block.client.generic;

import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BaseGeckoBlockRenderer<T extends BaseGeckoBlockEntity> extends GeoBlockRenderer<T> {

    public BaseGeckoBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
    }
}