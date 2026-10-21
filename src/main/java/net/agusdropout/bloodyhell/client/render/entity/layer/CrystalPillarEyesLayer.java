package net.agusdropout.bloodyhell.client.render.entity.layer;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.CrystalPillarModel;
import net.agusdropout.bloodyhell.entity.custom.CrystalPillar;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CrystalPillarEyesLayer<T extends CrystalPillar, M extends CrystalPillarModel<T>> extends EyesLayer<T, M> {

    public CrystalPillarEyesLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return RenderType.eyes(new ResourceLocation(BloodyHell.MODID, "textures/entity/crystal_pillar.png"));
    }

}