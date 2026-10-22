package net.agusdropout.bloodyhell.client.render.effect.api;

import net.agusdropout.bloodyhell.client.render.deferred.api.EffectState;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record OrbEffect(String name, Supplier<ShaderInstance> shader, float quadSize, EffectState state, Consumer<ShaderInstance> perOrbUniforms) {

    public static OrbEffect of(String name, Supplier<ShaderInstance> shader, float quadSize, EffectState state) {
        return new OrbEffect(name, shader, quadSize, state, instance -> {});
    }

    public OrbEffect withPerOrbUniforms(Consumer<ShaderInstance> uniforms) {
        return new OrbEffect(name, shader, quadSize, state, uniforms);
    }
}
