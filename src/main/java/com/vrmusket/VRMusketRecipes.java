package com.vrmusket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VRMusketRecipes {

    public static void register(Map<ResourceLocation, JsonElement> map) {
        registerNewBarrelRecipes(map);
        registerConvertedRecipes(map);
        disableOriginalMusketModRecipes(map);
    }

    private static void registerNewBarrelRecipes(Map<ResourceLocation, JsonElement> map) {
        // === Blunderbuss Barrel ===
        JsonObject blunderbussBarrelRecipe = new JsonObject();
        blunderbussBarrelRecipe.addProperty("type", "minecraft:crafting_shaped");

        JsonArray pattern = new JsonArray();
        pattern.add("PPP");
        pattern.add("HF ");
        pattern.add("   ");
        blunderbussBarrelRecipe.add("pattern", pattern);

        JsonObject key = new JsonObject();
        key.add("P", item("gtceu:brass_plate"));
        key.add("H", VRMusketTags.hammer());
        key.add("F", VRMusketTags.file());
        blunderbussBarrelRecipe.add("key", key);
        blunderbussBarrelRecipe.add("result", result("vrmusket:blunderbuss_barrel", 1));
        map.put(new ResourceLocation(VRMusket.MOD_ID, "blunderbuss_barrel_manual"), blunderbussBarrelRecipe);

        // === Musket Barrel ===
        JsonObject musketBarrelRecipe = new JsonObject();
        musketBarrelRecipe.addProperty("type", "minecraft:crafting_shaped");

        JsonArray musketPattern = new JsonArray();
        musketPattern.add("PPP");
        musketPattern.add("HF ");
        musketPattern.add("   ");
        musketBarrelRecipe.add("pattern", musketPattern);

        JsonObject musketKey = new JsonObject();
        musketKey.add("P", item("gtceu:steel_plate"));
        musketKey.add("H", VRMusketTags.hammer());
        musketKey.add("F", VRMusketTags.file());
        musketBarrelRecipe.add("key", musketKey);

        musketBarrelRecipe.add("result", result("vrmusket:musket_barrel", 1));
        map.put(new ResourceLocation(VRMusket.MOD_ID, "musket_barrel_manual"), musketBarrelRecipe);
    }

    private static void registerConvertedRecipes(Map<ResourceLocation, JsonElement> map) {
        // === Wooden Stock ===
        JsonArray woodenStockIngredients = new JsonArray();
        woodenStockIngredients.add(item("gtceu:treated_wood_planks"));
        woodenStockIngredients.add(item("gtceu:treated_wood_planks"));
        woodenStockIngredients.add(VRMusketTags.file());
        woodenStockIngredients.add(VRMusketTags.hammer());

        map.put(
                new ResourceLocation(VRMusket.MOD_ID, "wooden_stock"),
                createShapelessRecipe(woodenStockIngredients, result("vrmusket:wooden_stock", 1))
        );

        // === Blunderbuss ===
        Map<Character, JsonObject> blunderbussKeys = new HashMap<>();
        blunderbussKeys.put('C', item("vrmusket:blunderbuss_barrel"));
        blunderbussKeys.put('F', item("minecraft:flint_and_steel"));
        blunderbussKeys.put('P', item("vrmusket:wooden_stock"));
        blunderbussKeys.put('T', item("minecraft:tripwire_hook"));
        blunderbussKeys.put('S', VRMusketTags.screwdriver());
        blunderbussKeys.put('I', item("gtceu:steel_screw"));

        List<String> blunderbussPattern = List.of(
                "CS ",
                "IFI",
                " TP"
        );

        map.put(
                new ResourceLocation(VRMusket.MOD_ID, "blunderbuss"),
                createShapedRecipe("equipment", blunderbussPattern, blunderbussKeys, result("musketmod:blunderbuss", 1))
        );

        // === Musket ===
        Map<Character, JsonObject> musketKeys = new HashMap<>();
        musketKeys.put('C', item("vrmusket:musket_barrel"));
        musketKeys.put('F', item("minecraft:flint_and_steel"));
        musketKeys.put('P', item("vrmusket:wooden_stock"));
        musketKeys.put('T', item("minecraft:tripwire_hook"));
        musketKeys.put('S', VRMusketTags.screwdriver());
        musketKeys.put('I', item("gtceu:steel_screw"));

        List<String> musketPattern = List.of(
                "CS ",
                "IFI",
                " TP"
        );

        map.put(
                new ResourceLocation(VRMusket.MOD_ID, "musket"),
                createShapedRecipe("equipment", musketPattern, musketKeys, result("musketmod:musket", 1))
        );

        // === Pistol ===
        Map<Character, JsonObject> pistolKeys = new HashMap<>();
        pistolKeys.put('C', item("gtceu:steel_plate"));
        pistolKeys.put('F', item("minecraft:flint_and_steel"));
        pistolKeys.put('P', item("gtceu:treated_wood_planks"));
        pistolKeys.put('T', item("minecraft:tripwire_hook"));
        pistolKeys.put('S', VRMusketTags.file());
        pistolKeys.put('H', VRMusketTags.hammer());

        List<String> pistolPattern = List.of(
                "S  ",
                "CFH",
                "TP "
        );

        map.put(
                new ResourceLocation(VRMusket.MOD_ID, "pistol"),
                createShapedRecipe("equipment", pistolPattern, pistolKeys, result("musketmod:pistol", 1))
        );

        // === Musket with bayonet ===
        Map<Character, JsonObject> bayonetKeys = new HashMap<>();
        bayonetKeys.put('M', item("musketmod:musket"));
        bayonetKeys.put('K', VRMusketTags.knife());
        bayonetKeys.put('S', VRMusketTags.screwdriver());
        bayonetKeys.put('C', item("gtceu:steel_screw"));

        List<String> bayonetPattern = List.of(
                "K ",
                "CS",
                "M "
        );

        map.put(
                new ResourceLocation(VRMusket.MOD_ID, "musket_with_bayonet"),
                createShapedRecipe("equipment", bayonetPattern, bayonetKeys, result("musketmod:musket_with_bayonet", 1))
        );

        // === Musket with Scope ===
        Map<Character, JsonObject> scopeKeys = new HashMap<>();
        scopeKeys.put('M', item("musketmod:musket"));
        scopeKeys.put('P', item("minecraft:spyglass"));
        scopeKeys.put('S', VRMusketTags.screwdriver());
        scopeKeys.put('C', item("gtceu:steel_screw"));

        List<String> scopePattern = List.of(
                "P ",
                "CS",
                "M "
        );

        map.put(
                new ResourceLocation(VRMusket.MOD_ID, "musket_with_scope"),
                createShapedRecipe("equipment", scopePattern, scopeKeys, result("musketmod:musket_with_scope", 1))
        );
    }

    // =================================================================================================
    // Helper Methods for JSON Construction
    // =================================================================================================

    private static JsonObject createShapedRecipe(String category, List<String> pattern, Map<Character, JsonObject> keys, JsonObject result) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        if (category != null && !category.isEmpty()) {
            json.addProperty("category", category);
        }

        JsonArray patternArray = new JsonArray();
        for (String line : pattern) {
            patternArray.add(line);
        }
        json.add("pattern", patternArray);

        JsonObject keyObject = new JsonObject();
        for (Map.Entry<Character, JsonObject> entry : keys.entrySet()) {
            keyObject.add(String.valueOf(entry.getKey()), entry.getValue());
        }
        json.add("key", keyObject);

        json.add("result", result);
        return json;
    }

    private static JsonObject createShapelessRecipe(JsonArray ingredients, JsonObject result) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        json.add("ingredients", ingredients);
        json.add("result", result);
        return json;
    }

    private static JsonObject item(String itemId) {
        JsonObject json = new JsonObject();
        json.addProperty("item", itemId);
        return json;
    }

    private static JsonObject tag(String tagId) {
        JsonObject json = new JsonObject();
        json.addProperty("tag", tagId);
        return json;
    }

    private static JsonObject result(String itemId, int count) {
        JsonObject json = new JsonObject();
        json.addProperty("item", itemId);
        if (count > 1) {
            json.addProperty("count", count);
        }
        return json;
    }

    public static void disableOriginalMusketModRecipes(Map<ResourceLocation, JsonElement> map) {
        String[] musketRecipes = {
                "blunderbuss",
                "musket",
                "pistol",
                "musket_with_bayonet",
                "musket_with_scope",
        };

        for (String recipeId : musketRecipes) {
            ResourceLocation key = new ResourceLocation("musketmod", recipeId);
            map.put(key, new JsonObject());
        }
    }
}
