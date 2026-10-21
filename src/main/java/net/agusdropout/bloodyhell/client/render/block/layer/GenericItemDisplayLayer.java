package net.agusdropout.bloodyhell.client.render.block.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.base.ISingleItemRenderBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class GenericItemDisplayLayer<T extends GeoAnimatable & ISingleItemRenderBlock> extends GeoRenderLayer<T> {

    public GenericItemDisplayLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ItemStack stack = animatable.getRenderItemStack();
        if (stack.isEmpty()) return;

        String boneName = animatable.getItemBoneName();
        GeoBone locatorBone = bakedModel.getBone(boneName).orElse(null);

        if (locatorBone != null) {
            poseStack.pushPose();

            RenderUtils.prepMatrixForBone(poseStack, locatorBone);

            poseStack.scale(0.45f, 0.45f, 0.45f);
            poseStack.translate(0,2.5,0);



            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    bufferSource,
                    animatable instanceof net.minecraft.world.level.block.entity.BlockEntity be ? be.getLevel() : null,
                    0
            );

            poseStack.popPose();
        }
    }
}