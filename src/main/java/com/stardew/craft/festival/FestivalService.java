package com.stardew.craft.festival;

import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FestivalService {
    private static String debugActiveFestivalId;
    private static String lastActiveFestivalStartMessageKey;

    private FestivalService() {
    }

    public static boolean isFestivalDay() {
        StardewTimeManager time = StardewTimeManager.get();
        return getDebugActiveFestival().isPresent() || isFestivalDay(time.getCurrentDay(), time.getCurrentSeason());
    }

    public static boolean isFestivalDay(int day, int season) {
        return getActiveFestivalForDate(day, season).isPresent();
    }

    public static Optional<FestivalDefinition> getActiveFestivalToday() {
        Optional<FestivalDefinition> debugFestival = getDebugActiveFestival();
        if (debugFestival.isPresent()) {
            return debugFestival;
        }
        StardewTimeManager time = StardewTimeManager.get();
        return getActiveFestivalForDate(time.getCurrentDay(), time.getCurrentSeason());
    }

    public static void setDebugActiveFestival(String festivalId) {
        debugActiveFestivalId = festivalId == null || festivalId.isBlank() ? null : festivalId.trim();
    }

    public static void clearDebugActiveFestival(String festivalId) {
        if (festivalId == null || festivalId.isBlank()) {
            debugActiveFestivalId = null;
            return;
        }
        if (debugActiveFestivalId != null && debugActiveFestivalId.equalsIgnoreCase(festivalId.trim())) {
            debugActiveFestivalId = null;
        }
    }

    public static boolean isDebugActiveFestival(String festivalId) {
        return festivalId != null && debugActiveFestivalId != null && debugActiveFestivalId.equalsIgnoreCase(festivalId.trim());
    }

    private static Optional<FestivalDefinition> getDebugActiveFestival() {
        if (debugActiveFestivalId == null || debugActiveFestivalId.isBlank()) {
            return Optional.empty();
        }
        return FestivalRegistry.get(debugActiveFestivalId)
            .filter(definition -> definition.type() == FestivalType.ACTIVE);
    }

    public static Optional<FestivalDefinition> getActiveFestivalForDate(int day, int season) {
        return FestivalRegistry.activeFestivals().stream()
            .filter(definition -> definition.isDate(season, day))
            .findFirst();
    }

    public static List<FestivalDefinition> getActivePassiveFestivalsToday() {
        StardewTimeManager time = StardewTimeManager.get();
        int day = time.getCurrentDay();
        int season = time.getCurrentSeason();
        return FestivalRegistry.passiveFestivals().stream()
            .filter(definition -> definition.isDate(season, day))
            .filter(FestivalService::conditionsPass)
            .toList();
    }

    public static void onNewDay(ServerLevel level) {
        if (level == null) {
            return;
        }
        StardewTimeManager time = StardewTimeManager.get();
        FestivalWorldData data = FestivalWorldData.get(level);
        restoreStaleOverlays(level, data, time.getCurrentYear(), time.getCurrentSeason(), time.getCurrentDay());
        data.closeStaleSessions(time.getCurrentYear(), time.getCurrentSeason(), time.getCurrentDay());
        lastActiveFestivalStartMessageKey = null;

        getActiveFestivalToday().ifPresent(definition -> {
            FestivalSessionState session = data.getOrCreateSession(definition, time.getCurrentYear(), time.getCurrentSeason(), time.getCurrentDay());
            if (session.phase() == FestivalSessionPhase.SCHEDULED && !requiresUnregisteredOverlay(definition)) {
                boolean overlayStarted = FestivalMapOverlayManager.beginApply(
                    level,
                    definition,
                    time.getCurrentYear(),
                    time.getCurrentSeason(),
                    time.getCurrentDay()
                );
                if (overlayStarted) {
                    session.setPhase(FestivalSessionPhase.PREPARING_MAP);
                    data.setDirty();
                }
            }
        });

        List<FestivalDefinition> passiveToday = getActivePassiveFestivalsToday();
        data.setActivePassiveFestivalIds(passiveToday.stream().map(FestivalDefinition::id).collect(Collectors.toList()));
        for (FestivalDefinition definition : passiveToday) {
            data.getOrCreateSession(definition, time.getCurrentYear(), time.getCurrentSeason(), time.getCurrentDay());
        }
    }

    public static void onTimeChanged(MinecraftServer server) {
        if (server == null) {
            return;
        }
        ServerLevel stardewLevel = server.getLevel(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) {
            return;
        }
        FestivalWorldData data = FestivalWorldData.get(stardewLevel);
        StardewTimeManager time = StardewTimeManager.get();
        broadcastActiveFestivalStartIfDue(server, time);
        for (FestivalDefinition definition : getActivePassiveFestivalsToday()) {
            if (!isPassiveFestivalOpen(definition.id())) {
                continue;
            }
            FestivalSessionState session = data.getOrCreateSession(
                definition,
                time.getCurrentYear(),
                time.getCurrentSeason(),
                time.getCurrentDay()
            );
            if (session.phase() == FestivalSessionPhase.SCHEDULED) {
                if (requiresUnregisteredOverlay(definition)) {
                    continue;
                }
                boolean overlayStarted = FestivalMapOverlayManager.beginApply(
                    stardewLevel,
                    definition,
                    time.getCurrentYear(),
                    time.getCurrentSeason(),
                    time.getCurrentDay()
                );
                session.setPhase(overlayStarted ? FestivalSessionPhase.PREPARING_MAP : FestivalSessionPhase.OPEN);
                data.setDirty();
            }
        }
    }

    private static void broadcastActiveFestivalStartIfDue(MinecraftServer server, StardewTimeManager time) {
        Optional<FestivalDefinition> definitionOpt = getActiveFestivalToday();
        if (definitionOpt.isEmpty()) {
            return;
        }
        FestivalDefinition definition = definitionOpt.get();
        if (!"spring13".equalsIgnoreCase(definition.id()) || isDebugActiveFestival(definition.id())) {
            return;
        }
        if (currentTimeOfDay() != definition.startTime()) {
            return;
        }
        String messageKey = time.getCurrentYear() + ":" + time.getCurrentSeason() + ":" + time.getCurrentDay() + ":" + definition.id();
        if (messageKey.equals(lastActiveFestivalStartMessageKey)) {
            return;
        }
        lastActiveFestivalStartMessageKey = messageKey;
        Component message = Component.translatable("message.stardewcraft.festival.egg.started");
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.displayClientMessage(message, false);
        }
    }

    public static boolean isActiveFestivalEntryOpen(String festivalId) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel stardewLevel = server == null ? null : server.getLevel(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY);
        return isActiveFestivalEntryOpen(stardewLevel, festivalId);
    }

    public static boolean isActiveFestivalEntryOpen(ServerLevel level, String festivalId) {
        if (isDebugActiveFestival(festivalId)) {
            return true;
        }
        Optional<FestivalDefinition> definitionOpt = getActiveFestivalToday()
            .filter(definition -> definition.id().equalsIgnoreCase(festivalId))
            .filter(definition -> {
                int now = currentTimeOfDay();
                return now >= definition.startTime() && now < definition.endTime();
            });
        return definitionOpt.isPresent() && !isCurrentSessionRestoring(level, festivalId);
    }

    public static Optional<FestivalSessionState> openActiveFestival(ServerPlayer player, String festivalId) {
        if (player == null || !isActiveFestivalEntryOpen(player.serverLevel(), festivalId)) {
            return Optional.empty();
        }
        Optional<FestivalDefinition> definitionOpt = getActiveFestivalToday()
            .filter(definition -> definition.id().equalsIgnoreCase(festivalId));
        if (definitionOpt.isEmpty()) {
            return Optional.empty();
        }

        FestivalDefinition definition = definitionOpt.get();
        if (requiresUnregisteredOverlay(definition)) {
            return Optional.empty();
        }
        StardewTimeManager time = StardewTimeManager.get();
        ServerLevel level = player.serverLevel();
        FestivalWorldData data = FestivalWorldData.get(level);
        FestivalSessionState session = data.getOrCreateSession(
            definition,
            time.getCurrentYear(),
            time.getCurrentSeason(),
            time.getCurrentDay()
        );
        if (session.phase() == FestivalSessionPhase.RESTORING_MAP) {
            return Optional.empty();
        }
        if (isTerminalSessionPhase(session.phase())) {
            session.setPhase(FestivalSessionPhase.SCHEDULED);
            data.setDirty();
        }
        if (session.phase() == FestivalSessionPhase.SCHEDULED) {
            boolean overlayStarted = FestivalMapOverlayManager.beginApply(
                level,
                definition,
                time.getCurrentYear(),
                time.getCurrentSeason(),
                time.getCurrentDay()
            );
            if (!overlayStarted && !definition.mapOverlayId().isBlank()) {
                return Optional.empty();
            }
            session.setPhase(overlayStarted ? FestivalSessionPhase.PREPARING_MAP : FestivalSessionPhase.OPEN);
        }
        session.addParticipant(player.getUUID());
        data.setDirty();
        return Optional.of(session);
    }

    public static boolean endFestival(ServerLevel level, String festivalId) {
        if (level == null || festivalId == null || festivalId.isBlank()) {
            return false;
        }
        FestivalDefinition definition = FestivalRegistry.get(festivalId).orElse(null);
        if (definition == null) {
            return false;
        }
        FestivalWorldData data = FestivalWorldData.get(level);
        StardewTimeManager time = StardewTimeManager.get();
        FestivalSessionState session = data.getOrCreateSession(
            definition,
            time.getCurrentYear(),
            time.getCurrentSeason(),
            time.getCurrentDay()
        );
        if (!definition.mapOverlayId().isBlank() && FestivalMapOverlayManager.beginRestore(level, definition.mapOverlayId())) {
            session.setPhase(FestivalSessionPhase.RESTORING_MAP);
        } else {
            session.setPhase(FestivalSessionPhase.CLOSED);
        }
        clearDebugActiveFestival(festivalId);
        data.setDirty();
        return true;
    }

    public static void advancePreparingSessions(ServerLevel level) {
        if (level == null) {
            return;
        }
        FestivalWorldData data = FestivalWorldData.get(level);
        for (FestivalDefinition definition : FestivalRegistry.all()) {
            data.getSession(definition.id()).ifPresent(session -> {
                if (session.phase() != FestivalSessionPhase.PREPARING_MAP) {
                    return;
                }
                if (definition.mapOverlayId().isBlank() || FestivalMapOverlayManager.isApplied(level, definition.mapOverlayId())) {
                    session.setPhase(FestivalSessionPhase.OPEN);
                    data.setDirty();
                }
            });
            data.getSession(definition.id()).ifPresent(session -> {
                if (session.phase() != FestivalSessionPhase.RESTORING_MAP) {
                    return;
                }
                if (definition.mapOverlayId().isBlank() || FestivalMapOverlayManager.isRestored(level, definition.mapOverlayId())) {
                    session.setPhase(FestivalSessionPhase.CLOSED);
                    data.setDirty();
                }
            });
        }
    }

    public static boolean isPassiveFestivalDay(String festivalId) {
        return getPassiveFestivalToday(festivalId).isPresent();
    }

    public static Optional<FestivalDefinition> getPassiveFestivalToday(String festivalId) {
        StardewTimeManager time = StardewTimeManager.get();
        return FestivalRegistry.get(festivalId)
            .filter(definition -> definition.type() == FestivalType.PASSIVE)
            .filter(definition -> definition.isDate(time.getCurrentSeason(), time.getCurrentDay()))
            .filter(FestivalService::conditionsPass);
    }

    public static boolean isPassiveFestivalOpen(String festivalId) {
        return getPassiveFestivalToday(festivalId)
            .filter(definition -> currentTimeOfDay() >= definition.startTime())
            .isPresent();
    }

    public static int getDayOfPassiveFestival(String festivalId) {
        StardewTimeManager time = StardewTimeManager.get();
        return getPassiveFestivalToday(festivalId)
            .map(definition -> definition.dayOfFestival(time.getCurrentSeason(), time.getCurrentDay()))
            .orElse(-1);
    }

    public static int currentTimeOfDay() {
        StardewTimeManager time = StardewTimeManager.get();
        return time.getHour() * 100 + time.getMinute();
    }

    private static boolean conditionsPass(FestivalDefinition definition) {
        String condition = definition.sourceCondition();
        if (condition == null || condition.isBlank()) {
            return true;
        }
        String normalized = condition.trim().toUpperCase(Locale.ROOT);
        if ("LOCATION_ACCESSIBLE DESERT".equals(normalized)) {
            return isDesertAccessible();
        }
        return false;
    }

    private static boolean requiresUnregisteredOverlay(FestivalDefinition definition) {
        return definition != null && !definition.mapOverlayId().isBlank() && !FestivalMapOverlayRegistry.isRegistered(definition.mapOverlayId());
    }

    private static boolean isCurrentSessionRestoring(ServerLevel level, String festivalId) {
        if (level == null || festivalId == null || festivalId.isBlank()) {
            return false;
        }
        FestivalDefinition definition = FestivalRegistry.get(festivalId).orElse(null);
        if (definition == null) {
            return false;
        }
        StardewTimeManager time = StardewTimeManager.get();
        return FestivalWorldData.get(level).getSession(definition.id())
            .filter(session -> session.year() == time.getCurrentYear()
                && session.season() == time.getCurrentSeason()
                && session.day() == time.getCurrentDay())
            .map(session -> session.phase() == FestivalSessionPhase.RESTORING_MAP)
            .orElse(false);
    }

    private static boolean isTerminalSessionPhase(FestivalSessionPhase phase) {
        return phase == FestivalSessionPhase.ENDING
            || phase == FestivalSessionPhase.RESTORING_MAP
            || phase == FestivalSessionPhase.CLOSED;
    }

    private static void restoreStaleOverlays(ServerLevel level, FestivalWorldData data, int year, int season, int day) {
        for (FestivalMapOverlayState state : data.overlayStates()) {
            if (state.phase() != FestivalMapOverlayPhase.APPLIED) {
                continue;
            }
            if (state.year() == year && state.season() == season && state.day() == day) {
                continue;
            }
            FestivalMapOverlayManager.beginRestore(level, state.overlayId());
        }
    }

    private static boolean isDesertAccessible() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return false;
        }
        try {
            for (PlayerStardewData data : PlayerDataManager.get().getAllPlayerData().values()) {
                if (data.hasMailFlag("ccVault")) {
                    return true;
                }
            }
        } catch (IllegalStateException ignored) {
            return false;
        }
        return false;
    }
}