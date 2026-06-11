package com.stardew.craft.specialorder;

import com.stardew.craft.StardewCraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.stardew.craft.specialorder.SpecialOrderDefinition.Duration.MONTH;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.Duration.TWO_WEEKS;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.Duration.WEEK;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.ObjectiveType.COLLECT;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.ObjectiveType.DELIVER;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.ObjectiveType.DONATE;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.ObjectiveType.FISH;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.ObjectiveType.SHIP;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.ObjectiveType.SLAY;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.RewardType.FRIENDSHIP;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.RewardType.MAIL;
import static com.stardew.craft.specialorder.SpecialOrderDefinition.RewardType.MONEY;

public final class SpecialOrderDefinitions {
    private static final Map<String, SpecialOrderDefinition> DEFINITIONS = build();

    private SpecialOrderDefinitions() {
    }

    public static List<SpecialOrderDefinition> all() {
        return List.copyOf(DEFINITIONS.values());
    }

    public static SpecialOrderDefinition get(String id) {
        return DEFINITIONS.get(id);
    }

    private static Map<String, SpecialOrderDefinition> build() {
        Map<String, SpecialOrderDefinition> out = new LinkedHashMap<>();
        add(out, order("Willy", "Willy", WEEK, false, List.of(), List.of(),
            List.of(
                obj(COLLECT, "Willy_Objective_0_Text", 100, "item_bug_meat"),
                donate("Willy_Objective_1_Text", 100, "item_bug_meat", "WillyBarrel", 0)
            ),
            List.of(money(3000), mailFlag("willyBugWadCutscene")), null, null));
        add(out, order("Pam", "Pam", TWO_WEEKS, false, List.of("season_spring"), List.of(),
            List.of(
                obj(COLLECT, "Pam_Objective_0_Text", 12, "juice_item, preserve_sheet_index_192"),
                donate("Pam_Objective_1_Text", 12, "juice_item, preserve_sheet_index_192", "PamKitchen", 0)
            ),
            List.of(money(3000), mailFlag("pamPotatoJuice"), friendship()), null, null));
        add(out, order("Pierre", "Pierre", MONTH, false, List.of(), List.of(),
            List.of(
                obj(COLLECT, "Pierre_Objective_0_Text", 25, "!forage_item, category_vegetable, quality_gold"),
                donate("Pierre_Objective_1_Text", 25, "!forage_item, category_vegetable, quality_gold", "PierreBox", 0)
            ),
            List.of(money(2500), mailFlag("pierreQualityCrops")), null, null));
        add(out, order("Robin", "Robin", WEEK, false, List.of(), List.of(),
            List.of(
                obj(COLLECT, "Robin_Objective_0_Text", 80, "item_hardwood"),
                donate("Robin_Objective_1_Text", 80, "item_hardwood", "RobinWood", 0)
            ),
            List.of(money(2000), mailFlag("robinDeluxeBed"), friendship()), null, null));
        add(out, order("Emily", "Emily", WEEK, false, List.of("event_992559"), List.of(),
            List.of(
                deliver("Emily_Objective_0_Text", "item_ruby", "Emily", null),
                deliver("Emily_Objective_1_Text", "item_topaz", "Emily", null),
                deliver("Emily_Objective_2_Text", "item_emerald", "Emily", null),
                deliver("Emily_Objective_3_Text", "item_jade", "Emily", null),
                deliver("Emily_Objective_4_Text", "item_amethyst", "Emily", null)
            ),
            List.of(money(1000), friendship(), mailReadable("emilyStones")), null, null));
        add(out, order("Demetrius", "Demetrius", WEEK, true, List.of(), fishTypeBySeason(), List.of(
            obj(FISH, "Demetrius_Objective_0_Text", 10, "{FishType:Tags}")
        ), List.of(moneyTemplate("{FishType:Price}", 10), mailReadable("DemetriusReward")), null, null));
        add(out, order("Demetrius2", "Demetrius", WEEK, true, List.of(), biomeFishType(), List.of(
            obj(FISH, "Demetrius2_Objective_0_Text", 20, "{FishType:Tags}")
        ), List.of(money(1500), mailReadable("DemetriusReward")), null, null));
        add(out, order("Gus", "Gus", TWO_WEEKS, true, List.of(), gusGreeting(), List.of(
            obj(COLLECT, "Gus_Objective_0_Text", 24, "egg_item"),
            donate("Gus_Objective_1_Text", 24, "egg_item", "GusFridge", 30)
        ), List.of(money(3000), mailReadable("gusGiantOmelet")), null, null));
        add(out, order("Lewis", "Lewis", MONTH, true, List.of("!season_winter"), lewisRandoms(), List.of(
            obj(COLLECT, "Lewis_Objective_1_Text", 100, "{Crop:Tags}"),
            obj(SHIP, "Lewis_Objective_0_Text", 100, "{Crop:Tags}")
        ), List.of(moneyTemplate("{Crop:Price}", 50), mailReadableHost("MSB_Lewis")), null, null));
        add(out, order("Wizard", "Wizard", WEEK, false, List.of(), List.of(), List.of(
            deliver("Wizard_Objective_0_Text", "item_ectoplasm", "Wizard", "Wizard_Objective_0_Message")
        ), List.of(money(2500), mailReadable("WizardReward2")), "stardewcraft:ectoplasm", "ectoplasmDrop"));
        add(out, order("Clint", "Clint", WEEK, true, List.of(), clintMonsters(), List.of(
            slay("Clint_Objective_0_Text", 50, "{Monster:Target}")
        ), List.of(money(6000), mailReadable("ClintReward")), null, null));
        add(out, order("Linus", "Linus", WEEK, false, List.of(), List.of(), List.of(
            obj(COLLECT, "Linus_Objective_0_Text", 20, "trash_item"),
            donate("Linus_Objective_1_Text", 20, "trash_item", "Dumpster", 0)
        ), List.of(money(500), mailReadable("linusTrashCleanup"), friendship()), null, null));
        add(out, order("Evelyn", "Evelyn", MONTH, false, List.of("season_spring"), List.of(), List.of(
            obj(COLLECT, "Evelyn_Objective_0_Text", 12, "item_leek"),
            donate("Evelyn_Objective_1_Text", 12, "item_leek", "EvelynKitchen", 0)
        ), List.of(money(2000), mailReadable("georgeGifts")), null, null));
        add(out, order("Wizard2", "Wizard", WEEK, false, List.of(), List.of(), List.of(
            slay("Wizard2_Objective_0_Text", 1, "Prismatic Slime"),
            deliver("Wizard2_Objective_1_Text", "item_prismatic_jelly", "Wizard", "Wizard2_Objective_1_Message")
        ), List.of(money(5000), mailReadable("WizardReward")), "stardewcraft:prismatic_jelly", "prismaticJellyDrop"));
        add(out, order("Robin2", "Robin", WEEK, true, List.of(), robinResource(), List.of(
            obj(COLLECT, "Robin2_Objective_0_Text", 1000, "{Resource:Tags}")
        ), List.of(money(2500), mailReadable("robinResource")), null, null));
        add(out, order("Gunther", "Gunther", WEEK, false, List.of(), List.of(), List.of(
            obj(COLLECT, "Gunther_Objective_1_Text", 100, "bone_item"),
            donate("Gunther_Objective_0_Text", 100, "bone_item", "GuntherBox", 0)
        ), List.of(money(3500), mailReadable("guntherBones")), null, null));
        return out;
    }

