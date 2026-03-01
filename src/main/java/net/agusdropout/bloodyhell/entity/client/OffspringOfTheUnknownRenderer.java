package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.client.base.InsightCreatureRenderer;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class OffspringOfTheUnknownRenderer extends InsightCreatureRenderer<OffspringOfTheUnknownEntity> {

    public OffspringOfTheUnknownRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new OffspringOfTheUnknownModel());
    }

    @Override
    public ResourceLocation getTextureLocation(OffspringOfTheUnknownEntity instance) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/offspring_of_the_unknown.png");
    }
}