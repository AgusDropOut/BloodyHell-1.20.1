package net.agusdropout.bloodyhell.block.client.renderer;

import net.agusdropout.bloodyhell.block.client.generic.BaseGeckoBlockModel;
import net.agusdropout.bloodyhell.block.client.generic.BaseGeckoBlockRenderer;
import net.agusdropout.bloodyhell.block.client.layer.GenericEmissiveLayer;
import net.agusdropout.bloodyhell.block.client.model.MainBlasphemousBloodAltarModel;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBlasphemousBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.RhnullBloodEngineBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RhnullBloodEngineRenderer extends GeoBlockRenderer<RhnullBloodEngineBlockEntity> {
    public RhnullBloodEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
    }
}