    private static void add(Map<String, SpecialOrderDefinition> out, SpecialOrderDefinition definition) {
        out.put(definition.id(), definition);
    }

    private static SpecialOrderDefinition order(String id, String requester, SpecialOrderDefinition.Duration duration,
                                                boolean repeatable, List<String> requiredTags,
                                                List<SpecialOrderDefinition.RandomElement> randomElements,
                                                List<SpecialOrderDefinition.ObjectiveDefinition> objectives,
                                                List<SpecialOrderDefinition.RewardDefinition> rewards,
                                                String itemToRemoveOnEnd, String mailToRemoveOnEnd) {
        return new SpecialOrderDefinition(id, requester, duration, repeatable, requiredTags,
            "stardewcraft.special_orders." + id + ".name",
            "stardewcraft.special_orders." + id + ".text",
            objectives, rewards, randomElements, itemToRemoveOnEnd, mailToRemoveOnEnd);
    }

    private static SpecialOrderDefinition.ObjectiveDefinition obj(SpecialOrderDefinition.ObjectiveType type, String textKey, int count, String tags) {
        return new SpecialOrderDefinition.ObjectiveDefinition(type, "stardewcraft.special_orders." + textKey, count, tags, "", "", 0, "");
    }

    private static SpecialOrderDefinition.ObjectiveDefinition donate(String textKey, int count, String tags, String dropBox, int minimumCapacity) {
        return new SpecialOrderDefinition.ObjectiveDefinition(DONATE, "stardewcraft.special_orders." + textKey, count, tags, dropBox, "", minimumCapacity, "");
    }

