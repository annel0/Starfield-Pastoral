package com.stardew.craft.festival;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.cutscene.server.ServerCutsceneTracker;
import com.stardew.craft.farm.FarmInstance;
import com.stardew.craft.farm.FarmInstanceRegistry;
import com.stardew.craft.festival.client.FlowerDanceCutsceneClientState;
import com.stardew.craft.network.TimeSyncPacket;
import com.stardew.craft.network.payload.FlowerDanceCutsceneStatePayload;
import com.stardew.craft.network.payload.FestivalHudStatePayload;
import com.stardew.craft.network.payload.OpenFlowerDancePlayerAskPayload;
import com.stardew.craft.network.payload.OpenFlowerDancePlayerInvitePayload;
import com.stardew.craft.network.payload.OpenFestivalConfirmPayload;
import com.stardew.craft.network.payload.OpenFlowerDanceInvitePayload;
import com.stardew.craft.network.payload.OpenShopScreenPayload;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.shop.ShopItemEntry;
import com.stardew.craft.shop.ShopRegistry;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.warp.ModTeleport;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class FlowerDanceService {
    public static final String FESTIVAL_ID = "spring24";
    private static final String OVERLAY_ID = "Forest-FlowerFestival";
    private static final String MAIN_EVENT_CUTSCENE_ID = "flower_dance_main_event";
    private static final int FESTIVAL_START_MINUTE = 9 * 60;
    private static final int FESTIVAL_END_MINUTE = 22 * 60;
    private static final long PLAYER_INVITE_TIMEOUT_TICKS = 1200L;
    private static final AABB ENTRY_TRIGGER_BOUNDS = inclusiveBox(new BlockPos(-56, 80, 3), new BlockPos(-283, 58, 157));
    private static final BlockPos ENTRY_POS = new BlockPos(-176, 64, 106);
    private static final float WEST_YAW = 90.0F;
    private static final AABB PIERRE_SHOP_ZONE = inclusiveBox(new BlockPos(-224, 63, 135), new BlockPos(-219, 59, 137));
    private static final String SHOP_ID = "Festival_FlowerDance_Pierre";
    private static final Set<String> DANCEABLE_NPC_IDS = Set.of(
        "abigail", "alex", "elliott", "emily", "haley", "harvey",
        "leah", "maru", "penny", "sam", "sebastian", "shane"
    );
    private static final Set<String> FEMALE_DANCEABLE_NPC_IDS = Set.of(
        "abigail", "emily", "haley", "leah", "maru", "penny"
    );
    private static final List<DefaultDancePair> DEFAULT_DANCE_PAIRS = List.of(
        new DefaultDancePair("emily", "shane"),
        new DefaultDancePair("haley", "alex"),
        new DefaultDancePair("maru", "harvey"),
        new DefaultDancePair("leah", "elliott"),
        new DefaultDancePair("penny", "sam"),
        new DefaultDancePair("abigail", "sebastian")
    );
    private static final int MAX_DANCE_PAIRS = 18;
    private static final int MAX_SPECTATORS = 10;

    private static final String TAG_PARTICIPATING = "stardewcraft_flower_dance_participating";
    private static final String TAG_HUD_HIDDEN = "stardewcraft_flower_dance_hud_hidden";

    private static final Map<UUID, Vec3> LAST_OUTSIDE_ENTRY = new LinkedHashMap<>();
    private static final Map<UUID, PartnerSelection> DANCE_PARTNERS = new LinkedHashMap<>();
    private static final Map<UUID, Set<String>> FESTIVAL_DIALOGUE_SEEN = new LinkedHashMap<>();
    private static final Map<UUID, PendingPlayerInvite> PENDING_PLAYER_INVITES = new LinkedHashMap<>();
    private static final Set<UUID> ENTRY_DIALOG_OPEN = new LinkedHashSet<>();
    private static final Set<UUID> START_DANCE_DIALOG_OPEN = new LinkedHashSet<>();
    private static final Set<UUID> EXIT_DIALOG_OPEN = new LinkedHashSet<>();
    private static final Set<UUID> EXIT_VOTES = new LinkedHashSet<>();
    private static final Set<UUID> EXIT_VOTE_PARTICIPANTS = new LinkedHashSet<>();
    private static Integer frozenMinute;
    private static Long frozenOverworldDayTime;
    private static MainEventPhase mainEventPhase = MainEventPhase.FREE;
    private static boolean mainEventStagePrepared;
    private static final Set<UUID> MAIN_EVENT_CUTSCENE_PARTICIPANTS = new LinkedHashSet<>();
    private static final Set<UUID> MAIN_EVENT_CUTSCENE_DONE = new LinkedHashSet<>();

    private FlowerDanceService() {
    }

    public static void tick(ServerLevel level) {
        if (level == null) {
            return;
        }
        boolean activeDay = isActiveFlowerDanceDay();
        if (!activeDay) {
            clearRuntimeState(level);
            return;
        }
        if (!hasOnlineParticipant(level)) {
            stopTimeFreeze();
        }
        expirePlayerInvites(level);
        tickMainEvent(level);
        for (ServerPlayer player : level.players()) {
            tickPlayer(level, player);
        }
    }

    public static void tickNpcActors(ServerLevel level) {
        if (level == null) {
            return;
        }
        boolean activeDay = isActiveFlowerDanceDay();
        if (!activeDay) {
            FlowerDanceNpcService.tick(level, false);
            return;
        }
        boolean overlayApplied = FestivalMapOverlayManager.isApplied(level, OVERLAY_ID);
        boolean venueActive = overlayApplied && (FestivalService.isActiveFestivalEntryOpen(level, FESTIVAL_ID) || hasOnlineParticipant(level));
        FlowerDanceNpcService.tick(level, venueActive);
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

    public static boolean isTimeFreezeActive() {
        if (frozenMinute == null) {
            return false;
        }
        var server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel stardewLevel = server == null ? null : server.getLevel(ModDimensions.STARDEW_VALLEY);
        return stardewLevel != null && hasOnlineParticipant(stardewLevel);
    }

    public static boolean handlesConfirmation(ServerPlayer player, OpenFestivalConfirmPayload.Action action) {
        if (player == null || action == null) {
            return false;
        }
        if (isParticipant(player) || ENTRY_DIALOG_OPEN.contains(player.getUUID()) || START_DANCE_DIALOG_OPEN.contains(player.getUUID()) || EXIT_DIALOG_OPEN.contains(player.getUUID())) {
            return action == OpenFestivalConfirmPayload.Action.ENTER
                || action == OpenFestivalConfirmPayload.Action.START_DANCE
                || action == OpenFestivalConfirmPayload.Action.EXIT;
        }
        return isActiveFlowerDanceDay()
            && (action == OpenFestivalConfirmPayload.Action.ENTER
                || action == OpenFestivalConfirmPayload.Action.START_DANCE
                || action == OpenFestivalConfirmPayload.Action.EXIT);
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
        if (action == OpenFestivalConfirmPayload.Action.START_DANCE) {
            START_DANCE_DIALOG_OPEN.remove(player.getUUID());
            if (!isParticipant(player)) {
                return;
            }
            if (confirmed) {
                startMainEventAfterConfirmation(player);
            } else {
                player.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.start_cancelled"), true);
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

    public static boolean canAskNpcToDance(String npcId) {
        return DANCEABLE_NPC_IDS.contains(canonical(npcId));
    }

    public static boolean hasDancePartner(ServerPlayer player) {
        return player != null && DANCE_PARTNERS.containsKey(player.getUUID());
    }

    public static boolean isMainEventCutsceneActive() {
        return mainEventPhase == MainEventPhase.MAIN_EVENT;
    }

    public static boolean tryStartMainEvent(ServerPlayer player) {
        if (player == null || !isParticipant(player)) {
            return false;
        }
        if (mainEventPhase == MainEventPhase.MAIN_EVENT || mainEventPhase == MainEventPhase.ENDING) {
            return true;
        }
        promptStartDance(player);
        return true;
    }

    public static boolean tryOpenPlayerDanceAsk(ServerPlayer sender, ServerPlayer target) {
        if (sender == null || target == null || sender.getUUID().equals(target.getUUID())) {
            return false;
        }
        if (!isParticipant(sender) || !isParticipant(target)) {
            return false;
        }
        if (hasDancePartner(sender)) {
            sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.already_have_partner"), true);
            return true;
        }
        if (hasDancePartner(target)) {
            sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.target_has_partner", target.getScoreboardName()), true);
            return true;
        }
        PacketDistributor.sendToPlayer(sender, new OpenFlowerDancePlayerAskPayload(target.getUUID(), target.getScoreboardName()));
        return true;
    }

    public static void handlePlayerDanceAskResponse(ServerPlayer sender, UUID targetPlayerId, boolean confirmed) {
        if (sender == null || targetPlayerId == null || !confirmed) {
            return;
        }
        ServerPlayer target = sender.server.getPlayerList().getPlayer(targetPlayerId);
        if (target == null || !isParticipant(sender) || !isParticipant(target)) {
            sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_unavailable"), true);
            return;
        }
        if (hasDancePartner(sender)) {
            sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.already_have_partner"), true);
            return;
        }
        if (hasDancePartner(target)) {
            sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.target_has_partner", target.getScoreboardName()), true);
            return;
        }

        cancelPendingPlayerInvitesFor(sender.getUUID());
        cancelPendingPlayerInvitesFor(target.getUUID());
        UUID inviteId = UUID.randomUUID();
        PendingPlayerInvite invite = new PendingPlayerInvite(inviteId, sender.getUUID(), target.getUUID(), sender.serverLevel().getGameTime());
        PENDING_PLAYER_INVITES.put(inviteId, invite);
        PacketDistributor.sendToPlayer(target, new OpenFlowerDancePlayerInvitePayload(inviteId, sender.getScoreboardName()));
        sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_sent", target.getScoreboardName()), true);
    }

    public static void handlePlayerDanceInviteResponse(ServerPlayer target, UUID inviteId, boolean accepted) {
        if (target == null || inviteId == null) {
            return;
        }
        PendingPlayerInvite invite = PENDING_PLAYER_INVITES.remove(inviteId);
        if (invite == null || !target.getUUID().equals(invite.targetId())) {
            target.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_expired"), true);
            return;
        }
        ServerPlayer sender = target.server.getPlayerList().getPlayer(invite.senderId());
        if (sender == null || !isParticipant(sender) || !isParticipant(target)) {
            target.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_unavailable"), true);
            return;
        }
        if (!accepted) {
            sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_rejected", target.getScoreboardName()), true);
            return;
        }
        if (hasDancePartner(sender) || hasDancePartner(target)) {
            sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_unavailable"), true);
            target.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_unavailable"), true);
            return;
        }
        DANCE_PARTNERS.put(sender.getUUID(), PartnerSelection.player(target.getUUID()));
        DANCE_PARTNERS.put(target.getUUID(), PartnerSelection.player(sender.getUUID()));
        sender.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_invite_accepted", target.getScoreboardName()), true);
        target.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.player_partner_set", sender.getScoreboardName()), true);
    }

    public static void onPlayerLogout(ServerPlayer player) {
        if (player == null) {
            return;
        }
        UUID playerId = player.getUUID();
        cancelPendingPlayerInvitesFor(playerId);
        PartnerSelection selection = DANCE_PARTNERS.remove(playerId);
        if (selection != null && selection.kind() == PartnerKind.PLAYER && selection.playerId() != null) {
            DANCE_PARTNERS.remove(selection.playerId());
        }
        FESTIVAL_DIALOGUE_SEEN.remove(playerId);
        LAST_OUTSIDE_ENTRY.remove(playerId);
    }

    public static boolean hasFestivalDialogueSeen(ServerPlayer player, String npcId) {
        if (player == null) {
            return false;
        }
        Set<String> seen = FESTIVAL_DIALOGUE_SEEN.get(player.getUUID());
        return seen != null && seen.contains(canonical(npcId));
    }

    public static void markFestivalDialogueSeen(ServerPlayer player, String npcId) {
        if (player == null || npcId == null || npcId.isBlank()) {
            return;
        }
        FESTIVAL_DIALOGUE_SEEN.computeIfAbsent(player.getUUID(), ignored -> new LinkedHashSet<>()).add(canonical(npcId));
    }

    public static boolean tryOpenNpcDanceInvite(ServerPlayer player, String npcId) {
        if (player == null || !isParticipant(player) || hasDancePartner(player) || !canAskNpcToDance(npcId)) {
            return false;
        }
        if (!hasFestivalDialogueSeen(player, npcId)) {
            return false;
        }
        PacketDistributor.sendToPlayer(player, new OpenFlowerDanceInvitePayload(canonical(npcId)));
        return true;
    }

    public static boolean isNpcDancePartnerTaken(String npcId) {
        String canonicalNpcId = canonical(npcId);
        if (canonicalNpcId.isBlank()) {
            return false;
        }
        for (PartnerSelection selection : DANCE_PARTNERS.values()) {
            if (selection.kind() == PartnerKind.NPC && canonicalNpcId.equals(selection.npcId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean setNpcDancePartner(ServerPlayer player, String npcId) {
        if (player == null || !isParticipant(player) || hasDancePartner(player) || !canAskNpcToDance(npcId) || isNpcDancePartnerTaken(npcId)) {
            return false;
        }
        DANCE_PARTNERS.put(player.getUUID(), PartnerSelection.npc(canonical(npcId)));
        return true;
    }

    public static boolean tryOpenPierreFestivalShop(ServerPlayer player) {
        if (player == null || !isParticipant(player) || !PIERRE_SHOP_ZONE.contains(player.position())) {
            return false;
        }
        ShopRegistry.ShopDefinition shop = ShopRegistry.get(SHOP_ID);
        if (shop == null) {
            player.displayClientMessage(Component.literal("Unknown shopId: " + SHOP_ID), true);
            return true;
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
        return true;
    }

    public static void startDebugFestival(ServerLevel level) {
        if (level == null) {
            return;
        }
        FestivalService.setDebugActiveFestival(FESTIVAL_ID);
        startTimeFreeze(level);
        ENTRY_DIALOG_OPEN.clear();
        EXIT_DIALOG_OPEN.clear();
        EXIT_VOTES.clear();
        EXIT_VOTE_PARTICIPANTS.clear();
        LAST_OUTSIDE_ENTRY.clear();
        DANCE_PARTNERS.clear();
        FESTIVAL_DIALOGUE_SEEN.clear();
        PENDING_PLAYER_INVITES.clear();
        for (ServerPlayer player : level.players()) {
            player.getPersistentData().putBoolean(TAG_PARTICIPATING, true);
            syncHud(player, true);
        }
        FlowerDanceNpcService.requestDebugStart(level);
    }

    public static void restoreDebugFestival(ServerLevel level) {
        if (level == null) {
            return;
        }
        FestivalService.clearDebugActiveFestival(FESTIVAL_ID);
        clearRuntimeState(level);
        FlowerDanceNpcService.restore(level);
    }

    public static String debugStatus(ServerLevel level) {
        int participants = level == null ? 0 : onlineParticipants(level).size();
        return "Flower Dance: participants=" + participants
            + " partners=" + DANCE_PARTNERS.size()
            + " pendingPlayerInvites=" + PENDING_PLAYER_INVITES.size()
            + " mainEventPhase=" + mainEventPhase.name()
            + " cutsceneDone=" + MAIN_EVENT_CUTSCENE_DONE.size() + "/" + MAIN_EVENT_CUTSCENE_PARTICIPANTS.size()
            + " overlayApplied=" + (level != null && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID))
            + " freeze=" + (frozenMinute == null ? "none" : frozenMinute)
            + "\n" + FlowerDanceNpcService.debugStatus(level);
    }

    private static void enterFestival(ServerPlayer player) {
        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        if (!FestivalService.isActiveFestivalEntryOpen(player.serverLevel(), FESTIVAL_ID)) {
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.setup"), true);
            return;
        }
        if (FestivalService.openActiveFestival(player, FESTIVAL_ID).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.unavailable"), true);
            return;
        }
        startTimeFreeze(player.serverLevel());
        player.getPersistentData().putBoolean(TAG_PARTICIPATING, true);
        syncHud(player, true);
        ModTeleport.to(player, player.serverLevel(), ENTRY_POS, WEST_YAW, 0.0F);
    }

    private static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (player == null || player.isSpectator()) {
            return;
        }
        boolean insideEntry = ENTRY_TRIGGER_BOUNDS.contains(player.position());
        if (!insideEntry) {
            LAST_OUTSIDE_ENTRY.put(player.getUUID(), player.position());
            ENTRY_DIALOG_OPEN.remove(player.getUUID());
            if (!isParticipant(player)) {
                syncHud(player, false);
            }
            return;
        }

        if (isParticipant(player)) {
            syncHud(player, true);
            return;
        }

        syncHud(player, false);
        if (!FestivalService.isActiveFestivalEntryOpen(level, FESTIVAL_ID)) {
            player.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.setup"), true);
            moveToLastOutsideEntry(level, player);
            return;
        }
        promptEnter(player);
        moveToLastOutsideEntry(level, player);
    }

    private static void promptEnter(ServerPlayer player) {
        if (ENTRY_DIALOG_OPEN.add(player.getUUID())) {
            PacketDistributor.sendToPlayer(player, new OpenFestivalConfirmPayload(OpenFestivalConfirmPayload.Action.ENTER));
        }
    }

    private static void promptStartDance(ServerPlayer player) {
        if (START_DANCE_DIALOG_OPEN.add(player.getUUID())) {
            PacketDistributor.sendToPlayer(player, new OpenFestivalConfirmPayload(OpenFestivalConfirmPayload.Action.START_DANCE));
        }
    }

    private static void startMainEventAfterConfirmation(ServerPlayer player) {
        if (player == null || !isParticipant(player) || mainEventPhase != MainEventPhase.FREE) {
            return;
        }
        startMainEventCutscene(player.serverLevel());
    }

    private static void startMainEventCutscene(ServerLevel level) {
        List<ServerPlayer> participants = onlineParticipants(level);
        if (participants.isEmpty()) {
            return;
        }
        mainEventPhase = MainEventPhase.MAIN_EVENT;
        mainEventStagePrepared = false;
        MAIN_EVENT_CUTSCENE_PARTICIPANTS.clear();
        MAIN_EVENT_CUTSCENE_PARTICIPANTS.addAll(participants.stream().map(ServerPlayer::getUUID).toList());
        MAIN_EVENT_CUTSCENE_DONE.clear();
        START_DANCE_DIALOG_OPEN.clear();
        PENDING_PLAYER_INVITES.clear();
        setSessionPhase(level, FestivalSessionPhase.MAIN_EVENT);
        sendCutsceneState(participants);
        for (ServerPlayer participant : participants) {
            ServerCutsceneTracker.startEvent(participant, MAIN_EVENT_CUTSCENE_ID);
        }
    }

    public static void onCutsceneStage(ServerPlayer player, String stage) {
        if (player == null || stage == null || !"main".equals(stage)) {
            return;
        }
        if (mainEventPhase != MainEventPhase.MAIN_EVENT || mainEventStagePrepared) {
            return;
        }
        mainEventStagePrepared = true;
    }

    public static void onCutsceneCompleted(ServerPlayer player, String eventId) {
        if (player == null || !MAIN_EVENT_CUTSCENE_ID.equals(eventId)) {
            return;
        }
        MAIN_EVENT_CUTSCENE_DONE.add(player.getUUID());
        List<ServerPlayer> participants = onlineSnapshotParticipants(player.serverLevel(), MAIN_EVENT_CUTSCENE_PARTICIPANTS);
        if (mainEventPhase == MainEventPhase.MAIN_EVENT
            && (participants.isEmpty() || containsAllOnlineParticipants(MAIN_EVENT_CUTSCENE_DONE, participants))) {
            mainEventPhase = MainEventPhase.ENDING;
            for (ServerPlayer participant : participants) {
                participant.displayClientMessage(Component.translatable("message.stardewcraft.festival.flower_dance.finished"), false);
            }
            finishFestival(player.serverLevel());
        }
    }

    private static void moveToLastOutsideEntry(ServerLevel level, ServerPlayer player) {
        Vec3 target = LAST_OUTSIDE_ENTRY.get(player.getUUID());
        if (target == null || ENTRY_TRIGGER_BOUNDS.contains(target)) {
            target = pushOutsideEntry(player.position());
        }
        ModTeleport.to(player, level, target.x, target.y, target.z, player.getYRot(), player.getXRot());
    }

    private static Vec3 pushOutsideEntry(Vec3 current) {
        double x = current.x;
        double y = current.y;
        double z = current.z;
        double left = Math.abs(x - ENTRY_TRIGGER_BOUNDS.minX);
        double right = Math.abs(ENTRY_TRIGGER_BOUNDS.maxX - x);
        double north = Math.abs(z - ENTRY_TRIGGER_BOUNDS.minZ);
        double south = Math.abs(ENTRY_TRIGGER_BOUNDS.maxZ - z);
        double min = Math.min(Math.min(left, right), Math.min(north, south));
        if (min == left) {
            x = ENTRY_TRIGGER_BOUNDS.minX - 0.25D;
        } else if (min == right) {
            x = ENTRY_TRIGGER_BOUNDS.maxX + 0.25D;
        } else if (min == north) {
            z = ENTRY_TRIGGER_BOUNDS.minZ - 0.25D;
        } else {
            z = ENTRY_TRIGGER_BOUNDS.maxZ + 0.25D;
        }
        return new Vec3(x, y, z);
    }

    private static void castExitVote(ServerPlayer player) {
        if (EXIT_VOTE_PARTICIPANTS.isEmpty()) {
            EXIT_VOTE_PARTICIPANTS.addAll(onlineParticipants(player.serverLevel()).stream().map(ServerPlayer::getUUID).toList());
        }
        EXIT_VOTES.retainAll(EXIT_VOTE_PARTICIPANTS);
        EXIT_VOTES.add(player.getUUID());
        List<ServerPlayer> voters = onlineVoteParticipants(player.serverLevel(), EXIT_VOTE_PARTICIPANTS);
        int voteCount = voteCount(voters, EXIT_VOTES);
        if (voters.isEmpty() || voteCount >= voters.size()) {
            finishFestival(player.serverLevel());
            return;
        }
        for (ServerPlayer participant : voters) {
            if (!EXIT_VOTES.contains(participant.getUUID()) && EXIT_DIALOG_OPEN.add(participant.getUUID())) {
                PacketDistributor.sendToPlayer(participant, new OpenFestivalConfirmPayload(OpenFestivalConfirmPayload.Action.EXIT));
            }
            participant.displayClientMessage(Component.translatable(
                "message.stardewcraft.festival.exit_vote_waiting", voteCount, voters.size()), true);
        }
    }

    private static void finishFestival(ServerLevel level) {
        List<ServerPlayer> participants = onlineParticipants(level);
        setSessionPhase(level, FestivalSessionPhase.ENDING);
        jumpToFestivalEndTime(level, participants);
        for (ServerPlayer participant : participants) {
            participant.getPersistentData().putBoolean(TAG_PARTICIPATING, false);
            syncHud(participant, false);
            returnToFarm(participant);
        }
        ENTRY_DIALOG_OPEN.clear();
        START_DANCE_DIALOG_OPEN.clear();
        EXIT_DIALOG_OPEN.clear();
        EXIT_VOTES.clear();
        EXIT_VOTE_PARTICIPANTS.clear();
        START_DANCE_DIALOG_OPEN.clear();
        MAIN_EVENT_CUTSCENE_PARTICIPANTS.clear();
        MAIN_EVENT_CUTSCENE_DONE.clear();
        mainEventStagePrepared = false;
        mainEventPhase = MainEventPhase.FREE;
        stopTimeFreeze();
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

    private static boolean hasOnlineParticipant(ServerLevel level) {
        return level != null && level.players().stream().anyMatch(FlowerDanceService::isParticipant);
    }

    private static List<ServerPlayer> onlineParticipants(ServerLevel level) {
        if (level == null) {
            return List.of();
        }
        return level.players().stream()
            .filter(FlowerDanceService::isParticipant)
            .sorted(Comparator.comparing(ServerPlayer::getScoreboardName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private static List<ServerPlayer> onlineVoteParticipants(ServerLevel level, Set<UUID> participantIds) {
        if (level == null || participantIds.isEmpty()) {
            return List.of();
        }
        return level.players().stream()
            .filter(player -> participantIds.contains(player.getUUID()))
            .filter(FlowerDanceService::isParticipant)
            .sorted(Comparator.comparing(ServerPlayer::getScoreboardName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private static List<ServerPlayer> onlineSnapshotParticipants(ServerLevel level, Set<UUID> participantIds) {
        if (level == null || participantIds.isEmpty()) {
            return List.of();
        }
        return level.players().stream()
            .filter(player -> participantIds.contains(player.getUUID()))
            .filter(FlowerDanceService::isParticipant)
            .sorted(Comparator.comparing(ServerPlayer::getScoreboardName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private static boolean containsAllOnlineParticipants(Set<UUID> done, List<ServerPlayer> participants) {
        if (participants.isEmpty()) {
            return false;
        }
        for (ServerPlayer participant : participants) {
            if (!done.contains(participant.getUUID())) {
                return false;
            }
        }
        return true;
    }

    private static void tickMainEvent(ServerLevel level) {
        if (mainEventPhase != MainEventPhase.MAIN_EVENT) {
            return;
        }
        if (onlineParticipants(level).isEmpty()) {
            finishFestival(level);
            return;
        }
        if (onlineSnapshotParticipants(level, MAIN_EVENT_CUTSCENE_PARTICIPANTS).isEmpty()) {
            finishFestival(level);
        }
    }

    private static void sendCutsceneState(List<ServerPlayer> participants) {
        DanceFormation formation = buildDanceFormation(participants);
        FlowerDanceCutsceneStatePayload payload = new FlowerDanceCutsceneStatePayload(formation.pairs(), formation.spectators());
        for (ServerPlayer participant : participants) {
            PacketDistributor.sendToPlayer(participant, payload);
        }
    }

    private static DanceFormation buildDanceFormation(List<ServerPlayer> participants) {
        Map<UUID, ServerPlayer> byId = new LinkedHashMap<>();
        for (ServerPlayer participant : participants) {
            byId.put(participant.getUUID(), participant);
        }
        List<ServerPlayer> sourcePlayers = participants.stream()
            .sorted(Comparator.comparing(player -> player.getUUID().toString()))
            .toList();
        List<FlowerDanceCutsceneClientState.DancePair> sourcePairs = new ArrayList<>();
        Set<UUID> pairedPlayers = new LinkedHashSet<>();
        Set<String> occupiedNpcs = new LinkedHashSet<>();

        for (ServerPlayer player : sourcePlayers) {
            UUID playerId = player.getUUID();
            if (pairedPlayers.contains(playerId)) {
                continue;
            }
            PartnerSelection selection = DANCE_PARTNERS.get(playerId);
            if (selection == null) {
                continue;
            }
            if (selection.kind() == PartnerKind.NPC) {
                String npcId = canonical(selection.npcId());
                if (!canAskNpcToDance(npcId) || occupiedNpcs.contains(npcId)) {
                    continue;
                }
                occupiedNpcs.add(npcId);
                pairedPlayers.add(playerId);
                sourcePairs.add(pairForPlayerAndNpc(playerId, npcId));
                continue;
            }
            UUID partnerId = selection.playerId();
            if (partnerId == null || pairedPlayers.contains(partnerId) || !byId.containsKey(partnerId)) {
                continue;
            }
            pairedPlayers.add(playerId);
            pairedPlayers.add(partnerId);
            sourcePairs.add(pairForPlayers(playerId, partnerId));
        }

        for (DefaultDancePair defaultPair : DEFAULT_DANCE_PAIRS) {
            if (sourcePairs.size() >= 6) {
                break;
            }
            if (occupiedNpcs.contains(defaultPair.femaleNpcId()) || occupiedNpcs.contains(defaultPair.maleNpcId())) {
                continue;
            }
            occupiedNpcs.add(defaultPair.femaleNpcId());
            occupiedNpcs.add(defaultPair.maleNpcId());
            sourcePairs.add(new FlowerDanceCutsceneClientState.DancePair(
                FlowerDanceCutsceneClientState.Partner.npc(defaultPair.femaleNpcId()),
                FlowerDanceCutsceneClientState.Partner.npc(defaultPair.maleNpcId())
            ));
        }

        List<FlowerDanceCutsceneClientState.DancePair> displayPairs = sourceOrderedDisplayPairs(sourcePairs);
        if (displayPairs.size() > MAX_DANCE_PAIRS) {
            displayPairs = List.copyOf(displayPairs.subList(0, MAX_DANCE_PAIRS));
        }
        List<FlowerDanceCutsceneClientState.Partner> spectators = new ArrayList<>();
        for (ServerPlayer participant : participants) {
            if (spectators.size() >= MAX_SPECTATORS) {
                break;
            }
            if (!pairedPlayers.contains(participant.getUUID())) {
                spectators.add(FlowerDanceCutsceneClientState.Partner.player(participant.getUUID()));
            }
        }
        for (DefaultDancePair defaultPair : DEFAULT_DANCE_PAIRS) {
            if (spectators.size() >= MAX_SPECTATORS) {
                break;
            }
            if (!occupiedNpcs.contains(defaultPair.femaleNpcId())) {
                spectators.add(FlowerDanceCutsceneClientState.Partner.npc(defaultPair.femaleNpcId()));
            }
            if (spectators.size() >= MAX_SPECTATORS) {
                break;
            }
            if (!occupiedNpcs.contains(defaultPair.maleNpcId())) {
                spectators.add(FlowerDanceCutsceneClientState.Partner.npc(defaultPair.maleNpcId()));
            }
        }
        return new DanceFormation(displayPairs, spectators);
    }

    private static List<FlowerDanceCutsceneClientState.DancePair> sourceOrderedDisplayPairs(List<FlowerDanceCutsceneClientState.DancePair> sourcePairs) {
        List<FlowerDanceCutsceneClientState.DancePair> display = new ArrayList<>();
        boolean addLeft = true;
        for (FlowerDanceCutsceneClientState.DancePair pair : sourcePairs) {
            if (addLeft) {
                display.add(0, pair);
            } else {
                display.add(pair);
            }
            addLeft = !addLeft;
        }
        return display;
    }

    private static FlowerDanceCutsceneClientState.DancePair pairForPlayerAndNpc(UUID playerId, String npcId) {
        FlowerDanceCutsceneClientState.Partner player = FlowerDanceCutsceneClientState.Partner.player(playerId);
        FlowerDanceCutsceneClientState.Partner npc = FlowerDanceCutsceneClientState.Partner.npc(npcId);
        if (FEMALE_DANCEABLE_NPC_IDS.contains(npcId)) {
            return new FlowerDanceCutsceneClientState.DancePair(npc, player);
        }
        return new FlowerDanceCutsceneClientState.DancePair(player, npc);
    }

    private static FlowerDanceCutsceneClientState.DancePair pairForPlayers(UUID firstPlayerId, UUID secondPlayerId) {
        if (firstPlayerId.toString().compareTo(secondPlayerId.toString()) <= 0) {
            return new FlowerDanceCutsceneClientState.DancePair(
                FlowerDanceCutsceneClientState.Partner.player(firstPlayerId),
                FlowerDanceCutsceneClientState.Partner.player(secondPlayerId)
            );
        }
        return new FlowerDanceCutsceneClientState.DancePair(
            FlowerDanceCutsceneClientState.Partner.player(secondPlayerId),
            FlowerDanceCutsceneClientState.Partner.player(firstPlayerId)
        );
    }

    private static int voteCount(List<ServerPlayer> voters, Set<UUID> votes) {
        int count = 0;
        for (ServerPlayer voter : voters) {
            if (votes.contains(voter.getUUID())) {
                count++;
            }
        }
        return count;
    }

    private static void syncHud(ServerPlayer player, boolean hidden) {
        boolean current = player.getPersistentData().getBoolean(TAG_HUD_HIDDEN);
        if (current == hidden) {
            return;
        }
        player.getPersistentData().putBoolean(TAG_HUD_HIDDEN, hidden);
        PacketDistributor.sendToPlayer(player, new FestivalHudStatePayload(hidden));
    }

    private static void clearRuntimeState(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (isParticipant(player)) {
                player.getPersistentData().putBoolean(TAG_PARTICIPATING, false);
            }
            syncHud(player, false);
        }
        ENTRY_DIALOG_OPEN.clear();
        START_DANCE_DIALOG_OPEN.clear();
        EXIT_DIALOG_OPEN.clear();
        EXIT_VOTES.clear();
        EXIT_VOTE_PARTICIPANTS.clear();
        LAST_OUTSIDE_ENTRY.clear();
        DANCE_PARTNERS.clear();
        FESTIVAL_DIALOGUE_SEEN.clear();
        PENDING_PLAYER_INVITES.clear();
        MAIN_EVENT_CUTSCENE_PARTICIPANTS.clear();
        MAIN_EVENT_CUTSCENE_DONE.clear();
        mainEventStagePrepared = false;
        mainEventPhase = MainEventPhase.FREE;
        stopTimeFreeze();
        FlowerDanceNpcService.restore(level);
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

    private static boolean isActiveFlowerDanceDay() {
        return FestivalService.getActiveFestivalToday()
            .filter(definition -> FESTIVAL_ID.equals(definition.id()))
            .isPresent();
    }

    private static String canonical(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static void expirePlayerInvites(ServerLevel level) {
        if (level == null || PENDING_PLAYER_INVITES.isEmpty()) {
            return;
        }
        long now = level.getGameTime();
        PENDING_PLAYER_INVITES.entrySet().removeIf(entry -> now - entry.getValue().createdTick() > PLAYER_INVITE_TIMEOUT_TICKS);
    }

    private static void cancelPendingPlayerInvitesFor(UUID playerId) {
        if (playerId == null || PENDING_PLAYER_INVITES.isEmpty()) {
            return;
        }
        PENDING_PLAYER_INVITES.entrySet().removeIf(entry -> playerId.equals(entry.getValue().senderId()) || playerId.equals(entry.getValue().targetId()));
    }

    private enum PartnerKind {
        NPC,
        PLAYER
    }

    private record PartnerSelection(PartnerKind kind, String npcId, UUID playerId) {
        private static PartnerSelection npc(String npcId) {
            return new PartnerSelection(PartnerKind.NPC, npcId, null);
        }

        private static PartnerSelection player(UUID playerId) {
            return new PartnerSelection(PartnerKind.PLAYER, "", playerId);
        }
    }

    private record PendingPlayerInvite(UUID inviteId, UUID senderId, UUID targetId, long createdTick) {
    }

    private record DefaultDancePair(String femaleNpcId, String maleNpcId) {
    }

    private record DanceFormation(List<FlowerDanceCutsceneClientState.DancePair> pairs, List<FlowerDanceCutsceneClientState.Partner> spectators) {
    }

    private enum MainEventPhase {
        FREE,
        MAIN_EVENT,
        ENDING
    }

    private static AABB inclusiveBox(BlockPos first, BlockPos second) {
        int minX = Math.min(first.getX(), second.getX());
        int minY = Math.min(first.getY(), second.getY());
        int minZ = Math.min(first.getZ(), second.getZ());
        int maxX = Math.max(first.getX(), second.getX());
        int maxY = Math.max(first.getY(), second.getY());
        int maxZ = Math.max(first.getZ(), second.getZ());
        return new AABB(minX, minY, minZ, maxX + 1.0D, maxY + 1.0D, maxZ + 1.0D);
    }
}