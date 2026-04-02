package com.stardew.craft.npc.runtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.emote.EmoteCatalog;
import com.stardew.craft.emote.EmoteType;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.network.payload.EmoteBroadcastPayload;
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
    private static final String STARDROP_TEA_ID = "stardewcraft:stardrop_tea";

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
            String resultText = receiveGift(serverPlayer, npc, held, npcId, state, dayContext);
            friendshipManager.setDirty();
            syncFriendshipStatus(serverPlayer, npcId, state, dayContext);
            sendDialoguePacket(serverPlayer, npcId, resultText, state.points());
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
        sendDialoguePacket(serverPlayer, npcId, dialogueText, state.points());
        return InteractionResult.SUCCESS;
    }

    public static int getMaxFriendshipPointsFor(String npcId) {
        NpcCapabilityProfile profile = NpcDataRegistry.capabilities().get(npcId);
        boolean datable = profile != null && profile.datable();
        // Since we don't have a dating/bouquet system yet, datable NPCs are capped at 8 hearts.
        int maxHearts = datable ? 8 : 10;
        // Vanilla formula: (maxHearts + 1) * 250 - 1
        // Thus 8 hearts -> 9 * 250 - 1 = 2249 points (which safely renders as 8 hearts and no more)
        return (maxHearts + 1) * 250 - 1;
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
        state.addPoints(20, getMaxFriendshipPointsFor(npcId));
        StardewCraft.LOGGER.info(
            "[NPC_FRIENDSHIP_TRACE] npc={} source=talk dayKey={} delta=20 before={} after={}",
            npcId,
            dayContext.dayKey(),
            before,
            state.points()
        );
    }

    private static boolean isStardropTea(ItemStack held) {
        return STARDROP_TEA_ID.equals(normalizeItemId(held));
    }

    private static String receiveGift(ServerPlayer player,
                                      StardewNpcEntity npcEntity,
                                      ItemStack held,
                                      String npcId,
                                      NpcFriendshipDataManager.FriendshipState state,
                                      DayContext dayContext) {
        boolean stardropTea = isStardropTea(held);

        // StardropTea bypasses daily & weekly limits (vanilla parity)
        if (!stardropTea) {
            if (state.lastGiftDayKey() == dayContext.dayKey()) {
                return "stardewcraft.npc.generic.gift.already_today";
            }
            if (state.giftsThisWeek() >= 2) {
                return "stardewcraft.npc.generic.gift.already_week_limit";
            }
        }

        boolean birthday = isNpcBirthday(npcId, dayContext);
        float birthdayMul = birthday ? 8f : 1f;

        int finalDelta;
        GiftTasteResult tasteResult;
        GiftTaste taste;
        if (stardropTea) {
            // Vanilla: gift_taste_stardroptea = 7 → min(750, 250 * multiplier)
            tasteResult = new GiftTasteResult(GiftTaste.LOVED, "stardrop_tea_special");
            taste = GiftTaste.LOVED;
            finalDelta = Math.min(750, (int)(250f * birthdayMul));
        } else {
            tasteResult = getGiftTasteForThisItem(held, npcId);
            taste = tasteResult.taste();
            int baseDelta = friendshipDeltaFromTaste(taste);
            float qualityMul = qualityMultiplier(held);
            // Vanilla: quality multiplier only applies to positive tastes
            if (baseDelta > 0) {
                finalDelta = (int)(baseDelta * birthdayMul * qualityMul);
            } else {
                finalDelta = (int)(baseDelta * birthdayMul);
            }
        }

        int before = state.points();
        state.addPoints(finalDelta, getMaxFriendshipPointsFor(npcId));
        // StardropTea does NOT count toward daily/weekly gift limits (vanilla parity)
        if (!stardropTea) {
            state.applyGiftCounters(dayContext.dayKey(), dayContext.weekKey());
        }

        StardewCraft.LOGGER.info(
            "[NPC_GIFT_TRACE] npc={} item={} taste={} tasteSource={} birthday={} finalDelta={} weekGifts={} before={} after={}",
            npcId,
            normalizeItemId(held),
            taste,
            tasteResult.source(),
            birthday,
            finalDelta,
            state.giftsThisWeek(),
            before,
            state.points()
        );

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        // Broadcast NPC emote to all players (vanilla parity)
        broadcastGiftEmote(npcEntity, taste);

        return buildGiftResponseText(npcId, held, taste, birthday, finalDelta);
    }

    private static GiftTasteResult getGiftTasteForThisItem(ItemStack held, String npcId) {
        String key = normalizeItemId(held);

        // 1. NPC-specific item match (highest priority)
        JsonObject npcTastes = NpcDataRegistry.tastes().get(npcId);
        GiftTaste npcResult = findTasteInTable(npcTastes, key);
        if (npcResult != null) {
            return new GiftTasteResult(npcResult, "npc");
        }

        // 2. NPC-specific category match
        String itemCategory = resolveItemCategory(held);
        if (itemCategory != null) {
            GiftTaste npcCatResult = findTasteByCategory(npcTastes, itemCategory);
            if (npcCatResult != null) {
                return new GiftTasteResult(npcCatResult, "npc-category");
            }
        }

        // 3. Universal item match
        JsonObject universalTastes = NpcDataRegistry.tastes().get("universal");
        GiftTaste universalResult = findTasteInTable(universalTastes, key);
        if (universalResult != null) {
            return new GiftTasteResult(universalResult, "universal");
        }

        // 4. Universal category match
        if (itemCategory != null) {
            GiftTaste univCatResult = findTasteByCategory(universalTastes, itemCategory);
            if (univCatResult != null) {
                return new GiftTasteResult(univCatResult, "universal-category");
            }
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

    /**
     * Check taste table for category-based entries.
     * Checks "loved_categories", "liked_categories", etc. arrays against the item's category tag.
     */
    private static GiftTaste findTasteByCategory(JsonObject tastes, String category) {
        if (tastes == null || category == null) {
            return null;
        }
        if (containsTaste(tastes, "loved_categories", category)) return GiftTaste.LOVED;
        if (containsTaste(tastes, "hated_categories", category)) return GiftTaste.HATED;
        if (containsTaste(tastes, "liked_categories", category)) return GiftTaste.LIKED;
        if (containsTaste(tastes, "disliked_categories", category)) return GiftTaste.DISLIKED;
        if (containsTaste(tastes, "neutral_categories", category)) return GiftTaste.NEUTRAL;
        return null;
    }

    /**
     * Resolve item category from NBT custom data tag "StardewCategory".
     * Returns a lowercase category string (e.g. "fish", "gem", "cooking", "archaeology")
     * or null if no category is set.
     */
    private static String resolveItemCategory(ItemStack held) {
        if (held.isEmpty()) return null;
        net.minecraft.world.item.component.CustomData customData =
            held.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                              net.minecraft.world.item.component.CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag tag = customData.copyTag();
        if (tag.contains("StardewCategory")) {
            String cat = tag.getString("StardewCategory");
            return (cat != null && !cat.isBlank()) ? cat.toLowerCase(Locale.ROOT) : null;
        }
        return null;
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
        NpcCapabilityProfile profile = NpcDataRegistry.capabilities().get(npcId);
        int manners = profile != null ? profile.manners() : NpcCapabilityProfile.MANNERS_NEUTRAL;

        if (birthday) {
            if (taste == GiftTaste.LOVED) {
                String loved = resolveDialogueTextByKey(dialogueRoot, "AcceptBirthdayGift_Loved", currentDayKey());
                if (loved != null && !loved.isBlank()) {
                    return loved;
                }
            }

            // Personality-branched birthday responses (vanilla NPC.cs parity)
            boolean positive = (taste == GiftTaste.LOVED || taste == GiftTaste.LIKED || taste == GiftTaste.NEUTRAL);
            boolean negative = (taste == GiftTaste.DISLIKED || taste == GiftTaste.HATED);

            if (positive) {
                String birthdayKey = "AcceptBirthdayGift_Positive";
                String birthdayText = resolveDialogueTextByKey(dialogueRoot, birthdayKey, currentDayKey());
                if (birthdayText != null && !birthdayText.isBlank()) {
                    return "$h" + birthdayText;
                }
                // Manners-based fallback (vanilla NPC.cs:4274-4277)
                if (manners == NpcCapabilityProfile.MANNERS_RUDE) {
                    return "$h" + "stardewcraft.npc.generic.birthday.positive_rude";
                }
                return "$h" + "stardewcraft.npc.generic.birthday.positive";
            }
            if (negative) {
                String birthdayKey = "AcceptBirthdayGift_Negative";
                String birthdayText = resolveDialogueTextByKey(dialogueRoot, birthdayKey, currentDayKey());
                if (birthdayText != null && !birthdayText.isBlank()) {
                    return "$s" + birthdayText;
                }
                // Manners-based fallback (vanilla NPC.cs:4278-4279)
                if (manners == NpcCapabilityProfile.MANNERS_RUDE) {
                    return "$s" + "stardewcraft.npc.generic.birthday.negative_rude";
                }
                return "$s" + "stardewcraft.npc.generic.birthday.negative";
            }
            // Neutral birthday
            if (manners == NpcCapabilityProfile.MANNERS_RUDE) {
                return "stardewcraft.npc.generic.birthday.neutral_rude";
            }
            return "stardewcraft.npc.generic.birthday.neutral";
        }

        // Item-specific dialogue (e.g. AcceptGift_(O)StardropTea, AcceptGift_(O)66 for Amethyst)
        String itemSpecificKey = "AcceptGift_(O)" + stardewObjectToken(held);
        String itemSpecificText = resolveDialogueTextByKey(dialogueRoot, itemSpecificKey, currentDayKey());
        if (itemSpecificText != null && !itemSpecificText.isBlank()) {
            return itemSpecificText;
        }

        // NPC-specific taste response messages from taste data (vanilla parity)
        String tasteMsg = findNpcTasteMessage(npcId, taste);
        if (tasteMsg != null && !tasteMsg.isBlank()) {
            String emotionPrefix = switch (taste) {
                case LOVED, LIKED -> "$h";
                case HATED, DISLIKED -> "$s";
                case NEUTRAL -> "";
            };
            return emotionPrefix + tasteMsg;
        }

        // Generic fallback with portrait emotion tags
        return switch (taste) {
            case LOVED -> "$h" + "stardewcraft.npc.generic.gift.loved";
            case LIKED -> "$h" + "stardewcraft.npc.generic.gift.liked";
            case NEUTRAL -> "stardewcraft.npc.generic.gift.neutral";
            case DISLIKED -> "$s" + "stardewcraft.npc.generic.gift.disliked";
            case HATED -> "$s" + "stardewcraft.npc.generic.gift.hated";
        };
    }

    /**
     * Look up NPC-specific taste response message from the taste data table.
     * Vanilla NPCGiftTastes stores per-NPC messages for each taste level.
     * Our taste JSON supports optional "loved_msg", "liked_msg", "neutral_msg", "disliked_msg", "hated_msg" fields.
     */
    private static String findNpcTasteMessage(String npcId, GiftTaste taste) {
        JsonObject npcTastes = NpcDataRegistry.tastes().get(npcId);
        if (npcTastes == null) {
            return null;
        }
        String msgKey = switch (taste) {
            case LOVED -> "loved_msg";
            case LIKED -> "liked_msg";
            case NEUTRAL -> "neutral_msg";
            case DISLIKED -> "disliked_msg";
            case HATED -> "hated_msg";
        };
        if (npcTastes.has(msgKey) && npcTastes.get(msgKey).isJsonPrimitive()) {
            String msg = npcTastes.get(msgKey).getAsString();
            if (!msg.isBlank()) {
                // Wrap in translatable with NPC-specific key
                String langKey = "stardewcraft.npc." + npcId + ".gift_taste." + taste.name().toLowerCase(Locale.ROOT);
                return langKey;
            }
        }
        return null;
    }

    /**
     * Broadcast an emote bubble above the NPC entity based on gift taste.
     * Vanilla parity: loved → heart(20), liked → happy(32), hated → angry(12),
     * disliked → sad(28), neutral → no emote.
     */
    private static void broadcastGiftEmote(StardewNpcEntity npcEntity, GiftTaste taste) {
        EmoteType emote = switch (taste) {
            case LOVED -> EmoteCatalog.byId("heart");
            case LIKED -> EmoteCatalog.byId("happy");
            case HATED -> EmoteCatalog.byId("angry");
            case DISLIKED -> EmoteCatalog.byId("sad");
            case NEUTRAL -> null;
        };
        if (emote != null) {
            int baseIndex = EmoteCatalog.getBubbleBaseIndex(emote);
            PacketDistributor.sendToAllPlayers(new EmoteBroadcastPayload(npcEntity.getId(), baseIndex));
        }
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

    /** Vanilla quality multiplier: silver=1.1, gold=1.25, iridium=1.5, otherwise 1.0 */
    private static float qualityMultiplier(ItemStack held) {
        if (held.isEmpty()) return 1f;
        net.minecraft.world.item.component.CustomData customData =
            held.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                              net.minecraft.world.item.component.CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag tag = customData.copyTag();
        if (tag.contains("StardewQuality")) {
            int q = tag.getInt("StardewQuality");
            return switch (q) {
                case 1 -> 1.1f;  // silver
                case 2 -> 1.25f; // gold
                case 4 -> 1.5f;  // iridium
                default -> 1f;
            };
        }
        return 1f;
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
        if (entry.isJsonObject()) {
            JsonObject obj = entry.getAsJsonObject();
            if (obj.has("translate") && obj.get("translate").isJsonPrimitive()) {
                return obj.get("translate").getAsString();
            }
            return null;
        }
        if (entry.isJsonArray()) {
            JsonArray arr = entry.getAsJsonArray();
            if (arr.isEmpty()) {
                return null;
            }
            JsonElement el = arr.get(Math.floorMod(dayKey, arr.size()));
            return pickTextFromEntry(el, dayKey);
        }
        return null;
    }

    /**
     * Send dialogue to client. Accepts either a plain translate key or the
     * internal {@code tr::key::base64} format (strips it down to just the key).
     */
    private static void sendDialoguePacket(ServerPlayer player, String npcId, String translateKey, int points) {
        if (translateKey == null || translateKey.isBlank()) {
            translateKey = "...";
        }
        PacketDistributor.sendToPlayer(player,
                new OpenNpcDialogueScreenPayload(npcId, translateKey, points));
    }

    public static void handleClientQuestionAnswer(ServerPlayer player, String npcId, String nextDialogueNode, int friendshipDelta) {
        if (npcId == null || npcId.isBlank()) return;
        NpcFriendshipDataManager friendshipManager = NpcFriendshipDataManager.get((net.minecraft.server.level.ServerLevel) player.level());
        NpcFriendshipDataManager.FriendshipState state = friendshipManager.getOrCreate(player.getUUID(), npcId);
        
        if (friendshipDelta != 0) {
            state.addPoints(friendshipDelta, getMaxFriendshipPointsFor(npcId));
            friendshipManager.setDirty();
            DayContext dayContext = currentDayContext((net.minecraft.server.level.ServerLevel) player.level());
            syncFriendshipStatus(player, npcId, state, dayContext);
        }

        if (nextDialogueNode != null && !nextDialogueNode.isBlank() && !nextDialogueNode.equals("null")) {
            JsonObject dialogueRoot = NpcDataRegistry.dialogues().get(npcId);
            String text = resolveDialogueTextByKey(dialogueRoot, nextDialogueNode, currentDayKey());
            if (text != null && !text.isBlank()) {
                sendDialoguePacket(player, npcId, text, state.points());
            }
        }
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
