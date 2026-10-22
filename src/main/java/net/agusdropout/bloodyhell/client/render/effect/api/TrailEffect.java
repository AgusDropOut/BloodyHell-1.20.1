package net.agusdropout.bloodyhell.client.render.effect.api;

import net.agusdropout.bloodyhell.client.render.deferred.api.EffectState;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Supplier;

public record TrailEffect(String name, Supplier<ShaderInstance> shader, EffectState state) {
}
