package net.agusdropout.bloodyhell.entity.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;

import net.agusdropout.bloodyhell.entity.projectile.SmallCrimsonDagger;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

public class SmallCrimsonDaggerEntityRenderer extends GeoEntityRenderer<SmallCrimsonDagger> {
    public SmallCrimsonDaggerEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SmallCrimsonDaggerEntityModel());
        this.shadowRadius = 0.01f;

    }

    @Override
    public ResourceLocation getTextureLocation(SmallCrimsonDagger instance) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/small_crimson_dagger.png");
    }

    @Override
    public RenderType getRenderType(SmallCrimsonDagger animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(SmallCrimsonDagger entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);


    }


}

