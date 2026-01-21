package net.agusdropout.bloodyhell.util;

import net.minecraftforge.fml.ModList; // Or Fabric equivalent
import java.lang.reflect.Field;

public class ShaderUtils {

    private static Boolean hasIris = null;
    private static Boolean hasOptifine = null;
    private static Field ofShadersField = null;

    /**
     * Checks if any shader pack is currently active.
     */
    public static boolean areShadersActive() {
        return isIrisShaderActive() || isOptifineShaderActive();
    }

    // ==========================================
    //                 IRIS
    // ==========================================
    private static boolean isIrisShaderActive() {
        if (hasIris == null) {
            // Check if the mod "iris" (or "oculus" on Forge) is loaded
            hasIris = ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus");
        }

        if (hasIris) {
            try {


                // Reflection version to avoid hard crashes if API changes/missing:
                Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                Object instance = apiClass.getMethod("getInstance").invoke(null);
                return (boolean) apiClass.getMethod("isShaderPackInUse").invoke(instance);
            } catch (Exception e) {
                // Fail silently
                return false;
            }
        }
        return false;
    }

    // ==========================================
    //               OPTIFINE
    // ==========================================
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
                // We look for net.optifine.shaders.Shaders.shaderPackLoaded
                if (ofShadersField == null) {
                    Class<?> shadersClass = Class.forName("net.optifine.shaders.Shaders");
                    ofShadersField = shadersClass.getDeclaredField("shaderPackLoaded");
                    ofShadersField.setAccessible(true);
                }
                return (boolean) ofShadersField.get(null);
            } catch (Exception e) {
                // If reflection fails, assume no shaders
                return false;
            }
        }
        return false;
    }
}