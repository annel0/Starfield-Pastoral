package com.stardew.craft.client.gui.specialorder;

import com.stardew.craft.quest.StardewQuest;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SpecialOrderQuestView extends StardewQuest {
    private static final Pattern TOKEN = Pattern.compile("\\{([A-Za-z0-9_]+)(?::([A-Za-z0-9_]+))?}");

    private final CompoundTag order;
    private final List<ObjectiveView> objectives = new ArrayList<>();

    public SpecialOrderQuestView(CompoundTag order) {
        super(order.getString("Id"));
        this.order = order.copy();
        this.id = order.getString("Id");
        this.daysLeft = order.getInt("DaysLeft");
        this.completed = order.getBoolean("Complete");
        this.moneyReward = order.getInt("RewardMoney");
        this.accepted = order.getBoolean("Accepted");
        this.canBeCancelled = false;
        ListTag objectiveTags = order.getList("Objectives", 10);
        for (int i = 0; i < objectiveTags.size(); i++) {
            objectives.add(new ObjectiveView(objectiveTags.getCompound(i)));
        }
    }

    @Override
    public Component getTitleComponent() {
        return Component.literal(resolve(I18n.get(order.getString("TitleKey"))));
    }

    @Override
    public Component getDescriptionComponent() {
        return Component.literal(resolve(I18n.get(order.getString("TextKey"))).trim());
    }

    @Override
    public List<Component> getObjectiveComponents() {
        List<Component> out = new ArrayList<>();
        for (ObjectiveView objective : objectives) {
            out.add(Component.literal(resolve(I18n.get(objective.textKey()))));
        }
        return out;
    }

    @Override
    public boolean shouldDisplayAsNew() {
        return !completed && !ClientSpecialOrderBoardData.isViewed(id);
    }

    @Override
    public boolean shouldDisplayAsComplete() {
        return completed;
    }

    @Override
    public boolean isTimedQuest() {
        return daysLeft > 0;
    }

    @Override
    public boolean hasReward() {
        return moneyReward > 0;
    }

    @Override
    public boolean hasMoneyReward() {
        return moneyReward > 0;
    }

    @Override
    public int getCurrentObjectiveCount() {
        int total = 0;
        for (ObjectiveView objective : objectives) {
            total += objective.progress();
        }
        return total;
    }

    @Override
    public int getTotalObjectiveCount() {
        int total = 0;
        for (ObjectiveView objective : objectives) {
            total += objective.required();
        }
        return total;
    }

    public int objectiveCount() {
        return objectives.size();
    }

    public boolean isObjectiveComplete(int index) {
        return index >= 0 && index < objectives.size() && objectives.get(index).isComplete();
    }

    public boolean shouldShowProgress(int index) {
        return index >= 0 && index < objectives.size() && objectives.get(index).required() > 1;
    }

    public int objectiveProgress(int index) {
        return index >= 0 && index < objectives.size() ? objectives.get(index).progress() : 0;
    }

    public int objectiveRequired(int index) {
        return index >= 0 && index < objectives.size() ? objectives.get(index).required() : 0;
    }

    private String resolve(String text) {
        String out = text == null ? "" : text;
        for (int i = 0; i < 4; i++) {
            String resolved = resolveOnce(out);
            if (resolved.equals(out)) {
                return resolved;
            }
            out = resolved;
        }
        return out;
    }

    private String resolveOnce(String text) {
        Map<String, Map<String, String>> randomValues = new LinkedHashMap<>();
        CompoundTag randomTag = order.getCompound("RandomValues");
        for (String element : randomTag.getAllKeys()) {
            CompoundTag values = randomTag.getCompound(element);
            Map<String, String> map = new LinkedHashMap<>();
            for (String key : values.getAllKeys()) {
                String value = values.getString(key);
                map.put(key, value.startsWith("stardewcraft.") || value.startsWith("item.") ? I18n.get(value) : value);
            }
            randomValues.put(element, map);
        }
        Matcher matcher = TOKEN.matcher(text);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            Map<String, String> values = randomValues.get(matcher.group(1));
            String field = matcher.group(2);
            if (field == null || field.isBlank()) {
                field = "Text";
            }
            String replacement = values == null ? "" : values.getOrDefault(field, values.getOrDefault(matcher.group(1), ""));
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private record ObjectiveView(String textKey, int progress, int required) {
        ObjectiveView(CompoundTag tag) {
            this(tag.getString("TextKey"), tag.getInt("Progress"), tag.getInt("Required"));
        }

        boolean isComplete() {
            return required <= 0 || progress >= required;
        }
    }
}
