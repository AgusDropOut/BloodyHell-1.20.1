package net.agusdropout.bloodyhell.client.render.item.deferred;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.render.deferred.MatrixSnapshot;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.item.deferred.api.ItemDraw;
import net.agusdropout.bloodyhell.client.render.item.deferred.data.BookTask;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class OmnitomeBookRenderManager implements DeferredRenderManager {
    private final List<BookTask> tasks = new ArrayList<>();

    public void queueBook(ItemDraw draw, ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, int packedLight, int packedOverlay) {
        if (RenderPassGuard.shouldSkipDeferred(transform)) return;
        if (transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            tasks.add(new BookTask(
                    draw,
                    stack.copy(),
                    transform,
                    new Matrix4f(poseStack.last().pose()),
                    new Matrix3f(poseStack.last().normal()),
                    MatrixSnapshot.capture(),
                    packedLight,
                    packedOverlay));
        }
    }

    @Override
    public String name() {
        return "OmnitomeBook";
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (tasks.isEmpty()) return;

        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorting currentSorting = RenderSystem.getVertexSorting();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        ItemPassState.managerPass(() -> {
            for (BookTask task : tasks) {
                task.matrices().apply(ItemPassState.sortingFor(task.transform()));

                PoseStack reconstructedPose = new PoseStack();
                reconstructedPose.last().pose().set(task.pose());
                reconstructedPose.last().normal().set(task.normal());

                task.draw().draw(task.stack(), task.transform(), reconstructedPose, immediateBuffer, task.packedLight(), task.packedOverlay());

                immediateBuffer.endBatch();

                MatrixSnapshot.release();
            }
        });

        RenderSystem.setProjectionMatrix(currentProj, currentSorting);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        tasks.clear();
    }
}
