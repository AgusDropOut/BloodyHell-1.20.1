package net.agusdropout.bloodyhell.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_CAMERA_SHAKE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_FOV_EFFECTS;

    public static final ForgeConfigSpec.BooleanValue DISABLE_MOD_TOOLTIPS;

    static {
        BUILDER.push("Accessibility Settings");

        ENABLE_CAMERA_SHAKE = BUILDER
                .comment("Enable or disable camera shaking effects (Turn off if you experience motion sickness).")
                .define("enableCameraShake", true);

        ENABLE_FOV_EFFECTS = BUILDER
                .comment("Enable or disable dynamic FOV changes caused by mod items and effects.")
                .define("enableFovEffects", true);

        BUILDER.pop();

        BUILDER.push("Mod Tooltips Settings");

        DISABLE_MOD_TOOLTIPS = BUILDER
                .comment("True if you want to disable the red mod name and icon on bloody hell!'s items")
                .define("disable_mod_tooltips", false);



        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}