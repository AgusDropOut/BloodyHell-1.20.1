package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.entity.custom.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.agusdropout.bloodyhell.util.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MainBloodAltarRenderer extends GeoBlockRenderer<MainBloodAltarBlockEntity> {
    public MainBloodAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new MainBloodAltarModel());
    }


    @Override
    public void actuallyRender(PoseStack pPoseStack, MainBloodAltarBlockEntity pBlockEntity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(pPoseStack, pBlockEntity, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        pPoseStack.pushPose();

        int items = 3; // Number of items to render
        float[] angles = new float[items];
        float anglePer = 360F / items;
        float totalAngle = 0F;
        for (int i = 0; i < angles.length; i++) {
            angles[i] = totalAngle;
            totalAngle += anglePer;
        }

        double time = ClientTickHandler.ticksInGame + partialTick;
        for (int i = 0; i < items; i++) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5F , 1.25F, 0.5F);
            pPoseStack.mulPose(VecHelper.rotateY(angles[i] + (float) (time % 360)));
            pPoseStack.translate(1.125F, 0F, 0.25F);
            pPoseStack.mulPose(VecHelper.rotateY((float) (45F+(float) (time % 360))));

            // Adjust pivot point
            pPoseStack.translate(0F, 0.25F, 0F); // Move up
            pPoseStack.mulPose(VecHelper.rotateX((float) (1/(Math.cos((time%360)/50F)*0.8F))));
            pPoseStack.mulPose(VecHelper.rotateZ((float) (1/Math.sin((time%360)/50F)*0.8F)));
            pPoseStack.translate(0F, -0.25F, 0F); // Move back down

            pPoseStack.translate(0D, 0.075, 0F);
            ItemStack stack = pBlockEntity.getItemHandler().getStackInSlot(i);

            Minecraft mc = Minecraft.getInstance();
            if (!stack.isEmpty()) {
                mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, pPoseStack, bufferSource, pBlockEntity.getLevel(), 0);
            }
            pPoseStack.popPose();
        }

        pPoseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
