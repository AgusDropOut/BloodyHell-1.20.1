package net.agusdropout.bloodyhell.client.shader;

import net.minecraft.Util;

public final class ShaderTime {
    private static final long WRAP_MILLIS = 100000L;

    private ShaderTime() {}

    public static float seconds() {
        return (float) (Util.getMillis() % WRAP_MILLIS) / 1000.0f;
    }
}
