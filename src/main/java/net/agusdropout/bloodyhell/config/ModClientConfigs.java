package net.agusdropout.bloodyhell.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_CAMERA_SHAKE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_FOV_EFFECTS;

    static {
        BUILDER.push("Accessibility Settings");

        ENABLE_CAMERA_SHAKE = BUILDER
                .comment("Enable or disable camera shaking effects (Turn off if you experience motion sickness).")
                .define("enableCameraShake", true);

        ENABLE_FOV_EFFECTS = BUILDER
                .comment("Enable or disable dynamic FOV changes caused by mod items and effects.")
                .define("enableFovEffects", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}