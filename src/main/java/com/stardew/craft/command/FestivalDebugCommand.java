package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.festival.EggFestivalService;
import com.stardew.craft.festival.FestivalDefinition;
import com.stardew.craft.festival.EggFestivalNpcService;
import com.stardew.craft.festival.FestivalMapOverlayDefinition;
import com.stardew.craft.festival.FestivalMapOverlayManager;
import com.stardew.craft.festival.FestivalMapOverlayRegistry;
import com.stardew.craft.festival.FestivalMapOverlayState;
import com.stardew.craft.festival.FestivalRegistry;
import com.stardew.craft.festival.FestivalWorldData;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("null")
public final class FestivalDebugCommand {
    private FestivalDebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stardew")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("festival")
                .then(Commands.literal("apply")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(Stream.of("spring13"), builder))
                        .executes(FestivalDebugCommand::applyFestival)))
                .then(Commands.literal("restore")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(Stream.of("spring13"), builder))
                        .executes(FestivalDebugCommand::restoreFestival)))
                .then(Commands.literal("overlay")
                    .then(Commands.literal("apply")
                        .then(Commands.argument("id", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(idSuggestions(), builder))
                            .executes(FestivalDebugCommand::applyOverlay)))
                    .then(Commands.literal("restore")
                        .then(Commands.argument("id", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(idSuggestions(), builder))
                            .executes(FestivalDebugCommand::restoreOverlay)))
                    .then(Commands.literal("status")
                        .executes(FestivalDebugCommand::statusAll)
                        .then(Commands.argument("id", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(idSuggestions(), builder))
                            .executes(FestivalDebugCommand::statusOne))))
                .then(Commands.literal("npc")
                    .then(Commands.literal("apply")
                        .then(Commands.argument("id", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(Stream.of("spring13"), builder))
                            .executes(FestivalDebugCommand::applyNpcs)))
                    .then(Commands.literal("restore")
                        .then(Commands.argument("id", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(Stream.of("spring13"), builder))
                            .executes(FestivalDebugCommand::restoreNpcs)))
                    .then(Commands.literal("status")
                        .executes(FestivalDebugCommand::npcStatus)))
                .then(Commands.literal("main")
                    .then(Commands.literal("start")
                        .then(Commands.argument("id", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(Stream.of("spring13"), builder))
                            .executes(FestivalDebugCommand::startMainEvent)))
                    .then(Commands.literal("status")
                        .executes(FestivalDebugCommand::mainEventStatus)))));
    }

    private static int applyFestival(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (!EggFestivalService.FESTIVAL_ID.equalsIgnoreCase(id)) {
            context.getSource().sendFailure(Component.literal("当前只支持 Egg Festival 总调试: spring13"));
            return 0;
        }

        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }

        FestivalDefinition festival = FestivalRegistry.get(EggFestivalService.FESTIVAL_ID).orElse(null);
        if (festival == null) {
            context.getSource().sendFailure(Component.literal("未注册 Egg Festival: spring13"));
            return 0;
        }

        StardewTimeManager time = StardewTimeManager.get();
        boolean overlayStarted = FestivalMapOverlayManager.beginApply(
            level,
            festival,
            time.getCurrentYear(),
            time.getCurrentSeason(),
            time.getCurrentDay()
        );
        if (!overlayStarted) {
            context.getSource().sendFailure(Component.literal("地图 overlay 启动失败: " + festival.mapOverlayId()));
            return 0;
        }

        EggFestivalService.startDebugFestival(level);
        context.getSource().sendSuccess(() -> Component.literal(
            "已启动 Egg Festival 总调试: 当前日期按 spring13 处理，overlay 应用中，NPC 会在地图应用完成后进入节日点位"), true);
        return 1;
    }

    private static int restoreFestival(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (!EggFestivalService.FESTIVAL_ID.equalsIgnoreCase(id)) {
            context.getSource().sendFailure(Component.literal("当前只支持 Egg Festival 总调试: spring13"));
            return 0;
        }

        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }

        EggFestivalService.restoreDebugFestival(level);
        boolean overlayRestoreStarted = FestivalMapOverlayManager.beginRestore(level, "Town-EggFestival");
        context.getSource().sendSuccess(() -> Component.literal(overlayRestoreStarted
            ? "已恢复 Egg Festival 总调试: NPC/玩家状态已恢复，地图 overlay 正在恢复"
            : "已恢复 Egg Festival 总调试: NPC/玩家状态已恢复，地图 overlay 当前无需恢复或尚未应用"), true);
        return 1;
    }

    private static int applyOverlay(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        Optional<FestivalDefinition> festivalOpt = resolveFestival(id);
        if (festivalOpt.isEmpty()) {
            context.getSource().sendFailure(Component.literal("未找到带地图 overlay 的节日或 overlay: " + id));
            return 0;
        }

        FestivalDefinition festival = festivalOpt.get();
        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }

        StardewTimeManager time = StardewTimeManager.get();
        boolean started = FestivalMapOverlayManager.beginApply(
            level,
            festival,
            time.getCurrentYear(),
            time.getCurrentSeason(),
            time.getCurrentDay()
        );
        if (!started) {
            context.getSource().sendFailure(Component.literal("地图 overlay 启动失败: " + festival.mapOverlayId()));
            return 0;
        }

        if ("spring13".equalsIgnoreCase(festival.id())) {
            EggFestivalNpcService.requestDebugStart(level);
        }

        context.getSource().sendSuccess(() -> Component.literal("已开始应用节日地图 overlay: " + festival.mapOverlayId()), true);
        return 1;
    }

    private static int restoreOverlay(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        Optional<String> overlayId = resolveOverlayId(id);
        if (overlayId.isEmpty()) {
            context.getSource().sendFailure(Component.literal("未找到 overlay: " + id));
            return 0;
        }

        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }

        if (!FestivalMapOverlayManager.beginRestore(level, overlayId.get())) {
            context.getSource().sendFailure(Component.literal("地图 overlay 无法恢复，可能尚未应用: " + overlayId.get()));
            return 0;
        }

        if ("Town-EggFestival".equalsIgnoreCase(overlayId.get())) {
            EggFestivalNpcService.restore(level);
        }

        context.getSource().sendSuccess(() -> Component.literal("已开始恢复节日地图 overlay: " + overlayId.get()), true);
        return 1;
    }

    private static int applyNpcs(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (!"spring13".equalsIgnoreCase(id)) {
            context.getSource().sendFailure(Component.literal("当前只支持 Egg Festival NPC 调试: spring13"));
            return 0;
        }
        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }
        EggFestivalNpcService.requestDebugStart(level);
        context.getSource().sendSuccess(() -> Component.literal("已请求 Egg Festival NPC 进入节日点位"), true);
        return 1;
    }

    private static int restoreNpcs(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (!"spring13".equalsIgnoreCase(id)) {
            context.getSource().sendFailure(Component.literal("当前只支持 Egg Festival NPC 调试: spring13"));
            return 0;
        }
        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }
        EggFestivalNpcService.restore(level);
        context.getSource().sendSuccess(() -> Component.literal("已恢复 Egg Festival NPC 到当前日程"), true);
        return 1;
    }

    private static int npcStatus(CommandContext<CommandSourceStack> context) {
        ServerLevel level = stardewLevel(context.getSource());
        context.getSource().sendSuccess(() -> Component.literal(EggFestivalNpcService.debugStatus(level)), false);
        return 1;
    }

    private static int startMainEvent(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (!EggFestivalService.FESTIVAL_ID.equalsIgnoreCase(id)) {
            context.getSource().sendFailure(Component.literal("当前只支持 Egg Festival 主事件调试: spring13"));
            return 0;
        }
        if (!(context.getSource().getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("该命令需要由玩家执行"));
            return 0;
        }
        if (!EggFestivalService.tryStartMainEvent(player)) {
            context.getSource().sendFailure(Component.literal("需要先进入 Egg Festival 会场"));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("已请求 Egg Festival 主事件 startContest gate"), true);
        return 1;
    }

    private static int mainEventStatus(CommandContext<CommandSourceStack> context) {
        ServerLevel level = stardewLevel(context.getSource());
        context.getSource().sendSuccess(() -> Component.literal(EggFestivalService.debugMainEventStatus(level)), false);
        return 1;
    }

    private static int statusAll(CommandContext<CommandSourceStack> context) {
        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }

        FestivalWorldData data = FestivalWorldData.get(level);
        StringBuilder message = new StringBuilder("节日地图 overlay 状态:");
        for (FestivalMapOverlayDefinition definition : FestivalMapOverlayRegistry.all()) {
            FestivalMapOverlayState state = data.getOverlayState(definition.overlayId()).orElse(null);
            message.append("\n- ").append(definition.overlayId()).append(": ")
                .append(state == null ? "NONE" : state.phase().name());
        }
        context.getSource().sendSuccess(() -> Component.literal(message.toString()), false);
        return 1;
    }

    private static int statusOne(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        Optional<String> overlayId = resolveOverlayId(id);
        if (overlayId.isEmpty()) {
            context.getSource().sendFailure(Component.literal("未找到 overlay: " + id));
            return 0;
        }

        ServerLevel level = stardewLevel(context.getSource());
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley 维度尚未加载"));
            return 0;
        }

        FestivalMapOverlayState state = FestivalWorldData.get(level).getOverlayState(overlayId.get()).orElse(null);
        String status = state == null ? "NONE" : state.phase().name() + " cursor=" + state.cursor();
        context.getSource().sendSuccess(() -> Component.literal(overlayId.get() + ": " + status), false);
        return 1;
    }

    private static ServerLevel stardewLevel(CommandSourceStack source) {
        return source.getServer().getLevel(ModDimensions.STARDEW_VALLEY);
    }

    private static Optional<FestivalDefinition> resolveFestival(String id) {
        Optional<FestivalDefinition> byFestivalId = FestivalRegistry.get(id)
            .filter(definition -> !definition.mapOverlayId().isBlank());
        return byFestivalId.isPresent() ? byFestivalId : FestivalRegistry.getByOverlayId(id);
    }

    private static Optional<String> resolveOverlayId(String id) {
        Optional<String> direct = FestivalMapOverlayRegistry.get(id).map(FestivalMapOverlayDefinition::overlayId);
        return direct.isPresent() ? direct : resolveFestival(id).map(FestivalDefinition::mapOverlayId);
    }

    private static Iterable<String> idSuggestions() {
        return Stream.concat(
                FestivalMapOverlayRegistry.all().stream().map(FestivalMapOverlayDefinition::overlayId),
                FestivalRegistry.all().stream()
                    .filter(definition -> !definition.mapOverlayId().isBlank())
                    .map(FestivalDefinition::id)
            )
            .distinct()
            .sorted(Comparator.naturalOrder())
            .toList();
    }
}