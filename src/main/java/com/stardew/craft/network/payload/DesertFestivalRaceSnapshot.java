package com.stardew.craft.network.payload;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record DesertFestivalRaceSnapshot(String raceState,
                                         int timerTicks,
                                         int currentTime,
                                         int eggCount,
                                         int lastWinner,
                                         int activeGuess,
                                         int nextGuess,
                                         int sabotageTarget,
                                         int rewardClaims,
                                         boolean specialRewardPending,
                                         boolean canGuess,
                                         String announceKey,
                                         List<RacerEntry> racers,
                                         List<RoomEntry> rooms) {
    public record RacerEntry(int racerIndex, float x, float y, int direction, boolean frame, boolean jumping,
                             boolean tripping, boolean drawAboveMap, float height, float progress, int sabotages) {
    }

    public record RoomEntry(String roomId, String hostName, boolean host, boolean open, boolean settled,
                            int winningRacer, int totalPool, int bettorCount, int playerRacer,
                            int playerAmount, int playerPayout, boolean playerClaimed, List<OddsEntry> odds) {
    }

    public record OddsEntry(int racerIndex, int pool, int projectedPayout) {
    }

    public static void write(FriendlyByteBuf buf, DesertFestivalRaceSnapshot snapshot) {
        buf.writeUtf(snapshot.raceState());
        buf.writeVarInt(snapshot.timerTicks());
        buf.writeVarInt(snapshot.currentTime());
        buf.writeVarInt(snapshot.eggCount());
        buf.writeVarInt(snapshot.lastWinner());
        buf.writeVarInt(snapshot.activeGuess());
        buf.writeVarInt(snapshot.nextGuess());
        buf.writeVarInt(snapshot.sabotageTarget());
        buf.writeVarInt(snapshot.rewardClaims());
        buf.writeBoolean(snapshot.specialRewardPending());
        buf.writeBoolean(snapshot.canGuess());
        buf.writeUtf(snapshot.announceKey());
        buf.writeVarInt(snapshot.racers().size());
        for (RacerEntry racer : snapshot.racers()) {
            buf.writeVarInt(racer.racerIndex());
            buf.writeFloat(racer.x());
            buf.writeFloat(racer.y());
            buf.writeVarInt(racer.direction());
            buf.writeBoolean(racer.frame());
            buf.writeBoolean(racer.jumping());
            buf.writeBoolean(racer.tripping());
            buf.writeBoolean(racer.drawAboveMap());
            buf.writeFloat(racer.height());
            buf.writeFloat(racer.progress());
            buf.writeVarInt(racer.sabotages());
        }
        buf.writeVarInt(snapshot.rooms().size());
        for (RoomEntry room : snapshot.rooms()) {
            buf.writeUtf(room.roomId());
            buf.writeUtf(room.hostName());
            buf.writeBoolean(room.host());
            buf.writeBoolean(room.open());
            buf.writeBoolean(room.settled());
            buf.writeVarInt(room.winningRacer());
            buf.writeVarInt(room.totalPool());
            buf.writeVarInt(room.bettorCount());
            buf.writeVarInt(room.playerRacer());
            buf.writeVarInt(room.playerAmount());
            buf.writeVarInt(room.playerPayout());
            buf.writeBoolean(room.playerClaimed());
            buf.writeVarInt(room.odds().size());
            for (OddsEntry odds : room.odds()) {
                buf.writeVarInt(odds.racerIndex());
                buf.writeVarInt(odds.pool());
                buf.writeVarInt(odds.projectedPayout());
            }
        }
    }

    public static DesertFestivalRaceSnapshot read(FriendlyByteBuf buf) {
        String raceState = buf.readUtf();
        int timerTicks = buf.readVarInt();
        int currentTime = buf.readVarInt();
        int eggCount = buf.readVarInt();
        int lastWinner = buf.readVarInt();
        int activeGuess = buf.readVarInt();
        int nextGuess = buf.readVarInt();
        int sabotageTarget = buf.readVarInt();
        int rewardClaims = buf.readVarInt();
        boolean specialRewardPending = buf.readBoolean();
        boolean canGuess = buf.readBoolean();
        String announceKey = buf.readUtf();
        int racerCount = buf.readVarInt();
        List<RacerEntry> racers = new ArrayList<>(racerCount);
        for (int i = 0; i < racerCount; i++) {
            racers.add(new RacerEntry(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readVarInt(),
                buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readFloat(),
                buf.readFloat(), buf.readVarInt()));
        }
        int roomCount = buf.readVarInt();
        List<RoomEntry> rooms = new ArrayList<>(roomCount);
        for (int i = 0; i < roomCount; i++) {
            String roomId = buf.readUtf();
            String hostName = buf.readUtf();
            boolean host = buf.readBoolean();
            boolean open = buf.readBoolean();
            boolean settled = buf.readBoolean();
            int winningRacer = buf.readVarInt();
            int totalPool = buf.readVarInt();
            int bettorCount = buf.readVarInt();
            int playerRacer = buf.readVarInt();
            int playerAmount = buf.readVarInt();
            int playerPayout = buf.readVarInt();
            boolean playerClaimed = buf.readBoolean();
            int oddsCount = buf.readVarInt();
            List<OddsEntry> odds = new ArrayList<>(oddsCount);
            for (int j = 0; j < oddsCount; j++) {
                odds.add(new OddsEntry(buf.readVarInt(), buf.readVarInt(), buf.readVarInt()));
            }
            rooms.add(new RoomEntry(roomId, hostName, host, open, settled, winningRacer, totalPool, bettorCount,
                playerRacer, playerAmount, playerPayout, playerClaimed, odds));
        }
        return new DesertFestivalRaceSnapshot(raceState, timerTicks, currentTime, eggCount, lastWinner, activeGuess,
            nextGuess, sabotageTarget, rewardClaims, specialRewardPending, canGuess, announceKey, racers, rooms);
    }
}