package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.deco.DecorationStyle;
import com.stardew.craft.deco.DecorationStyleRegistry;
import com.stardew.craft.deco.DecorationType;
import com.stardew.craft.player.PlayerDataEventHandler;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("null")
public final class DecorationDebugCommand {
    private DecorationDebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stardew")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("deco")
                .then(nodeForType("wallpaper", DecorationType.WALLPAPER))
                .then(nodeForType("flooring", DecorationType.FLOORING))));
    }

    @SuppressWarnings("null")
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> nodeForType(String literal, DecorationType type) {
        return Commands.literal(literal)
            .then(Commands.literal("unlock")
                .then(CommandTargets.executesWithTarget(
                    Commands.literal("all"),
                    ctx -> unlockAll(ctx, type)))
                .then(CommandTargets.executesWithTarget(
                    Commands.argument("style", StringArgumentType.word())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(styleIds(type), builder)),
                    ctx -> unlockOne(ctx, type))))
            .then(Commands.literal("lock")
                .then(CommandTargets.executesWithTarget(
                    Commands.literal("all"),
                    ctx -> lockAll(ctx, type)))
                .then(CommandTargets.executesWithTarget(
                    Commands.argument("style", StringArgumentType.word())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(styleIds(type), builder)),
                    ctx -> lockOne(ctx, type))));
    }

    private static Iterable<String> styleIds(DecorationType type) {
        return DecorationStyleRegistry.getStyles(type).stream().map(DecorationStyle::id).toList();
    }

    private static int unlockAll(CommandContext<CommandSourceStack> ctx, DecorationType type) {
        ServerPlayer player = CommandTargets.resolve(ctx);
        if (player == null) {
            return 0;
        }
        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        int changed = 0;
        for (DecorationStyle style : DecorationStyleRegistry.getStyles(type)) {
            if (data.unlockDecoration(type, style.id())) {
                changed++;
            }
        }
        PlayerDataEventHandler.syncPlayerData(player, data);
        final int changedCount = changed;
        ctx.getSource().sendSuccess(() -> Component.literal("Unlocked " + changedCount + " " + type.name().toLowerCase() + " styles."), true);
        return 1;
    }

    private static int lockAll(CommandContext<CommandSourceStack> ctx, DecorationType type) {
        ServerPlayer player = CommandTargets.resolve(ctx);
        if (player == null) {
            return 0;
        }
        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        int changed = 0;
        for (DecorationStyle style : DecorationStyleRegistry.getStyles(type)) {
            if (!"0".equals(style.id()) && data.lockDecoration(type, style.id())) {
                changed++;
            }
        }
        PlayerDataEventHandler.syncPlayerData(player, data);
        final int changedCount = changed;
        ctx.getSource().sendSuccess(() -> Component.literal("Locked " + changedCount + " " + type.name().toLowerCase() + " styles."), true);
        return 1;
    }

    private static int unlockOne(CommandContext<CommandSourceStack> ctx, DecorationType type) {
        ServerPlayer player = CommandTargets.resolve(ctx);
        if (player == null) {
            return 0;
        }
        String styleId = StringArgumentType.getString(ctx, "style");
        if (DecorationStyleRegistry.getStyle(type, styleId) == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown style id: " + styleId));
            return 0;
        }
        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        data.unlockDecoration(type, styleId);
        PlayerDataEventHandler.syncPlayerData(player, data);
        ctx.getSource().sendSuccess(() -> Component.literal("Unlocked " + type.name().toLowerCase() + " style " + styleId), true);
        return 1;
    }

    private static int lockOne(CommandContext<CommandSourceStack> ctx, DecorationType type) {
        ServerPlayer player = CommandTargets.resolve(ctx);
        if (player == null) {
            return 0;
        }
        String styleId = StringArgumentType.getString(ctx, "style");
        if ("0".equals(styleId)) {
            ctx.getSource().sendFailure(Component.literal("Default style 0 cannot be locked."));
            return 0;
        }
        if (DecorationStyleRegistry.getStyle(type, styleId) == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown style id: " + styleId));
            return 0;
        }
        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        data.lockDecoration(type, styleId);
        PlayerDataEventHandler.syncPlayerData(player, data);
        ctx.getSource().sendSuccess(() -> Component.literal("Locked " + type.name().toLowerCase() + " style " + styleId), true);
        return 1;
    }
}
