package net.agusdropout.bloodyhell.block.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.client.layer.SanguinitePipeFluidLayer;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguinitePipeBlock;
import net.agusdropout.bloodyhell.block.entity.custom.SanguinitePipeBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SanguinitePipeRenderer extends GeoBlockRenderer<SanguinitePipeBlockEntity> {

    public SanguinitePipeRenderer(BlockEntityRendererProvider.Context context) {
        super(new SanguinitePipeModel());
        addRenderLayer(new SanguinitePipeFluidLayer(this));
    }

    @Override
    public void preRender(PoseStack poseStack, SanguinitePipeBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        BlockState state = animatable.getBlockState();
        boolean n = state.getValue(SanguinitePipeBlock.NORTH);
        boolean s = state.getValue(SanguinitePipeBlock.SOUTH);
        boolean e = state.getValue(SanguinitePipeBlock.EAST);
        boolean w = state.getValue(SanguinitePipeBlock.WEST);
        boolean u = state.getValue(SanguinitePipeBlock.UP);
        boolean d = state.getValue(SanguinitePipeBlock.DOWN);

        // --- BONE HIDING LOGIC (Only for Cons) ---
        // If we are in "Simple Mode", these bones don't exist, so .ifPresent() just does nothing. Safe!

        // Vertical Con Logic
        model.getBone("northCon").ifPresent(b -> b.setHidden(!n));
        model.getBone("southCon").ifPresent(b -> b.setHidden(!s));
        model.getBone("eastCon").ifPresent(b -> b.setHidden(!e)); // Vertical base uses standard E/W
        model.getBone("westCon").ifPresent(b -> b.setHidden(!w));

        // Horizontal Con Logic
        // We need to know if we are rotated (X-Axis) or standard (Z-Axis)
        boolean isXAxis = (e || w) && !n && !s && !u && !d; // Strict X-Check for rotation

        if (isXAxis) {
            // If rotated East-West, we map World directions to Model bones differently
            model.getBone("upCon").ifPresent(b -> b.setHidden(!u));
            model.getBone("downCon").ifPresent(b -> b.setHidden(!d));
            model.getBone("westCon").ifPresent(b -> b.setHidden(!n)); // Rotated mapping
            model.getBone("eastCon").ifPresent(b -> b.setHidden(!s)); // Rotated mapping
        } else {
            // Standard North-South orientation
            model.getBone("upCon").ifPresent(b -> b.setHidden(!u));
            model.getBone("downCon").ifPresent(b -> b.setHidden(!d));
            model.getBone("eastCon").ifPresent(b -> b.setHidden(!e));
            model.getBone("westCon").ifPresent(b -> b.setHidden(!w));
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        // --- ROTATION LOGIC (Only for Simple Pipes) ---
        BlockState state = getAnimatable().getBlockState();
        boolean n = state.getValue(SanguinitePipeBlock.NORTH);
        boolean s = state.getValue(SanguinitePipeBlock.SOUTH);
        boolean e = state.getValue(SanguinitePipeBlock.EAST);
        boolean w = state.getValue(SanguinitePipeBlock.WEST);
        boolean u = state.getValue(SanguinitePipeBlock.UP);
        boolean d = state.getValue(SanguinitePipeBlock.DOWN);

        // Is it a Simple X-Axis Line?
        boolean isXLine = (e || w) && !u && !d && !n && !s;

        // If it is a Simple X Line, rotate it.
        // (The Con models handle their own orientation via bone logic, so we don't rotate them)
        if (isXLine) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }
    }
}
