package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.SteelFalchionLineBurstPayload;
import com.stardew.craft.combat.network.SteelFalchionLineCreatePayload;
import com.stardew.craft.combat.network.SteelFalchionLinePointPayload;
import com.stardew.craft.combat.network.SteelFalchionLinePulsePayload;
import com.stardew.craft.combat.network.SteelFalchionTracePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import com.stardew.craft.effect.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("null")
public final class SteelFalchionLineTracker {

    private static final int LINE_DURATION_TICKS = 100;
    private static final int DOT_DURATION_TICKS = 100;
    private static final int DOT_INTERVAL_TICKS = 20;
    private static final int SPEED_DURATION_TICKS = 100;
    private static final float LINE_LENGTH = 7.0f;
    private static final float LINE_WIDTH = 0.55f;
    private static final float TRIGGER_RADIUS = 0.70f;
    private static final float BURST_RADIUS = 1.0f;
    private static final float TRACE_POINT_STEP = 0.35f;
    private static final float TRACE_MIN_DISTANCE = 0.18f;

    private static int nextLineId = 1;

    private static final class LineState {
        private final int id;
        private final List<Vec3> points = new ArrayList<>();
        private final Set<UUID> triggeredTargets = new HashSet<>();
        private final float width;
        private final float dotMultiplier;
        private final SkillContext.SkillTier tier;
        private final long endTick;
        private final boolean burstOnEnd;
        private final long burstTick;
        private boolean burstDone = false;
        private boolean speedTriggered = false;

        private LineState(int id, float width, float dotMultiplier, SkillContext.SkillTier tier,
                          long endTick, boolean burstOnEnd, long burstTick) {
            this.id = id;
            this.width = width;
            this.dotMultiplier = dotMultiplier;
            this.tier = tier;
            this.endTick = endTick;
            this.burstOnEnd = burstOnEnd;
            this.burstTick = burstTick;
        }
    }

    private static final class TraceState {
        private final long endTick;
        private final int lineId;
        private Vec3 lastPos;

        private TraceState(long endTick, int lineId, Vec3 lastPos) {
            this.endTick = endTick;
            this.lineId = lineId;
            this.lastPos = lastPos;
        }
    }

    private static final class DotState {
        private final UUID targetId;
        private final float damageMultiplier;
        private final SkillContext.SkillTier tier;
        private final String skillId;
        private long endTick;
        private long nextDamageTick;

        private DotState(UUID targetId, float damageMultiplier, SkillContext.SkillTier tier,
                         String skillId, long endTick, long nextDamageTick) {
            this.targetId = targetId;
            this.damageMultiplier = damageMultiplier;
            this.tier = tier;
            this.skillId = skillId;
            this.endTick = endTick;
            this.nextDamageTick = nextDamageTick;
        }
    }

    private static final class PlayerState {
        private final List<LineState> lines = new ArrayList<>();
        private final Map<UUID, DotState> dots = new HashMap<>();
        private TraceState trace;
    }

    private static final Map<UUID, PlayerState> ACTIVE = new HashMap<>();

    private SteelFalchionLineTracker() {}

