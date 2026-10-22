package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullOrbEmitter;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.render.RenderHelper;
import net.agusdropout.bloodyhell.util.visuals.manager.DeferredTaskQueues;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RhnullOrbEmitterRenderer extends EntityRenderer<RhnullOrbEmitter> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/misc/rhnull.png");

    public RhnullOrbEmitterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RhnullOrbEmitter entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float time = entity.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(0.5f));
        poseStack.mulPose(Axis.YP.rotation(time * 0.1f));

        Matrix4f capturedPose = new Matrix4f(poseStack.last().pose());

        DeferredTaskQueues.SHAPE_GLITTER.queue(() -> {
            PoseStack taskStack = new PoseStack();
            taskStack.last().pose().set(capturedPose);

            RenderSystem.setShader(() -> ModShaders.SHAPE_GLITTER_SHADER);

            if (ModShaders.SHAPE_GLITTER_SHADER != null) {
                Uniform timeUniform = ModShaders.SHAPE_GLITTER_SHADER.getUniform("GlitterTime");
                if (timeUniform != null) {
                    timeUniform.set(time);
                }
            }

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            RenderHelper.renderColorSphere(bufferbuilder, taskStack.last().pose(),
                    0.4f, 32, 32,
                    1.0f, 0.8f, 0.0f, 0.65f);

            tesselator.end();
        });

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RhnullOrbEmitter entity) {
        return TEXTURE;
    }
}
