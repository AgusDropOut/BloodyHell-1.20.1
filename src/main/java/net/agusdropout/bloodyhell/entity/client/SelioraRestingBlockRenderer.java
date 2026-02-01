package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.block.entity.custom.SelioraRestingBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SelioraRestingBlockRenderer extends GeoBlockRenderer<SelioraRestingBlockEntity> {
    public SelioraRestingBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new SelioraRestingBlockModel());
    }


}

