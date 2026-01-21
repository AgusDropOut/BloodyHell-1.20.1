package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class RitekeeperHeartLayer extends GeoRenderLayer<RitekeeperEntity> {

    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation("textures/particle/flash.png");

    public RitekeeperHeartLayer(GeoRenderer<RitekeeperEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, RitekeeperEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        bakedModel.getBone("heart").ifPresent(heartBone -> {
            poseStack.pushPose();

            // ... Position mapping code (kept same) ...
            Vector3d worldPos = heartBone.getWorldPosition();
            double lerpX = Mth.lerp(partialTick, animatable.xo, animatable.getX());
            double lerpY = Mth.lerp(partialTick, animatable.yo, animatable.getY());
            double lerpZ = Mth.lerp(partialTick, animatable.zo, animatable.getZ());
            poseStack.translate(worldPos.x() - lerpX, worldPos.y() - lerpY, worldPos.z() - lerpZ);

            float age = animatable.tickCount + partialTick;
            float beat = (Mth.sin(age * 0.2f) + 1.0f) * 0.5f;
            float pulseScale = 0.8f + (beat * 0.1f);

            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(age * 1.5f));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(age * 0.5f));
            poseStack.scale(pulseScale, pulseScale, pulseScale);

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(BLANK_TEXTURE));

            // Core
            RenderHelper.renderIcosahedron(consumer, poseStack.last().pose(), poseStack.last().normal(),
                    0.08f, 0.8f, 0.04f, 0.04f, 1.0f, 15728880);

            // Shell
            RenderHelper.renderIcosahedron(consumer, poseStack.last().pose(), poseStack.last().normal(),
                    0.15f, 1.0f, 0.2f, 0.2f, 0.3f, 15728880);

            poseStack.popPose();
        });
    }
}