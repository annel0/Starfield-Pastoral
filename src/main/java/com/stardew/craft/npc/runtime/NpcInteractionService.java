package com.stardew.craft.npc.runtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.network.payload.OpenNpcDialogueScreenPayload;
import com.stardew.craft.network.payload.SyncNpcFriendshipStatusPayload;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.weather.WeatherManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("null")
public final class NpcInteractionService {
    private static final String[] WEEKDAY_SHORT = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private NpcInteractionService() {
    }

    public static InteractionResult onInteract(net.minecraft.world.entity.player.Player player,
                                               StardewNpcEntity npc,
                                               InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        Level level = serverPlayer.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        String npcId = npc.getNpcId() == null ? "" : npc.getNpcId().trim().toLowerCase(Locale.ROOT);
        if (npcId.isBlank()) {
            return InteractionResult.PASS;
        }
        ItemStack held = serverPlayer.getItemInHand(hand);

        DayContext dayContext = currentDayContext(serverLevel);

        NpcFriendshipDataManager friendshipManager = NpcFriendshipDataManager.get(serverLevel);
        NpcFriendshipDataManager.FriendshipState state = friendshipManager.getOrCreate(serverPlayer.getUUID(), npcId);
        state.normalizeGiftWeek(dayContext.weekKey());

        if (!held.isEmpty()) {
            String resultText = receiveGift(serverPlayer, held, npcId, state, dayContext);
            friendshipManager.setDirty();
            syncFriendshipStatus(serverPlayer, npcId, state, dayContext);
            PacketDistributor.sendToPlayer(serverPlayer, new OpenNpcDialogueScreenPayload(npcId, formatDialogueForPlayer(resultText, serverPlayer), state.points()));
            return InteractionResult.SUCCESS;
        }

        if (state.lastTalkDayKey() == dayContext.dayKey()) {
            syncFriendshipStatus(serverPlayer, npcId, state, dayContext);
            return InteractionResult.SUCCESS;
        }

        String dialogueText = loadCurrentDialogue(serverLevel, npcId, state, dayContext);
        grantConversationFriendship(npcId, state, dayContext, dialogueText);
        friendshipManager.setDirty();
        syncFriendshipStatus(serverPlayer, npcId, state, dayContext);
        PacketDistributor.sendToPlayer(serverPlayer, new OpenNpcDialogueScreenPayload(npcId, formatDialogueForPlayer(dialogueText, serverPlayer), state.points()));
        return InteractionResult.SUCCESS;
    }

    private static void syncFriendshipStatus(ServerPlayer player,
                                             String npcId,
                                             NpcFriendshipDataManager.FriendshipState state,
                                             DayContext dayContext) {
        int points = Math.max(0, state.points());
        int hearts = Math.max(0, Math.min(14, points / 250));
        int giftsThisWeek = Math.max(0, Math.min(2, state.giftsThisWeek()));
        boolean giftedToday = state.lastGiftDayKey() == dayContext.dayKey();
        boolean talkedToday = state.lastTalkDayKey() == dayContext.dayKey();
        PacketDistributor.sendToPlayer(player, new SyncNpcFriendshipStatusPayload(
            npcId,
            points,
            hearts,
            giftsThisWeek,
            giftedToday,
            talkedToday
        ));
    }

    private static void grantConversationFriendship(String npcId,
                                                    NpcFriendshipDataManager.FriendshipState state,
                                                    DayContext dayContext,
                                                    String dialogueText) {
        if (state.lastTalkDayKey() == dayContext.dayKey()) {
            return;
        }
        if (dialogueText == null || dialogueText.isBlank() || "...".equals(dialogueText)) {
            StardewCraft.LOGGER.info(
                "[NPC_FRIENDSHIP_TRACE] npc={} source=talk dayKey={} delta=0 reason=no-valid-dialogue",
                npcId,
                dayContext.dayKey()
            );
            return;
        }
        int before = state.points();
        state.setLastTalkDayKey(dayContext.dayKey());
        state.addPoints(20);
        StardewCraft.LOGGER.info(
            "[NPC_FRIENDSHIP_TRACE] npc={} source=talk dayKey={} delta=20 before={} after={}",
            npcId,
            dayContext.dayKey(),
            before,
            state.points()
        );
    }

