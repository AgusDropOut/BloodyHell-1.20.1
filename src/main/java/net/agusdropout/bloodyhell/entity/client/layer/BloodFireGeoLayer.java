package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BloodFireGeoLayer<T extends LivingEntity & software.bernie.geckolib.animatable.GeoEntity> extends GeoRenderLayer<T> {

    // --- CONFIGURATION ---
    private static final boolean FLIP_X = true;
    private static final boolean FLIP_Y = false;

    // ADJUST THIS IF IT'S STILL ROTATED WRONG!
    // Common values to try: 90.0F, -90.0F, 180.0F
    private static final float ROTATION_OFFSET = 180.0F;

    public BloodFireGeoLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        MobEffectInstance instance = animatable.getEffect(ModEffects.BLOOD_FIRE_EFFECT.get());
        if (instance == null || instance.getDuration() <= 1 || animatable.isInWater()) {
            return;
        }

        poseStack.pushPose();

        // 1. Center
        poseStack.translate(0.0D, animatable.getBbHeight() * 0.5F, 0.0D);

        // 2. Undo Body Rotation
        float bodyRot = Mth.rotLerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(-bodyRot));

        // 3. Billboard (Face Camera)
        Quaternionf cameraRot = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        poseStack.mulPose(cameraRot);

        // 4. --- ROTATION OFFSET FIX ---
        // We apply a manual correction here to fix the "Rotated to the right" issue.
        poseStack.mulPose(Axis.YP.rotationDegrees(ROTATION_OFFSET));

        // Move slightly towards camera
        poseStack.translate(0.0F, 0.0F, 0.1F);

        // 5. Dynamic Scaling
        float xScale = animatable.getBbWidth() * 2.0F;
        float yScale = Math.max(xScale, animatable.getBbHeight() * 1.2F);

        float xFinal = FLIP_X ? -xScale : xScale;
        float yFinal = FLIP_Y ? -yScale : yScale;

        poseStack.scale(xFinal, yFinal, xScale);

        // 6. Render
        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(ModBlocks.BLOOD_FIRE.get().defaultBlockState())
                .getParticleIcon();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        float size = 0.5f;

        float u0 = sprite.getU0(); float u1 = sprite.getU1();
        float v0 = sprite.getV0(); float v1 = sprite.getV1();

        consumer.vertex(pose, -size, size, 0.0f).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, -size, -size, 0.0f).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, size, -size, 0.0f).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, size, size, 0.0f).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();

        poseStack.popPose();
    }
}