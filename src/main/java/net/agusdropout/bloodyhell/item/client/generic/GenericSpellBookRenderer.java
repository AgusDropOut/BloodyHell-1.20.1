package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.item.client.books.OmnitomeRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.util.visuals.ModRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericSpellBookRenderer extends GeoItemRenderer<BaseSpellBookItem<?>> {

    public GenericSpellBookRenderer() {
        super(new GenericSpellBookModel());
    }

    @Override
    public RenderType getRenderType(BaseSpellBookItem<?> animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        if (OmnitomeRenderer.isChildMorphing) {
            return ModRenderTypes.getEmergeClip(texture);
        }
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}