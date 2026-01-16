package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
 
import net.agusdropout.bloodyhell.entity.projectile.VirulentAnchorProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Optional;

public class VirulentAnchorProjectileEntityModel extends GeoModel<VirulentAnchorProjectileEntity> {
    @Override
    public ResourceLocation getModelResource(VirulentAnchorProjectileEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/virulent_anchor_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VirulentAnchorProjectileEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/virulent_anchor_projectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VirulentAnchorProjectileEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/virulent_anchor_projectile.animation.json") ;
    }

    @Override
    public void setCustomAnimations(VirulentAnchorProjectileEntity animatable, long instanceId, AnimationState<VirulentAnchorProjectileEntity> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("virulent_anchor_projectile");

        if (bone != null) {
                float yRot = animatable.getYRot();
                float xRot = animatable.getXRot();
                bone.setRotY(yRot * Mth.DEG_TO_RAD);
                bone.setRotX(-xRot * Mth.DEG_TO_RAD);
                bone.setRotZ(-xRot * Mth.DEG_TO_RAD);
        }
    }
}
