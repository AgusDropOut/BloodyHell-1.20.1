package net.agusdropout.bloodyhell.block.client.renderer;

import net.agusdropout.bloodyhell.block.client.layer.GenericEmissiveLayer;
import net.agusdropout.bloodyhell.block.client.model.MainBlasphemousBloodAltarModel;
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