package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalBuildingType;
import com.stardew.craft.animal.model.AnimalTypeCatalog;
import com.stardew.craft.animal.service.AnimalAcquireService;
import com.stardew.craft.animal.service.AnimalDoorStateService;
import com.stardew.craft.animal.service.AnimalEntitySyncService;
import com.stardew.craft.animal.service.AnimalShopService;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AnimalDebugCommand {

    @SuppressWarnings("null")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("stardew")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("building")
                    .then(Commands.literal("create")
                        .then(Commands.literal("silo")
                            .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("range", IntegerArgumentType.integer(2, 64))
                                    .executes(AnimalDebugCommand::createSilo)
                                )
                            )
                        )
                        .then(Commands.literal("coop")
                            .then(Commands.argument("tier", IntegerArgumentType.integer(1, 3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                    .then(Commands.argument("range", IntegerArgumentType.integer(2, 64))
                                        .executes(AnimalDebugCommand::createCoop)
                                    )
                                )
                            )
                        )
                        .then(Commands.literal("barn")
                            .then(Commands.argument("tier", IntegerArgumentType.integer(1, 3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                    .then(Commands.argument("range", IntegerArgumentType.integer(2, 64))
                                        .executes(AnimalDebugCommand::createBarn)
                                    )
                                )
                            )
                        )
                        .then(Commands.argument("family", StringArgumentType.word())
                            .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"coop", "barn", "silo"}, builder))
                            .then(Commands.argument("tier", IntegerArgumentType.integer(1, 3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                    .then(Commands.argument("range", IntegerArgumentType.integer(2, 64))
                                        .then(Commands.argument("capacity", IntegerArgumentType.integer(1, 64))
                                            .executes(AnimalDebugCommand::createBuilding)
                                        )
                                    )
                                )
                            )
                        )
                    )
                    .then(Commands.literal("rename")
                        .then(Commands.argument("buildingId", StringArgumentType.word())
                            .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(AnimalDebugCommand::renameBuilding)
                            )
                        )
                    )
                    .then(Commands.literal("remove")
                        .then(Commands.argument("buildingId", StringArgumentType.word())
                            .executes(AnimalDebugCommand::removeBuilding)
                        )
                    )
                    .then(Commands.literal("info")
                        .then(Commands.literal("silo")
                            .executes(ctx -> buildingInfoByFamily(ctx, "silo"))
                        )
                        .then(Commands.literal("coop")
                            .executes(ctx -> buildingInfoByFamily(ctx, "coop"))
                        )
                        .then(Commands.literal("barn")
                            .executes(ctx -> buildingInfoByFamily(ctx, "barn"))
                        )
                        .then(Commands.literal("all")
                            .executes(AnimalDebugCommand::listBuildings)
                        )
                        .then(Commands.argument("buildingId", StringArgumentType.word())
                            .executes(AnimalDebugCommand::buildingInfo)
                        )
                    )
                    .then(Commands.literal("door")
                        .then(Commands.argument("buildingId", StringArgumentType.word())
                            .then(Commands.literal("open").executes(ctx -> setDoor(ctx, true)))
                            .then(Commands.literal("close").executes(ctx -> setDoor(ctx, false)))
                            .then(Commands.literal("toggle").executes(AnimalDebugCommand::toggleDoor))
                        )
                    )
                    .then(Commands.literal("list")
                        .then(Commands.literal("silo")
                            .executes(ctx -> buildingInfoByFamily(ctx, "silo"))
                        )
                        .then(Commands.literal("coop")
                            .executes(ctx -> buildingInfoByFamily(ctx, "coop"))
                        )
                        .then(Commands.literal("barn")
                            .executes(ctx -> buildingInfoByFamily(ctx, "barn"))
                        )
                        .executes(AnimalDebugCommand::listBuildings)
                    )
                    .then(Commands.literal("animal")
                        .then(Commands.literal("purchase")
                            .then(Commands.argument("animalType", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(AnimalTypeCatalog.knownTypeIds(), builder))
                                .then(Commands.argument("buildingId", StringArgumentType.word())
                                    .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .executes(AnimalDebugCommand::purchase)
                                    )
                                )
                            )
                        )
                        .then(Commands.literal("list")
                            .executes(AnimalDebugCommand::listAnimals)
                        )
                        .then(Commands.literal("shop")
                            .executes(AnimalDebugCommand::openAnimalShop)
                        )
                        .then(Commands.literal("sync")
                            .executes(AnimalDebugCommand::syncAnimals)
                        )
                    )
                    .then(Commands.literal("hay")
                        .then(Commands.literal("status")
                            .executes(AnimalDebugCommand::hayStatus)
                        )
                        .then(Commands.literal("store")
                            .then(Commands.argument("count", IntegerArgumentType.integer(1, 999))
                                .executes(AnimalDebugCommand::storeHay)
                            )
                        )
                        .then(Commands.literal("take")
                            .then(Commands.argument("count", IntegerArgumentType.integer(1, 999))
                                .executes(AnimalDebugCommand::takeHay)
                            )
                        )
                    )
                )
        );
    }

    private static int createBuilding(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String family = StringArgumentType.getString(context, "family");
        int tier = IntegerArgumentType.getInteger(context, "tier");
        String name = StringArgumentType.getString(context, "name");
        int range = IntegerArgumentType.getInteger(context, "range");
        int capacity = IntegerArgumentType.getInteger(context, "capacity");

        AnimalBuildingType type = AnimalBuildingType.of(family, tier);
        ServerPlayer player = context.getSource().getPlayerOrException();
        String id = AnimalWorldData.get(level).createBuilding(
            level,
            type,
            player.getUUID(),
            player.blockPosition(),
            range,
            name,
            capacity
        );

        context.getSource().sendSuccess(() -> Component.literal(
            "建筑已创建: id=" + id + ", type=" + type.id() + ", name=" + name + ", capacity=" + capacity
        ), true);
        return 1;
    }

    private static int createSilo(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String name = StringArgumentType.getString(context, "name");
        int range = IntegerArgumentType.getInteger(context, "range");
        ServerPlayer player = context.getSource().getPlayerOrException();
        AnimalBuildingType type = AnimalBuildingType.SILO_TIER_1;
        String id = AnimalWorldData.get(level).createBuilding(level, type, player.getUUID(), player.blockPosition(), range, name, type.defaultCapacity());
        context.getSource().sendSuccess(() -> Component.literal("筒仓已创建: id=" + id + ", hayCap=" + type.hayCapacity()), true);
        return 1;
    }

    private static int createCoop(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        int tier = IntegerArgumentType.getInteger(context, "tier");
        String name = StringArgumentType.getString(context, "name");
        int range = IntegerArgumentType.getInteger(context, "range");
        ServerPlayer player = context.getSource().getPlayerOrException();
        AnimalBuildingType type = AnimalBuildingType.of("coop", tier);
        String id = AnimalWorldData.get(level).createBuilding(level, type, player.getUUID(), player.blockPosition(), range, name, type.defaultCapacity());
        context.getSource().sendSuccess(() -> Component.literal("鸡舍已创建: id=" + id + ", cap=" + type.defaultCapacity()), true);
        return 1;
    }

    private static int createBarn(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        int tier = IntegerArgumentType.getInteger(context, "tier");
        String name = StringArgumentType.getString(context, "name");
        int range = IntegerArgumentType.getInteger(context, "range");
        ServerPlayer player = context.getSource().getPlayerOrException();
        AnimalBuildingType type = AnimalBuildingType.of("barn", tier);
        String id = AnimalWorldData.get(level).createBuilding(level, type, player.getUUID(), player.blockPosition(), range, name, type.defaultCapacity());
        context.getSource().sendSuccess(() -> Component.literal("畜棚已创建: id=" + id + ", cap=" + type.defaultCapacity()), true);
        return 1;
    }

    private static int renameBuilding(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String buildingId = StringArgumentType.getString(context, "buildingId");
        String name = StringArgumentType.getString(context, "name");

        AnimalWorldData.get(level).renameBuilding(buildingId, name);
        context.getSource().sendSuccess(() -> Component.literal("建筑重命名成功: " + buildingId + " -> " + name), true);
        return 1;
    }

    private static int removeBuilding(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String buildingId = StringArgumentType.getString(context, "buildingId");
        AnimalWorldData.get(level).removeBuilding(buildingId);
        context.getSource().sendSuccess(() -> Component.literal("建筑已移除: " + buildingId), true);
        return 1;
    }

    private static int buildingInfo(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String buildingId = StringArgumentType.getString(context, "buildingId");
        var record = AnimalWorldData.get(level).getBuilding(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Building not found: " + buildingId));
        AnimalWorldData data = AnimalWorldData.get(level);
        int hayStored = parseOwnerHay(data, record.ownerPlayerUuid());
        int hayCap = data.getHayCapacity(java.util.UUID.fromString(record.ownerPlayerUuid()));

        context.getSource().sendSuccess(() -> Component.literal("建筑信息: " + record.buildingId()), false);
        context.getSource().sendSuccess(() -> Component.literal(
            "type=" + record.buildingType().id()
                + " | name=" + record.customName()
                + " | owner=" + record.ownerPlayerUuid()
                + " | door=" + (AnimalDoorStateService.isAnyBoundaryDoorOpen(level, record) ? "open" : "closed")
        ), false);
        context.getSource().sendSuccess(() -> Component.literal(
            "animals=" + record.memberAnimalIds().size() + "/" + record.capacity()
                + " | hayCap=" + record.hayCapacity()
                + " | hayStored=" + hayStored + "/" + hayCap
                + " | managerPos=" + record.managerPos()
                + " | range=" + record.range()
        ), false);
        return 1;
    }

    private static int buildingInfoByFamily(CommandContext<CommandSourceStack> context, String family) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        AnimalWorldData data = AnimalWorldData.get(level);
        var buildings = AnimalWorldData.get(level).getBuildings();
        int count = 0;
        for (var building : buildings) {
            if (!family.equalsIgnoreCase(building.buildingType().family())) {
                continue;
            }
            count++;
            int hayStored = parseOwnerHay(data, building.ownerPlayerUuid());
            int hayCap = data.getHayCapacity(java.util.UUID.fromString(building.ownerPlayerUuid()));
            context.getSource().sendSuccess(() -> Component.literal(
                "- " + building.buildingId()
                    + " | " + building.buildingType().id()
                    + " | name=" + building.customName()
                    + " | owner=" + building.ownerPlayerUuid()
                    + " | animals=" + building.memberAnimalIds().size() + "/" + building.capacity()
                    + " | hayCap=" + building.hayCapacity()
                    + " | hayStored=" + hayStored + "/" + hayCap
                    + " | door=" + (AnimalDoorStateService.isAnyBoundaryDoorOpen(level, building) ? "open" : "closed")
            ), false);
        }
        if (count == 0) {
            context.getSource().sendSuccess(() -> Component.literal("未找到 " + family + " 建筑记录"), false);
        }
        return 1;
    }

    private static int setDoor(CommandContext<CommandSourceStack> context, boolean open) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String buildingId = StringArgumentType.getString(context, "buildingId");
        var record = AnimalWorldData.get(level).getBuilding(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Building not found: " + buildingId));
        int changed = AnimalDoorStateService.setBoundaryDoorsOpen(level, record, open);
        context.getSource().sendSuccess(() -> Component.literal("物理门状态已更新: " + buildingId + " -> " + (open ? "open" : "closed") + " (changed=" + changed + ")"), true);
        return 1;
    }

    private static int toggleDoor(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String buildingId = StringArgumentType.getString(context, "buildingId");
        var record = AnimalWorldData.get(level).getBuilding(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Building not found: " + buildingId));
        boolean next = !AnimalDoorStateService.isAnyBoundaryDoorOpen(level, record);
        int changed = AnimalDoorStateService.setBoundaryDoorsOpen(level, record, next);
        context.getSource().sendSuccess(() -> Component.literal("物理门状态已切换: " + buildingId + " -> " + (next ? "open" : "closed") + " (changed=" + changed + ")"), true);
        return 1;
    }

    private static int listBuildings(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        var buildings = AnimalWorldData.get(level).getBuildings();
        if (buildings.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("暂无建筑记录"), false);
            return 1;
        }

        for (var building : buildings) {
            context.getSource().sendSuccess(() -> Component.literal(
                "- " + building.buildingId()
                    + " | " + building.buildingType().id()
                    + " | name=" + building.customName()
                    + " | owner=" + building.ownerPlayerUuid()
                    + " | animals=" + building.memberAnimalIds().size() + "/" + building.capacity()
                    + " | hayCap=" + building.hayCapacity()
                    + " | door=" + (AnimalDoorStateService.isAnyBoundaryDoorOpen(level, building) ? "open" : "closed")
            ), false);
        }
        return 1;
    }

    private static int hayStatus(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        ServerPlayer player = context.getSource().getPlayerOrException();
        AnimalWorldData data = AnimalWorldData.get(level);
        int hay = data.getHayAmount(player.getUUID());
        int cap = data.getHayCapacity(player.getUUID());
        boolean hasSilo = data.hasAnySilo(player.getUUID());
        context.getSource().sendSuccess(() -> Component.literal(
            "干草状态: hay=" + hay + "/" + cap + " | silo=" + (hasSilo ? "yes" : "no")
        ), false);
        return 1;
    }

    @SuppressWarnings("null")
    private static int storeHay(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        ServerPlayer player = context.getSource().getPlayerOrException();
        int count = IntegerArgumentType.getInteger(context, "count");
        AnimalWorldData data = AnimalWorldData.get(level);
        if (!data.hasAnySilo(player.getUUID())) {
            context.getSource().sendFailure(Component.literal("你还没有筒仓，无法存放干草。"));
            return 0;
        }
        int stored = data.storeHay(player.getUUID(), count);
        int left = count - stored;
        context.getSource().sendSuccess(() -> Component.literal(
            "存草完成: stored=" + stored + ", left=" + left + ", now="
                + data.getHayAmount(player.getUUID()) + "/" + data.getHayCapacity(player.getUUID())
        ), true);
        return 1;
    }

    private static int takeHay(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        ServerPlayer player = context.getSource().getPlayerOrException();
        int count = IntegerArgumentType.getInteger(context, "count");
        AnimalWorldData data = AnimalWorldData.get(level);
        int removed = data.takeHay(player.getUUID(), count);
        context.getSource().sendSuccess(() -> Component.literal(
            "取草完成: removed=" + removed + ", now="
                + data.getHayAmount(player.getUUID()) + "/" + data.getHayCapacity(player.getUUID())
        ), true);
        return 1;
    }

    private static int purchase(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        String animalType = StringArgumentType.getString(context, "animalType");
        String buildingId = StringArgumentType.getString(context, "buildingId");
        String name = StringArgumentType.getString(context, "name");

        var record = AnimalAcquireService.purchase(level, animalType, name, buildingId);
        context.getSource().sendSuccess(() -> Component.literal(
            "购买成功: animalId=" + record.animalId()
                + ", type=" + record.animalTypeId()
                + ", name=" + record.customName()
                + ", stage=" + (record.isBaby() ? "baby" : "adult")
                + ", age=" + record.ageDays() + "/" + record.daysToMature()
                + ", building=" + record.buildingId()
        ), true);
        return 1;
    }

    private static int openAnimalShop(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        requireStardewLevel(context);
        ServerPlayer player = context.getSource().getPlayerOrException();
        AnimalShopService.openForPlayer(player);
        return 1;
    }

    private static int listAnimals(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        var animals = AnimalWorldData.get(level).getAnimals();
        if (animals.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("暂无动物记录"), false);
            return 1;
        }

        for (var animal : animals) {
            context.getSource().sendSuccess(() -> Component.literal(
                "- id=" + animal.animalId()
                    + " | type=" + animal.animalTypeId()
                    + " | name=" + animal.customName()
                    + " | stage=" + (animal.isBaby() ? "baby" : "adult")
                    + " | age=" + animal.ageDays() + "/" + animal.daysToMature()
                    + " | source=" + animal.acquisitionSource().name()
                    + " | building=" + animal.buildingId()
            ), false);
        }
        return 1;
    }

    private static int syncAnimals(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = requireStardewLevel(context);
        AnimalEntitySyncService.SyncResult result = AnimalEntitySyncService.syncAll(level);
        context.getSource().sendSuccess(() -> Component.literal(
            "动物同步完成: updated=" + result.updated() + ", spawned=" + result.spawned() + ", orphansRemoved=" + result.orphansRemoved()
        ), true);
        return 1;
    }

    private static ServerLevel requireStardewLevel(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            throw new IllegalStateException("此命令只能在星露谷维度使用");
        }
        return level;
    }

    private static int parseOwnerHay(AnimalWorldData data, String ownerUuid) {
        try {
            return data.getHayAmount(java.util.UUID.fromString(ownerUuid));
        } catch (IllegalArgumentException ignored) {
            return 0;
        }
    }
}
