package net.agusdropout.bloodyhell.client.render.effect.data;

import net.agusdropout.bloodyhell.client.shader.ShaderTime;

public record OrbCharge(float charge, float clock, float flash, float pulse) {
    public static OrbCharge idle() {
        return new OrbCharge(0.0f, ShaderTime.seconds(), 0.0f, 0.0f);
    }
}