    private static String receiveGift(ServerPlayer player,
                                      ItemStack held,
                                      String npcId,
                                      NpcFriendshipDataManager.FriendshipState state,
                                      DayContext dayContext) {
        if (state.lastGiftDayKey() == dayContext.dayKey()) {
            return "I've already received a gift from you today.";
        }
        if (state.giftsThisWeek() >= 2) {
            return "You've already given me two gifts this week.";
        }

        GiftTasteResult tasteResult = getGiftTasteForThisItem(held, npcId);
        GiftTaste taste = tasteResult.taste();
        int baseDelta = friendshipDeltaFromTaste(taste);
        boolean birthday = isNpcBirthday(npcId, dayContext);
        int finalDelta = birthday ? baseDelta * 8 : baseDelta;

        int before = state.points();
        state.addPoints(finalDelta);
        state.applyGiftCounters(dayContext.dayKey(), dayContext.weekKey());

        StardewCraft.LOGGER.info(
            "[NPC_GIFT_TRACE] npc={} item={} taste={} tasteSource={} birthday={} baseDelta={} finalDelta={} weekGifts={} before={} after={}",
            npcId,
            normalizeItemId(held),
            taste,
            tasteResult.source(),
            birthday,
            baseDelta,
            finalDelta,
            state.giftsThisWeek(),
            before,
            state.points()
        );

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        return buildGiftResponseText(npcId, held, taste, birthday, finalDelta);
    }

    private static GiftTasteResult getGiftTasteForThisItem(ItemStack held, String npcId) {
        String key = normalizeItemId(held);
        JsonObject npcTastes = NpcDataRegistry.tastes().get(npcId);
        GiftTaste npcResult = findTasteInTable(npcTastes, key);
        if (npcResult != null) {
            return new GiftTasteResult(npcResult, "npc");
        }

        JsonObject universalTastes = NpcDataRegistry.tastes().get("universal");
        GiftTaste universalResult = findTasteInTable(universalTastes, key);
        if (universalResult != null) {
            return new GiftTasteResult(universalResult, "universal");
        }

        return new GiftTasteResult(GiftTaste.NEUTRAL, "fallback-neutral");
    }

    private static GiftTaste findTasteInTable(JsonObject tastes, String key) {
        if (containsTaste(tastes, "loved", key)) {
            return GiftTaste.LOVED;
        }
        if (containsTaste(tastes, "liked", key)) {
            return GiftTaste.LIKED;
        }
        if (containsTaste(tastes, "neutral", key)) {
            return GiftTaste.NEUTRAL;
        }
        if (containsTaste(tastes, "disliked", key)) {
            return GiftTaste.DISLIKED;
        }
        if (containsTaste(tastes, "hated", key)) {
            return GiftTaste.HATED;
        }
        return null;
    }

