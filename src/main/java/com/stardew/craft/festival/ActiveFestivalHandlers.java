package com.stardew.craft.festival;

import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ActiveFestivalHandlers {
    private static final Map<String, ActiveFestivalHandler> HANDLERS = new LinkedHashMap<>();

    static {
        register(new DelegateHandler(
            EggFestivalService.FESTIVAL_ID,
            "Egg Festival",
            EggFestivalService::tick,
            EggFestivalService::startDebugFestival,
            EggFestivalService::restoreDebugFestival,
            level -> EggFestivalService.debugMainEventStatus(level) + "\n" + EggFestivalNpcService.debugStatus(level),
            EggFestivalService::tickNpcActors,
            EggFestivalNpcService::requestDebugStart,
            EggFestivalNpcService::restore,
            EggFestivalNpcService::debugStatus,
            EggFestivalNpcService::controlsNpc,
            EggFestivalService::isParticipant,
            ActiveFestivalHandlers::noopPlayer,
            ActiveFestivalHandlers::noopPlayer,
            ActiveFestivalHandlers::noopDialogueSeen,
            EggFestivalService::tryOpenPierreFestivalShop,
            () -> EggFestivalService.isEggHuntActive() || EggFestivalService.isMainEventCutsceneActive(),
            EggFestivalService::tryStartMainEvent,
            EggFestivalService::debugMainEventStatus,
            EggFestivalService::isTimeFreezeActive,
            EggFestivalService::applyTimeFreeze,
            "已启动 Egg Festival 总调试: 当前日期按 spring13 处理，overlay 应用中，NPC 会在地图应用完成后进入节日点位"
        ));
        register(new DelegateHandler(
            FlowerDanceService.FESTIVAL_ID,
            "Flower Dance",
            FlowerDanceService::tick,
            FlowerDanceService::startDebugFestival,
            FlowerDanceService::restoreDebugFestival,
            FlowerDanceService::debugStatus,
            ActiveFestivalHandlers::noop,
            FlowerDanceNpcService::requestDebugStart,
            FlowerDanceNpcService::restore,
            FlowerDanceNpcService::debugStatus,
            FlowerDanceNpcService::controlsNpc,
            FlowerDanceService::isParticipant,
            FlowerDanceService::onPlayerLogin,
            FlowerDanceService::onPlayerLogout,
            FlowerDanceService::markFestivalDialogueSeen,
            FlowerDanceService::tryOpenPierreFestivalShop,
            FlowerDanceService::isMainEventCutsceneActive,
            FlowerDanceService::tryStartMainEvent,
            FlowerDanceService::debugStatus,
            FlowerDanceService::isTimeFreezeActive,
            FlowerDanceService::applyTimeFreeze,
            "已启动 Flower Dance 总调试: 当前日期按 spring24 处理，overlay 应用中，已确认的自由阶段 NPC 会进入节日点位"
        ));
        register(new DelegateHandler(
            LuauFestivalService.FESTIVAL_ID,
            "Luau",
            LuauFestivalService::tick,
            LuauFestivalService::startDebugFestival,
            LuauFestivalService::restoreDebugFestival,
            LuauFestivalService::debugStatus,
            LuauFestivalService::tickNpcActors,
            LuauFestivalService::requestDebugNpcs,
            LuauFestivalService::restoreNpcs,
            LuauFestivalService::debugStatus,
            LuauFestivalService::controlsNpc,
            LuauFestivalService::isParticipant,
            LuauFestivalService::onPlayerLogin,
            LuauFestivalService::onPlayerLogout,
            LuauFestivalService::markFestivalDialogueSeen,
            LuauFestivalService::tryOpenPierreFestivalShop,
            LuauFestivalService::isMainEventActive,
            LuauFestivalService::tryStartMainEvent,
            LuauFestivalService::debugStatus,
            LuauFestivalService::isTimeFreezeActive,
            LuauFestivalService::applyTimeFreeze,
            "已启动 Luau 总调试: 当前日期按 summer11 处理，overlay 应用中，NPC 会在地图应用完成后进入节日点位"
        ));
        register(new DelegateHandler(
            MoonlightJelliesFestivalService.FESTIVAL_ID,
            "Dance of the Moonlight Jellies",
            MoonlightJelliesFestivalService::tick,
            MoonlightJelliesFestivalService::startDebugFestival,
            MoonlightJelliesFestivalService::restoreDebugFestival,
            MoonlightJelliesFestivalService::debugStatus,
            MoonlightJelliesFestivalService::tickNpcActors,
            MoonlightJelliesFestivalService::requestDebugNpcs,
            MoonlightJelliesFestivalService::restoreNpcs,
            MoonlightJelliesFestivalService::debugStatus,
            MoonlightJelliesFestivalService::controlsNpc,
            MoonlightJelliesFestivalService::isParticipant,
            MoonlightJelliesFestivalService::onPlayerLogin,
            MoonlightJelliesFestivalService::onPlayerLogout,
            MoonlightJelliesFestivalService::markFestivalDialogueSeen,
            MoonlightJelliesFestivalService::tryOpenPierreFestivalShop,
            MoonlightJelliesFestivalService::isMainEventActive,
            MoonlightJelliesFestivalService::tryStartMainEvent,
            MoonlightJelliesFestivalService::debugStatus,
            MoonlightJelliesFestivalService::isTimeFreezeActive,
            MoonlightJelliesFestivalService::applyTimeFreeze,
            "已启动 Moonlight Jellies 总调试: 当前日期按 summer28 处理，overlay 应用中，NPC 会在地图应用完成后进入节日点位"
        ));
    }

    private ActiveFestivalHandlers() {
    }

    private static void register(ActiveFestivalHandler handler) {
        if (handler == null || handler.festivalId() == null || handler.festivalId().isBlank()) {
            return;
        }
        HANDLERS.put(key(handler.festivalId()), handler);
    }

    public static Optional<ActiveFestivalHandler> get(String festivalId) {
        return Optional.ofNullable(HANDLERS.get(key(festivalId)));
    }

    public static Optional<ActiveFestivalHandler> get(FestivalDefinition definition) {
        if (definition == null || definition.type() != FestivalType.ACTIVE) {
            return Optional.empty();
        }
        return get(definition.id());
    }

    public static Collection<ActiveFestivalHandler> all() {
        return HANDLERS.values();
    }

    public static List<String> festivalIds() {
        return HANDLERS.values().stream().map(ActiveFestivalHandler::festivalId).toList();
    }

    public static List<String> mainEventFestivalIds() {
        return HANDLERS.values().stream()
            .filter(ActiveFestivalHandler::supportsMainEventDebug)
            .map(ActiveFestivalHandler::festivalId)
            .toList();
    }

    public static void tickAll(ServerLevel level) {
        for (ActiveFestivalHandler handler : HANDLERS.values()) {
            handler.tick(level);
        }
    }

    public static boolean controlsNpc(String npcId) {
        return currentActiveHandler()
            .map(handler -> handler.controlsNpc(npcId))
            .orElse(false);
    }

    public static boolean isParticipant(ServerPlayer player) {
        return getParticipating(player).isPresent();
    }

    public static Optional<ActiveFestivalHandler> getParticipating(ServerPlayer player) {
        if (player == null) {
            return Optional.empty();
        }
        return currentActiveHandler()
            .filter(handler -> handler.isParticipant(player));
    }

    public static void onPlayerLogin(ServerPlayer player) {
        for (ActiveFestivalHandler handler : HANDLERS.values()) {
            handler.onPlayerLogin(player);
        }
    }

    public static void onPlayerLogout(ServerPlayer player) {
        for (ActiveFestivalHandler handler : HANDLERS.values()) {
            handler.onPlayerLogout(player);
        }
    }

    public static boolean tryOpenPierreFestivalShop(ServerPlayer player) {
        return getParticipating(player)
            .map(handler -> handler.tryOpenPierreFestivalShop(player))
            .orElse(false);
    }

    public static void restoreNpcsForOverlay(ServerLevel level, String overlayId) {
        for (ActiveFestivalHandler handler : HANDLERS.values()) {
            FestivalDefinition definition = FestivalRegistry.get(handler.festivalId()).orElse(null);
            if (definition != null && definition.mapOverlayId().equalsIgnoreCase(overlayId == null ? "" : overlayId)) {
                handler.restoreDebugNpcs(level);
            }
        }
    }

    public static void onMapOverlayApplied(ServerLevel level, String overlayId) {
        for (ActiveFestivalHandler handler : HANDLERS.values()) {
            FestivalDefinition definition = FestivalRegistry.get(handler.festivalId()).orElse(null);
            if (definition != null && definition.mapOverlayId().equalsIgnoreCase(overlayId == null ? "" : overlayId)) {
                handler.onMapOverlayApplied(level);
            }
        }
    }

    public static String debugNpcStatus(ServerLevel level) {
        return HANDLERS.values().stream()
            .map(handler -> handler.debugNpcStatus(level))
            .reduce((left, right) -> left + "\n" + right)
            .orElse("No active festival NPC controllers registered");
    }

    public static String debugMainEventStatus(ServerLevel level) {
        return HANDLERS.values().stream()
            .map(handler -> handler.debugMainEventStatus(level))
            .reduce((left, right) -> left + "\n" + right)
            .orElse("No active festival handlers registered");
    }

    public static boolean isAnyTimeFreezeActive() {
        for (ActiveFestivalHandler handler : HANDLERS.values()) {
            if (handler.isTimeFreezeActive()) {
                return true;
            }
        }
        return false;
    }

    public static long applyTimeFreeze(ServerLevel level, StardewTimeManager timeManager) {
        long virtualDayTime = timeManager.getVirtualDayTime(level);
        for (ActiveFestivalHandler handler : HANDLERS.values()) {
            virtualDayTime = handler.applyTimeFreeze(level, timeManager);
        }
        return virtualDayTime;
    }

    private static String key(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static Optional<ActiveFestivalHandler> currentActiveHandler() {
        return FestivalService.getActiveFestivalToday()
            .flatMap(definition -> get(definition.id()));
    }

    private static void noop(ServerLevel level) {
    }

    private static void noopPlayer(ServerPlayer player) {
    }

    private static void noopDialogueSeen(ServerPlayer player, String npcId) {
    }

    private record DelegateHandler(
        String festivalId,
        String displayName,
        Consumer<ServerLevel> tickAction,
        Consumer<ServerLevel> startDebugAction,
        Consumer<ServerLevel> restoreDebugAction,
        Function<ServerLevel, String> debugStatusAction,
        Consumer<ServerLevel> overlayAppliedAction,
        Consumer<ServerLevel> requestDebugNpcsAction,
        Consumer<ServerLevel> restoreDebugNpcsAction,
        Function<ServerLevel, String> debugNpcStatusAction,
        Predicate<String> controlsNpcAction,
        Predicate<ServerPlayer> participantAction,
        Consumer<ServerPlayer> playerLoginAction,
        Consumer<ServerPlayer> playerLogoutAction,
        BiConsumer<ServerPlayer, String> npcDialogueSeenAction,
        Predicate<ServerPlayer> pierreShopAction,
        BooleanSupplier npcInteractionLockAction,
        Predicate<ServerPlayer> startMainEventAction,
        Function<ServerLevel, String> debugMainEventStatusAction,
        BooleanSupplier timeFreezeActiveAction,
        TimeFreezeApplier timeFreezeAction,
        String debugApplyMessage
    ) implements ActiveFestivalHandler {
        @Override
        public void tick(ServerLevel level) {
            tickAction.accept(level);
        }

        @Override
        public void startDebugFestival(ServerLevel level) {
            startDebugAction.accept(level);
        }

        @Override
        public void restoreDebugFestival(ServerLevel level) {
            restoreDebugAction.accept(level);
        }

        @Override
        public String debugStatus(ServerLevel level) {
            return debugStatusAction.apply(level);
        }

        @Override
        public void onMapOverlayApplied(ServerLevel level) {
            overlayAppliedAction.accept(level);
        }

        @Override
        public void requestDebugNpcs(ServerLevel level) {
            requestDebugNpcsAction.accept(level);
        }

        @Override
        public void restoreDebugNpcs(ServerLevel level) {
            restoreDebugNpcsAction.accept(level);
        }

        @Override
        public String debugNpcStatus(ServerLevel level) {
            return debugNpcStatusAction.apply(level);
        }

        @Override
        public boolean controlsNpc(String npcId) {
            return controlsNpcAction.test(npcId);
        }

        @Override
        public boolean isParticipant(ServerPlayer player) {
            return participantAction.test(player);
        }

        @Override
        public void onPlayerLogin(ServerPlayer player) {
            playerLoginAction.accept(player);
        }

        @Override
        public void onPlayerLogout(ServerPlayer player) {
            playerLogoutAction.accept(player);
        }

        @Override
        public void onNpcDialogueSeen(ServerPlayer player, String npcId) {
            npcDialogueSeenAction.accept(player, npcId);
        }

        @Override
        public boolean tryOpenPierreFestivalShop(ServerPlayer player) {
            return pierreShopAction.test(player);
        }

        @Override
        public boolean blocksNpcInteractionDuringMainEvent() {
            return npcInteractionLockAction.getAsBoolean();
        }

        @Override
        public boolean supportsMainEventDebug() {
            return startMainEventAction != null;
        }

        @Override
        public boolean tryStartMainEvent(ServerPlayer player) {
            return startMainEventAction != null && startMainEventAction.test(player);
        }

        @Override
        public String debugMainEventStatus(ServerLevel level) {
            return debugMainEventStatusAction.apply(level);
        }

        @Override
        public boolean isTimeFreezeActive() {
            return timeFreezeActiveAction.getAsBoolean();
        }

        @Override
        public long applyTimeFreeze(ServerLevel level, StardewTimeManager timeManager) {
            return timeFreezeAction.apply(level, timeManager);
        }

        @Override
        public String debugApplyMessage() {
            return debugApplyMessage;
        }
    }

    @FunctionalInterface
    private interface BooleanSupplier {
        boolean getAsBoolean();
    }

    @FunctionalInterface
    private interface TimeFreezeApplier {
        long apply(ServerLevel level, StardewTimeManager timeManager);
    }
}