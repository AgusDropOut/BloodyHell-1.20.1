package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.render.effect.api.BillboardEffect;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.minecraft.client.Minecraft;

public final class BillboardEffects {
    public static final BillboardRenderManager FRENZIED_FLAME = new BillboardRenderManager(
            BillboardEffect.builder("FrenziedFlame", () -> ModShaders.FRENZIED_FLAME_SHADER, EffectStates.PREMULTIPLIED)
                    .perItemTime("AnimTime")
                    .build());

    public static final BillboardRenderManager LINEAR_FRENZIED_FLAME = new BillboardRenderManager(
            BillboardEffect.builder("LinearFrenziedFlame", () -> ModShaders.LINEAR_FRENZIED_FLAME_SHADER, EffectStates.PREMULTIPLIED)
                    .perItemTime("AnimTime")
                    .build());

    public static final BillboardRenderManager FRENZIED_EXPLOSION = new BillboardRenderManager(
            BillboardEffect.builder("FrenziedExplosion", () -> ModShaders.FRENZIED_EXPLOSION_SHADER, EffectStates.PREMULTIPLIED)
                    .perItemTime("AnimTime")
                    .build());

    public static final BillboardRenderManager SWIRL = new BillboardRenderManager(
            BillboardEffect.builder("Swirl", () -> ModShaders.ETHEREAL_SWIRL_SHADER, EffectStates.SWIRL)
                    .perItemTime("EtherealTime")
                    .uv(0.0f, 1.0f)
                    .afterBind(BillboardEffects::bindSwirlScreen)
                    .build());

    public static final BillboardRenderManager TINY_BLOOM = new BillboardRenderManager(
            BillboardEffect.builder("TinyBloom", () -> ModShaders.TINY_BLOOM_SHADER, EffectStates.PREMULTIPLIED)
                    .build());

    public static final BillboardRenderManager BLOOD_DROP = new BillboardRenderManager(
            BillboardEffect.builder("BloodDrop", () -> ModShaders.BLOOD_DROP_SHADER, EffectStates.ALPHA)
                    .globalTime("AnimTime")
                    .uv(-0.5f, 0.5f)
                    .cameraView()
                    .build());

    private BillboardEffects() {}

    private static void bindSwirlScreen() {
        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();
        int screenW = mc.getWindow().getWidth();
        int screenH = mc.getWindow().getHeight();

        Uniform screenSize = ModShaders.ETHEREAL_SWIRL_SHADER.getUniform("ScreenSize");
        if (screenSize != null) {
            screenSize.set((float) screenW, (float) screenH);
        }

        RenderSystem.setShaderTexture(0, mainTarget.getColorTextureId());
    }
}
