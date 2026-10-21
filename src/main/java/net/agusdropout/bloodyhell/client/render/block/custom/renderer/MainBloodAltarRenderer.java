package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import net.agusdropout.bloodyhell.client.render.block.FloatingItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.custom.altar.MainBloodAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.NoiseSphereParticleOptions;
import net.agusdropout.bloodyhell.client.render.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class MainBloodAltarRenderer implements BlockEntityRenderer<MainBloodAltarBlockEntity> {

    private static final FloatingItemRenderer.Style RESULT_ITEM_STYLE = new FloatingItemRenderer.Style(
            0.5, 1.8, 0.5, -0.60f, 2.0f, 0.05f, 0.1f, ItemDisplayContext.FIXED);

    private static final Vector3f HEART_COLOR = new Vector3f(0.8f, 0.05f, 0.05f);

    public MainBloodAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MainBloodAltarBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        long gameTime = be.getLevel().getGameTime();
        renderItem( be, be.getLevel().getGameTime() + partialTick, poseStack, bufferSource, packedLight, packedOverlay);


        if (!be.getBlockState().getValue(MainBloodAltarBlock.ACTIVE)) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 1.8, 0.5);


        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lightning());
        RenderHelper.renderAtlasHeart(consumer, poseStack.last().pose(), null,
                gameTime, partialTick,
                HEART_COLOR.x(), HEART_COLOR.y(), HEART_COLOR.z(),
                0.9f, 15728880);

        poseStack.popPose();


        if (gameTime % 40 == 0 && partialTick <= 1.0f && be.getLevel().isClientSide) {
            spawnHeartbeatParticle(be);
        }
    }

    private void spawnHeartbeatParticle(MainBloodAltarBlockEntity be) {
        NoiseSphereParticleOptions options = new NoiseSphereParticleOptions(HEART_COLOR, 1.2f, 30);


        double x = be.getBlockPos().getX() + 0.5;
        double y = be.getBlockPos().getY() + 1.8;
        double z = be.getBlockPos().getZ() + 0.5;

        be.getLevel().addParticle(options, x, y, z, 0, 0, 0);
    }

    private void renderItem(MainBloodAltarBlockEntity be, float time, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (be.hasResultItem()) {
            FloatingItemRenderer.render(RESULT_ITEM_STYLE, be.getResultItem(), be.getLevel(), time, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}