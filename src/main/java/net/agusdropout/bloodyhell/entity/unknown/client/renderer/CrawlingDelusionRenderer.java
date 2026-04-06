package net.agusdropout.bloodyhell.entity.unknown.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.entity.client.base.InsightCreatureRenderer;
import net.agusdropout.bloodyhell.entity.unknown.client.model.CrawlingDelusionModel;
import net.agusdropout.bloodyhell.entity.unknown.custom.CrawlingDelusionEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.LinearFrenziedFlameParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class CrawlingDelusionRenderer extends InsightCreatureRenderer<CrawlingDelusionEntity> {

    private int lastParticleTick = -1;

    public CrawlingDelusionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CrawlingDelusionModel());
    }

    @Override
    public RenderType getRenderType(CrawlingDelusionEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        /* Bypasses the default cutout render type to allow partial transparency */
        if (ClientInsightData.getPlayerInsight() >= animatable.getMinimumInsight()) {
            return RenderType.entityTranslucent(texture);
        }
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(CrawlingDelusionEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (entity.level().isClientSide() && entity.tickCount != this.lastParticleTick) {
            this.lastParticleTick = entity.tickCount;
            this.spawnLocatorParticles(entity);
        }
    }

    private void spawnLocatorParticles(CrawlingDelusionEntity entity) {
        if (ClientInsightData.getPlayerInsight() < entity.getMinimumInsight()) {
            return;
        }

        BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity));
        if (model == null) return;

        Vector3f fixedYellow = new Vector3f(1.0F, 0.9F, 0.1F);

        ChillFallingParticleOptions jawParticle = new ChillFallingParticleOptions(
                fixedYellow,
                0.03F,
                40,
                20
        );

        String[] jawLocators = {"jawParticle1", "jawParticle2"};
        for (String locatorName : jawLocators) {
            if (entity.getRandom().nextFloat() > 0.05F) continue;

            model.getBone(locatorName).ifPresent(bone -> {
                Vector3d bonePos = bone.getWorldPosition();
                entity.level().addParticle(jawParticle, bonePos.x(), bonePos.y(), bonePos.z(), 0.0D, 0.0D, 0.0D);
            });
        }

        if (entity.getRandom().nextFloat() <= 0.9F) {
            model.getBone("headParticle").ifPresent(bone -> {


                Vector3d bonePos = bone.getWorldPosition();

                for( int i = 0; i < 10; i++) {


                    double offsetX = (entity.getRandom().nextDouble() - 0.5D) * 0.15D;
                    double offsetY = (entity.getRandom().nextDouble() - 0.5D) * 0.15D;
                    double offsetZ = (entity.getRandom().nextDouble() - 0.5D) * 0.15D;

                    float size = 0.01F + entity.getRandom().nextFloat() * 0.05F;

                    /* Calculates the gradient color based on a randomized float */
                    Vector3f colorStart = new Vector3f(1.0F, 0.9F, 0.1F);
                    Vector3f colorMid = new Vector3f(1.0F, 0.5F, 0.0F);
                    Vector3f colorEnd = new Vector3f(0.8F, 0.1F, 0.0F);

                    Vector3f gradientColor = ParticleHelper.gradient3(entity.getRandom().nextFloat(), colorStart, colorMid, colorEnd);
                    int lifetime = 15 + entity.getRandom().nextInt(20);
                    TinyBloomParticleOptions headParticle = new TinyBloomParticleOptions(gradientColor, size);

                    entity.level().addParticle(headParticle,
                            bonePos.x() + offsetX,
                            bonePos.y() + offsetY,
                            bonePos.z() + offsetZ,
                            0.0D, 0.025D, 0.0D);

                    if (entity.getRandom().nextFloat() <= 0.1F) {

                        LinearFrenziedFlameParticleOptions headParticleOptional = new LinearFrenziedFlameParticleOptions(
                                1.0F, 1.0F, 1.0F,
                                size*5,
                                lifetime
                        );

                        entity.level().addParticle(headParticleOptional,
                                bonePos.x() + offsetX,
                                bonePos.y() + offsetY,
                                bonePos.z() + offsetZ,
                                0.0D, 0.025D, 0.0D);

                    }

                }
            });
        }
    }
}