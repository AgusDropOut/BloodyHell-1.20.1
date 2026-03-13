package net.agusdropout.bloodyhell.datagen.patchouli;

import com.google.gson.JsonObject;

public class PatchouliCategoryBuilder {
    private final JsonObject category = new JsonObject();
    private final String id;

    public static PatchouliCategoryBuilder create(String id, String name, String description, String icon) {
        return new PatchouliCategoryBuilder(id, name, description, icon);
    }

    private PatchouliCategoryBuilder(String id, String name, String description, String icon) {
        this.id = id;
        this.category.addProperty("name", name);
        this.category.addProperty("description", description);
        this.category.addProperty("icon", icon);
    }

    public String getId() {
        return this.id;
    }

    public JsonObject build() {
        return this.category;
    }
}