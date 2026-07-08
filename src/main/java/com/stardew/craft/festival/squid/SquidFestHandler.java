package com.stardew.craft.festival.squid;

import com.stardew.craft.festival.FestivalDefinition;
import com.stardew.craft.festival.FestivalSessionState;
import com.stardew.craft.festival.PassiveFestivalHandler;
import net.minecraft.server.level.ServerLevel;

public final class SquidFestHandler implements PassiveFestivalHandler {
    @Override
    public String festivalId() {
        return SquidFestService.FESTIVAL_ID;
    }

    @Override
    public void onNewDay(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        SquidFestService.forceRefreshNpcSchedules(level);
    }

    @Override
    public void onOpen(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        SquidFestService.forceRefreshNpcSchedules(level);
    }

    @Override
    public void onCleanup(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        SquidFestService.forceRefreshNpcSchedules(level);
    }
}
