package com.stardew.craft.animal.model;

public enum AnimalBuildingType {
    COOP_TIER_1("coop", 1, 4, 0),
    COOP_TIER_2("coop", 2, 8, 0),
    COOP_TIER_3("coop", 3, 12, 0),
    BARN_TIER_1("barn", 1, 4, 0),
    BARN_TIER_2("barn", 2, 8, 0),
    BARN_TIER_3("barn", 3, 12, 0),
    SILO_TIER_1("silo", 1, 0, 240);

    private final String family;
    private final int tier;
    private final int defaultCapacity;
    private final int hayCapacity;

    AnimalBuildingType(String family, int tier, int defaultCapacity, int hayCapacity) {
        this.family = family;
        this.tier = tier;
        this.defaultCapacity = defaultCapacity;
        this.hayCapacity = hayCapacity;
    }

    public String family() {
        return family;
    }

    public int tier() {
        return tier;
    }

    public int defaultCapacity() {
        return defaultCapacity;
    }

    public int hayCapacity() {
        return hayCapacity;
    }

    public String id() {
        return family + "_tier_" + tier;
    }

    public static AnimalBuildingType of(String family, int tier) {
        for (AnimalBuildingType value : values()) {
            if (value.family.equalsIgnoreCase(family) && value.tier == tier) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown building type: family=" + family + ", tier=" + tier);
    }

    public static AnimalBuildingType fromId(String id) {
        for (AnimalBuildingType value : values()) {
            if (value.id().equalsIgnoreCase(id)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown building type id: " + id);
    }
}
