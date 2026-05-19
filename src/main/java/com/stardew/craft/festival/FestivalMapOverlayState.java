package com.stardew.craft.festival;

import net.minecraft.nbt.CompoundTag;

public final class FestivalMapOverlayState {
    private final String overlayId;
    private String festivalId;
    private int year;
    private int season;
    private int day;
    private FestivalMapOverlayPhase phase;
    private int cursor;

    public FestivalMapOverlayState(String overlayId) {
        this.overlayId = overlayId == null ? "" : overlayId;
        this.phase = FestivalMapOverlayPhase.NONE;
    }

    public String overlayId() {
        return overlayId;
    }

    public String festivalId() {
        return festivalId;
    }

    public int year() {
        return year;
    }

    public int season() {
        return season;
    }

    public int day() {
        return day;
    }

    public FestivalMapOverlayPhase phase() {
        return phase;
    }

    public int cursor() {
        return cursor;
    }

    public void begin(String festivalId, int year, int season, int day, FestivalMapOverlayPhase phase) {
        this.festivalId = festivalId == null ? "" : festivalId;
        this.year = year;
        this.season = season;
        this.day = day;
        this.phase = phase == null ? FestivalMapOverlayPhase.NONE : phase;
        this.cursor = 0;
    }

    public void setPhase(FestivalMapOverlayPhase phase) {
        this.phase = phase == null ? FestivalMapOverlayPhase.NONE : phase;
    }

    public void setCursor(int cursor) {
        this.cursor = Math.max(0, cursor);
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("OverlayId", overlayId);
        tag.putString("FestivalId", festivalId == null ? "" : festivalId);
        tag.putInt("Year", year);
        tag.putInt("Season", season);
        tag.putInt("Day", day);
        tag.putString("Phase", phase.name());
        tag.putInt("Cursor", cursor);
        return tag;
    }

    static FestivalMapOverlayState load(CompoundTag tag) {
        FestivalMapOverlayState state = new FestivalMapOverlayState(tag.getString("OverlayId"));
        state.festivalId = tag.getString("FestivalId");
        state.year = tag.getInt("Year");
        state.season = tag.getInt("Season");
        state.day = tag.getInt("Day");
        state.phase = parsePhase(tag.getString("Phase"));
        state.cursor = tag.getInt("Cursor");
        return state;
    }

    private static FestivalMapOverlayPhase parsePhase(String value) {
        try {
            return FestivalMapOverlayPhase.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return FestivalMapOverlayPhase.NONE;
        }
    }
}