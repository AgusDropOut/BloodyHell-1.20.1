package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.custom.HostileUnknownEntityArms;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HostileUnknownEntityArmsRenderer extends GeoEntityRenderer<HostileUnknownEntityArms> {

    public HostileUnknownEntityArmsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HostileUnknownEntityArmsModel());
        addRenderLayer( new AutoGlowingGeoLayer<>(this));
    }

}