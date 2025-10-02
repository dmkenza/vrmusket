package com.vrmusket;

import com.google.gson.JsonObject;

public class VRMusketTags {

    private static final boolean FABRIC;

    static {
        boolean fabricLoader = true;
        try {
            Class.forName("net.minecraftforge.fml.loading.FMLLoader");
            fabricLoader = false;
        } catch (ClassNotFoundException ignored) {}
        FABRIC = fabricLoader;
    }

    // ======== Fabric ========
    private static JsonObject fabricTool(String tag) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "gtceu:tool");
        json.addProperty("tag", tag);
        return json;
    }

    // ======== Forge ========
    private static JsonObject forgeTool(String tag) {
        JsonObject json = new JsonObject();
        json.addProperty("tag", tag);
        return json;
    }

    // ======== Public API ========
    public static JsonObject hammer() {
        return FABRIC ? fabricTool("c:hammers") : forgeTool("gtceu:tools/crafting_hammers");
    }

    public static JsonObject file() {
        return FABRIC ? fabricTool("c:files") : forgeTool("forge:tools/files");
    }

    public static JsonObject saw() {
        return FABRIC ? fabricTool("c:saws") : forgeTool("forge:tools/saws");
    }

    public static JsonObject screwdriver() {
        return FABRIC ? fabricTool("c:screwdrivers") : forgeTool("forge:tools/screwdrivers");
    }

    public static JsonObject knife() {
        return FABRIC ? fabricTool("c:knives") : forgeTool("gtceu:tools/crafting_knives");
    }
}
