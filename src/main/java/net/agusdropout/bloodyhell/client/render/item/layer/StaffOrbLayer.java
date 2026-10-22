package net.agusdropout.bloodyhell.client.render.item.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class StaffOrbLayer extends GeoRenderLayer<BaseStaffItem> {

    @FunctionalInterface
    public interface OrbSink {
        void accept(Matrix4f pose, Matrix4f projection, Matrix3f rotation, ItemDisplayContext transform);
    }

    private final GenericStaffRenderer staffRenderer;
    private final OrbSink sink;

    public StaffOrbLayer(GenericStaffRenderer renderer, OrbSink sink) {
        super(renderer);
        this.staffRenderer = renderer;
        this.sink = sink;
    }

    @Override
    public void render(PoseStack poseStack, BaseStaffItem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemDisplayContext context = this.staffRenderer.getCurrentTransform();

        if (context == ItemDisplayContext.GUI) return;

        if (mc.options.getCameraType().isFirstPerson() || mc.options.getCameraType().isMirrored()) {
            bakedModel.getBone("orb").ifPresent(orbBone -> {
                poseStack.pushPose();

                Matrix4f m = orbBone.getModelSpaceMatrix();
                poseStack.last().pose().mul(m);

                float pivotX = orbBone.getPivotX() / 16.0f;
                float pivotY = orbBone.getPivotY() / 16.0f;
                float pivotZ = orbBone.getPivotZ() / 16.0f;

                poseStack.translate(pivotX, pivotY, pivotZ);

                Matrix4f boneMat = new Matrix4f(poseStack.last().pose());
                Matrix4f handProj = new Matrix4f(RenderSystem.getProjectionMatrix());

                Matrix3f boneRotation = new Matrix3f();
                boneMat.get3x3(boneRotation);

                sink.accept(boneMat, handProj, boneRotation, context);

                poseStack.popPose();
            });
        }
    }
}
