package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.entity.custom.HostileUnknownEntityArms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HostileUnknownEntityArmsRenderer extends GeoEntityRenderer<HostileUnknownEntityArms> {

    public HostileUnknownEntityArmsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HostileUnknownEntityArmsModel());
    }
}
