package net.agusdropout.bloodyhell.client.render.effect.api;

import net.agusdropout.bloodyhell.client.render.deferred.api.EffectState;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Supplier;

public record BillboardEffect(
        String name,
        Supplier<ShaderInstance> shader,
        EffectState state,
        TimeMode timeMode,
        String timeUniform,
        float uvMin,
        float uvMax,
        boolean cameraView,
        Runnable afterBind) {

    public enum TimeMode {
        NONE,
        PER_ITEM,
        GLOBAL
    }

    public static Builder builder(String name, Supplier<ShaderInstance> shader, EffectState state) {
        return new Builder(name, shader, state);
    }

    public static final class Builder {
        private final String name;
        private final Supplier<ShaderInstance> shader;
        private final EffectState state;
        private TimeMode timeMode = TimeMode.NONE;
        private String timeUniform;
        private float uvMin = -1.0f;
        private float uvMax = 1.0f;
        private boolean cameraView;
        private Runnable afterBind = () -> {};

        private Builder(String name, Supplier<ShaderInstance> shader, EffectState state) {
            this.name = name;
            this.shader = shader;
            this.state = state;
        }

        public Builder perItemTime(String uniform) {
            this.timeMode = TimeMode.PER_ITEM;
            this.timeUniform = uniform;
            return this;
        }

        public Builder globalTime(String uniform) {
            this.timeMode = TimeMode.GLOBAL;
            this.timeUniform = uniform;
            return this;
        }

        public Builder uv(float min, float max) {
            this.uvMin = min;
            this.uvMax = max;
            return this;
        }

        public Builder cameraView() {
            this.cameraView = true;
            return this;
        }

        public Builder afterBind(Runnable afterBind) {
            this.afterBind = afterBind;
            return this;
        }

        public BillboardEffect build() {
            return new BillboardEffect(name, shader, state, timeMode, timeUniform, uvMin, uvMax, cameraView, afterBind);
        }
    }
}
