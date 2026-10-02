package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class BloodFireLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public BloodFireLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {


        MobEffectInstance instance = entity.getEffect(ModEffects.BLOOD_FIRE_EFFECT.get());

        if (instance == null || instance.getDuration() <= 1 || entity.isInWater()) {
            return;
        }

        poseStack.pushPose();


        poseStack.translate(0.0D, entity.getBbHeight() * 0.5F, 0.0D);


        poseStack.scale(-1.0F, -1.0F, 1.0F);

        float bodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(bodyRot));


        Quaternionf cameraRot = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        poseStack.mulPose(cameraRot);


        poseStack.translate(0.0F, 0.0F, 0.1F);



        float xScale = entity.getBbWidth() * 2.0F;
        float yScale = Math.max(xScale, entity.getBbHeight() * 1.2F);


        poseStack.scale(xScale, yScale, xScale);

        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(ModBlocks.BLOOD_FIRE.get().defaultBlockState())
                .getParticleIcon();

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
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