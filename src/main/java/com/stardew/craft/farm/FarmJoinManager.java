package com.stardew.craft.farm;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理农场加入请求。
 * 每个请求者同时只能有一个待处理请求。
 * 请求在 5 分钟后自动过期。
 */
public final class FarmJoinManager {

    private FarmJoinManager() {}

    /** 请求者UUID → 请求信息 */
    private static final Map<UUID, JoinRequest> pendingRequests = new ConcurrentHashMap<>();

    private static final long EXPIRE_TICKS = 6000L; // 5 minutes

    public record JoinRequest(UUID requesterUUID, String requesterName,
                              UUID ownerUUID, long createdTick) {}

    /**
     * 创建加入请求。
     * @return true 成功创建, false 失败（已有待处理请求或农场不存在等）
     */
    public static boolean createRequest(ServerPlayer requester, UUID ownerUUID, MinecraftServer server) {
        UUID reqUUID = requester.getUUID();

        // 清除过期请求
        cleanExpired(server);

        // 已有待处理请求
        if (pendingRequests.containsKey(reqUUID)) {
            requester.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.already_pending"));
            return false;
        }

        // 验证目标农场
        FarmInstanceRegistry registry = FarmInstanceRegistry.get();
        FarmInstance farm = registry.getFarm(ownerUUID);
        if (farm == null || !farm.isInitialized()) {
            requester.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.not_found"));
            return false;
        }

        // 农场已满
        if (farm.getFarmerCount() >= FarmInstance.MAX_FARMERS) {
            requester.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.farm_full"));
            return false;
        }

        // 请求者已有农场
        if (registry.hasFarm(reqUUID)) {
            requester.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.already_has_farm"));
            return false;
        }

        // 存储请求
        long tick = server.overworld().getGameTime();
        pendingRequests.put(reqUUID, new JoinRequest(reqUUID, requester.getName().getString(),
                ownerUUID, tick));

        // 向农场主人发送可点击消息
        ServerPlayer owner = server.getPlayerList().getPlayer(ownerUUID);
        if (owner != null) {
            sendJoinRequestToOwner(owner, requester.getName().getString(), reqUUID);
        } else {
            requester.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.owner_offline"));
            pendingRequests.remove(reqUUID);
            return false;
        }

        requester.sendSystemMessage(
                Component.translatable("stardewcraft.farm.join.request_sent", farm.getFarmName()));
        StardewCraft.LOGGER.info("[FARM_JOIN] {} requested to join {}'s farm",
                requester.getName().getString(), farm.getOwnerName());
        return true;
    }

    /**
     * 处理加入响应（接受/拒绝）。
     */
    public static boolean handleResponse(ServerPlayer owner, UUID requesterUUID, boolean accept,
                                          MinecraftServer server) {
        JoinRequest request = pendingRequests.get(requesterUUID);
        if (request == null) {
            owner.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.no_pending"));
            return false;
        }

        // 验证是否是对应的农场主人
        if (!request.ownerUUID.equals(owner.getUUID())) {
            owner.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.not_your_request"));
            return false;
        }

        pendingRequests.remove(requesterUUID);

        if (!accept) {
            // 拒绝
            ServerPlayer requester = server.getPlayerList().getPlayer(requesterUUID);
            if (requester != null) {
                requester.sendSystemMessage(
                        Component.translatable("stardewcraft.farm.join.rejected",
                                owner.getName().getString()));
            }
            owner.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.you_rejected",
                            request.requesterName));
            StardewCraft.LOGGER.info("[FARM_JOIN] {} rejected {}'s join request",
                    owner.getName().getString(), request.requesterName);
            return true;
        }

        // 接受 — 添加成员
        FarmInstanceRegistry registry = FarmInstanceRegistry.get();

        // 再次验证
        if (registry.hasFarm(requesterUUID)) {
            owner.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.already_has_farm"));
            return false;
        }

        if (!registry.addMember(owner.getUUID(), requesterUUID)) {
            owner.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.farm_full"));
            return false;
        }

        // 通知双方
        FarmInstance farm = registry.getFarm(owner.getUUID());
        String farmName = farm != null ? farm.getFarmName() : "???";

        owner.sendSystemMessage(
                Component.translatable("stardewcraft.farm.join.accepted",
                        request.requesterName));

        ServerPlayer requester = server.getPlayerList().getPlayer(requesterUUID);
        if (requester != null) {
            requester.sendSystemMessage(
                    Component.translatable("stardewcraft.farm.join.welcome",
                            farmName, owner.getName().getString()));
        }

        StardewCraft.LOGGER.info("[FARM_JOIN] {} accepted {}'s join request -> farm '{}'",
                owner.getName().getString(), request.requesterName, farmName);
        return true;
    }

    /**
     * 获取某个请求者的待处理请求。
     */
    public static JoinRequest getPending(UUID requesterUUID) {
        return pendingRequests.get(requesterUUID);
    }

    /**
     * 取消某个请求者的待处理请求。
     */
    public static void cancelRequest(UUID requesterUUID) {
        pendingRequests.remove(requesterUUID);
    }

    /**
     * 清除过期请求。
     */
    private static void cleanExpired(MinecraftServer server) {
        long now = server.overworld().getGameTime();
        pendingRequests.entrySet().removeIf(e -> now - e.getValue().createdTick > EXPIRE_TICKS);
    }

    /**
     * 向农场主人发送带 [接受] [拒绝] 可点击按钮的消息。
     */
    private static void sendJoinRequestToOwner(ServerPlayer owner, String requesterName, UUID requesterUUID) {
        MutableComponent msg = Component.translatable("stardewcraft.farm.join.incoming", requesterName);
        msg.append(Component.literal(" "));

        // [接受] 按钮
        MutableComponent acceptBtn = Component.literal("[")
                .append(Component.translatable("stardewcraft.farm.join.accept"))
                .append("]");
        acceptBtn.setStyle(Style.EMPTY
                .withColor(0x2E7D32)
                .withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/stardew farm accept " + requesterUUID))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("stardewcraft.farm.join.accept.hover"))));
        msg.append(acceptBtn);

        msg.append(Component.literal(" "));

        // [拒绝] 按钮
        MutableComponent rejectBtn = Component.literal("[")
                .append(Component.translatable("stardewcraft.farm.join.reject"))
                .append("]");
        rejectBtn.setStyle(Style.EMPTY
                .withColor(0xC62828)
                .withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/stardew farm reject " + requesterUUID))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("stardewcraft.farm.join.reject.hover"))));
        msg.append(rejectBtn);

        owner.sendSystemMessage(msg);
    }
}
