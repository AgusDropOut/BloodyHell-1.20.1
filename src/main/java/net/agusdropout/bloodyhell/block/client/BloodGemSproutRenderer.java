package net.agusdropout.bloodyhell.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.agusdropout.bloodyhell.block.entity.base.BaseGemSproutBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.plant.BloodGemSproutBlockEntity;
import net.agusdropout.bloodyhell.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BloodGemSproutRenderer implements BlockEntityRenderer<BaseGemSproutBlockEntity> {

    // --- MANUAL OFFSETS ---
    private static final float OFF_X = 0.0f;
    private static final float OFF_Z = 0.5f;
    private static final float OFF_Y = 0.9f;


    private static final ResourceLocation GEM_TEXTURE = new ResourceLocation("textures/block/white_concrete.png");


    public BloodGemSproutRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BaseGemSproutBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = be.getBlockState();


        if (state.getValue(BloodGemSproutBlock.AGE) < 4) {
            return;
        }


        Direction facing = state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                : Direction.NORTH;

        poseStack.pushPose();


        poseStack.translate(0.5, 0.5, 0.5);


        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        poseStack.translate(OFF_X,0,OFF_Z);

        float time = be.getLevel().getGameTime() + partialTick;
        float bobOffset = Mth.sin(time * 0.1f) * 0.01f;
        float gemSpin = time * 1.2f;

        float localY = OFF_Y - 0.5f;


        float localZ = 0.0f;


        poseStack.translate(0.0, localY + bobOffset, localZ);


        poseStack.mulPose(Axis.YP.rotationDegrees(gemSpin));


        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(GEM_TEXTURE));


        be.getRenderingGemShape(consumer, poseStack);

        poseStack.popPose();
    }
}