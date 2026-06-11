package com.stardew.craft.specialorder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SpecialOrderInstance {
    private final String orderId;
    private final int generationDay;
    private final int dueDay;
    private final Map<String, Map<String, String>> randomValues = new HashMap<>();
    private final List<ObjectiveState> objectives = new ArrayList<>();
    private final List<DonatedItem> donatedItems = new ArrayList<>();
    private final List<UUID> participants = new ArrayList<>();
    private final List<UUID> rewardClaimedBy = new ArrayList<>();
    private boolean accepted;
    private boolean complete;
    private boolean failed;
    private boolean rewardClaimed;

    public SpecialOrderInstance(String orderId, int generationDay, int dueDay) {
        this.orderId = orderId;
        this.generationDay = generationDay;
        this.dueDay = dueDay;
    }

    public static SpecialOrderInstance create(SpecialOrderDefinition definition, int generationDay, int dueDay) {
        SpecialOrderInstance instance = new SpecialOrderInstance(definition.id(), generationDay, dueDay);
        for (SpecialOrderDefinition.ObjectiveDefinition objective : definition.objectives()) {
            instance.objectives.add(new ObjectiveState(objective.requiredCount()));
        }
        return instance;
    }

    public String orderId() { return orderId; }
    public int generationDay() { return generationDay; }
    public int dueDay() { return dueDay; }
    public Map<String, Map<String, String>> randomValues() { return randomValues; }
    public List<ObjectiveState> objectives() { return objectives; }
    public List<DonatedItem> donatedItems() { return donatedItems; }
    public boolean accepted() { return accepted; }
    public boolean complete() { return complete; }
    public boolean failed() { return failed; }
    public boolean rewardClaimed() { return rewardClaimed; }
    public List<UUID> participants() { return participants; }
    public List<UUID> rewardClaimedBy() { return rewardClaimedBy; }

    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public void setComplete(boolean complete) { this.complete = complete; }
    public void setFailed(boolean failed) { this.failed = failed; }
    public void setRewardClaimed(boolean rewardClaimed) { this.rewardClaimed = rewardClaimed; }

    public int daysLeft(int absoluteDay) {
        return Math.max(0, dueDay - absoluteDay);
    }

    public boolean addParticipant(UUID playerId) {
        if (!participants.contains(playerId)) {
            participants.add(playerId);
            return true;
        }
        return false;
    }

    public boolean hasUnclaimedReward(UUID playerId) {
        return complete && participants.contains(playerId) && !rewardClaimedBy.contains(playerId);
    }

    public boolean markRewardClaimed(UUID playerId) {
        if (!rewardClaimedBy.contains(playerId)) {
            rewardClaimedBy.add(playerId);
            rewardClaimed = true;
            return true;
        }
        return false;
    }

    public boolean allParticipantRewardsClaimed() {
        return participants.isEmpty() || rewardClaimedBy.containsAll(participants);
    }

    public boolean isObjectivesComplete() {
        for (ObjectiveState state : objectives) {
            if (!state.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("OrderId", orderId);
        tag.putInt("GenerationDay", generationDay);
        tag.putInt("DueDay", dueDay);
        tag.putBoolean("Accepted", accepted);
        tag.putBoolean("Complete", complete);
        tag.putBoolean("Failed", failed);
        tag.putBoolean("RewardClaimed", rewardClaimed);

        CompoundTag randomTag = new CompoundTag();
        for (Map.Entry<String, Map<String, String>> entry : randomValues.entrySet()) {
            CompoundTag values = new CompoundTag();
            for (Map.Entry<String, String> value : entry.getValue().entrySet()) {
                values.putString(value.getKey(), value.getValue());
            }
            randomTag.put(entry.getKey(), values);
        }
        tag.put("RandomValues", randomTag);

        ListTag objectiveList = new ListTag();
        for (ObjectiveState objective : objectives) {
            objectiveList.add(objective.save());
        }
        tag.put("Objectives", objectiveList);

        ListTag donatedList = new ListTag();
        for (DonatedItem item : donatedItems) {
            donatedList.add(item.save());
        }
        tag.put("DonatedItems", donatedList);

        ListTag participantList = new ListTag();
        for (UUID participant : participants) {
            participantList.add(StringTag.valueOf(participant.toString()));
        }
        tag.put("Participants", participantList);

        ListTag rewardClaimedByList = new ListTag();
        for (UUID playerId : rewardClaimedBy) {
            rewardClaimedByList.add(StringTag.valueOf(playerId.toString()));
        }
        tag.put("RewardClaimedBy", rewardClaimedByList);
        return tag;
    }

    public static SpecialOrderInstance load(CompoundTag tag) {
        SpecialOrderInstance instance = new SpecialOrderInstance(
            tag.getString("OrderId"),
            tag.getInt("GenerationDay"),
            tag.getInt("DueDay")
        );
        instance.accepted = tag.getBoolean("Accepted");
        instance.complete = tag.getBoolean("Complete");
        instance.failed = tag.getBoolean("Failed");
        instance.rewardClaimed = tag.getBoolean("RewardClaimed");

        CompoundTag randomTag = tag.getCompound("RandomValues");
        for (String element : randomTag.getAllKeys()) {
            CompoundTag values = randomTag.getCompound(element);
            Map<String, String> map = new HashMap<>();
            for (String key : values.getAllKeys()) {
                map.put(key, values.getString(key));
            }
            instance.randomValues.put(element, map);
        }

        ListTag objectiveList = tag.getList("Objectives", 10);
        for (int i = 0; i < objectiveList.size(); i++) {
            instance.objectives.add(ObjectiveState.load(objectiveList.getCompound(i)));
        }
        ListTag donatedList = tag.getList("DonatedItems", 10);
        for (int i = 0; i < donatedList.size(); i++) {
            instance.donatedItems.add(DonatedItem.load(donatedList.getCompound(i)));
        }
        ListTag participantList = tag.getList("Participants", 8);
        for (int i = 0; i < participantList.size(); i++) {
            try {
                instance.participants.add(UUID.fromString(participantList.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        ListTag rewardClaimedByList = tag.getList("RewardClaimedBy", 8);
        for (int i = 0; i < rewardClaimedByList.size(); i++) {
            try {
                instance.rewardClaimedBy.add(UUID.fromString(rewardClaimedByList.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (instance.rewardClaimed && instance.rewardClaimedBy.isEmpty()) {
            instance.rewardClaimedBy.addAll(instance.participants);
        }
        return instance;
    }

    public static final class ObjectiveState {
        private int progress;
        private int requiredCount;

        public ObjectiveState(int requiredCount) {
            this.requiredCount = Math.max(1, requiredCount);
        }

        public int progress() { return progress; }
        public int requiredCount() { return requiredCount; }
        public boolean isComplete() { return progress >= requiredCount; }

        public boolean add(int amount) {
            int before = progress;
            progress = Math.min(requiredCount, Math.max(0, progress + amount));
            return progress != before;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Progress", progress);
            tag.putInt("RequiredCount", requiredCount);
            return tag;
        }

        public static ObjectiveState load(CompoundTag tag) {
            ObjectiveState state = new ObjectiveState(tag.getInt("RequiredCount"));
            state.progress = Math.max(0, tag.getInt("Progress"));
            return state;
        }
    }

    public record DonatedItem(String itemId, int count) {
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putString("ItemId", itemId);
            tag.putInt("Count", count);
            return tag;
        }

        public static DonatedItem load(CompoundTag tag) {
            return new DonatedItem(tag.getString("ItemId"), Math.max(0, tag.getInt("Count")));
        }
    }
}
