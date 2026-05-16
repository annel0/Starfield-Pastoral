package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.stardew.craft.communitycenter.state.CCStoryFlags;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.joja.JojaCDService;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.npc.runtime.NpcSpawnManager;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Debug commands for the Joja line (plan §8).
 *
 * <ul>
 *   <li>/joja openmenu          — open the JojaCDMenu (skips Morris dialogue gate)</li>
 *   <li>/joja join              — set JOJA_MEMBER flag</li>
 *   <li>/joja buy &lt;idx&gt;       — simulate purchase (skips money check)</li>
 *   <li>/joja reset             — clear all joja* / JojaMember flags</li>
 * </ul>
 */
public final class JojaDebugCommand {

    private JojaDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("joja")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("openmenu").executes(ctx -> {
                ServerPlayer sp = ctx.getSource().getPlayerOrException();
                JojaCDService.openMenu(sp);
                return 1;
            }))
            .then(Commands.literal("join").executes(ctx -> {
                ServerPlayer sp = ctx.getSource().getPlayerOrException();
                CCStoryFlags.addFlag(sp, CCStoryFlags.JOJA_MEMBER);
                ctx.getSource().sendSuccess(() -> Component.literal("JOJA_MEMBER set."), false);
                return 1;
            }))
            .then(Commands.literal("buy")
                .then(Commands.argument("idx", IntegerArgumentType.integer(0, 4))
                    .executes(ctx -> {
                        ServerPlayer sp = ctx.getSource().getPlayerOrException();
                        int idx = IntegerArgumentType.getInteger(ctx, "idx");
                        CCStoryFlags.addFlag(sp, CCStoryFlags.jojaAreaFlag(idx));
                        CCStoryFlags.addFlag(sp, CCStoryFlags.jojaButtonToCcFlag(idx));
                        ctx.getSource().sendSuccess(() -> Component.literal(
                            "Joja area " + idx + " flagged (no money charged)."), false);
                        return 1;
                    })))
            .then(Commands.literal("tp").executes(ctx -> {
                ServerPlayer sp = ctx.getSource().getPlayerOrException();
                BlockPos o = InteriorSubspaceManager.JOJA_MART_ORIGIN;
                sp.teleportTo(o.getX() + 3.5, o.getY() + 1, o.getZ() + 13.5);
                return 1;
            }))
            .then(Commands.literal("diag").executes(ctx -> {
                ServerPlayer sp = ctx.getSource().getPlayerOrException();
                for (String id : new String[]{"morris", "joja_cashier"}) {
                    NpcCapabilityProfile p = NpcDataRegistry.capabilities().get(id);
                    boolean impl = p != null && p.implemented();
                    com.stardew.craft.entity.npc.StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(sp.serverLevel(), id);
                    String posStr = (npc == null) ? "null"
                        : String.format("%.1f,%.1f,%.1f", npc.getX(), npc.getY(), npc.getZ());
                    ctx.getSource().sendSuccess(() -> Component.literal(
                        "[" + id + "] profile=" + (p == null ? "MISSING" : "ok")
                        + " implemented=" + impl + " tracked=" + (npc != null)
                        + " pos=" + posStr
                    ), false);
                }
                return 1;
            }))
            .then(Commands.literal("tpnpc").executes(ctx -> {
                // 强制把 Morris / 收银员瞬移回正确坐标（可能被别的系统挪走了）
                ServerPlayer sp = ctx.getSource().getPlayerOrException();
                com.stardew.craft.entity.npc.StardewNpcEntity m =
                    NpcSpawnManager.getTrackedNpc(sp.serverLevel(), "morris");
                if (m != null) m.teleportTo(114.5, 45.0, -22.5);
                com.stardew.craft.entity.npc.StardewNpcEntity c =
                    NpcSpawnManager.getTrackedNpc(sp.serverLevel(), "joja_cashier");
                if (c != null) c.teleportTo(104.5, 45.0, -21.5);
                ctx.getSource().sendSuccess(() -> Component.literal(
                    "Morris=" + (m != null) + " Cashier=" + (c != null) + " teleported"), false);
                return 1;
            }))
            .then(Commands.literal("respawn").executes(ctx -> {
                net.minecraft.server.MinecraftServer server = ctx.getSource().getServer();
                com.stardew.craft.joja.JojaNpcEvents.forceCheckNow(
                    server.getLevel(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY));
                ctx.getSource().sendSuccess(() -> Component.literal(
                    "Joja NPC sweep done. Run /joja diag to verify positions."), false);
                return 1;
            }))
            .then(Commands.literal("reset").executes(ctx -> {
                ServerPlayer sp = ctx.getSource().getPlayerOrException();
                PlayerStardewData data = PlayerDataManager.getPlayerData(sp);
                for (String f : new String[]{
                    CCStoryFlags.JOJA_MEMBER, CCStoryFlags.JOJA_GREETING,
                    CCStoryFlags.JOJA_VAULT, CCStoryFlags.JOJA_BOILER_ROOM,
                    CCStoryFlags.JOJA_CRAFTS_ROOM, CCStoryFlags.JOJA_PANTRY,
                    CCStoryFlags.JOJA_FISH_TANK,
                    CCStoryFlags.CC_MOVIE_THEATER, CCStoryFlags.CC_MOVIE_THEATER_JOJA
                }) {
                    data.removeMailFlag(f);
                }
                ctx.getSource().sendSuccess(() -> Component.literal("Joja flags cleared."), false);
                return 1;
            }))
        );
    }
}
