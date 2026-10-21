package net.agusdropout.bloodyhell.client.render.block.custom.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.render.block.generic.BaseGeckoBlockModel;
import net.agusdropout.bloodyhell.client.render.block.layer.GenericItemDisplayLayer;
import net.agusdropout.bloodyhell.client.render.block.layer.GeoFluidLayer;
import net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteBloodHarvesterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CondenserRenderer extends GeoBlockRenderer<AbstractCondenserBlockEntity> {
    public CondenserRenderer(BlockEntityRendererProvider.Context context) {
        super(new BaseGeckoBlockModel<>());
        this.addRenderLayer(new GeoFluidLayer<>(this));
        this.addRenderLayer(new GenericItemDisplayLayer<>(this));
    }

    @Override
    public RenderType getRenderType(AbstractCondenserBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}