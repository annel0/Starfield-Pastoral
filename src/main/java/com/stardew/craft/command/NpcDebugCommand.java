package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.runtime.NpcRuntimeDataManager;
import com.stardew.craft.npc.runtime.NpcRuntimeState;
import com.stardew.craft.npc.runtime.NpcCentralMovementService;
import com.stardew.craft.npc.runtime.NpcSpawnManager;
import com.stardew.craft.npc.runtime.NpcScheduleRuntimeService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("null")
public final class NpcDebugCommand {
    private static final net.minecraft.world.phys.AABB GLOBAL_NPC_SCAN = new net.minecraft.world.phys.AABB(
        -30_000_000D, -2_048D, -30_000_000D,
        30_000_000D, 4_096D, 30_000_000D
    );

    private NpcDebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("stardew")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("npc")
                    .then(Commands.literal("debug")
                        .executes(NpcDebugCommand::debugAll)
                        .then(Commands.argument("npcId", StringArgumentType.word())
                            .executes(NpcDebugCommand::debugSingle)
                        )
                    )
                )
        );
    }

    private static int debugAll(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getServer().getLevel(ModDimensions.STARDEW_VALLEY);
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley dimension is not loaded."));
            return 0;
        }

        Map<String, NpcRuntimeState> states = NpcRuntimeDataManager.get(level).states();
        if (states.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("NPC runtime state map is empty."), false);
            return 1;
        }

        List<String> ids = new ArrayList<>(states.keySet());
        ids.sort(Comparator.naturalOrder());
        int shown = 0;

        context.getSource().sendSuccess(() -> Component.literal("=== NPC runtime debug (first 20) ==="), false);
        for (String npcId : ids) {
            if (shown >= 20) {
                break;
            }
            shown++;
            sendNpcSnapshot(context.getSource(), level, npcId, states.get(npcId));
        }

        int hidden = ids.size() - shown;
        if (hidden > 0) {
            int finalHidden = hidden;
            context.getSource().sendSuccess(() -> Component.literal("... and " + finalHidden + " more NPC states."), false);
        }
        return 1;
    }

    private static int debugSingle(CommandContext<CommandSourceStack> context) {
        String npcId = StringArgumentType.getString(context, "npcId").toLowerCase(Locale.ROOT);
        ServerLevel level = context.getSource().getServer().getLevel(ModDimensions.STARDEW_VALLEY);
        if (level == null) {
            context.getSource().sendFailure(Component.literal("Stardew Valley dimension is not loaded."));
            return 0;
        }

        NpcRuntimeState state = NpcRuntimeDataManager.get(level).states().get(npcId);
        if (state == null) {
            context.getSource().sendFailure(Component.literal("No runtime state found for npcId='" + npcId + "'."));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("=== NPC runtime debug: " + npcId + " ==="), false);
        sendNpcSnapshot(context.getSource(), level, npcId, state);
        return 1;
    }

    private static void sendNpcSnapshot(CommandSourceStack source, ServerLevel level, String npcId, NpcRuntimeState state) {
        List<StardewNpcEntity> entities = level.getEntitiesOfClass(
            StardewNpcEntity.class,
            GLOBAL_NPC_SCAN,
            entity -> npcId.equals(entity.getNpcId())
        );

        StardewNpcEntity entity = entities.isEmpty() ? null : entities.get(0);
        Vec3 fallback = entity != null ? entity.position() : Vec3.atCenterOf(level.getSharedSpawnPos());
        NpcScheduleRuntimeService.TargetPoint target = NpcScheduleRuntimeService.resolveWorldTarget(level, state, fallback);

        String header = String.format(
            Locale.ROOT,
            "%s | schedule=%s@%d node=%d | loc=%s (%d,%d) facing=%d behavior=%s",
            npcId,
            state.activeScheduleKey(),
            state.scheduleCheckpoint(),
            state.scheduleNodeIndex(),
            state.locationName(),
            state.tileX(),
            state.tileY(),
            state.facing(),
            state.routeBehaviorToken().isBlank() ? "<none>" : state.routeBehaviorToken()
        );
        source.sendSuccess(() -> Component.literal(header), false);

        if (entity == null) {
            source.sendSuccess(() -> Component.literal("  entity=<missing> target=" + formatVec(target.position()) + " indoor=" + target.indoorTarget()), false);
            return;
        }

        Vec3 pos = entity.position();
        Vec3 t = target.position();
        double dx = t.x - pos.x;
        double dz = t.z - pos.z;
        double dist2d = Math.sqrt(dx * dx + dz * dz);
        var nav = entity.getNavigation();
        boolean hasPath = nav.getPath() != null;
        boolean pathDone = !hasPath || nav.getPath().isDone();
        var navTarget = nav.getTargetPos();

        String line = String.format(
            Locale.ROOT,
            "  entity=%s target=%s d2d=%.2f indoor=%s count=%d nav(hasPath=%s,done=%s,target=%s)",
            formatVec(pos),
            formatVec(t),
            dist2d,
            target.indoorTarget(),
            entities.size(),
            hasPath,
            pathDone,
            navTarget == null ? "<none>" : navTarget.toShortString()
        );
        source.sendSuccess(() -> Component.literal(line), false);

        NpcCentralMovementService.DebugSnapshot movement = NpcCentralMovementService.getDebugSnapshot(npcId);
        if (movement != null) {
            String movementLine = String.format(
                Locale.ROOT,
                "  move(stage=%s,loc=%s,point=%s,path=%d@%d,fallbackTp=%s,target=%s,next=%s,repath=%s,noPathTicks=%d)",
                movement.stage(),
                movement.location(),
                movement.pointId(),
                movement.pathSize(),
                movement.pathIndex(),
                movement.fallbackTeleportUsed(),
                formatVec(movement.target()),
                formatVec(movement.nextWaypoint()),
                movement.repathReason(),
                movement.noPathTicks()
            );
            String moveLineWithChunk = movementLine.substring(0, movementLine.length() - 1)
                + ",forcedTargetChunk=" + movement.forcedTargetChunk() + ")";
            source.sendSuccess(() -> Component.literal(moveLineWithChunk), false);
        }

        NpcScheduleRuntimeService.ScheduleKeyTrace keyTrace = NpcScheduleRuntimeService.getLastKeyTrace(npcId);
        if (keyTrace != null) {
            String keyTraceLine = String.format(
                Locale.ROOT,
                "  keyTrace(day=%d,season=%s,weekday=%s,weather=%s,hearts=%d,selected=%s,candidates=%s,rejects=%s)",
                keyTrace.day(),
                keyTrace.season(),
                keyTrace.weekday(),
                keyTrace.weather().isBlank() ? "<none>" : keyTrace.weather(),
                keyTrace.hearts(),
                keyTrace.selectedKey(),
                compactList(keyTrace.candidates(), 8),
                compactList(keyTrace.rejections(), 5)
            );
            source.sendSuccess(() -> Component.literal(keyTraceLine), false);
        }

        NpcSpawnManager.SpawnDebugSnapshot spawn = NpcSpawnManager.getDebugSnapshot(level, npcId);
        if (spawn != null) {
            String spawnLine = String.format(
                Locale.ROOT,
                "  spawn(tracked=%s,trackedAlive=%s,loadedCount=%d,firstLoaded=%s,miss=%d,spawnAge=%d,forcedChunk=%s)",
                spawn.trackedUuid(),
                spawn.trackedAlive(),
                spawn.loadedCount(),
                spawn.firstLoadedUuid(),
                spawn.missCount(),
                spawn.spawnAgeTicks(),
                spawn.forcedChunk()
            );
            source.sendSuccess(() -> Component.literal(spawnLine), false);
        }
    }

    private static String formatVec(Vec3 v) {
        if (v == null) {
            return "<none>";
        }
        return String.format(Locale.ROOT, "(%.2f, %.2f, %.2f)", v.x, v.y, v.z);
    }

    private static String compactList(List<String> values, int max) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        int end = Math.min(values.size(), Math.max(1, max));
        String suffix = values.size() > end ? " ..." : "";
        return values.subList(0, end).toString() + suffix;
    }
}
