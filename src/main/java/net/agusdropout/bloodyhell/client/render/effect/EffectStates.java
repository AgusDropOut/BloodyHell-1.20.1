package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.render.deferred.api.EffectState;
import org.lwjgl.opengl.GL11;

public final class EffectStates {
    public static final EffectState PREMULTIPLIED = new EffectState(
            () -> {
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                );
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });

    public static final EffectState PREMULTIPLIED_NO_DEPTH_TEST = new EffectState(
            () -> {
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                );
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });

    public static final EffectState ALPHA = new EffectState(
            () -> {
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(
                        GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                );
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });

    public static final EffectState SCREEN_OVERLAY = new EffectState(
            () -> {
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.disableBlend();
            });

    public static final EffectState BLACK_HOLE_ENTITY = new EffectState(
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.disableCull();
                RenderSystem.depthMask(false);
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.enableCull();
                RenderSystem.disableBlend();
            });

    public static final EffectState BLACK_HOLE_SCREEN = new EffectState(
            () -> {
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
            });

    public static final EffectState MAGIC_RING = new EffectState(
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                RenderSystem.disableCull();
                RenderSystem.depthMask(false);
                RenderSystem.enableDepthTest();
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.enableCull();
                RenderSystem.disableBlend();
            });

    public static final EffectState NOISE_SPHERE = new EffectState(
            () -> {
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
            });

    public static final EffectState SHIELD = new EffectState(
            () -> {
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.disableCull();
            },
            () -> {
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.enableCull();
            });

    private EffectStates() {}
}
