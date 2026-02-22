package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullHeavySwordEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.agusdropout.bloodyhell.util.visuals.ModRenderTypes;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class RhnullIHeavySwordRenderer extends EntityRenderer<RhnullHeavySwordEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/rhnull_heavy_sword_entity.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/rhnull_heavy_sword_entity_glowmask.png");

    private final RhnullHeavySwordModel<RhnullHeavySwordEntity> model;

    public RhnullIHeavySwordRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new RhnullHeavySwordModel<RhnullHeavySwordEntity>(context.bakeLayer(RhnullHeavySwordModel.LAYER_LOCATION));
    }

    @Override
    public void render(RhnullHeavySwordEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        if (entity.getDelayTicks() > 0) {
            return;
        }

        poseStack.pushPose();

        float yRot = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));

        float scale = entity.getEntityData().get(RhnullHeavySwordEntity.SPELL_SCALE);
        poseStack.scale(scale * 0.5f, scale * 0.5f, scale * 0.5f);
        this.model.setupAnim(entity, 0, 0, entity.tickCount + partialTicks, 0, 0);

        if (ModShaders.ENTITY_GLITTER_SHADER != null) {
            Uniform timeUniform = ModShaders.ENTITY_GLITTER_SHADER.getUniform("GlitterTime");
            if (timeUniform == null) {

                System.out.println("CRITICAL: GameTime uniform NOT FOUND in shader!");
            } else {
                float renderTime = entity.tickCount + partialTicks;
                timeUniform.set(renderTime);
                ModShaders.ENTITY_GLITTER_SHADER.apply();
            }
        }

        RenderType glitterType = ModRenderTypes.getGlitterRenderType(TEXTURE);

        VertexConsumer vertexconsumer = buffer.getBuffer(glitterType);

        if(entity.getLifeTicks() > 0.8 * entity.getLifeTimeTicks()) {
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, (float) (Math.sin(entity.getLifeTicks()*1.5f) * 0.5+ 0.5f));
        } else {
          this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.7F);
        }


        float time = entity.tickCount + partialTicks;
        float pulse = (Mth.sin(time * 0.15f) * 0.4f) + 0.6f;
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLOW_TEXTURE));
        this.model.renderToBuffer(poseStack, glowConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, pulse);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RhnullHeavySwordEntity entity) {
        return TEXTURE;
    }

    @Nullable
    protected RenderType getRenderType(RhnullHeavySwordEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucentCull(this.getTextureLocation(entity));
    }
}