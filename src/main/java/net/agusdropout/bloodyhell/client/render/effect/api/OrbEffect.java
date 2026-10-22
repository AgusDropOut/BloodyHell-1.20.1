package net.agusdropout.bloodyhell.client.render.effect.api;

import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record OrbEffect(String name, Supplier<ShaderInstance> shader, float quadSize, Consumer<ShaderInstance> perOrbUniforms) {

    public static OrbEffect of(String name, Supplier<ShaderInstance> shader, float quadSize) {
        return new OrbEffect(name, shader, quadSize, instance -> {});
    }

    public OrbEffect withPerOrbUniforms(Consumer<ShaderInstance> uniforms) {
        return new OrbEffect(name, shader, quadSize, uniforms);
    }
}
