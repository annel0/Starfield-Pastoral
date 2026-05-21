package com.stardew.craft.festival;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.cutscene.server.ServerCutsceneTracker;
import com.stardew.craft.farm.FarmInstance;
import com.stardew.craft.farm.FarmInstanceRegistry;
import com.stardew.craft.network.TimeSyncPacket;
import com.stardew.craft.network.payload.EggFestivalCutsceneStatePayload;
import com.stardew.craft.network.payload.FestivalHudStatePayload;
import com.stardew.craft.network.payload.FestivalMusicStatePayload;
import com.stardew.craft.network.payload.OpenFestivalConfirmPayload;
import com.stardew.craft.network.payload.OpenShopScreenPayload;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.shop.ShopItemEntry;
import com.stardew.craft.shop.ShopRegistry;
import com.stardew.craft.sound.ModSounds;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.warp.ModTeleport;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class EggFestivalService {
    public static final String FESTIVAL_ID = "spring13";
    private static final String OVERLAY_ID = "Town-EggFestival";
    private static final String MAIN_EVENT_CUTSCENE_ID = "egg_festival_main_event";
    private static final String AWARD_CUTSCENE_ID = "egg_festival_award";

    private static final BlockPos FESTIVAL_MIN = new BlockPos(-46, 64, -36);
    private static final BlockPos FESTIVAL_MAX = new BlockPos(137, 84, 53);
    private static final AABB FESTIVAL_BOUNDS = inclusiveBox(FESTIVAL_MIN, FESTIVAL_MAX);
    private static final BlockPos ENTRY_POS = new BlockPos(-39, 64, -34);
    private static final float SOUTH_YAW = 0.0F;

    private static final AABB SHOP_ZONE = inclusiveBox(new BlockPos(-7, 64, -8), new BlockPos(-2, 67, -6));
    private static final String SHOP_ID = "Festival_EggFestival_Pierre";
    private static final int FESTIVAL_START_MINUTE = 9 * 60;
    private static final int FESTIVAL_END_MINUTE = 22 * 60;

    private static final String TAG_PARTICIPATING = "stardewcraft_egg_festival_participating";
    private static final String TAG_HUD_HIDDEN = "stardewcraft_egg_festival_hud_hidden";
    private static final String TAG_SHOP_OPENED = "stardewcraft_egg_festival_shop_opened";
    private static final String TAG_EGG_ROOT = "stardewcraft_egg_festival_egg";
    private static final String TAG_EGG_DISPLAY = "stardewcraft_egg_festival_egg_display";
    private static final String TAG_EGG_INTERACTION = "stardewcraft_egg_festival_egg_interaction";
    private static final String TAG_EGG_ID_PREFIX = "stardewcraft_egg_festival_egg_id_";

    private static final double EGG_INTERACTION_OFFSET_X = 0.5625D;
    private static final double EGG_INTERACTION_OFFSET_Y = 0.0D;
    private static final double EGG_INTERACTION_OFFSET_Z = 0.4375D;
    private static final float EGG_INTERACTION_SIZE = 0.3F;
    private static final int EGG_HUNT_DURATION_TICKS = 52 * 20;
    private static final String EGG_HUNT_SCOREBOARD_OBJECTIVE = "sdvEggHunt";
    private static final float NORTH_YAW = 180.0F;

    private static final List<EggSpot> ODD_YEAR_EGG_SPOTS = List.of(
        new EggSpot(-2.5D, 64.0D, -21.8125D),
        new EggSpot(3.25D, 64.0D, -22.9375D),
        new EggSpot(-2.5D, 64.0D, -21.8125D),
        new EggSpot(4.1875D, 64.0D, -10.8125D),
        new EggSpot(-6.0D, 64.0D, -4.5625D),
        new EggSpot(-30.9375D, 64.0D, -4.5625D),
        new EggSpot(35.6875D, 64.0D, -9.5D),
        new EggSpot(58.393310546875D, 64.0D, 1.4375D),
        new EggSpot(67.5625D, 64.0D, 0.0D),
        new EggSpot(82.1875D, 64.0D, 6.5625D),
        new EggSpot(47.875D, 64.0D, 21.125D),
        new EggSpot(61.5D, 64.0D, 25.4375D),
        new EggSpot(40.1875D, 64.0D, 28.75D),
        new EggSpot(24.8125D, 64.0D, 22.125D),
        new EggSpot(31.0D, 64.0D, 32.375D),
        new EggSpot(20.3125D, 64.0D, 35.8125D),
        new EggSpot(37.375D, 64.0D, 43.6875D),
        new EggSpot(20.646742172468123D, 64.0D, 51.25D),
        new EggSpot(29.3125D, 64.0D, 49.125D),
        new EggSpot(43.125D, 64.0D, 47.25D),
        new EggSpot(69.75D, 64.0D, 47.25D),
        new EggSpot(75.9375D, 64.0D, 47.1875D),
        new EggSpot(-18.75D, 64.0D, 18.875D),
        new EggSpot(-36.955322265625D, 64.0D, 25.1875D),
        new EggSpot(-12.875D, 64.0D, 40.8125D),
        new EggSpot(-29.6875D, 64.0D, 46.0625D),
        new EggSpot(-13.5625D, 64.0D, 0.875D),
        new EggSpot(25.409485322311774D, 65.0D, 3.0625D)
    );
    private static final List<EggSpot> EVEN_YEAR_EGG_SPOTS = ODD_YEAR_EGG_SPOTS;

    private static final Map<UUID, Vec3> LAST_OUTSIDE = new LinkedHashMap<>();
    private static final Map<UUID, Vec3> LAST_INSIDE = new LinkedHashMap<>();
    private static final Set<UUID> ENTRY_DIALOG_OPEN = new LinkedHashSet<>();
    private static final Set<UUID> START_CONTEST_DIALOG_OPEN = new LinkedHashSet<>();
    private static final Set<UUID> EXIT_DIALOG_OPEN = new LinkedHashSet<>();
    private static final Set<UUID> EXIT_VOTES = new LinkedHashSet<>();
    private static final Map<UUID, PlayerPose> DEBUG_RETURN_POSES = new LinkedHashMap<>();
    private static final Map<UUID, Integer> EGG_HUNT_COUNTS = new LinkedHashMap<>();
    private static final Set<UUID> MAIN_EVENT_CUTSCENE_DONE = new LinkedHashSet<>();
    private static final Set<UUID> AWARD_CUTSCENE_DONE = new LinkedHashSet<>();
    private static Integer frozenMinute;
    private static Long frozenOverworldDayTime;
    private static MainEventPhase mainEventPhase = MainEventPhase.FREE;
    private static long eggHuntEndTick = -1L;
    private static long lastEggHuntHudTick = -1L;
    private static boolean awardPlayerWon;
    private static String awardWinnerText = "";
    private static final List<UUID> AWARD_WINNER_IDS = new ArrayList<>();
    private static boolean mainEventBlackoutPrepared;
    private static boolean awardBlackoutPrepared;

    private EggFestivalService() {
    }

    public static void tick(ServerLevel level) {
        if (level == null) {
            return;
        }
        boolean activeDay = FestivalService.getActiveFestivalToday()
            .filter(definition -> FESTIVAL_ID.equals(definition.id()))
            .isPresent();
        if (!activeDay) {
            clearRuntimeState(level);
            return;
        }

        for (ServerPlayer player : level.players()) {
            tickPlayer(level, player);
        }
        tickEggHunt(level);
    }

    public static void tickNpcActors(ServerLevel level) {
        if (level == null) {
            return;
        }
        boolean activeDay = FestivalService.getActiveFestivalToday()
            .filter(definition -> FESTIVAL_ID.equals(definition.id()))
            .isPresent();
        if (!activeDay) {
            EggFestivalNpcService.tick(level, false);
            return;
        }
        boolean overlayApplied = FestivalMapOverlayManager.isApplied(level, OVERLAY_ID);
        boolean venueActive = overlayApplied && (FestivalService.isActiveFestivalEntryOpen(level, FESTIVAL_ID) || hasOnlineParticipant(level));
        EggFestivalNpcService.tick(level, venueActive);
    }

    public static long applyTimeFreeze(ServerLevel level, StardewTimeManager timeManager) {
        long currentVirtual = timeManager.getVirtualDayTime(level);
        if (frozenMinute == null || !hasOnlineParticipant(level)) {
            frozenOverworldDayTime = null;
            return currentVirtual;
        }
        ServerLevel overworld = level.getServer().overworld();
        if (frozenOverworldDayTime == null) {
            frozenOverworldDayTime = overworld.getDayTime();
        }
        long dayBase = Math.floorDiv(currentVirtual, 24000L) * 24000L;
        long target = dayBase + com.stardew.craft.event.DimensionEventHandler.stardewMinutesToMcTime(frozenMinute);
        if (overworld.getDayTime() != frozenOverworldDayTime) {
            overworld.setDayTime(frozenOverworldDayTime);
        }
        long targetOffset = target - frozenOverworldDayTime;
        if (timeManager.getDayTimeOffset() != targetOffset) {
            timeManager.setDayTimeOffsetRaw(targetOffset);
        }
        return target;
    }

    public static void onPlayerConfirmed(ServerPlayer player, OpenFestivalConfirmPayload.Action action, boolean confirmed) {
        if (player == null || action == null) {
            return;
        }
        if (action == OpenFestivalConfirmPayload.Action.ENTER) {
            ENTRY_DIALOG_OPEN.remove(player.getUUID());
            if (confirmed) {
                enterFestival(player);
            }
            return;
        }

        if (action == OpenFestivalConfirmPayload.Action.START_CONTEST) {
            START_CONTEST_DIALOG_OPEN.remove(player.getUUID());
            if (!isParticipant(player)) {
                return;
            }
            if (confirmed) {
                startMainEventAfterConfirmation(player);
            } else {
                player.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.start_vote_cancelled"), true);
            }
            return;
        }

        if (action != OpenFestivalConfirmPayload.Action.EXIT) {
            return;
        }

        EXIT_DIALOG_OPEN.remove(player.getUUID());
        if (!isParticipant(player)) {
            return;
        }
        if (confirmed) {
            castExitVote(player);
        } else {
            EXIT_VOTES.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.exit_vote_cancelled"), true);
        }
    }

    public static boolean isParticipant(ServerPlayer player) {
        return player != null && player.getPersistentData().getBoolean(TAG_PARTICIPATING);
    }

    public static boolean isEggHuntActive() {
        return mainEventPhase == MainEventPhase.EGG_HUNT_ACTIVE;
    }

    public static boolean isTimeFreezeActive() {
        if (frozenMinute == null) {
            return false;
        }
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        ServerLevel stardewLevel = server == null ? null : server.getLevel(ModDimensions.STARDEW_VALLEY);
        return stardewLevel != null && hasOnlineParticipant(stardewLevel);
    }

    public static boolean isMainEventCutsceneActive() {
        return mainEventPhase == MainEventPhase.MAIN_EVENT_INTRO
            || mainEventPhase == MainEventPhase.AWARD_CUTSCENE
            || mainEventPhase == MainEventPhase.FESTIVAL_ENDING;
    }

    public static void startDebugFestival(ServerLevel level) {
        if (level == null) {
            return;
        }
        FestivalService.setDebugActiveFestival(FESTIVAL_ID);
        startTimeFreeze(level);
        ENTRY_DIALOG_OPEN.clear();
        START_CONTEST_DIALOG_OPEN.clear();
        EXIT_DIALOG_OPEN.clear();
        EXIT_VOTES.clear();
        resetMainEventState();
        for (ServerPlayer player : level.players()) {
            DEBUG_RETURN_POSES.putIfAbsent(player.getUUID(), new PlayerPose(player.position(), player.getYRot(), player.getXRot()));
            player.getPersistentData().putBoolean(TAG_PARTICIPATING, true);
            player.getPersistentData().putBoolean(TAG_SHOP_OPENED, false);
            syncHud(player, true);
            ModTeleport.to(player, level, ENTRY_POS, SOUTH_YAW, 0.0F);
            LAST_INSIDE.put(player.getUUID(), Vec3.atBottomCenterOf(ENTRY_POS));
        }
        EggFestivalNpcService.requestDebugStart(level);
    }

    public static void restoreDebugFestival(ServerLevel level) {
        if (level == null) {
            return;
        }
        FestivalService.clearDebugActiveFestival(FESTIVAL_ID);
        for (ServerPlayer player : level.players()) {
            player.getPersistentData().putBoolean(TAG_PARTICIPATING, false);
            player.getPersistentData().putBoolean(TAG_SHOP_OPENED, false);
            syncHud(player, false);
            PlayerPose pose = DEBUG_RETURN_POSES.remove(player.getUUID());
            if (pose != null) {
                ModTeleport.to(player, level, pose.position().x, pose.position().y, pose.position().z, pose.yaw(), pose.pitch());
            }
        }
        DEBUG_RETURN_POSES.clear();
        LAST_OUTSIDE.clear();
        LAST_INSIDE.clear();
        ENTRY_DIALOG_OPEN.clear();
        START_CONTEST_DIALOG_OPEN.clear();
        EXIT_DIALOG_OPEN.clear();
        EXIT_VOTES.clear();
        clearEggHuntEntities(level);
        clearEggHuntScoreboard(level);
        resetMainEventState();
        stopTimeFreeze();
        EggFestivalNpcService.restore(level);
    }

    public static boolean tryStartMainEvent(ServerPlayer player) {
        if (player == null || !isParticipant(player)) {
            return false;
        }
        if (mainEventPhase == MainEventPhase.EGG_HUNT_PENDING_POINTS) {
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.hunt_missing_points"), false);
            return true;
        }
        if (mainEventPhase == MainEventPhase.MAIN_EVENT_INTRO || mainEventPhase == MainEventPhase.AWARD_CUTSCENE) {
            return true;
        }
        if (mainEventPhase == MainEventPhase.EGG_HUNT_ACTIVE) {
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.hunt_in_progress"), true);
            return true;
        }
        if (mainEventPhase == MainEventPhase.FESTIVAL_ENDING) {
            return true;
        }
        promptStartContest(player);
        return true;
    }

    public static String debugMainEventStatus(ServerLevel level) {
        int participants = level == null ? 0 : onlineParticipants(level).size();
        return "Egg Festival main event: phase=" + mainEventPhase.name()
            + " mainDone=" + MAIN_EVENT_CUTSCENE_DONE.size() + "/" + participants
            + " awardDone=" + AWARD_CUTSCENE_DONE.size() + "/" + participants
            + " timer=" + eggHuntSecondsRemaining(level)
            + " scores=" + EGG_HUNT_COUNTS;
    }

    private static void tickEggHunt(ServerLevel level) {
        if (mainEventPhase == MainEventPhase.MAIN_EVENT_INTRO) {
            if (onlineParticipants(level).isEmpty()) {
                finishFestival(level);
            }
            return;
        }

        if (mainEventPhase == MainEventPhase.EGG_HUNT_ACTIVE) {
            if (eggHuntEndTick < 0L) {
                eggHuntEndTick = level.getGameTime() + EGG_HUNT_DURATION_TICKS;
            }
            if (level.getGameTime() >= eggHuntEndTick) {
                endEggHunt(level);
                return;
            }
            if (level.getGameTime() != lastEggHuntHudTick && level.getGameTime() % 10L == 0L) {
                lastEggHuntHudTick = level.getGameTime();
                updateEggHuntDisplays(level);
            }
            return;
        }

        if (mainEventPhase == MainEventPhase.AWARD_CUTSCENE) {
            if (onlineParticipants(level).isEmpty()) {
                finishFestival(level);
            }
            return;
        }
    }

    private static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (player.isSpectator()) {
            return;
        }
        boolean inside = FESTIVAL_BOUNDS.contains(player.position());
        if (inside) {
            LAST_INSIDE.put(player.getUUID(), player.position());
        } else {
            LAST_OUTSIDE.put(player.getUUID(), player.position());
        }

        if (isParticipant(player)) {
            syncHud(player, true);
            if (!inside) {
                promptExit(player);
                moveToLastInside(level, player);
                return;
            }
            return;
        }

        syncHud(player, false);
        if (!inside) {
            ENTRY_DIALOG_OPEN.remove(player.getUUID());
            return;
        }

        if (!FestivalService.isActiveFestivalEntryOpen(level, FESTIVAL_ID)) {
            blockUnstartedEntry(level, player);
            return;
        }

        promptEnter(player);
        moveToLastOutside(level, player);
    }

    private static void promptEnter(ServerPlayer player) {
        if (ENTRY_DIALOG_OPEN.add(player.getUUID())) {
            PacketDistributor.sendToPlayer(player, new OpenFestivalConfirmPayload(OpenFestivalConfirmPayload.Action.ENTER));
        }
    }

    private static void promptExit(ServerPlayer player) {
        if (EXIT_DIALOG_OPEN.add(player.getUUID())) {
            PacketDistributor.sendToPlayer(player, new OpenFestivalConfirmPayload(OpenFestivalConfirmPayload.Action.EXIT));
        }
    }

    private static void promptStartContest(ServerPlayer player) {
        if (START_CONTEST_DIALOG_OPEN.add(player.getUUID())) {
            PacketDistributor.sendToPlayer(player, new OpenFestivalConfirmPayload(OpenFestivalConfirmPayload.Action.START_CONTEST));
        }
    }

    private static void startMainEventAfterConfirmation(ServerPlayer player) {
        if (player == null || !isParticipant(player) || mainEventPhase != MainEventPhase.FREE) {
            return;
        }
        startMainEventCutscene(player.serverLevel());
    }

    private static void enterFestival(ServerPlayer player) {
        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        if (!FestivalService.isActiveFestivalEntryOpen(player.serverLevel(), FESTIVAL_ID)) {
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.setup"), true);
            return;
        }
        if (FestivalService.openActiveFestival(player, FESTIVAL_ID).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.unavailable"), true);
            return;
        }
        startTimeFreeze(player.serverLevel());
        player.getPersistentData().putBoolean(TAG_PARTICIPATING, true);
        syncHud(player, true);
        ModTeleport.to(player, player.serverLevel(), ENTRY_POS, SOUTH_YAW, 0.0F);
        LAST_INSIDE.put(player.getUUID(), player.position());
    }

    private static void castExitVote(ServerPlayer player) {
        EXIT_VOTES.add(player.getUUID());
        List<ServerPlayer> voters = onlineParticipants(player.serverLevel());
        if (voters.isEmpty() || EXIT_VOTES.containsAll(voters.stream().map(ServerPlayer::getUUID).toList())) {
            finishFestival(player.serverLevel());
            return;
        }
        for (ServerPlayer participant : voters) {
            if (!EXIT_VOTES.contains(participant.getUUID()) && EXIT_DIALOG_OPEN.add(participant.getUUID())) {
                PacketDistributor.sendToPlayer(participant, new OpenFestivalConfirmPayload(OpenFestivalConfirmPayload.Action.EXIT));
            }
            participant.displayClientMessage(Component.translatable(
                "message.stardewcraft.festival.exit_vote_waiting", EXIT_VOTES.size(), voters.size()), true);
        }
    }

    private static void startMainEventCutscene(ServerLevel level) {
        List<ServerPlayer> participants = onlineParticipants(level);
        if (participants.isEmpty()) {
            return;
        }
        mainEventPhase = MainEventPhase.MAIN_EVENT_INTRO;
        MAIN_EVENT_CUTSCENE_DONE.clear();
        mainEventBlackoutPrepared = false;
        EGG_HUNT_COUNTS.clear();
        setSessionPhase(level, FestivalSessionPhase.MAIN_EVENT);
        broadcastFestivalMusic(level, FestivalMusicStatePayload.NONE);
        sendCutsceneState(participants, 0);
        for (ServerPlayer participant : participants) {
            ServerCutsceneTracker.startEvent(participant, MAIN_EVENT_CUTSCENE_ID);
        }
    }

    private static void startAwardCutscene(ServerLevel level) {
        List<ServerPlayer> participants = onlineParticipants(level);
        if (participants.isEmpty()) {
            finishFestival(level);
            return;
        }
        prepareAwardResult(participants);
        mainEventPhase = MainEventPhase.AWARD_CUTSCENE;
        AWARD_CUTSCENE_DONE.clear();
        awardBlackoutPrepared = false;
        playWhistle(level);
        sendCutsceneState(participants, awardWinnerMask(participants));
        for (ServerPlayer participant : participants) {
            ServerCutsceneTracker.startEvent(participant, AWARD_CUTSCENE_ID);
        }
    }

    public static void onCutsceneBlackout(ServerPlayer player, String stage) {
        if (player == null || stage == null) {
            return;
        }
        ServerLevel level = player.serverLevel();
        List<ServerPlayer> participants = onlineParticipants(level);
        if (participants.isEmpty()) {
            return;
        }
        if ("main".equals(stage)) {
            if (mainEventPhase != MainEventPhase.MAIN_EVENT_INTRO || mainEventBlackoutPrepared) {
                return;
            }
            mainEventBlackoutPrepared = true;
            EggFestivalNpcService.startMainEventStage(level);
            placeParticipantsAtLineup(level, participants);
            return;
        }
        if ("award".equals(stage)) {
            if (mainEventPhase != MainEventPhase.AWARD_CUTSCENE || awardBlackoutPrepared) {
                return;
            }
            awardBlackoutPrepared = true;
            EggFestivalNpcService.startAwardStage(level);
            placeParticipantsAtLineup(level, participants);
        }
    }

    public static void onCutsceneCompleted(ServerPlayer player, String eventId) {
        if (player == null || eventId == null) {
            return;
        }
        if (MAIN_EVENT_CUTSCENE_ID.equals(eventId)) {
            MAIN_EVENT_CUTSCENE_DONE.add(player.getUUID());
            List<ServerPlayer> participants = onlineParticipants(player.serverLevel());
            if (mainEventPhase == MainEventPhase.MAIN_EVENT_INTRO
                && !participants.isEmpty()
                && MAIN_EVENT_CUTSCENE_DONE.containsAll(participants.stream().map(ServerPlayer::getUUID).toList())) {
                beginEggHuntOrBlock(player.serverLevel());
            }
            return;
        }
        if (AWARD_CUTSCENE_ID.equals(eventId)) {
            AWARD_CUTSCENE_DONE.add(player.getUUID());
            List<ServerPlayer> participants = onlineParticipants(player.serverLevel());
            if (mainEventPhase == MainEventPhase.AWARD_CUTSCENE
                && !participants.isEmpty()
                && AWARD_CUTSCENE_DONE.containsAll(participants.stream().map(ServerPlayer::getUUID).toList())) {
                mainEventPhase = MainEventPhase.FESTIVAL_ENDING;
                finishFestival(player.serverLevel());
            }
        }
    }

    private static void prepareAwardResult(List<ServerPlayer> participants) {
        AWARD_WINNER_IDS.clear();
        int target = eggsNeededToWin(participants.size());
        int mostEggs = participants.stream()
            .mapToInt(participant -> EGG_HUNT_COUNTS.getOrDefault(participant.getUUID(), 0))
            .max()
            .orElse(0);
        List<ServerPlayer> winners = participants.stream()
            .filter(participant -> EGG_HUNT_COUNTS.getOrDefault(participant.getUUID(), 0) == mostEggs)
            .toList();
        awardPlayerWon = mostEggs >= target;
        if (awardPlayerWon) {
            for (ServerPlayer winner : winners) {
                AWARD_WINNER_IDS.add(winner.getUUID());
            }
            awardWinnerText = winners.size() == 1
                ? winners.get(0).getScoreboardName() + "!"
                : "The winners are " + winnerNames(winners) + "!";
        } else {
            awardWinnerText = "Abigail!";
        }
    }

    private static int awardWinnerMask(List<ServerPlayer> participants) {
        int mask = 0;
        if (!awardPlayerWon) {
            return mask;
        }
        for (int index = 0; index < participants.size() && index < Integer.SIZE - 1; index++) {
            if (AWARD_WINNER_IDS.contains(participants.get(index).getUUID())) {
                mask |= 1 << index;
            }
        }
        return mask;
    }

    private static void sendCutsceneState(List<ServerPlayer> participants, int winnerMask) {
        EggFestivalCutsceneStatePayload payload = new EggFestivalCutsceneStatePayload(
            participants.size(),
            awardPlayerWon,
            winnerMask,
            awardWinnerText,
            participants.stream().map(ServerPlayer::getUUID).toList()
        );
        for (ServerPlayer participant : participants) {
            PacketDistributor.sendToPlayer(participant, payload);
        }
    }

    private static void placeParticipantsAtLineup(ServerLevel level, List<ServerPlayer> participants) {
        int offset = EggFestivalNpcService.mainEventContestantCount();
        for (int index = 0; index < participants.size(); index++) {
            ServerPlayer participant = participants.get(index);
            Vec3 target = EggFestivalNpcService.lineupPosition(offset + index);
            ModTeleport.to(participant, level, target.x, target.y, target.z, NORTH_YAW, 0.0F);
            participant.setDeltaMovement(Vec3.ZERO);
        }
    }

    private static void beginEggHuntOrBlock(ServerLevel level) {
        if (!hasEggHuntSpatialData()) {
            mainEventPhase = MainEventPhase.EGG_HUNT_PENDING_POINTS;
            for (ServerPlayer participant : onlineParticipants(level)) {
                participant.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.hunt_missing_points"), false);
            }
            return;
        }
        spawnEggHuntEntities(level);
        eggHuntEndTick = level.getGameTime() + EGG_HUNT_DURATION_TICKS;
        lastEggHuntHudTick = -1L;
        mainEventPhase = MainEventPhase.EGG_HUNT_ACTIVE;
        EggFestivalNpcService.startHuntStage(level);
        startEggHuntScoreboard(level);
        broadcastFestivalMusic(level, FestivalMusicStatePayload.TICK_TOCK);
        updateEggHuntDisplays(level);
    }

    private static void endEggHunt(ServerLevel level) {
        List<ServerPlayer> participants = onlineParticipants(level);
        if (participants.isEmpty()) {
            finishFestival(level);
            return;
        }
        clearEggHuntEntities(level);
        updateEggHuntScoreboard(level);
        startAwardCutscene(level);
    }

    private static void finishFestival(ServerLevel level) {
        List<ServerPlayer> participants = onlineParticipants(level);
        setSessionPhase(level, FestivalSessionPhase.ENDING);
        jumpToFestivalEndTime(level, participants);
        for (ServerPlayer participant : participants) {
            participant.getPersistentData().putBoolean(TAG_PARTICIPATING, false);
            participant.getPersistentData().putBoolean(TAG_SHOP_OPENED, false);
            syncHud(participant, false);
            returnToFarm(participant);
        }
        START_CONTEST_DIALOG_OPEN.clear();
        EXIT_DIALOG_OPEN.clear();
        EXIT_VOTES.clear();
        clearEggHuntEntities(level);
        clearEggHuntScoreboard(level);
        resetMainEventState();
        stopTimeFreeze();
        EggFestivalNpcService.restore(level);
        FestivalService.endFestival(level, FESTIVAL_ID);
    }

    private static void jumpToFestivalEndTime(ServerLevel level, List<ServerPlayer> participants) {
        if (level == null) {
            return;
        }
        StardewTimeManager timeManager = StardewTimeManager.get();
        long currentVirtual = timeManager.getVirtualDayTime(level);
        long dayBase = Math.floorDiv(currentVirtual, 24000L) * 24000L;
        long targetVirtual = dayBase + com.stardew.craft.event.DimensionEventHandler.stardewMinutesToMcTime(FESTIVAL_END_MINUTE);
        timeManager.setVirtualDayTime(level, targetVirtual);
        timeManager.setCurrentTime(FESTIVAL_END_MINUTE);
        level.setDayTime(targetVirtual);
        ServerLevel miningLevel = level.getServer().getLevel(com.stardew.craft.core.ModMiningDimensions.STARDEW_MINING);
        if (miningLevel != null) {
            miningLevel.setDayTime(targetVirtual);
        }
        TimeSyncPacket packet = TimeSyncPacket.fromTimeManager(timeManager);
        for (ServerPlayer participant : participants) {
            PacketDistributor.sendToPlayer(participant, packet);
        }
    }

    private static void returnToFarm(ServerPlayer player) {
        FarmInstance farm = FarmInstanceRegistry.get().getFarmForPlayer(player.getUUID());
        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (farm == null || stardewLevel == null) {
            player.displayClientMessage(Component.translatable("stardewcraft.warp.farm.unavailable"), true);
            return;
        }
        ModTeleport.to(player, stardewLevel, farm.getSpawnPoint(), farm.getSpawnYaw(), 0.0F);
    }

    public static boolean tryOpenPierreFestivalShop(ServerPlayer player) {
        if (player == null || !isParticipant(player) || !SHOP_ZONE.contains(player.position())) {
            return false;
        }
        player.getPersistentData().putBoolean(TAG_SHOP_OPENED, true);
        openFestivalShop(player);
        return true;
    }

    private static void openFestivalShop(ServerPlayer player) {
        ShopRegistry.ShopDefinition shop = ShopRegistry.get(SHOP_ID);
        if (shop == null) {
            player.displayClientMessage(Component.literal("Unknown shopId: " + SHOP_ID), true);
            return;
        }
        int money = PlayerStardewDataAPI.getMoney(player);
        List<ShopItemEntry> items = ShopRegistry.getFilteredItemsForPlayer(SHOP_ID, shop, player);
        PacketDistributor.sendToPlayer(player, new OpenShopScreenPayload(
            SHOP_ID,
            money,
            items,
            shop.ownerNpcId(),
            shop.ownerDialogue(),
            new ArrayList<>(shop.acceptedSellTypes())
        ));
    }

    public static boolean tryCollectEgg(ServerPlayer player, Entity target) {
        if (player == null || !(target instanceof Interaction) || !target.getTags().contains(TAG_EGG_INTERACTION)) {
            return false;
        }
        if (!isParticipant(player) || mainEventPhase != MainEventPhase.EGG_HUNT_ACTIVE) {
            return false;
        }
        String idTag = eggIdTag(target);
        if (idTag == null) {
            return false;
        }
        int removed = clearEggById(player.serverLevel(), idTag);
        if (removed <= 0) {
            return true;
        }
        EGG_HUNT_COUNTS.merge(player.getUUID(), 1, Integer::sum);
        playCoin(player);
        int remaining = countRemainingEggInteractions(player.serverLevel());
        updateEggHuntDisplays(player.serverLevel());
        if (remaining == 0) {
            for (ServerPlayer participant : onlineParticipants(player.serverLevel())) {
                participant.displayClientMessage(Component.literal("所有彩蛋都已经被收集完了。"), false);
            }
        }
        return true;
    }

    private static void blockUnstartedEntry(ServerLevel level, ServerPlayer player) {
        player.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.setup"), true);
        moveToLastOutside(level, player);
    }

    private static void moveToLastOutside(ServerLevel level, ServerPlayer player) {
        Vec3 target = LAST_OUTSIDE.get(player.getUUID());
        if (target == null || FESTIVAL_BOUNDS.contains(target)) {
            target = pushOutside(player.position());
        }
        ModTeleport.to(player, level, target.x, target.y, target.z, player.getYRot(), player.getXRot());
    }

    private static void moveToLastInside(ServerLevel level, ServerPlayer player) {
        Vec3 target = LAST_INSIDE.get(player.getUUID());
        if (target == null || !FESTIVAL_BOUNDS.contains(target)) {
            target = Vec3.atBottomCenterOf(ENTRY_POS);
        }
        ModTeleport.to(player, level, target.x, target.y, target.z, player.getYRot(), player.getXRot());
    }

    private static Vec3 pushOutside(Vec3 current) {
        double x = current.x;
        double y = current.y;
        double z = current.z;
        double left = Math.abs(x - FESTIVAL_BOUNDS.minX);
        double right = Math.abs(FESTIVAL_BOUNDS.maxX - x);
        double north = Math.abs(z - FESTIVAL_BOUNDS.minZ);
        double south = Math.abs(FESTIVAL_BOUNDS.maxZ - z);
        double min = Math.min(Math.min(left, right), Math.min(north, south));
        if (min == left) {
            x = FESTIVAL_BOUNDS.minX - 0.25D;
        } else if (min == right) {
            x = FESTIVAL_BOUNDS.maxX + 0.25D;
        } else if (min == north) {
            z = FESTIVAL_BOUNDS.minZ - 0.25D;
        } else {
            z = FESTIVAL_BOUNDS.maxZ + 0.25D;
        }
        return new Vec3(x, y, z);
    }

    private static boolean hasOnlineParticipant(ServerLevel level) {
        return level.players().stream().anyMatch(EggFestivalService::isParticipant);
    }

    private static List<ServerPlayer> onlineParticipants(ServerLevel level) {
        return level.players().stream()
            .filter(EggFestivalService::isParticipant)
            .sorted(Comparator.comparing(ServerPlayer::getScoreboardName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private static void syncHud(ServerPlayer player, boolean hidden) {
        boolean current = player.getPersistentData().getBoolean(TAG_HUD_HIDDEN);
        if (current == hidden) {
            return;
        }
        player.getPersistentData().putBoolean(TAG_HUD_HIDDEN, hidden);
        PacketDistributor.sendToPlayer(player, new FestivalHudStatePayload(hidden));
        syncFestivalMusic(player, hidden ? currentFestivalMusicTrack() : FestivalMusicStatePayload.RELEASE);
    }

    private static void clearRuntimeState(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (isParticipant(player)) {
                player.getPersistentData().putBoolean(TAG_PARTICIPATING, false);
            }
            player.getPersistentData().putBoolean(TAG_SHOP_OPENED, false);
            syncHud(player, false);
        }
        LAST_OUTSIDE.clear();
        LAST_INSIDE.clear();
        ENTRY_DIALOG_OPEN.clear();
        START_CONTEST_DIALOG_OPEN.clear();
        EXIT_DIALOG_OPEN.clear();
        EXIT_VOTES.clear();
        clearEggHuntEntities(level);
        clearEggHuntScoreboard(level);
        resetMainEventState();
        stopTimeFreeze();
        EggFestivalNpcService.restore(level);
    }

    private static void updateEggHuntDisplays(ServerLevel level) {
        updateEggHuntScoreboard(level);
        int seconds = eggHuntSecondsRemaining(level);
        for (ServerPlayer participant : onlineParticipants(level)) {
            int count = EGG_HUNT_COUNTS.getOrDefault(participant.getUUID(), 0);
            participant.displayClientMessage(Component.translatable("message.stardewcraft.festival.egg.hud", seconds, count), true);
        }
    }

    private static void playWhistle(ServerLevel level) {
        for (ServerPlayer participant : onlineParticipants(level)) {
            participant.playNotifySound(ModSounds.WHISTLE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    private static void playCoin(ServerPlayer player) {
        player.playNotifySound(ModSounds.COIN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void broadcastFestivalMusic(ServerLevel level, String track) {
        for (ServerPlayer participant : onlineParticipants(level)) {
            syncFestivalMusic(participant, track);
        }
    }

    private static void syncFestivalMusic(ServerPlayer player, String track) {
        PacketDistributor.sendToPlayer(player, new FestivalMusicStatePayload(track));
    }

    private static String currentFestivalMusicTrack() {
        return switch (mainEventPhase) {
            case FREE -> FestivalMusicStatePayload.FALL_FEST;
            case EGG_HUNT_PENDING_POINTS, MAIN_EVENT_INTRO, AWARD_CUTSCENE, FESTIVAL_ENDING -> FestivalMusicStatePayload.EVENT1;
            case EGG_HUNT_ACTIVE -> FestivalMusicStatePayload.TICK_TOCK;
        };
    }

    private static int eggHuntSecondsRemaining(ServerLevel level) {
        if (level == null || mainEventPhase != MainEventPhase.EGG_HUNT_ACTIVE || eggHuntEndTick < 0L) {
            return 0;
        }
        long remainingTicks = Math.max(0L, eggHuntEndTick - level.getGameTime());
        return (int) ((remainingTicks + 19L) / 20L);
    }

    private static int eggsNeededToWin(int playerCount) {
        return switch (Math.max(1, playerCount)) {
            case 1 -> 9;
            case 2 -> 6;
            case 3 -> 5;
            default -> 4;
        };
    }

    private static String winnerNames(List<ServerPlayer> winners) {
        return winners.stream()
            .map(ServerPlayer::getScoreboardName)
            .reduce((left, right) -> left + "、" + right)
            .orElse("<none>");
    }

    private static void startEggHuntScoreboard(ServerLevel level) {
        runServerCommand(level, "scoreboard objectives remove " + EGG_HUNT_SCOREBOARD_OBJECTIVE);
        runServerCommand(level, "scoreboard objectives add " + EGG_HUNT_SCOREBOARD_OBJECTIVE + " dummy {\"text\":\"彩蛋\"}");
        runServerCommand(level, "scoreboard objectives setdisplay sidebar " + EGG_HUNT_SCOREBOARD_OBJECTIVE);
        updateEggHuntScoreboard(level);
    }

    private static void updateEggHuntScoreboard(ServerLevel level) {
        if (level == null) {
            return;
        }
        for (ServerPlayer participant : onlineParticipants(level)) {
            runServerCommand(level, "scoreboard players set " + participant.getScoreboardName() + " "
                + EGG_HUNT_SCOREBOARD_OBJECTIVE + " " + EGG_HUNT_COUNTS.getOrDefault(participant.getUUID(), 0));
        }
    }

    private static void clearEggHuntScoreboard(ServerLevel level) {
        runServerCommand(level, "scoreboard objectives remove " + EGG_HUNT_SCOREBOARD_OBJECTIVE);
    }

    private static void runServerCommand(ServerLevel level, String command) {
        if (level == null || command == null || command.isBlank()) {
            return;
        }
        level.getServer().getCommands().performPrefixedCommand(
            level.getServer().createCommandSourceStack().withSuppressedOutput(), command);
    }

    private static void setSessionPhase(ServerLevel level, FestivalSessionPhase phase) {
        FestivalDefinition definition = FestivalRegistry.get(FESTIVAL_ID).orElse(null);
        if (level == null || definition == null) {
            return;
        }
        StardewTimeManager time = StardewTimeManager.get();
        FestivalWorldData data = FestivalWorldData.get(level);
        FestivalSessionState session = data.getOrCreateSession(
            definition,
            time.getCurrentYear(),
            time.getCurrentSeason(),
            time.getCurrentDay()
        );
        session.setPhase(phase);
        data.setDirty();
    }

    private static boolean hasEggHuntSpatialData() {
        return !currentYearEggSpots().isEmpty();
    }

    private static List<EggSpot> currentYearEggSpots() {
        return StardewTimeManager.get().getCurrentYear() % 2 == 0 ? EVEN_YEAR_EGG_SPOTS : ODD_YEAR_EGG_SPOTS;
    }

    static boolean isInsideFestivalBounds(Vec3 position) {
        return position != null && FESTIVAL_BOUNDS.contains(position);
    }

    static Vec3 randomEggHuntNpcWanderTarget(ServerLevel level) {
        List<EggSpot> spots = currentYearEggSpots();
        if (level == null || spots.isEmpty()) {
            return null;
        }
        EggSpot spot = spots.get(level.getRandom().nextInt(spots.size()));
        return new Vec3(spot.x(), spot.y(), spot.z());
    }

    private static int spawnEggHuntEntities(ServerLevel level) {
        if (level == null) {
            return 0;
        }
        clearEggHuntEntities(level);
        List<EggSpot> spots = currentYearEggSpots();
        int spawned = 0;
        for (int index = 0; index < spots.size(); index++) {
            EggSpot spot = spots.get(index);
            String idTag = eggIdTag(index);
            boolean displaySpawned = spawnEggDisplay(level, index, spot, idTag);
            boolean interactionSpawned = spawnEggInteraction(level, spot, idTag);
            if (displaySpawned && interactionSpawned) {
                spawned++;
            }
        }
        return spawned;
    }

    private static boolean spawnEggDisplay(ServerLevel level, int index, EggSpot spot, String idTag) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", "minecraft:block_display");
        putPos(tag, spot.x(), spot.y(), spot.z());
        putEggTags(tag, TAG_EGG_DISPLAY, idTag);

        CompoundTag blockState = new CompoundTag();
        blockState.putString("Name", "stardewcraft:egg_festival_egg");
        CompoundTag properties = new CompoundTag();
        properties.putString("variant", Integer.toString(index % 4 + 1));
        blockState.put("Properties", properties);
        tag.put("block_state", blockState);

        Entity entity = EntityType.loadEntityRecursive(tag, level, e -> e);
        return entity != null && level.addFreshEntity(entity);
    }

    private static boolean spawnEggInteraction(ServerLevel level, EggSpot spot, String idTag) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", "minecraft:interaction");
        tag.putFloat("width", EGG_INTERACTION_SIZE);
        tag.putFloat("height", EGG_INTERACTION_SIZE);
        tag.putBoolean("response", true);
        putPos(
            tag,
            spot.x() + EGG_INTERACTION_OFFSET_X,
            spot.y() + EGG_INTERACTION_OFFSET_Y,
            spot.z() + EGG_INTERACTION_OFFSET_Z
        );
        putEggTags(tag, TAG_EGG_INTERACTION, idTag);

        Entity entity = EntityType.loadEntityRecursive(tag, level, e -> e);
        return entity != null && level.addFreshEntity(entity);
    }

    private static int clearEggHuntEntities(ServerLevel level) {
        if (level == null) {
            return 0;
        }
        int removed = 0;
        for (Entity entity : level.getEntitiesOfClass(Entity.class, FESTIVAL_BOUNDS.inflate(8.0D), EggFestivalService::isEggHuntEntity)) {
            entity.discard();
            removed++;
        }
        return removed;
    }

    private static int clearEggById(ServerLevel level, String idTag) {
        int removed = 0;
        for (Entity entity : level.getEntitiesOfClass(Entity.class, FESTIVAL_BOUNDS.inflate(8.0D), entity -> entity.getTags().contains(idTag))) {
            entity.discard();
            removed++;
        }
        return removed;
    }

    private static int countRemainingEggInteractions(ServerLevel level) {
        return countEggEntities(level, TAG_EGG_INTERACTION);
    }

    private static int countEggEntities(ServerLevel level, String tag) {
        if (level == null) {
            return 0;
        }
        return level.getEntitiesOfClass(Entity.class, FESTIVAL_BOUNDS.inflate(8.0D), entity -> entity.getTags().contains(tag)).size();
    }

    private static boolean isEggHuntEntity(Entity entity) {
        return entity.getTags().contains(TAG_EGG_ROOT)
            || entity.getTags().contains(TAG_EGG_DISPLAY)
            || entity.getTags().contains(TAG_EGG_INTERACTION);
    }

    private static String eggIdTag(Entity entity) {
        for (String tag : entity.getTags()) {
            if (tag.startsWith(TAG_EGG_ID_PREFIX)) {
                return tag;
            }
        }
        return null;
    }

    private static String eggIdTag(int index) {
        return TAG_EGG_ID_PREFIX + index;
    }

    private static void putPos(CompoundTag tag, double x, double y, double z) {
        ListTag pos = new ListTag();
        pos.add(DoubleTag.valueOf(x));
        pos.add(DoubleTag.valueOf(y));
        pos.add(DoubleTag.valueOf(z));
        tag.put("Pos", pos);
    }

    private static void putEggTags(CompoundTag tag, String roleTag, String idTag) {
        ListTag tags = new ListTag();
        tags.add(StringTag.valueOf(TAG_EGG_ROOT));
        tags.add(StringTag.valueOf(roleTag));
        tags.add(StringTag.valueOf(idTag));
        tag.put("Tags", tags);
    }

    private static void startTimeFreeze(ServerLevel level) {
        if (frozenMinute == null) {
            frozenMinute = FESTIVAL_START_MINUTE;
        }
        if (level != null && frozenOverworldDayTime == null) {
            frozenOverworldDayTime = level.getServer().overworld().getDayTime();
        }
    }

    private static void stopTimeFreeze() {
        frozenMinute = null;
        frozenOverworldDayTime = null;
    }

    private static void resetMainEventState() {
        MAIN_EVENT_CUTSCENE_DONE.clear();
        AWARD_CUTSCENE_DONE.clear();
        EGG_HUNT_COUNTS.clear();
        AWARD_WINNER_IDS.clear();
        awardPlayerWon = false;
        awardWinnerText = "";
        mainEventBlackoutPrepared = false;
        awardBlackoutPrepared = false;
        EggFestivalNpcService.setHuntStageActive(false);
        mainEventPhase = MainEventPhase.FREE;
        eggHuntEndTick = -1L;
        lastEggHuntHudTick = -1L;
    }

    private enum MainEventPhase {
        FREE,
        MAIN_EVENT_INTRO,
        EGG_HUNT_ACTIVE,
        AWARD_CUTSCENE,
        FESTIVAL_ENDING,
        EGG_HUNT_PENDING_POINTS
    }

    private record EggSpot(double x, double y, double z) {
    }

    private record PlayerPose(Vec3 position, float yaw, float pitch) {
    }

    private static AABB inclusiveBox(BlockPos a, BlockPos b) {
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxX = Math.max(a.getX(), b.getX()) + 1;
        int maxY = Math.max(a.getY(), b.getY()) + 1;
        int maxZ = Math.max(a.getZ(), b.getZ()) + 1;
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}