    private static boolean containsTaste(JsonObject tastes, String category, String itemId) {
        if (tastes == null || !tastes.has(category) || !tastes.get(category).isJsonArray()) {
            return false;
        }
        JsonArray arr = tastes.getAsJsonArray(category);
        for (JsonElement el : arr) {
            if (el.isJsonPrimitive() && itemId.equals(el.getAsString().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static String loadCurrentDialogue(ServerLevel level,
                                              String npcId,
                                              NpcFriendshipDataManager.FriendshipState state,
                                              DayContext dayContext) {
        JsonObject dialogueRoot = NpcDataRegistry.dialogues().get(npcId);
        if (dialogueRoot == null) {
            return "...";
        }

        int hearts = Math.max(0, Math.min(14, state.points() / 250));
        List<String> trace = new ArrayList<>();
        String selectedKey = null;
        String selectedText = null;

        for (String prefix : buildDialoguePrefixes(dayContext)) {
            String heartKey = findBestHeartVariantKey(dialogueRoot, prefix, hearts);
            if (heartKey != null) {
                selectedText = resolveDialogueTextByKey(dialogueRoot, heartKey, dayContext.dayKey());
                trace.add(traceEntry(heartKey, selectedText));
                if (selectedText != null && !selectedText.isBlank()) {
                    selectedKey = heartKey;
                    break;
                }
            } else {
                trace.add(prefix + "<heart>:no-matching-threshold");
            }

            selectedText = resolveDialogueTextByKey(dialogueRoot, prefix, dayContext.dayKey());
            trace.add(traceEntry(prefix, selectedText));
            if (selectedText != null && !selectedText.isBlank()) {
                selectedKey = prefix;
                break;
            }
        }

        if (selectedText == null || selectedText.isBlank()) {
            String fallback = findFirstPrimitiveDialogue(dialogueRoot, dayContext.dayKey());
            if (fallback != null && !fallback.isBlank()) {
                selectedText = fallback;
                selectedKey = "<first_primitive_fallback>";
            }
        }

        if (selectedText == null || selectedText.isBlank()) {
            selectedText = "...";
            selectedKey = "<ellipsis_fallback>";
        }

        StardewCraft.LOGGER.info(
            "[NPC_DIALOGUE_TRACE] npc={} season={} day={} weekday={} weather={} hearts={} selectedKey={} probes={}",
            npcId,
            dayContext.seasonLower(),
            dayContext.dayInSeason(),
            dayContext.weekdayShort(),
            dayContext.weatherLower(),
            hearts,
            selectedKey,
            String.join("|", trace)
        );

        return selectedText;
    }

    private static String traceEntry(String key, String text) {
        if (text == null) {
            return key + ":missing";
        }
        if (text.isBlank()) {
            return key + ":blank";
        }
        return key + ":hit";
    }

    private static String buildGiftResponseText(String npcId,
                                                ItemStack held,
                                                GiftTaste taste,
                                                boolean birthday,
                                                int finalDelta) {
        JsonObject dialogueRoot = NpcDataRegistry.dialogues().get(npcId);
        if (birthday) {
            if (taste == GiftTaste.LOVED) {
                String loved = resolveDialogueTextByKey(dialogueRoot, "AcceptBirthdayGift_Loved", currentDayKey());
                if (loved != null && !loved.isBlank()) {
                    return loved;
                }
            }
            String birthdayKey = (taste == GiftTaste.DISLIKED || taste == GiftTaste.HATED)
                ? "AcceptBirthdayGift_Negative"
                : "AcceptBirthdayGift_Positive";
            String birthdayText = resolveDialogueTextByKey(dialogueRoot, birthdayKey, currentDayKey());
            if (birthdayText != null && !birthdayText.isBlank()) {
                return birthdayText;
            }
        }

        String itemSpecificKey = "AcceptGift_(O)" + stardewObjectToken(held);
        String itemSpecificText = resolveDialogueTextByKey(dialogueRoot, itemSpecificKey, currentDayKey());
        if (itemSpecificText != null && !itemSpecificText.isBlank()) {
            return itemSpecificText;
        }

        if (finalDelta >= 80) {
            return "I love this!";
        }
        if (finalDelta >= 45) {
            return "This is a really nice gift.";
        }
        if (finalDelta >= 20) {
            return "Thanks.";
        }
        if (finalDelta <= -40) {
            return "I hate this...";
        }
        return "I don't really like this.";
    }

    private static int friendshipDeltaFromTaste(GiftTaste taste) {
        return switch (taste) {
            case LOVED -> 80;
            case LIKED -> 45;
            case NEUTRAL -> 20;
            case DISLIKED -> -20;
            case HATED -> -40;
        };
    }

    private static List<String> buildDialoguePrefixes(DayContext dayContext) {
        List<String> out = new ArrayList<>();
        String weatherToken = normalizedWeatherToken(dayContext.weatherLower());
        if (!weatherToken.isBlank()) {
            out.add(weatherToken);
        }
        if (dayContext.weatherLower().contains("rain") || dayContext.weatherLower().contains("storm")) {
            out.add("rain");
            out.add("Rain");
        }
        out.add(dayContext.seasonLower() + "_" + dayContext.dayInSeason());
        out.add(dayContext.seasonLower() + "_" + dayContext.weekdayShort());
        out.add(dayContext.weekdayShort());
        out.add(String.valueOf(dayContext.dayInSeason()));
        out.add(dayContext.seasonLower());
        out.add("Introduction");
        out.add("default");
        return out;
    }

    private static String normalizedWeatherToken(String weatherLower) {
        if (weatherLower == null || weatherLower.isBlank()) {
            return "";
        }
        String[] parts = weatherLower.replace('-', '_').split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }

    private static String findBestHeartVariantKey(JsonObject root, String prefix, int hearts) {
        JsonObject scope = dialogueScope(root);
        String winner = null;
        int winnerHeart = -1;

        for (String key : scope.keySet()) {
            if (!key.regionMatches(true, 0, prefix, 0, prefix.length())) {
                continue;
            }
            if (key.length() <= prefix.length()) {
                continue;
            }

            String suffix = key.substring(prefix.length());
            if (!allDigits(suffix)) {
                continue;
            }

            int threshold;
            try {
                threshold = Integer.parseInt(suffix);
            } catch (NumberFormatException ignored) {
                continue;
            }

            if (threshold <= hearts && threshold > winnerHeart) {
                winnerHeart = threshold;
                winner = key;
            }
        }
        return winner;
    }

    private static String resolveDialogueTextByKey(JsonObject root, String key, int dayKey) {
        if (root == null || key == null || key.isBlank()) {
            return null;
        }
        JsonObject scope = dialogueScope(root);
        String actual = findKeyCaseInsensitive(scope, key);
        if (actual == null) {
            return null;
        }
        return pickTextFromEntry(scope.get(actual), dayKey);
    }

    private static String findFirstPrimitiveDialogue(JsonObject root, int dayKey) {
        JsonObject scope = dialogueScope(root);
        for (Map.Entry<String, JsonElement> entry : scope.entrySet()) {
            String text = pickTextFromEntry(entry.getValue(), dayKey);
            if (text != null && !text.isBlank()) {
                return text;
            }
        }
        return null;
    }

    private static JsonObject dialogueScope(JsonObject root) {
        if (root != null && root.has("entries") && root.get("entries").isJsonObject()) {
            return root.getAsJsonObject("entries");
        }
        return root;
    }

    private static String pickTextFromEntry(JsonElement entry, int dayKey) {
        if (entry == null) {
            return null;
        }
        if (entry.isJsonPrimitive()) {
            return entry.getAsString();
        }
        if (entry.isJsonArray()) {
            JsonArray arr = entry.getAsJsonArray();
            if (arr.isEmpty()) {
                return null;
            }
            JsonElement el = arr.get(Math.floorMod(dayKey, arr.size()));
            return el != null && el.isJsonPrimitive() ? el.getAsString() : null;
        }
        return null;
    }

    private static String formatDialogueForPlayer(String raw, ServerPlayer player) {
        if (raw == null || raw.isBlank() || player == null) {
            return raw;
        }
        String playerId = player.getGameProfile() == null ? "player" : player.getGameProfile().getName();
        if (playerId == null || playerId.isBlank()) {
            playerId = "player";
        }
        return raw.replace("@", playerId);
    }

    private static String findKeyCaseInsensitive(JsonObject obj, String candidate) {
        for (String key : obj.keySet()) {
            if (key.equalsIgnoreCase(candidate)) {
                return key;
            }
        }
        return null;
    }

    private static boolean allDigits(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNpcBirthday(String npcId, DayContext dayContext) {
        JsonObject root = NpcDataRegistry.events().get("npc_birthdays");
        if (root == null || !root.has("birthdays") || !root.get("birthdays").isJsonObject()) {
            return false;
        }
        JsonObject birthdays = root.getAsJsonObject("birthdays");
        String key = findKeyCaseInsensitive(birthdays, npcId);
        if (key == null) {
            return false;
        }
        JsonElement el = birthdays.get(key);
        if (el == null || !el.isJsonObject()) {
            return false;
        }

        JsonObject birthday = el.getAsJsonObject();
        String season = birthday.has("season") && birthday.get("season").isJsonPrimitive()
            ? birthday.get("season").getAsString().toLowerCase(Locale.ROOT)
            : "";
        int day = birthday.has("day") && birthday.get("day").isJsonPrimitive() ? birthday.get("day").getAsInt() : -1;
        return day == dayContext.dayInSeason() && season.equals(dayContext.seasonLower());
    }

    private static String normalizeItemId(ItemStack held) {
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(held.getItem());
        return itemId == null ? "minecraft:air" : itemId.toString().toLowerCase(Locale.ROOT);
    }

    private static String stardewObjectToken(ItemStack held) {
        String itemId = normalizeItemId(held);
        int colon = itemId.indexOf(':');
        String path = colon >= 0 ? itemId.substring(colon + 1) : itemId;
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1));
            }
        }
        return sb.isEmpty() ? path : sb.toString();
    }

    private static DayContext currentDayContext(ServerLevel level) {
        StardewTimeManager tm = StardewTimeManager.get();
        int dayInSeason = tm.getCurrentDay();
        int dayKey = currentDayKey();
        int weekKey = dayKey / 7;
        String seasonLower = tm.getSeasonName().toLowerCase(Locale.ROOT);
        String weekdayShort = WEEKDAY_SHORT[(Math.max(1, dayInSeason) - 1) % WEEKDAY_SHORT.length];
        String weatherLower = WeatherManager.getCurrentWeather(level).toLowerCase(Locale.ROOT);
        return new DayContext(dayInSeason, dayKey, weekKey, seasonLower, weekdayShort, weatherLower);
    }

    private static int currentDayKey() {
        StardewTimeManager tm = StardewTimeManager.get();
        return (tm.getCurrentYear() - 1) * 112 + tm.getCurrentSeason() * 28 + tm.getCurrentDay();
    }

    private enum GiftTaste {
        LOVED,
        LIKED,
        NEUTRAL,
        DISLIKED,
        HATED
    }

    private record GiftTasteResult(
        GiftTaste taste,
        String source
    ) {
    }

    private record DayContext(
        int dayInSeason,
        int dayKey,
        int weekKey,
        String seasonLower,
        String weekdayShort,
        String weatherLower
    ) {
    }
}
