package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.client.generic.BaseGeckoBlockModel;
import net.agusdropout.bloodyhell.block.client.layer.GenericEmissiveLayer;
import net.agusdropout.bloodyhell.block.custom.mechanism.RhnullBloodEnginePillarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.RhnullBloodEnginePillarBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RhnullBloodEnginePillarRenderer extends GeoBlockRenderer<RhnullBloodEnginePillarBlockEntity> {

    private final ItemRenderer itemRenderer;

    public RhnullBloodEnginePillarRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
        this.itemRenderer = context.getItemRenderer();



    }

    @Override
    public void actuallyRender(PoseStack poseStack, RhnullBloodEnginePillarBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        BlockState state = animatable.getBlockState();
        RhnullBloodEnginePillarBlock.Corner corner = state.getValue(RhnullBloodEnginePillarBlock.CORNER);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);

        float rotation = 0f;
        switch (corner) {
            case NORTH_EAST -> rotation = 180f;
            case SOUTH_EAST -> rotation = 90f;
            case SOUTH_WEST -> rotation = 0f;
            case NORTH_WEST -> rotation = -90f;
            default -> rotation = 0f;
        }

        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
        poseStack.translate(-0.5f, 0.0f, -0.5f);

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();

        ItemStack renderStack = animatable.getRenderStack();
        if (!renderStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 2.3f, 0.5f);

            float time = animatable.getLevel().getGameTime() + partialTick;
            poseStack.translate(0.0f, (float) Math.sin(time / 15.0f) * 0.15f, 0.0f);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 3.0f));
            poseStack.scale(0.6f, 0.6f, 0.6f);

            itemRenderer.renderStatic(renderStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
            poseStack.popPose();
        }
    }
}