package net.agusdropout.bloodyhell.datagen.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class PatchouliEntryBuilder {
    private final JsonObject entry = new JsonObject();
    private final JsonArray pages = new JsonArray();
    private final String id;
    private final String categoryId;

    public static PatchouliEntryBuilder create(String id, String categoryId, String name, String icon) {
        return new PatchouliEntryBuilder(id, categoryId, name, icon);
    }

    private PatchouliEntryBuilder(String id, String categoryId, String name, String icon) {
        this.id = id;
        this.categoryId = categoryId;
        this.entry.addProperty("name", name);
        this.entry.addProperty("category", "bloodyhell:" + categoryId);
        this.entry.addProperty("icon", icon);
    }

    public PatchouliEntryBuilder addTextPage(String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("text", text);
        this.pages.add(page);
        return this;
    }

    public PatchouliEntryBuilder addItemWithTextPage(String item, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "bloodyhell:item_and_text"); // Uses the template we defined
        page.addProperty("item", item);
        page.addProperty("text", text);
        this.pages.add(page);
        return this;
    }

    public PatchouliEntryBuilder addSpotlightPage(String item, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:spotlight");
        page.addProperty("item", item);
        page.addProperty("text", text);
        pages.add(page);
        return this;
    }

    public PatchouliEntryBuilder addImagePage(String title, String imagePath, boolean border) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:image");


        JsonArray imagesArray = new JsonArray();
        imagesArray.add(new JsonPrimitive(imagePath));
        page.add("images", imagesArray);

        page.addProperty("title", title);
        page.addProperty("border", border);
        pages.add(page);
        return this;
    }


    public PatchouliEntryBuilder addEntityPage(String entityId, String name, String text, float scale, float offset) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:entity");
        page.addProperty("entity", entityId);

        page.addProperty("scale", scale);
        page.addProperty("offset", offset);

        if (name != null) page.addProperty("name", name);
        if (text != null) page.addProperty("text", text);
        this.pages.add(page);
        return this;
    }

    public PatchouliEntryBuilder addMultiblockPage(String name, String text, JsonObject multiblock) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:multiblock");
        page.addProperty("name", name);
        page.addProperty("text", text);
        page.add("multiblock", multiblock); // Adds the inline structure
        pages.add(page);
        return this;
    }

    public String getId() {
        return this.id;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public JsonObject build() {
        this.entry.add("pages", this.pages);
        return this.entry;
    }
}