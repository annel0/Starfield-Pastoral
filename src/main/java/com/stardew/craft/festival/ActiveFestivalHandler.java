package com.stardew.craft.festival;

import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface ActiveFestivalHandler {
    String festivalId();

    String displayName();

    void tick(ServerLevel level);

    void startDebugFestival(ServerLevel level);

    void restoreDebugFestival(ServerLevel level);

    String debugStatus(ServerLevel level);

    default void onMapOverlayApplied(ServerLevel level) {
    }

    default void requestDebugNpcs(ServerLevel level) {
    }

    default void restoreDebugNpcs(ServerLevel level) {
    }

    default String debugNpcStatus(ServerLevel level) {
        return displayName() + " NPC controller unavailable";
    }

    default boolean controlsNpc(String npcId) {
        return false;
    }

    default boolean isParticipant(ServerPlayer player) {
        return false;
    }

    default void onPlayerLogin(ServerPlayer player) {
    }

    default void onPlayerLogout(ServerPlayer player) {
    }

    default void onNpcDialogueSeen(ServerPlayer player, String npcId) {
    }

    default boolean tryOpenPierreFestivalShop(ServerPlayer player) {
        return false;
    }

    default boolean blocksNpcInteractionDuringMainEvent() {
        return false;
    }

    default boolean supportsMainEventDebug() {
        return false;
    }

    default boolean tryStartMainEvent(ServerPlayer player) {
        return false;
    }

    default String debugMainEventStatus(ServerLevel level) {
        return debugStatus(level);
    }

    default boolean isTimeFreezeActive() {
        return false;
    }

    default long applyTimeFreeze(ServerLevel level, StardewTimeManager timeManager) {
        return timeManager.getVirtualDayTime(level);
    }

    default String debugApplyMessage() {
        return "已启动 " + displayName() + " 总调试: 当前日期按 " + festivalId() + " 处理，overlay 应用中，NPC 会在地图应用完成后进入节日点位";
    }
}