package net.agusdropout.bloodyhell.client.render.entity.layer;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.CrystalPillarModel;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.VesperModel;
import net.agusdropout.bloodyhell.entity.custom.CrystalPillar;
import net.agusdropout.bloodyhell.entity.custom.VesperEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class VesperEyesLayer<T extends VesperEntity, M extends VesperModel<T>> extends EyesLayer<T, M> {

    public VesperEyesLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return RenderType.eyes(new ResourceLocation(BloodyHell.MODID, "textures/entity/vesper_eyes.png"));
    }

}