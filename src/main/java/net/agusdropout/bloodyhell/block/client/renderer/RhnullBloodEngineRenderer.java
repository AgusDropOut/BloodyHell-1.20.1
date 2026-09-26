package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.client.generic.BaseGeckoBlockModel;
import net.agusdropout.bloodyhell.block.entity.custom.engine.RhnullBloodEngineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RhnullBloodEngineRenderer extends GeoBlockRenderer<RhnullBloodEngineBlockEntity> {

    private final ItemRenderer itemRenderer;

    public RhnullBloodEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, RhnullBloodEngineBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        ItemStack renderStack = animatable.getRenderStack();
        if (!renderStack.isEmpty()) {
            poseStack.pushPose();

            poseStack.translate(0.0f, 2.5f, 0.0f);

            float time = animatable.getLevel().getGameTime() + partialTick;
            poseStack.translate(0.0f, (float) Math.sin(time / 15.0f) * 0.1f, 0.0f);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 3.0f));
            poseStack.scale(0.8f, 0.8f, 0.8f);

            itemRenderer.renderStatic(renderStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
            poseStack.popPose();
        }
    }
}