package net.agusdropout.bloodyhell.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.entity.base.BaseSanguineLapidaryBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SanguineLapidaryRenderer implements BlockEntityRenderer<BaseSanguineLapidaryBlockEntity> {

    // --- CONFIGURATION ---
    private static final float BASE_HEIGHT = 2.0f;

    public SanguineLapidaryRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BaseSanguineLapidaryBlockEntity tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        long time = tile.getLevel().getGameTime();

        ItemStack weapon = tile.getRenderWeapon();
        boolean isAnimating = tile.animationTick > 0;


        float yHover = Mth.sin((time + partialTick) / 10.0f) * 0.05f;
        float currentY = BASE_HEIGHT + yHover;


        if (!weapon.isEmpty()) {
            poseStack.pushPose();


            poseStack.translate(0.5, currentY, 0.5);


            float spin = (time + partialTick) * 2.0f;
            if (isAnimating) {
                spin += (spin * 0.5f);
            }
            poseStack.mulPose(Axis.YP.rotationDegrees(spin));


            poseStack.scale(0.75f, 0.75f, 0.75f);

            itemRenderer.renderStatic(weapon, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, tile.getLevel(), 0);
            poseStack.popPose();
        }


        for (int i = 0; i < 3; i++) {
            ItemStack gem = tile.getRenderGem(i);
            if (gem.isEmpty()) continue;

            poseStack.pushPose();


            float angleOffset = i * 120.0f;
            float orbitSpeed = (time + partialTick) * 1.5f;
            float currentAngle = angleOffset + orbitSpeed;

            float radius = 0.6f;
            float scale = 0.4f;

            if (isAnimating) {
                float progress = (tile.animationTick - partialTick) / 40.0f;
                float t = 1.0f - progress;


                radius = Mth.lerp(t, 0.6f, 0.1f);


                currentAngle += (t * 360f);


                scale = Mth.lerp(t, 0.4f, 0.1f);
            }

            double xOffset = Math.cos(Math.toRadians(currentAngle)) * radius;
            double zOffset = Math.sin(Math.toRadians(currentAngle)) * radius;


            poseStack.translate(0.5 + xOffset, currentY, 0.5 + zOffset);



            poseStack.mulPose(Axis.YP.rotationDegrees(currentAngle));


            poseStack.scale(scale, scale, scale);

            itemRenderer.renderStatic(gem, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, tile.getLevel(), 0);
            poseStack.popPose();
        }
    }
}