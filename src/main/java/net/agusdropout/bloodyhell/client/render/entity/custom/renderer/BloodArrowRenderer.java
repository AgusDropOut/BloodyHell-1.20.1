package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.BloodArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BloodArrowRenderer extends ArrowRenderer<BloodArrowEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_arrow_entity.png");
    public BloodArrowRenderer(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }


    public ResourceLocation getTextureLocation(BloodArrowEntity arrow) {
        return TEXTURE;
    }
}
