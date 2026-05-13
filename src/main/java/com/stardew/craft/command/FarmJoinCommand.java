package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.farm.FarmInstance;
import com.stardew.craft.farm.FarmInstanceRegistry;
import com.stardew.craft.farm.FarmJoinManager;
import com.stardew.craft.network.payload.FarmListSyncPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * /stardew farm accept <uuid>  — 接受加入请求
 * /stardew farm reject <uuid>  — 拒绝加入请求
 *
 * 调试子命令（需要 OP 2）：
 * /stardew farm debug selectionscreen  — 打开农场选择界面（含"加入"按钮）
 * /stardew farm debug joinscreen       — 打开加入农场列表（模拟数据）
 * /stardew farm debug request          — 模拟收到一条加入请求聊天消息
 * /stardew farm debug addmember        — 给自己农场添加一个假成员
 * /stardew farm debug members          — 列出当前农场所有成员
 * /stardew farm debug clearmembers     — 清除所有假成员
 */
@SuppressWarnings("null")
public class FarmJoinCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("stardew")
                .then(Commands.literal("farm")
                    .then(Commands.literal("accept")
                        .executes(ctx -> handleLatestResponse(ctx, true))
                        .then(Commands.argument("uuid", UuidArgument.uuid())
                            .executes(ctx -> handleResponse(ctx, true))
                        )
                    )
                    .then(Commands.literal("reject")
                        .executes(ctx -> handleLatestResponse(ctx, false))
                        .then(Commands.argument("uuid", UuidArgument.uuid())
                            .executes(ctx -> handleResponse(ctx, false))
                        )
                    )
                    .then(Commands.literal("debug")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("selectionscreen")
                            .executes(FarmJoinCommand::debugSelectionScreen)
                        )
                        .then(Commands.literal("joinscreen")
                            .executes(FarmJoinCommand::debugJoinScreen)
                        )
                        .then(Commands.literal("request")
                            .executes(FarmJoinCommand::debugRequest)
                        )
                        .then(Commands.literal("addmember")
                            .executes(FarmJoinCommand::debugAddMember)
                        )
                        .then(Commands.literal("members")
                            .executes(FarmJoinCommand::debugListMembers)
                        )
                        .then(Commands.literal("clearmembers")
                            .executes(FarmJoinCommand::debugClearMembers)
                        )
                    )
                )
        );
    }

    private static int handleResponse(CommandContext<CommandSourceStack> ctx, boolean accept) {
        CommandSourceStack source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        UUID requesterUUID = UuidArgument.getUuid(ctx, "uuid");

        boolean success = FarmJoinManager.handleResponse(player, requesterUUID, accept, player.server);
        return success ? 1 : 0;
    }

    private static int handleLatestResponse(CommandContext<CommandSourceStack> ctx, boolean accept) {
        CommandSourceStack source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        boolean success = FarmJoinManager.handleLatestResponse(player, accept, player.server);
        return success ? 1 : 0;
    }

    // ══════════════════════════════════════════
    //  调试子命令
    // ══════════════════════════════════════════

    /** 打开农场选择界面（含"加入别人的农场"按钮） */
    private static int debugSelectionScreen(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;
        FarmJoinManager.syncPendingState(player, FarmJoinManager.hasPending(player.getUUID()));
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                new com.stardew.craft.network.payload.OpenFarmSelectionPayload());
        player.sendSystemMessage(Component.literal("§a[DEBUG] 已打开农场选择界面（左栏底部有\"加入别人的农场\"按钮）"));
        return 1;
    }

    /** 打开加入农场列表界面，填入模拟数据 */
    private static int debugJoinScreen(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        // 构建模拟农场列表
        List<FarmListSyncPayload.FarmEntry> mockFarms = new ArrayList<>();
        mockFarms.add(new FarmListSyncPayload.FarmEntry(
                UUID.randomUUID(), "小明", "阳光牧场", "standard", 0, false));
        mockFarms.add(new FarmListSyncPayload.FarmEntry(
                UUID.randomUUID(), "小红", "星露花园", "riverland", 0, false));
        mockFarms.add(new FarmListSyncPayload.FarmEntry(
                UUID.randomUUID(), "Alex", "丰收庄园", "forest", 0, false));
        mockFarms.add(new FarmListSyncPayload.FarmEntry(
                UUID.randomUUID(), "月光猎人", "碧波农场", "hilltop", 0, false));
        mockFarms.add(new FarmListSyncPayload.FarmEntry(
                UUID.randomUUID(), "StarFarmer", "幸运小院", "wilderness", 0, false));

        // 也加入真实已有的农场（如果有的话）
        FarmInstanceRegistry registry = FarmInstanceRegistry.get();
        for (FarmInstance farm : registry.getAllFarms()) {
            if (farm.isInitialized() && farm.getFarmerCount() < FarmInstance.MAX_FARMERS) {
                mockFarms.add(new FarmListSyncPayload.FarmEntry(
                        farm.getOwnerUUID(), farm.getOwnerName(), farm.getFarmName(),
                        farm.getFarmType().getId(), 0, false));
            }
        }

        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                new FarmListSyncPayload(mockFarms, "farm_join"));
        player.sendSystemMessage(Component.literal("§a[DEBUG] 已打开加入农场列表（含 " + mockFarms.size() + " 个模拟农场）"));
        return 1;
    }

    /** 模拟收到一条加入请求的聊天消息（带 [接受] [拒绝] 按钮） */
    private static int debugRequest(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        UUID fakeRequester = UUID.randomUUID();
        String fakeName = "测试玩家";

        MutableComponent msg = Component.translatable("stardewcraft.farm.join.incoming", fakeName);
        msg.append(Component.literal(" "));

        MutableComponent acceptBtn = Component.literal("[")
                .append(Component.translatable("stardewcraft.farm.join.accept"))
                .append("]");
        acceptBtn.setStyle(Style.EMPTY
                .withColor(0x2E7D32)
                .withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/stardew farm accept " + fakeRequester))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("stardewcraft.farm.join.accept.hover"))));
        msg.append(acceptBtn);
        msg.append(Component.literal(" "));

        MutableComponent rejectBtn = Component.literal("[")
                .append(Component.translatable("stardewcraft.farm.join.reject"))
                .append("]");
        rejectBtn.setStyle(Style.EMPTY
                .withColor(0xC62828)
                .withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/stardew farm reject " + fakeRequester))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("stardewcraft.farm.join.reject.hover"))));
        msg.append(rejectBtn);

        player.sendSystemMessage(msg);
        player.sendSystemMessage(Component.literal("§7[DEBUG] 以上是模拟的加入请求消息，可点击按钮测试"));
        return 1;
    }

    /** 给自己农场添加一个假成员 */
    private static int debugAddMember(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        FarmInstanceRegistry registry = FarmInstanceRegistry.get();
        FarmInstance farm = registry.getFarm(player.getUUID());
        if (farm == null) {
            player.sendSystemMessage(Component.literal("§c[DEBUG] 你还没有农场"));
            return 0;
        }

        UUID fakeUUID = UUID.randomUUID();
        if (!registry.addMember(player.getUUID(), fakeUUID)) {
            player.sendSystemMessage(Component.literal("§c[DEBUG] 添加失败（农场已满，最多4人）"));
            return 0;
        }

        player.sendSystemMessage(Component.literal(
                "§a[DEBUG] 已添加假成员 " + fakeUUID.toString().substring(0, 8) + "... 当前 "
                        + farm.getFarmerCount() + "/" + FarmInstance.MAX_FARMERS + " 人"));
        return 1;
    }

    /** 列出当前农场所有成员 */
    private static int debugListMembers(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        FarmInstanceRegistry registry = FarmInstanceRegistry.get();
        FarmInstance farm = registry.getFarmForPlayer(player.getUUID());
        if (farm == null) {
            player.sendSystemMessage(Component.literal("§c[DEBUG] 你还没有农场"));
            return 0;
        }

        player.sendSystemMessage(Component.literal("§6═══ 农场成员 (" + farm.getFarmerCount()
                + "/" + FarmInstance.MAX_FARMERS + ") ═══"));
        player.sendSystemMessage(Component.literal("§a★ 主人: " + farm.getOwnerName()
                + " §7(" + farm.getOwnerUUID().toString().substring(0, 8) + "...)"));
        int i = 1;
        for (UUID member : farm.getMembers()) {
            // 尝试查找在线玩家名
            ServerPlayer mp = player.server.getPlayerList().getPlayer(member);
            String name = mp != null ? mp.getName().getString() : "离线/假成员";
            player.sendSystemMessage(Component.literal("§b  成员" + i + ": " + name
                    + " §7(" + member.toString().substring(0, 8) + "...)"));
            i++;
        }
        return 1;
    }

    /** 清除所有假成员 */
    private static int debugClearMembers(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        FarmInstanceRegistry registry = FarmInstanceRegistry.get();
        FarmInstance farm = registry.getFarm(player.getUUID());
        if (farm == null) {
            player.sendSystemMessage(Component.literal("§c[DEBUG] 你还没有农场"));
            return 0;
        }

        List<UUID> toRemove = new ArrayList<>(farm.getMembers());
        for (UUID m : toRemove) {
            registry.removeMember(player.getUUID(), m);
        }
        player.sendSystemMessage(Component.literal("§a[DEBUG] 已清除 " + toRemove.size() + " 个成员"));
        return 1;
    }
}
