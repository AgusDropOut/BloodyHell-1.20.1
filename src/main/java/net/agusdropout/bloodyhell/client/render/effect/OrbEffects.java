package net.agusdropout.bloodyhell.client.render.effect;

import net.agusdropout.bloodyhell.client.render.effect.api.OrbEffect;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.minecraft.client.Minecraft;

public final class OrbEffects {
    public static final OrbRenderManager BLOOD = new OrbRenderManager(
            OrbEffect.of("BloodOrb", () -> ModShaders.BLOOD_ORB_SHADER, 0.5f));

    public static final OrbRenderManager GOLDEN_SORROW = new OrbRenderManager(
            OrbEffect.of("GoldenSorrowOrb", () -> ModShaders.GOLDEN_SORROW_ORB_SHADER, 0.8f));

    public static final OrbRenderManager SCARLET_CINDER = new OrbRenderManager(
            OrbEffect.of("ScarletCinderOrb", () -> ModShaders.SCARLET_CINDER_ORB_SHADER, 1.5f)
                    .withPerOrbUniforms(shader -> {
                        if (shader.safeGetUniform("u_Pitch") != null) {
                            shader.safeGetUniform("u_Pitch").set(Minecraft.getInstance().player.getXRot());
                        }
                    }));

    private OrbEffects() {}
}
