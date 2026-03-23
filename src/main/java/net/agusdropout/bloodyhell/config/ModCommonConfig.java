package net.agusdropout.bloodyhell.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.BooleanValue GIVE_GUIDE_BOOK_ON_JOIN;

    static {
        BUILDER.push("Gameplay Settings");

        GIVE_GUIDE_BOOK_ON_JOIN = BUILDER
                .comment("Should players receive the Unknown Guide Book when they first join the world?")
                .define("giveGuideBookOnJoin", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}