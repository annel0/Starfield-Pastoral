package com.stardew.craft.festival;

import net.minecraft.server.level.ServerLevel;

public interface PassiveFestivalHandler {
    String festivalId();

    default void onNewDay(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
    }

    default void onOpen(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
    }

    default void tick(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
    }

    default void onCleanup(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
    }
}