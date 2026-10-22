package net.agusdropout.bloodyhell.client.shader;

import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;

public class ShaderUtils {

    private static Boolean hasIris = null;
    private static Boolean hasOptifine = null;
    private static Field ofShadersField = null;

    public static boolean areShadersActive() {
        return isIrisShaderActive() || isOptifineShaderActive();
    }

    private static boolean isIrisShaderActive() {
        if (hasIris == null) {
            hasIris = ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus");
        }
        if (hasIris) {
            try {
                Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                Object instance = apiClass.getMethod("getInstance").invoke(null);
                return (boolean) apiClass.getMethod("isShaderPackInUse").invoke(instance);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static boolean isOptifineShaderActive() {
        if (hasOptifine == null) {
            try {
                Class.forName("net.optifine.Config");
                hasOptifine = true;
            } catch (ClassNotFoundException e) {
                hasOptifine = false;
            }
        }
        if (hasOptifine) {
            try {
                if (ofShadersField == null) {
                    Class<?> shadersClass = Class.forName("net.optifine.shaders.Shaders");
                    ofShadersField = shadersClass.getDeclaredField("shaderPackLoaded");
                    ofShadersField.setAccessible(true);
                }
                return (boolean) ofShadersField.get(null);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

}
