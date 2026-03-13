package net.agusdropout.bloodyhell.datagen.patchouli;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class PatchouliBookBuilder {
    private final JsonObject book = new JsonObject();

    public static PatchouliBookBuilder create(String name, String landingText) {
        return new PatchouliBookBuilder(name, landingText);
    }

    private PatchouliBookBuilder(String name, String landingText) {
        book.addProperty("name", name);
        book.addProperty("landing_text", landingText);
        book.addProperty("use_resource_pack", true);
        book.addProperty("book_texture", "patchouli:textures/gui/book_brown.png");
    }

    public PatchouliBookBuilder setMacros(JsonObject macros) {
        book.add("macros", macros); // MUST BE "macros"
        return this;
    }

    public PatchouliBookBuilder setModel(ResourceLocation model) {
        book.addProperty("model", model.toString());
        return this;
    }

    public PatchouliBookBuilder setI18n(boolean useI18n) {
        book.addProperty("i18n", useI18n);
        return this;
    }

    public JsonObject build() {
        return this.book;
    }
}