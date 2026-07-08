package com.stardew.craft.item.tool;

import com.stardew.craft.network.payload.PointPlanSyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PointPlanWandItem extends Item {
    private static final String TAG_SELECTED_PLAN = "SelectedPlan";
    private static final String TAG_PLANS = "PointPlans";
    private static final String TAG_PLAN_ID = "PlanId";
    private static final String TAG_POINTS = "Points";
    private static final String TAG_NPC_ID = "NpcId";
    private static final String TAG_POS = "Pos";
    private static final String TAG_DIRECTION = "Direction";
    private static final String DEFAULT_PLAN_ID = "festival_of_ice_npcs";

    public PointPlanWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        return handleUse(context.getLevel(), player, context.getItemInHand());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = handleUse(level, player, stack);
        return new InteractionResultHolder<>(result, stack);
    }

    private InteractionResult handleUse(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        ensureSelectedPlan(stack);
        if (player.isShiftKeyDown()) {
            sync(serverPlayer, stack, PointPlanSyncPayload.OPEN_MAIN, null);
            return InteractionResult.CONSUME;
        }

        PendingPoint pendingPoint = new PendingPoint(player.blockPosition().immutable(), normalizeDirection(player));
        sync(serverPlayer, stack, PointPlanSyncPayload.OPEN_ADD_POINT, pendingPoint);
        return InteractionResult.CONSUME;
    }

    public static String getSelectedPlanId(ItemStack stack) {
        CompoundTag tag = data(stack);
        String id = cleanPlanId(tag.getString(TAG_SELECTED_PLAN));
        if (!id.isEmpty()) {
            return id;
        }
        List<Plan> plans = getPlans(stack);
        return plans.isEmpty() ? DEFAULT_PLAN_ID : plans.getFirst().id();
    }

    public static void setSelectedPlanId(ItemStack stack, String planId) {
        String clean = cleanPlanId(planId);
        if (clean.isEmpty()) {
            clean = DEFAULT_PLAN_ID;
        }
        List<Plan> plans = getPlans(stack);
        String targetPlanId = clean;
        if (plans.stream().noneMatch(plan -> plan.id().equals(targetPlanId))) {
            plans = new ArrayList<>(plans);
            plans.add(new Plan(targetPlanId, List.of()));
            writePlans(stack, plans);
        }
        CompoundTag tag = data(stack);
        tag.putString(TAG_SELECTED_PLAN, targetPlanId);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static List<Plan> getPlans(ItemStack stack) {
        CompoundTag tag = data(stack);
        ListTag list = tag.getList(TAG_PLANS, Tag.TAG_COMPOUND);
        List<Plan> plans = new ArrayList<>(Math.max(1, list.size()));
        for (int i = 0; i < list.size(); i++) {
            CompoundTag planTag = list.getCompound(i);
            String id = cleanPlanId(planTag.getString(TAG_PLAN_ID));
            if (id.isEmpty()) {
                continue;
            }
            List<PointEntry> points = new ArrayList<>();
            ListTag pointList = planTag.getList(TAG_POINTS, Tag.TAG_COMPOUND);
            for (int pointIndex = 0; pointIndex < pointList.size(); pointIndex++) {
                CompoundTag pointTag = pointList.getCompound(pointIndex);
                points.add(new PointEntry(
                    cleanNpcId(pointTag.getString(TAG_NPC_ID)),
                    BlockPos.of(pointTag.getLong(TAG_POS)).immutable(),
                    cleanDirection(pointTag.getString(TAG_DIRECTION))
                ));
            }
            plans.add(new Plan(id, List.copyOf(points)));
        }
        if (plans.isEmpty()) {
            plans.add(new Plan(DEFAULT_PLAN_ID, List.of()));
        }
        plans.sort(Comparator.comparing(Plan::id));
        return List.copyOf(plans);
    }

    public static void createPlan(ItemStack stack, String planId) {
        setSelectedPlanId(stack, planId);
    }

    public static void deletePlan(ItemStack stack, String planId) {
        String clean = cleanPlanId(planId);
        List<Plan> remaining = new ArrayList<>();
        for (Plan plan : getPlans(stack)) {
            if (!plan.id().equals(clean)) {
                remaining.add(plan);
            }
        }
        if (remaining.isEmpty()) {
            remaining.add(new Plan(DEFAULT_PLAN_ID, List.of()));
        }
        writePlans(stack, remaining);
        setSelectedPlanId(stack, remaining.getFirst().id());
    }

    public static void clearPlan(ItemStack stack, String planId) {
        replacePlan(stack, cleanPlanId(planId), List.of());
    }

    public static void deletePoint(ItemStack stack, String planId, int index) {
        Plan plan = getPlan(stack, cleanPlanId(planId));
        if (plan == null || index < 0 || index >= plan.points().size()) {
            return;
        }
        List<PointEntry> points = new ArrayList<>(plan.points());
        points.remove(index);
        replacePlan(stack, plan.id(), points);
    }

    public static void addPoint(ItemStack stack, String planId, PointEntry point) {
        String cleanPlanId = cleanPlanId(planId);
        if (cleanPlanId.isEmpty()) {
            cleanPlanId = getSelectedPlanId(stack);
        }
        Plan plan = getPlan(stack, cleanPlanId);
        List<PointEntry> points = new ArrayList<>(plan == null ? List.of() : plan.points());
        points.add(new PointEntry(cleanNpcId(point.npcId()), point.pos().immutable(), cleanDirection(point.direction())));
        replacePlan(stack, cleanPlanId, points);
        setSelectedPlanId(stack, cleanPlanId);
    }

    public static void sync(ServerPlayer player, ItemStack stack, String openMode, PendingPoint pendingPoint) {
        List<String> npcIds = com.stardew.craft.npc.data.NpcDataRegistry.capabilities().keySet().stream()
            .sorted()
            .toList();
        PacketDistributor.sendToPlayer(player, new PointPlanSyncPayload(
            getSelectedPlanId(stack),
            getPlans(stack),
            npcIds,
            openMode == null ? PointPlanSyncPayload.OPEN_NONE : openMode,
            pendingPoint
        ));
    }

    private static void ensureSelectedPlan(ItemStack stack) {
        setSelectedPlanId(stack, getSelectedPlanId(stack));
    }

    private static void replacePlan(ItemStack stack, String planId, List<PointEntry> points) {
        String clean = cleanPlanId(planId);
        if (clean.isEmpty()) {
            clean = DEFAULT_PLAN_ID;
        }
        List<Plan> plans = new ArrayList<>();
        boolean replaced = false;
        for (Plan plan : getPlans(stack)) {
            if (plan.id().equals(clean)) {
                plans.add(new Plan(clean, List.copyOf(points)));
                replaced = true;
            } else {
                plans.add(plan);
            }
        }
        if (!replaced) {
            plans.add(new Plan(clean, List.copyOf(points)));
        }
        writePlans(stack, plans);
    }

    private static Plan getPlan(ItemStack stack, String planId) {
        for (Plan plan : getPlans(stack)) {
            if (plan.id().equals(planId)) {
                return plan;
            }
        }
        return null;
    }

    private static void writePlans(ItemStack stack, List<Plan> plans) {
        CompoundTag tag = data(stack);
        ListTag list = new ListTag();
        for (Plan plan : plans) {
            CompoundTag planTag = new CompoundTag();
            planTag.putString(TAG_PLAN_ID, cleanPlanId(plan.id()));
            ListTag pointList = new ListTag();
            for (PointEntry point : plan.points()) {
                CompoundTag pointTag = new CompoundTag();
                pointTag.putString(TAG_NPC_ID, cleanNpcId(point.npcId()));
                pointTag.putLong(TAG_POS, point.pos().asLong());
                pointTag.putString(TAG_DIRECTION, cleanDirection(point.direction()));
                pointList.add(pointTag);
            }
            planTag.put(TAG_POINTS, pointList);
            list.add(planTag);
        }
        tag.put(TAG_PLANS, list);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static String normalizeDirection(Player player) {
        Direction direction = Direction.fromYRot(player.getYRot());
        return cleanDirection(direction.getName());
    }

    private static String cleanPlanId(String raw) {
        String clean = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        clean = clean.replaceAll("[^a-z0-9_./-]", "_");
        return clean.length() > 96 ? clean.substring(0, 96) : clean;
    }

    private static String cleanNpcId(String raw) {
        String clean = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        clean = clean.replaceAll("[^a-z0-9_./-]", "_");
        return clean.length() > 64 ? clean.substring(0, 64) : clean;
    }

    private static String cleanDirection(String raw) {
        String clean = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        return switch (clean) {
            case "east", "south", "west", "north" -> clean;
            default -> "south";
        };
    }

    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable("item.stardewcraft.point_plan_wand");
    }

    public record Plan(String id, List<PointEntry> points) {
        public Plan {
            id = cleanPlanId(id);
            points = points == null ? List.of() : List.copyOf(points);
        }
    }

    public record PointEntry(String npcId, BlockPos pos, String direction) {
        public PointEntry {
            npcId = cleanNpcId(npcId);
            pos = pos == null ? BlockPos.ZERO : pos.immutable();
            direction = cleanDirection(direction);
        }
    }

    public record PendingPoint(BlockPos pos, String direction) {
        public PendingPoint {
            pos = pos == null ? BlockPos.ZERO : pos.immutable();
            direction = cleanDirection(direction);
        }
    }
}
