package com.stardew.craft.cutscene.network;

import com.mojang.logging.LogUtils;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.quest.QuestManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

/**
 * Client → Server: execute a server-side action from a cutscene command.
 * Supports: "add_quest", "set_flag"
 */
public record CutsceneServerActionPayload(String action, String value) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<CutsceneServerActionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "cutscene_server_action"));

    public static final StreamCodec<ByteBuf, CutsceneServerActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CutsceneServerActionPayload::action,
            ByteBufCodecs.STRING_UTF8, CutsceneServerActionPayload::value,
            CutsceneServerActionPayload::new);

    @SuppressWarnings("null")
    public static void handle(CutsceneServerActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            switch (payload.action) {
                case "add_quest" -> {
                    QuestManager mgr = QuestManager.of(player);
                    mgr.acceptQuest(payload.value, player);
                    LOGGER.debug("Cutscene added quest {} for {}", payload.value, player.getName().getString());
                }
                case "set_flag" -> {
                    PlayerStardewData data = PlayerDataManager.getPlayerData(player);
                    data.addMailFlag(payload.value);
                    LOGGER.debug("Cutscene set flag '{}' for {}", payload.value, player.getName().getString());
                    // canReadJunimoText 影响 bundle 界面渲染，需同步到客户端
                    if ("canReadJunimoText".equals(payload.value)) {
                        com.stardew.craft.communitycenter.network.BundleSyncPayload.sendFullSync(player);
                    }
                }
                case "add_friendship" -> {
                    // value format: "npc_id:points"
                    String[] parts = payload.value.split(":", 2);
                    if (parts.length == 2) {
                        String npcId = parts[0];
                        int points = Integer.parseInt(parts[1]);
                        var fm = com.stardew.craft.npc.runtime.NpcFriendshipDataManager.get(
                                (net.minecraft.server.level.ServerLevel) player.level());
                        var state = fm.getOrCreate(player.getUUID(), npcId);
                        state.addPoints(points, 2500);
                        LOGGER.debug("Cutscene added {} friendship to {} for {}", points, npcId,
                                player.getName().getString());
                    }
                }
                case "add_item" -> {
                    // value format: "item_id:count"，item_id 自身含 ':'，按最后一个 ':' 切分
                    int sep = payload.value.lastIndexOf(':');
                    if (sep > 0 && sep < payload.value.length() - 1) {
                        String[] parts = { payload.value.substring(0, sep), payload.value.substring(sep + 1) };
                        try {
                            var rl = ResourceLocation.parse(parts[0]);
                            int count = Integer.parseInt(parts[1]);
                            var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
                            if (item != net.minecraft.world.item.Items.AIR) {
                                var stack = new net.minecraft.world.item.ItemStack(item, count);
                                if (!player.getInventory().add(stack)) {
                                    player.drop(stack, false);
                                }
                                LOGGER.debug("Cutscene gave {}x{} to {}", count, rl,
                                        player.getName().getString());
                            }
                        } catch (Exception e) {
                            LOGGER.warn("Cutscene add_item failed: {}", e.getMessage());
                        }
                    }
                }
                case "teleport_cc" -> {
                    com.stardew.craft.event.InteriorPortalInteractionEvents.handleCCEntryForCutscene(player);
                    // Send the player their CC interior anchor so any anchor-tagged
                    // commands in the cutscene (Part B) resolve to the correct origin.
                    try {
                        net.minecraft.server.level.ServerLevel lvl = player.serverLevel();
                        net.minecraft.core.BlockPos origin = com.stardew.craft.interior.PlayerInteriorAllocator
                                .get(lvl).getCCOrigin(player.getUUID());
                        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                                new com.stardew.craft.cutscene.network.CutsceneAnchorPayload(
                                        "cc_interior",
                                        origin.getX(), origin.getY(), origin.getZ()));
                    } catch (Exception e) {
                        LOGGER.warn("Failed to send cc_interior anchor: {}", e.getMessage());
                    }
                    LOGGER.debug("Cutscene teleported {} to CC interior", player.getName().getString());
                }
                default -> LOGGER.warn("Unknown cutscene server action: {}", payload.action);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
