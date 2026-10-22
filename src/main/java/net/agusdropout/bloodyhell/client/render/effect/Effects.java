package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.render.deferred.base.TaskQueueRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.api.BillboardEffect;
import net.agusdropout.bloodyhell.client.render.effect.api.OrbEffect;
import net.agusdropout.bloodyhell.client.render.effect.api.TrailEffect;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.minecraft.client.Minecraft;

public final class Effects {
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
            BillboardEffect.builder("Swirl", () -> ModShaders.ETHEREAL_SWIRL_SHADER, EffectStates.SCREEN_OVERLAY)
                    .perItemTime("EtherealTime")
                    .uv(0.0f, 1.0f)
                    .afterBind(Effects::bindSwirlScreen)
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

    public static final TrailRenderManager FRENZIED_TRAIL = new TrailRenderManager(
            new TrailEffect("FrenziedTrail", () -> ModShaders.LINEAR_FRENZIED_FLAME_SHADER, () -> ModShaders.RADIANT_ENERGY_SHADER, EffectStates.PREMULTIPLIED));

    public static final TrailRenderManager RADIANT_TRAIL = new TrailRenderManager(
            new TrailEffect("RadiantEnergy", () -> ModShaders.RADIANT_ENERGY_SHADER, () -> ModShaders.RADIANT_ENERGY_SHADER, EffectStates.PREMULTIPLIED_NO_DEPTH_TEST));

    public static final OrbRenderManager BLOOD_ORB = new OrbRenderManager(
            OrbEffect.of("BloodOrb", () -> ModShaders.BLOOD_ORB_SHADER, 0.5f));

    public static final OrbRenderManager GOLDEN_SORROW_ORB = new OrbRenderManager(
            OrbEffect.of("GoldenSorrowOrb", () -> ModShaders.GOLDEN_SORROW_ORB_SHADER, 0.8f));

    public static final OrbRenderManager SCARLET_CINDER_ORB = new OrbRenderManager(
            OrbEffect.of("ScarletCinderOrb", () -> ModShaders.SCARLET_CINDER_ORB_SHADER, 1.5f)
                    .withPerOrbUniforms(shader -> {
                        AbstractUniform pitch = shader.safeGetUniform("u_Pitch");
                        if (pitch != null) {
                            pitch.set(Minecraft.getInstance().player.getXRot());
                        }
                    }));

    public static final BlackHoleRenderManager BLACK_HOLE = new BlackHoleRenderManager();
    public static final BlackHoleEntityRenderManager BLACK_HOLE_ENTITY = new BlackHoleEntityRenderManager();
    public static final RadialDistortionRenderManager RADIAL_DISTORTION = new RadialDistortionRenderManager();
    public static final MagicRingRenderManager MAGIC_RING = new MagicRingRenderManager();
    public static final NoiseSphereRenderManager NOISE_SPHERE = new NoiseSphereRenderManager();
    public static final TaskQueueRenderManager SPHERICAL_SHIELD = new TaskQueueRenderManager("SphericalShield", EffectStates.SHIELD);
    public static final BloodBlobRenderManager BLOOD_BLOB = new BloodBlobRenderManager();
    public static final MiniBlobRenderManager MINI_BLOB = new MiniBlobRenderManager();
    public static final BloodStainDecalRenderManager BLOOD_STAIN_DECAL = new BloodStainDecalRenderManager();

    private Effects() {}

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