    private static SpecialOrderDefinition.ObjectiveDefinition deliver(String textKey, String tags, String target, String messageKey) {
        return new SpecialOrderDefinition.ObjectiveDefinition(DELIVER, "stardewcraft.special_orders." + textKey, 1, tags, "", target, 0,
            messageKey == null ? "" : "stardewcraft.special_orders." + messageKey);
    }

    private static SpecialOrderDefinition.ObjectiveDefinition slay(String textKey, int count, String target) {
        return new SpecialOrderDefinition.ObjectiveDefinition(SLAY, "stardewcraft.special_orders." + textKey, count, "", "", target, 0, "");
    }

    private static SpecialOrderDefinition.RewardDefinition money(int amount) {
        return new SpecialOrderDefinition.RewardDefinition(MONEY, amount, "", false, false);
    }

    private static SpecialOrderDefinition.RewardDefinition moneyTemplate(String template, int multiplier) {
        return new SpecialOrderDefinition.RewardDefinition(MONEY, -multiplier, template, false, false);
    }

    private static SpecialOrderDefinition.RewardDefinition mailFlag(String mailId) {
        return new SpecialOrderDefinition.RewardDefinition(MAIL, 0, mailId, true, false);
    }

    private static SpecialOrderDefinition.RewardDefinition mailReadable(String mailId) {
        return new SpecialOrderDefinition.RewardDefinition(MAIL, 0, mailId, false, false);
    }

    private static SpecialOrderDefinition.RewardDefinition mailReadableHost(String mailId) {
        return new SpecialOrderDefinition.RewardDefinition(MAIL, 0, mailId, false, true);
    }

    private static SpecialOrderDefinition.RewardDefinition friendship() {
        return new SpecialOrderDefinition.RewardDefinition(FRIENDSHIP, 250, "", false, false);
    }

    private static List<SpecialOrderDefinition.RandomElement> fishTypeBySeason() {
        return List.of(new SpecialOrderDefinition.RandomElement("FishType", List.of(
            option(List.of("season_spring"), itemOptions("Sunfish", "sunfish", 30, "Sardine", "sardine", 40, "Flounder", "flounder", 100, "Largemouth Bass", "largemouth_bass", 100, "Halibut", "halibut", 80)),
            option(List.of("season_summer"), itemOptions("Rainbow Trout", "rainbow_trout", 65, "Dorado", "dorado", 100, "Tilapia", "tilapia", 75, "Tuna", "tuna", 100, "Red Mullet", "red_mullet", 75)),
            option(List.of("season_fall"), itemOptions("Tiger Trout", "tiger_trout", 150, "Albacore", "albacore", 75, "Midnight Carp", "midnight_carp", 150, "Salmon", "salmon", 75)),
            option(List.of("season_winter"), itemOptions("Squid", "squid", 80, "Perch", "perch", 55, "Lingcod", "lingcod", 120))
        )));
    }

    private static List<SpecialOrderDefinition.RandomElement> biomeFishType() {
        return List.of(new SpecialOrderDefinition.RandomElement("FishType", List.of(
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Demetrius2_RE_FishType_0_Text", "Tags", "fish_river")),
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Demetrius2_RE_FishType_1_Text", "Tags", "fish_ocean")),
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Demetrius2_RE_FishType_2_Text", "Tags", "fish_lake"))
        )));
    }

