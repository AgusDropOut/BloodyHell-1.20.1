package net.agusdropout.bloodyhell.client.render.effect;

import net.agusdropout.bloodyhell.client.render.effect.api.TrailEffect;
import net.agusdropout.bloodyhell.client.shader.ModShaders;

public final class TrailEffects {
    public static final TrailRenderManager FRENZIED = new TrailRenderManager(
            new TrailEffect("FrenziedTrail", () -> ModShaders.LINEAR_FRENZIED_FLAME_SHADER, () -> ModShaders.RADIANT_ENERGY_SHADER, EffectStates.PREMULTIPLIED));

    public static final TrailRenderManager RADIANT = new TrailRenderManager(
            new TrailEffect("RadiantEnergy", () -> ModShaders.RADIANT_ENERGY_SHADER, () -> ModShaders.RADIANT_ENERGY_SHADER, EffectStates.PREMULTIPLIED_NO_DEPTH_TEST));

    private TrailEffects() {}
}