    public static void startMinorLine(ServerPlayer player, long nowTick, Vec3 center, float yawDegrees,
                                      float dotMultiplier) {
        if (player == null) {
            return;
        }
        Vec3 dir = yawToDir(yawDegrees);
        Vec3 start = center.add(dir.scale(-LINE_LENGTH * 0.5));
        Vec3 end = center.add(dir.scale(LINE_LENGTH * 0.5));
        createLine(player, nowTick, start, end, dotMultiplier, SkillContext.SkillTier.MINOR, false, 0L);

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.7f, 1.35f);
    }

    public static void startTrace(ServerPlayer player, long nowTick, int durationTicks, float dotMultiplier) {
        if (player == null) {
            return;
        }
        PlayerState state = ACTIVE.computeIfAbsent(player.getUUID(), key -> new PlayerState());
        Vec3 start = new Vec3(player.getX(), player.getY() + 0.02, player.getZ());
        LineState line = createLine(player, nowTick, start, null, dotMultiplier, SkillContext.SkillTier.MAJOR,
            true, nowTick + durationTicks);
        state.trace = new TraceState(nowTick + durationTicks, line.id, start);

        PacketDistributor.sendToPlayer(player, new SteelFalchionTracePayload(true, durationTicks));

        player.addEffect(new MobEffectInstance(ModMobEffects.SPEED, durationTicks, 2, false, true, true));
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8f, 1.1f);
    }

    public static void tick(ServerPlayer player, long nowTick) {
        PlayerState state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }

        if (state.trace != null) {
            if (nowTick >= state.trace.endTick) {
                state.trace = null;
            } else {
                updateTrace(player, state, nowTick);
            }
        }

        if (!state.lines.isEmpty()) {
            Iterator<LineState> it = state.lines.iterator();
            while (it.hasNext()) {
                LineState line = it.next();
                if (line.burstOnEnd && !line.burstDone && nowTick >= line.burstTick) {
                    burstLine(player, line, nowTick);
                }

                if (nowTick >= line.endTick) {
                    it.remove();
                    continue;
                }

                handleLineTriggers(player, state, line, nowTick);
            }
        }

        tickDots(player, state, nowTick);

        if (state.lines.isEmpty() && state.dots.isEmpty() && state.trace == null) {
            ACTIVE.remove(player.getUUID());
        }
    }

    private static LineState createLine(ServerPlayer player, long nowTick, Vec3 start, Vec3 end,
                                        float dotMultiplier, SkillContext.SkillTier tier,
                                        boolean burstOnEnd, long burstTick) {
        PlayerState state = ACTIVE.computeIfAbsent(player.getUUID(), key -> new PlayerState());
        int lineId = nextLineId++;
        LineState line = new LineState(lineId, LINE_WIDTH, dotMultiplier, tier,
            nowTick + LINE_DURATION_TICKS, burstOnEnd, burstTick);
        line.points.add(start);
        state.lines.add(line);

        PacketDistributor.sendToPlayersInDimension(player.serverLevel(),
            new SteelFalchionLineCreatePayload(lineId, (float) start.x, (float) start.y, (float) start.z,
                LINE_DURATION_TICKS, LINE_WIDTH));

        if (end != null) {
            line.points.add(end);
            PacketDistributor.sendToPlayersInDimension(player.serverLevel(),
                new SteelFalchionLinePointPayload(lineId, (float) end.x, (float) end.y, (float) end.z));
        }

        return line;
    }

    private static void updateTrace(ServerPlayer player, PlayerState state, long nowTick) {
        TraceState trace = state.trace;
        if (trace == null) {
            return;
        }
        LineState line = findLine(state, trace.lineId);
        if (line == null) {
            state.trace = null;
            return;
        }

        Vec3 current = new Vec3(player.getX(), player.getY() + 0.02, player.getZ());
        double dist = current.subtract(trace.lastPos).horizontalDistance();
        if (dist < TRACE_MIN_DISTANCE) {
            return;
        }

        int steps = Math.max(1, (int) Math.ceil(dist / TRACE_POINT_STEP));
        Vec3 delta = current.subtract(trace.lastPos);
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3 point = trace.lastPos.add(delta.scale(t));
            line.points.add(point);
            PacketDistributor.sendToPlayersInDimension(player.serverLevel(),
                new SteelFalchionLinePointPayload(line.id, (float) point.x, (float) point.y, (float) point.z));
        }
        trace.lastPos = current;
    }

    private static void handleLineTriggers(ServerPlayer player, PlayerState state, LineState line, long nowTick) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (line.points.size() < 2) {
            return;
        }

        AABB box = computeBounds(line.points).inflate(TRIGGER_RADIUS, 1.0, TRIGGER_RADIUS);
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity.isAlive());

        for (LivingEntity target : targets) {
            if (target == player) {
                continue;
            }
            if (line.triggeredTargets.contains(target.getUUID())) {
                continue;
            }
            float triggerRadius = Math.max(line.width, TRIGGER_RADIUS);
            float targetRadius = (float) Math.max(0.2, target.getBbWidth() * 0.5);
            float hitRadius = Math.max(triggerRadius, targetRadius);
            if (distanceToPolylineSqr2D(target.position(), line.points) > (hitRadius * hitRadius)) {
                continue;
            }

            line.triggeredTargets.add(target.getUUID());
            applyDot(state, target, nowTick, line.dotMultiplier, line.tier);
            PacketDistributor.sendToPlayersInDimension(serverLevel, new SteelFalchionLinePulsePayload(line.id, 8));
            serverLevel.playSound(null, target.blockPosition(), SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 0.5f, 1.2f);
        }

        if (!line.speedTriggered && distanceToPolylineSqr2D(player.position(), line.points) <= (line.width + 0.05f) * (line.width + 0.05f)) {
            line.speedTriggered = true;
            player.addEffect(new MobEffectInstance(ModMobEffects.SPEED, SPEED_DURATION_TICKS, 1, false, true, true));
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.5f);
        }
    }

    private static void applyDot(PlayerState state, LivingEntity target, long nowTick,
                                 float damageMultiplier, SkillContext.SkillTier tier) {
        DotState existing = state.dots.get(target.getUUID());
        long endTick = nowTick + DOT_DURATION_TICKS;
        if (existing != null) {
            float finalMultiplier = Math.max(existing.damageMultiplier, damageMultiplier);
            SkillContext.SkillTier finalTier = existing.tier == SkillContext.SkillTier.MAJOR
                ? existing.tier
                : tier;
            long finalEnd = Math.max(existing.endTick, endTick);
            long nextTick = Math.min(existing.nextDamageTick, nowTick);
            state.dots.put(target.getUUID(), new DotState(target.getUUID(), finalMultiplier, finalTier,
                "steel_falchion_line_dot", finalEnd, nextTick));
            return;
        }
        DotState dot = new DotState(target.getUUID(), damageMultiplier, tier,
            "steel_falchion_line_dot", endTick, nowTick);
        state.dots.put(target.getUUID(), dot);
    }

    private static void tickDots(ServerPlayer player, PlayerState state, long nowTick) {
        if (state.dots.isEmpty()) {
            return;
        }
        ServerLevel level = player.serverLevel();
        Iterator<DotState> it = state.dots.values().iterator();
        while (it.hasNext()) {
            DotState dot = it.next();
            if (nowTick >= dot.endTick) {
                it.remove();
                continue;
            }
            if (nowTick < dot.nextDamageTick) {
                continue;
            }
            dot.nextDamageTick += DOT_INTERVAL_TICKS;
            LivingEntity target = level.getEntity(dot.targetId) instanceof LivingEntity living ? living : null;
            if (target == null || !target.isAlive()) {
                it.remove();
                continue;
            }

            applyDotDamage(player, target, nowTick, dot.damageMultiplier, dot.tier, dot.skillId);
        }
    }

    private static void applyDotDamage(ServerPlayer player, LivingEntity target, long nowTick,
                                       float damageMultiplier, SkillContext.SkillTier tier, String skillId) {
        SkillContext context = SkillContext.builder()
            .skillId(skillId)
            .tier(tier)
            .damageMultiplier(damageMultiplier)
            .build();
        WeaponSkillContextStore.setPending(player, context, nowTick + 5);

        target.invulnerableTime = 0;
        target.hurtTime = 0;
        target.hurt(player.damageSources().playerAttack(player), 1.0F);
    }

    private static void burstLine(ServerPlayer player, LineState line, long nowTick) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (line.points.size() < 2) {
            line.burstDone = true;
            return;
        }

        line.burstDone = true;
        PacketDistributor.sendToPlayersInDimension(serverLevel, new SteelFalchionLineBurstPayload(line.id));

        AABB box = computeBounds(line.points).inflate(BURST_RADIUS, 1.2, BURST_RADIUS);
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity.isAlive() && entity != player);

        Set<UUID> damaged = new HashSet<>();
        for (LivingEntity target : targets) {
            if (damaged.contains(target.getUUID())) {
                continue;
            }
            if (distanceToPolylineSqr2D(target.position(), line.points) > BURST_RADIUS * BURST_RADIUS) {
                continue;
            }
            damaged.add(target.getUUID());

            SkillContext context = SkillContext.builder()
                .skillId("steel_falchion_trace")
                .tier(SkillContext.SkillTier.MAJOR)
                .damageMultiplier(1.0f)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);

            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(player.damageSources().playerAttack(player), 1.0F);
        }

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.75f, 1.0f);
    }

    private static LineState findLine(PlayerState state, int lineId) {
        for (LineState line : state.lines) {
            if (line.id == lineId) {
                return line;
            }
        }
        return null;
    }

    private static Vec3 yawToDir(float yawDegrees) {
        double rad = Math.toRadians(yawDegrees);
        return new Vec3(-Math.sin(rad), 0.0, Math.cos(rad));
    }

    private static AABB computeBounds(List<Vec3> points) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (Vec3 p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            minZ = Math.min(minZ, p.z);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
            maxZ = Math.max(maxZ, p.z);
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static double distanceToPolylineSqr2D(Vec3 p, List<Vec3> points) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < points.size() - 1; i++) {
            Vec3 a = points.get(i);
            Vec3 b = points.get(i + 1);
            double d = distanceToSegmentSqr2D(p, a, b);
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

    private static double distanceToSegmentSqr2D(Vec3 p, Vec3 a, Vec3 b) {
        Vec3 ap = new Vec3(p.x - a.x, 0.0, p.z - a.z);
        Vec3 ab = new Vec3(b.x - a.x, 0.0, b.z - a.z);
        double abLen2 = ab.lengthSqr();
        if (abLen2 < 1.0E-6) {
            return ap.lengthSqr();
        }
        double t = (ap.x * ab.x + ap.z * ab.z) / abLen2;
        t = Math.max(0.0, Math.min(1.0, t));
        double cx = a.x + ab.x * t;
        double cz = a.z + ab.z * t;
        double dx = p.x - cx;
        double dz = p.z - cz;
        return dx * dx + dz * dz;
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
