package com.stardew.craft.mail;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardew.craft.StardewCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 数据驱动的邮件注册表。
 * <p>
 * 从 data/stardewcraft/mail/*.json 加载邮件定义。
 * 每个 JSON 文件包含一个邮件数组。
 */
public class MailRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailRegistry.class);
    private static final String DATA_PATH = "mail";

    private static final Map<String, MailEntry> ENTRIES = new HashMap<>();

    /**
     * 从资源包重新加载所有邮件定义。
     * 在 server data reload / AddReloadListenerEvent 中调用。
     */
    public static void reload(ResourceManager resourceManager) {
        ENTRIES.clear();
        // NeoForge 1.21.1: 按命名空间+路径遍历
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(
                DATA_PATH, loc -> loc.getNamespace().equals(StardewCraft.MODID) && loc.getPath().endsWith(".json"));

        for (var entry : resources.entrySet()) {
            ResourceLocation loc = entry.getKey();
            try (var reader = new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8)) {
                JsonElement root = JsonParser.parseReader(reader);
                if (root.isJsonArray()) {
                    for (JsonElement elem : root.getAsJsonArray()) {
                        MailEntry mail = parseEntry(elem.getAsJsonObject());
                        if (mail != null) {
                            ENTRIES.put(mail.getId(), mail);
                        }
                    }
                } else if (root.isJsonObject()) {
                    MailEntry mail = parseEntry(root.getAsJsonObject());
                    if (mail != null) {
                        ENTRIES.put(mail.getId(), mail);
                    }
                }
                LOGGER.debug("Loaded mail data from {}", loc);
            } catch (Exception e) {
                LOGGER.error("Failed to load mail data from {}", loc, e);
            }
        }
        LOGGER.info("Loaded {} mail entries", ENTRIES.size());
    }

    @Nullable
    public static MailEntry get(String mailId) {
        return ENTRIES.get(mailId);
    }

    public static Collection<MailEntry> getAll() {
        return Collections.unmodifiableCollection(ENTRIES.values());
    }

    public static boolean contains(String mailId) {
        return ENTRIES.containsKey(mailId);
    }

    @Nullable
    private static MailEntry parseEntry(JsonObject obj) {
        try {
            String id = obj.get("id").getAsString();
            String text = obj.get("text").getAsString();
            int background = obj.has("background") ? obj.get("background").getAsInt() : 0;
            String customBg = obj.has("customBgTexture") && !obj.get("customBgTexture").isJsonNull()
                    ? obj.get("customBgTexture").getAsString() : null;
            String textColor = obj.has("textColor") && !obj.get("textColor").isJsonNull()
                    ? obj.get("textColor").getAsString() : null;

            List<MailEntry.AttachedItem> items = new ArrayList<>();
            if (obj.has("attachedItems") && obj.get("attachedItems").isJsonArray()) {
                for (JsonElement itemElem : obj.getAsJsonArray("attachedItems")) {
                    JsonObject itemObj = itemElem.getAsJsonObject();
                    String itemId = itemObj.get("id").getAsString();
                    int count = itemObj.has("count") ? itemObj.get("count").getAsInt() : 1;
                    items.add(new MailEntry.AttachedItem(itemId, count));
                }
            }

            int money = obj.has("money") ? obj.get("money").getAsInt() : 0;
            String learnedRecipe = obj.has("learnedRecipe") && !obj.get("learnedRecipe").isJsonNull()
                    ? obj.get("learnedRecipe").getAsString() : null;
            boolean recipeIsCooking = obj.has("recipeIsCooking") && obj.get("recipeIsCooking").getAsBoolean();
            String questId = obj.has("questId") && !obj.get("questId").isJsonNull()
                    ? obj.get("questId").getAsString() : null;
            String specialOrderId = obj.has("specialOrderId") && !obj.get("specialOrderId").isJsonNull()
                    ? obj.get("specialOrderId").getAsString() : null;

            return new MailEntry(id, text, background, customBg, textColor,
                    items, money, learnedRecipe, recipeIsCooking, questId, specialOrderId);
        } catch (Exception e) {
            LOGGER.error("Failed to parse mail entry: {}", obj, e);
            return null;
        }
    }
}