    private static List<SpecialOrderDefinition.RandomElement> gusGreeting() {
        return List.of(new SpecialOrderDefinition.RandomElement("Greeting", List.of(
            option(List.of("!mail_gusGiantOmelet"), Map.of("Text", "stardewcraft.special_orders.Gus_RE_Greeting_0", "Greeting", "stardewcraft.special_orders.Gus_RE_Greeting_0")),
            option(List.of("mail_gusGiantOmelet"), Map.of("Text", "stardewcraft.special_orders.Gus_RE_Greeting_1", "Greeting", "stardewcraft.special_orders.Gus_RE_Greeting_1"))
        )));
    }

    private static List<SpecialOrderDefinition.RandomElement> lewisRandoms() {
        List<SpecialOrderDefinition.RandomElement> out = new ArrayList<>();
        out.add(new SpecialOrderDefinition.RandomElement("Crop", List.of(
            option(List.of("season_spring"), itemOptions("Potato", "potato", 80, "Green Bean", "green_bean", 40, "Garlic", "garlic", 60, "Cauliflower", "cauliflower", 175)),
            option(List.of("season_summer"), itemOptions("Tomato", "tomato", 60, "Blueberry", "blueberry", 50, "Radish", "radish", 90, "Melon", "melon", 250, "Hot Pepper", "hot_pepper", 40, "Wheat", "wheat", 25)),
            option(List.of("season_fall"), itemOptions("Pumpkin", "pumpkin", 320, "Eggplant", "eggplant", 60, "Cranberries", "cranberries", 75, "Bok Choy", "bok_choy", 80, "Amaranth", "amaranth", 150, "Grape", "grape", 80, "Yam", "yam", 160, "Artichoke", "artichoke", 160))
        )));
        out.add(new SpecialOrderDefinition.RandomElement("Text", List.of(
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Lewis_RE_Text_0")),
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Lewis_RE_Text_1")),
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Lewis_RE_Text_2")),
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Lewis_RE_Text_3")),
            option(List.of(), Map.of("Text", "stardewcraft.special_orders.Lewis_RE_Text_4"))
        )));
        return out;
    }

    private static List<SpecialOrderDefinition.RandomElement> clintMonsters() {
        return List.of(new SpecialOrderDefinition.RandomElement("Monster", List.of(
            option(List.of(), Map.of("Target", "Bat", "LocalizedName", "stardewcraft.special_orders.Clint_RE_Monster_0_LocalizedName")),
            option(List.of(), Map.of("Target", "Dust Spirit", "LocalizedName", "stardewcraft.special_orders.Clint_RE_Monster_1_LocalizedName")),
            option(List.of(), Map.of("Target", "Skeleton", "LocalizedName", "stardewcraft.special_orders.Clint_RE_Monster_2_LocalizedName")),
            option(List.of(), Map.of("Target", "Grub", "LocalizedName", "stardewcraft.special_orders.Clint_RE_Monster_3_LocalizedName"))
        )));
    }

    private static List<SpecialOrderDefinition.RandomElement> robinResource() {
        return List.of(new SpecialOrderDefinition.RandomElement("Resource", List.of(
            option(List.of(), itemOptions("Wood", "wood_normal", 2, "Stone", "stone", 2))
        )));
    }

    private static SpecialOrderDefinition.RandomOption option(List<String> tags, Map<String, String> values) {
        return new SpecialOrderDefinition.RandomOption(tags, values);
    }

    private static Map<String, String> itemOptions(Object... flat) {
        Map<String, String> out = new LinkedHashMap<>();
        int index = 0;
        for (int i = 0; i + 2 < flat.length; i += 3) {
            String text = String.valueOf(flat[i]);
            String id = String.valueOf(flat[i + 1]);
            String price = String.valueOf(flat[i + 2]);
            out.put("option." + index + ".Text", "item." + StardewCraft.MODID + "." + id);
            out.put("option." + index + ".TextPlural", "stardewcraft.special_orders.random." + id + ".plural");
            out.put("option." + index + ".TextPluralCapitalized", "stardewcraft.special_orders.random." + id + ".plural_capitalized");
            out.put("option." + index + ".Tags", "item_" + id);
            out.put("option." + index + ".Price", price);
            out.put("option." + index + ".ItemId", StardewCraft.MODID + ":" + id);
            index++;
        }
        out.put("OptionCount", String.valueOf(index));
        return out;
    }
}
