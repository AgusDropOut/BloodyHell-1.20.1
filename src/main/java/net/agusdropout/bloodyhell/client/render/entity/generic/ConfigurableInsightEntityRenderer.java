package net.agusdropout.bloodyhell.client.render.entity.generic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

import net.agusdropout.bloodyhell.entity.base.InsightEntity;

public class ConfigurableInsightEntityRenderer<T extends LivingEntity & GeoAnimatable & InsightEntity>
        extends InsightCreatureRenderer<T> {
    private final Function<T, Float> scale;

    public ConfigurableInsightEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model,
                                             float shadowRadius, Function<T, Float> scale) {
        super(context, model);
        this.shadowRadius = shadowRadius;
        this.scale = scale;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        float entityScale = scale.apply(entity);
        poseStack.scale(entityScale, entityScale, entityScale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
