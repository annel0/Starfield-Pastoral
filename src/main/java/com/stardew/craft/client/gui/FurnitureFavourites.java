package com.stardew.craft.client.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Client-side persistence for Furniture Catalogue favourites.
 * Stores a set of item IDs in config/stardewcraft_favourites.json.
 */
public class FurnitureFavourites {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type SET_TYPE = new TypeToken<LinkedHashSet<String>>() {}.getType();
    private static FurnitureFavourites instance;

    private final Set<String> favourites = new LinkedHashSet<>();
    private final Path savePath;

    private FurnitureFavourites(Path savePath) {
        this.savePath = savePath;
        load();
    }

    public static FurnitureFavourites getInstance() {
        if (instance == null) {
            Path gameDir = Minecraft.getInstance().gameDirectory.toPath();
            instance = new FurnitureFavourites(gameDir.resolve("config").resolve("stardewcraft_favourites.json"));
        }
        return instance;
    }

    public boolean isFavourite(String itemId) {
        return favourites.contains(itemId);
    }

    public void toggle(String itemId) {
        if (!favourites.remove(itemId)) {
            favourites.add(itemId);
        }
        save();
    }

    public Set<String> getFavourites() {
        return Set.copyOf(favourites);
    }

    private void load() {
        if (!Files.exists(savePath)) return;
        try {
            String json = Files.readString(savePath);
            Set<String> loaded = GSON.fromJson(json, SET_TYPE);
            if (loaded != null) {
                favourites.clear();
                favourites.addAll(loaded);
            }
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            // Silently ignore corrupt file
        }
    }

    private void save() {
        try {
            Files.createDirectories(savePath.getParent());
            Files.writeString(savePath, GSON.toJson(favourites));
        } catch (IOException e) {
            // Silently ignore save failures
        }
    }
}
