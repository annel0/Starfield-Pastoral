package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.command.MuseumDebugCommand;
import com.stardew.craft.command.AnimalDebugCommand;
import com.stardew.craft.command.PlayerDataCommand;
import com.stardew.craft.command.StardewTeleportCommand;
import com.stardew.craft.command.TimeDebugCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 命令注册事件
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class CommandEventHandler {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        StardewTeleportCommand.register(event.getDispatcher());
        TimeDebugCommand.register(event.getDispatcher());
        PlayerDataCommand.register(event.getDispatcher());
        MuseumDebugCommand.register(event.getDispatcher(), event.getBuildContext());
        AnimalDebugCommand.register(event.getDispatcher());
        StardewCraft.LOGGER.info("Registered Stardew commands");
    }
}
