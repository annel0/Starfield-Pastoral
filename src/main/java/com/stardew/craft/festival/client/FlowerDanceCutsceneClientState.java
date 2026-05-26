package com.stardew.craft.festival.client;

import java.util.List;
import java.util.UUID;

public final class FlowerDanceCutsceneClientState {
    private static List<DancePair> pairs = List.of();
    private static List<Partner> spectators = List.of();

    private FlowerDanceCutsceneClientState() {
    }

    public static void set(List<DancePair> newPairs, List<Partner> newSpectators) {
        pairs = newPairs == null ? List.of() : List.copyOf(newPairs);
        spectators = newSpectators == null ? List.of() : List.copyOf(newSpectators);
    }

    public static List<DancePair> pairs() {
        return pairs;
    }

    public static List<Partner> spectators() {
        return spectators;
    }

    public record DancePair(Partner femaleSide, Partner maleSide) {
    }

    public record Partner(Kind kind, String npcId, UUID playerId) {
        public enum Kind {
            NPC,
            PLAYER
        }

        public static Partner npc(String npcId) {
            return new Partner(Kind.NPC, npcId == null ? "" : npcId, null);
        }

        public static Partner player(UUID playerId) {
            return new Partner(Kind.PLAYER, "", playerId);
        }
    }
}
