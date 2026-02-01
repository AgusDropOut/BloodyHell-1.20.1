package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.custom.SelioraRestingBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StarLampBlockModel extends GeoModel<SelioraRestingBlockEntity> {
    @Override
    public ResourceLocation getModelResource(SelioraRestingBlockEntity selioraRestingBlockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "geo/seliora_resting_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SelioraRestingBlockEntity selioraRestingBlockEntity) {

            return new ResourceLocation(BloodyHell.MODID, "textures/block/seliora_resting_block_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SelioraRestingBlockEntity selioraRestingBlockEntity) {
        return new ResourceLocation(BloodyHell.MODID, "animations/seliora_resting_block.animation.json");
    }
}
