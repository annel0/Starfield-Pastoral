package com.stardew.craft.specialorder;

import java.util.List;
import java.util.Map;

public record SpecialOrderDefinition(
    String id,
    String requester,
    Duration duration,
    boolean repeatable,
    List<String> requiredTags,
    String titleKey,
    String textKey,
    List<ObjectiveDefinition> objectives,
    List<RewardDefinition> rewards,
    List<RandomElement> randomElements,
    String itemToRemoveOnEnd,
    String mailToRemoveOnEnd
) {
    public enum Duration {
        WEEK,
        TWO_WEEKS,
        MONTH
    }

    public enum ObjectiveType {
        COLLECT,
        DONATE,
        DELIVER,
        FISH,
        SHIP,
        SLAY
    }

    public enum RewardType {
        MONEY,
        MAIL,
        FRIENDSHIP
    }

    public record ObjectiveDefinition(
        ObjectiveType type,
        String textKey,
        int requiredCount,
        String acceptedTags,
        String dropBoxId,
        String targetName,
        int minimumCapacity,
        String messageKey
    ) {
    }

    public record RewardDefinition(
        RewardType type,
        int amount,
        String mailId,
        boolean noLetter,
        boolean host
    ) {
    }

    public record RandomElement(
        String name,
        List<RandomOption> options
    ) {
    }

    public record RandomOption(
        List<String> requiredTags,
        Map<String, String> values
    ) {
    }
}
