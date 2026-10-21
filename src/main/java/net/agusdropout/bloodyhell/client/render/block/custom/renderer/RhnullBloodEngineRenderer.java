package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import net.agusdropout.bloodyhell.client.render.block.FloatingItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.render.block.generic.BaseGeckoBlockModel;
import net.agusdropout.bloodyhell.block.entity.custom.engine.RhnullBloodEngineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RhnullBloodEngineRenderer extends GeoBlockRenderer<RhnullBloodEngineBlockEntity> {

    private static final FloatingItemRenderer.Style ITEM_STYLE = new FloatingItemRenderer.Style(
            0.0, 2.5, 0.0, 0.8f, 3.0f, 0.1f, 1.0f / 15.0f, ItemDisplayContext.GROUND);

    public RhnullBloodEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, RhnullBloodEngineBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        ItemStack renderStack = animatable.getRenderStack();
        if (!renderStack.isEmpty()) {
            float time = animatable.getLevel().getGameTime() + partialTick;
            FloatingItemRenderer.render(ITEM_STYLE, renderStack, animatable.getLevel(), time, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}