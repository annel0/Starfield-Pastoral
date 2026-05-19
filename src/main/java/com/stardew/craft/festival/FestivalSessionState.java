package com.stardew.craft.festival;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class FestivalSessionState {
    private final String festivalId;
    private final int year;
    private final int season;
    private final int day;
    private FestivalSessionPhase phase;
    private FestivalMapOverlayPhase mapOverlayPhase;
    private final Set<UUID> participants = new LinkedHashSet<>();

    public FestivalSessionState(String festivalId, int year, int season, int day) {
        this(festivalId, year, season, day, FestivalSessionPhase.SCHEDULED, FestivalMapOverlayPhase.NONE);
    }

    private FestivalSessionState(String festivalId, int year, int season, int day,
                                 FestivalSessionPhase phase, FestivalMapOverlayPhase mapOverlayPhase) {
        this.festivalId = festivalId == null ? "" : festivalId;
        this.year = year;
        this.season = season;
        this.day = day;
        this.phase = phase == null ? FestivalSessionPhase.SCHEDULED : phase;
        this.mapOverlayPhase = mapOverlayPhase == null ? FestivalMapOverlayPhase.NONE : mapOverlayPhase;
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

    public FestivalSessionPhase phase() {
        return phase;
    }

    public void setPhase(FestivalSessionPhase phase) {
        this.phase = phase == null ? FestivalSessionPhase.SCHEDULED : phase;
    }

    public FestivalMapOverlayPhase mapOverlayPhase() {
        return mapOverlayPhase;
    }

    public void setMapOverlayPhase(FestivalMapOverlayPhase mapOverlayPhase) {
        this.mapOverlayPhase = mapOverlayPhase == null ? FestivalMapOverlayPhase.NONE : mapOverlayPhase;
    }

    public Set<UUID> participants() {
        return Set.copyOf(participants);
    }

    public void addParticipant(UUID playerId) {
        if (playerId != null) {
            participants.add(playerId);
        }
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("FestivalId", festivalId);
        tag.putInt("Year", year);
        tag.putInt("Season", season);
        tag.putInt("Day", day);
        tag.putString("Phase", phase.name());
        tag.putString("MapOverlayPhase", mapOverlayPhase.name());
        ListTag participantList = new ListTag();
        for (UUID participant : participants) {
            CompoundTag participantTag = new CompoundTag();
            participantTag.putUUID("Uuid", participant);
            participantList.add(participantTag);
        }
        tag.put("Participants", participantList);
        return tag;
    }

    static FestivalSessionState load(CompoundTag tag) {
        FestivalSessionState state = new FestivalSessionState(
            tag.getString("FestivalId"),
            tag.getInt("Year"),
            tag.getInt("Season"),
            tag.getInt("Day"),
            parsePhase(tag.getString("Phase")),
            parseOverlayPhase(tag.getString("MapOverlayPhase"))
        );
        ListTag participantList = tag.getList("Participants", Tag.TAG_COMPOUND);
        for (int i = 0; i < participantList.size(); i++) {
            CompoundTag participantTag = participantList.getCompound(i);
            if (participantTag.hasUUID("Uuid")) {
                state.addParticipant(participantTag.getUUID("Uuid"));
            }
        }
        return state;
    }

    private static FestivalSessionPhase parsePhase(String value) {
        try {
            return FestivalSessionPhase.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return FestivalSessionPhase.SCHEDULED;
        }
    }

    private static FestivalMapOverlayPhase parseOverlayPhase(String value) {
        try {
            return FestivalMapOverlayPhase.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return FestivalMapOverlayPhase.NONE;
        }
    }
}