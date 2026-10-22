package net.agusdropout.bloodyhell.client.render.item.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.render.ModRenderTypes;
import net.agusdropout.bloodyhell.client.render.item.custom.renderer.OmnitomeRenderer;
import net.agusdropout.bloodyhell.client.render.item.deferred.ItemEffects;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class OmnitomeVoidLayer extends GeoRenderLayer<OmnitomeItem> {

    public OmnitomeVoidLayer(GeoRenderer<OmnitomeItem> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, OmnitomeItem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        OmnitomeRenderer renderer = (OmnitomeRenderer) this.getRenderer();

        GeoBone lPage = this.getGeoModel().getBone("lupperPage").orElse(null);
        GeoBone rPage = this.getGeoModel().getBone("rupperPage").orElse(null);

        if (lPage != null && rPage != null) {
            if (renderer.leftPageMatrix != null && renderer.leftPageNormal != null && renderer.rightPageMatrix != null && renderer.rightPageNormal != null) {

                ResourceLocation texture = this.getTextureResource(animatable);

                if (renderer.currentTransform == ItemDisplayContext.GUI) {
                    if (ModShaders.OMNITOME_VOID_SHADER != null) {
                        ModShaders.OMNITOME_VOID_SHADER.safeGetUniform("TimeAnim").set(ShaderTime.seconds());
                    }
                    VertexConsumer voidBuffer = bufferSource.getBuffer(ModRenderTypes.getOmnitomeVoid(texture));
                    renderer.renderVoidCubesDirect(renderer.leftPageMatrix, renderer.leftPageNormal, lPage, voidBuffer, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
                    renderer.renderVoidCubesDirect(renderer.rightPageMatrix, renderer.rightPageNormal, rPage, voidBuffer, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
                    return;
                }

                ItemEffects.OMNITOME_VOID.queueVoidBone(
                        renderer::renderVoidCubesDirect, renderer.leftPageMatrix, renderer.leftPageNormal, lPage, texture, renderer.currentTransform, packedLight, packedOverlay, 1f, 1f, 1f, 1f
                );

                ItemEffects.OMNITOME_VOID.queueVoidBone(
                        renderer::renderVoidCubesDirect, renderer.rightPageMatrix, renderer.rightPageNormal, rPage, texture, renderer.currentTransform, packedLight, packedOverlay, 1f, 1f, 1f, 1f
                );
            }
        }
    }
}
