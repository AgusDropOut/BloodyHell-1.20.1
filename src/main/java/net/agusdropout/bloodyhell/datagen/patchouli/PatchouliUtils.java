package net.agusdropout.bloodyhell.datagen.patchouli;

import net.agusdropout.bloodyhell.BloodyHell;

public class PatchouliUtils {
    public static String br() { return "$(br)"; }


    public static String link(String text) { return "$(l)" + text + "$()"; }

    public static String entryLink(String categoryId, String entryId, String text) {
        return "$(l:" + categoryId + "/" + entryId + ")" + text + "$()";
    }

    // Colors
    public static String blood(String text) { return "$(#f20000)" + text + "$()"; }
    public static String madness(String text) { return "$(#171200)" + text + "$()"; }
    public static String gold(String text) { return "$(#ffcc00)" + text + "$()"; }
    public static String green(String text) { return "$(#2b9900)" + text + "$()"; }

    // Custom Lore Colors
    public static String infected(String text) { return "$(#eeff00)" + text + "$()"; }
    public static String blasphemous(String text) { return "$(#ffdd00)" + text + "$()"; }

    // Specific Items & Concepts
    public static String insight(String amount) {
        return link(gold(amount + " Insight"));
    }

    public static String dagger() {
        return link(blood("Sacrificial Dagger"));
    }

    public static String imagePath(String name) {
        return BloodyHell.MODID + ":gui/patchouli/" + name;
    }
}