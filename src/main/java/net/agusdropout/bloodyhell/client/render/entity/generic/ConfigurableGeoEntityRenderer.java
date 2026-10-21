package net.agusdropout.bloodyhell.client.render.entity.generic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Consumer;

public class ConfigurableGeoEntityRenderer<T extends Entity & GeoAnimatable>
        extends GenericGeckoEntityRenderer<T> {
    private final Function<T, Float> scale;

    public ConfigurableGeoEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model,
                                          float shadowRadius, boolean glowing,
                                          Function<T, Float> scale,
                                          List<Consumer<ConfigurableGeoEntityRenderer<T>>> customLayers) {
        super(context, model);
        this.shadowRadius = shadowRadius;
        this.scale = scale;

        if (glowing) {
            addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
        customLayers.forEach(layer -> layer.accept(this));
    }

    public static <T extends Entity & GeoAnimatable> Builder<T> builder(
            EntityRendererProvider.Context context, GeoModel<T> model) {
        return new Builder<>(context, model);
    }

    public static final class Builder<T extends Entity & GeoAnimatable> {
        private final EntityRendererProvider.Context context;
        private final GeoModel<T> model;
        private float shadowRadius;
        private boolean glowing;
        private Function<T, Float> scale = entity -> 1.0f;
        private final List<Consumer<ConfigurableGeoEntityRenderer<T>>> customLayers = new ArrayList<>();

        private Builder(EntityRendererProvider.Context context, GeoModel<T> model) {
            this.context = context;
            this.model = model;
        }

        public Builder<T> shadow(float value) {
            this.shadowRadius = value;
            return this;
        }

        public Builder<T> glowing() {
            this.glowing = true;
            return this;
        }

        public Builder<T> scale(float value) {
            this.scale = entity -> value;
            return this;
        }

        public Builder<T> scaleBy(Function<T, Float> value) {
            this.scale = value;
            return this;
        }

        public Builder<T> layer(Consumer<ConfigurableGeoEntityRenderer<T>> layer) {
            customLayers.add(layer);
            return this;
        }

        public ConfigurableGeoEntityRenderer<T> build() {
            return new ConfigurableGeoEntityRenderer<>(context, model, shadowRadius, glowing, scale, customLayers);
        }
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        float entityScale = scale.apply(entity);
        poseStack.scale(entityScale, entityScale, entityScale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
