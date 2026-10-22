package net.agusdropout.bloodyhell.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.item.ItemDisplayContext;
import java.lang.reflect.Method;

public final class RenderPassGuard {
    private static boolean resolved = false;
    private static Object irisApi;
    private static Method isShadowPassMethod;

    private RenderPassGuard() {}

    private static void resolve() {
        resolved = true;
        for (String cn : new String[]{
                "net.irisshaders.iris.api.v0.IrisApi",
                "net.coderbot.iris.api.v0.IrisApi"}) {
            try {
                Class<?> c = Class.forName(cn);
                irisApi = c.getMethod("getInstance").invoke(null);
                isShadowPassMethod = c.getMethod("isRenderingShadowPass");
                return;
            } catch (Throwable ignored) {}
        }
    }

    public static boolean isShadowPass() {
        if (!resolved) resolve();
        if (isShadowPassMethod == null) return false;
        try {
            return (boolean) isShadowPassMethod.invoke(irisApi);
        } catch (Throwable t) {
            return false;
        }
    }


    public static boolean shouldSkipDeferred() {
        return shouldSkipDeferred(ItemDisplayContext.NONE);
    }

    public static boolean shouldSkipDeferred(ItemDisplayContext transform) {
        if (isShadowPass()) return true;

        if (transform != ItemDisplayContext.GUI) {
            float m33 = RenderSystem.getProjectionMatrix().m33();
            if (Math.abs(m33 - 1.0f) < 1e-4f) return true;
        }
        return false;
    }
}