package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import net.agusdropout.bloodyhell.client.render.block.layer.GenericEmissiveLayer;
import net.agusdropout.bloodyhell.client.render.block.custom.model.MainBlasphemousBloodAltarModel;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBlasphemousBloodAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MainBlasphemousBloodAltarRenderer extends GeoBlockRenderer<MainBlasphemousBloodAltarBlockEntity> {
    public MainBlasphemousBloodAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new MainBlasphemousBloodAltarModel());
        addRenderLayer(new GenericEmissiveLayer<>(this));
    }
}