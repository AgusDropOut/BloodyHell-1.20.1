package net.agusdropout.bloodyhell.client.render.item.deferred;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.render.ModRenderTypes;
import net.agusdropout.bloodyhell.client.render.deferred.MatrixSnapshot;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.item.deferred.api.VoidDraw;
import net.agusdropout.bloodyhell.client.render.item.deferred.data.VoidTask;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayList;
import java.util.List;

public class OmnitomeVoidRenderManager implements DeferredRenderManager {
    private final List<VoidTask> tasks = new ArrayList<>();

    public void queueVoidBone(VoidDraw draw, Matrix4f poseMatrix, Matrix3f normalMatrix, GeoBone bone, ResourceLocation texture, ItemDisplayContext transform, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (RenderPassGuard.shouldSkipDeferred(transform)) return;
        tasks.add(new VoidTask(
                draw,
                transform,
                new Matrix4f(poseMatrix),
                new Matrix3f(normalMatrix),
                MatrixSnapshot.capture(),
                bone,
                texture,
                packedLight,
                packedOverlay,
                red, green, blue, alpha));
    }

    @Override
    public String name() {
        return "OmnitomeVoid";
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (tasks.isEmpty()) return;

        RenderSystem.enableDepthTest();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorting currentSorting = RenderSystem.getVertexSorting();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        for (VoidTask task : tasks) {
            if (ModShaders.OMNITOME_VOID_SHADER != null) {
                ModShaders.OMNITOME_VOID_SHADER.safeGetUniform("TimeAnim").set(ShaderTime.seconds());
            }

            task.matrices().apply(ItemPassState.sortingFor(task.transform()));

            RenderType voidType = ModRenderTypes.getOmnitomeVoid(task.texture());
            VertexConsumer voidBuffer = immediateBuffer.getBuffer(voidType);

            task.draw().draw(task.pose(), task.normal(), task.bone(), voidBuffer, task.packedLight(), task.packedOverlay(), task.red(), task.green(), task.blue(), task.alpha());

            immediateBuffer.endBatch(voidType);

            MatrixSnapshot.release();
        }

        RenderSystem.setProjectionMatrix(currentProj, currentSorting);
        RenderSystem.enableDepthTest();
        tasks.clear();
    }
}
