package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import net.agusdropout.bloodyhell.block.entity.custom.SelioraRestingBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SelioraRestingBlockRenderer extends GeoBlockRenderer<SelioraRestingBlockEntity> {
    public SelioraRestingBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new SelioraRestingBlockModel());
    }


}
