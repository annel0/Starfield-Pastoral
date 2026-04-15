package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.network.payload.OpenJukeboxPayload;
import com.stardew.craft.sound.JukeboxData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 拦截唱片机方块的右键交互 → 打开选曲 GUI。
 * 不修改方块本身，保留原始 MapDecorStaticBlock 及其模型。
 */
@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class JukeboxInteractHandler {
    private JukeboxInteractHandler() {}

    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onRightClickJukebox(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        var state = player.level().getBlockState(event.getPos());
        if (!state.is(ModBlocks.JUKEBOX.get())) return;

        // 获取当前曲目
        String currentTrack = "";
        if (player.level() instanceof ServerLevel serverLevel) {
            currentTrack = JukeboxData.get(serverLevel).getTrack(event.getPos());
        }

        PacketDistributor.sendToPlayer(player, new OpenJukeboxPayload(event.getPos(), currentTrack));
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
