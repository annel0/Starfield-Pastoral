package com.stardew.craft.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

/**
 * 指令目标玩家解析：默认对 source 玩家生效；若尾部追加了 target 玩家参数，则改为对该玩家生效。
 *
 * 用法：
 *  - 挂执行器时用 {@link #executesWithTarget} 自动同时注册「无 target」与「带 target」两个分支。
 *  - 在 handler 里用 {@link #resolve} 取实际目标玩家。
 */
public final class CommandTargets {
    public static final String ARG = "target";

    private CommandTargets() {}

    /**
     * 给 builder 挂上 executes 回调，并派生一个带 target 玩家参数的子分支，执行同一回调。
     */
    public static <S extends ArgumentBuilder<CommandSourceStack, S>> S executesWithTarget(
            S builder, Command<CommandSourceStack> cmd) {
        return builder
                .executes(cmd)
                .then(Commands.argument(ARG, EntityArgument.player()).executes(cmd));
    }

    /**
     * 解析指令目标玩家：优先使用 target 参数，否则回退到 source 自身。
     * 返回 null 表示目标不可达（console 执行且未指定 target）。
     */
    public static ServerPlayer resolve(CommandContext<CommandSourceStack> ctx) {
        try {
            return EntityArgument.getPlayer(ctx, ARG);
        } catch (IllegalArgumentException | CommandSyntaxException ignored) {
            return ctx.getSource().getPlayer();
        }
    }
}
