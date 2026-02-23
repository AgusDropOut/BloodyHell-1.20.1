package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullDropletEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullOrbEmitter;
import net.agusdropout.bloodyhell.util.visuals.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class RhnullOrbEmitterRenderer extends EntityRenderer<RhnullOrbEmitter> {

    private static int captureTextureId = -1;

    public RhnullOrbEmitterRenderer(EntityRendererProvider.Context context) {
        super(context);

        if (captureTextureId == -1) {
            captureTextureId = GL11.glGenTextures();
        }
    }

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(BloodyHell.MODID, "textures/misc/rhnull.png");

    @Override
    public void render(RhnullOrbEmitter entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float time = entity.tickCount + partialTicks;

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(0.5f));
        poseStack.mulPose(Axis.YP.rotation(time * 0.1f));


        if (ModShaders.SHAPE_GLITTER_SHADER != null) {
            Uniform timeUniform = ModShaders.SHAPE_GLITTER_SHADER.getUniform("GlitterTime");
            if (timeUniform != null) {
                timeUniform.set(time);
            }
        }

        RenderType glitterType = ModRenderTypes.getShapeGlitterRenderType();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(glitterType);


        RenderHelper.renderColorSphere(vertexconsumer, poseStack.last().pose(),
                0.4f, 32, 32,
                1.0f, 0.8f, 0.0f, 0.65f);

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    @Override
    public ResourceLocation getTextureLocation(RhnullOrbEmitter entity) {
        return TEXTURE;
    }
}