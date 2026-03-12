package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.player.*;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static com.stardew.craft.player.PlayerDataEventHandler.syncPlayerData;

/**
 * 玩家数据命令
 * 用于测试和调试玩家数据系统
 */
public class PlayerDataCommand {

    private static final double LUCK_MIN = PlayerStardewDataAPI.DAILY_LUCK_MIN;
    private static final double LUCK_MAX = PlayerStardewDataAPI.DAILY_LUCK_MAX;
    
    @SuppressWarnings("null")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stardew")
            .then(Commands.literal("player")
                // 查看数据
                .then(Commands.literal("info")
                    .executes(PlayerDataCommand::showPlayerInfo))
                
                // 经验相关
                .then(Commands.literal("exp")
                    .then(Commands.literal("add")
                        .then(Commands.literal("farming")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> addExperience(ctx, SkillType.FARMING))))
                        .then(Commands.literal("fishing")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> addExperience(ctx, SkillType.FISHING))))
                        .then(Commands.literal("foraging")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> addExperience(ctx, SkillType.FORAGING))))
                        .then(Commands.literal("mining")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> addExperience(ctx, SkillType.MINING))))
                        .then(Commands.literal("combat")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> addExperience(ctx, SkillType.COMBAT)))))
                    .then(Commands.literal("set")
                        .then(Commands.literal("farming")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(ctx -> setExperience(ctx, SkillType.FARMING))))
                        .then(Commands.literal("fishing")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(ctx -> setExperience(ctx, SkillType.FISHING))))
                        .then(Commands.literal("foraging")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(ctx -> setExperience(ctx, SkillType.FORAGING))))
                        .then(Commands.literal("mining")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(ctx -> setExperience(ctx, SkillType.MINING))))
                        .then(Commands.literal("combat")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(ctx -> setExperience(ctx, SkillType.COMBAT)))))
                    .then(Commands.literal("remove")
                        .then(Commands.literal("farming")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> removeExperience(ctx, SkillType.FARMING))))
                        .then(Commands.literal("fishing")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> removeExperience(ctx, SkillType.FISHING))))
                        .then(Commands.literal("foraging")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> removeExperience(ctx, SkillType.FORAGING))))
                        .then(Commands.literal("mining")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> removeExperience(ctx, SkillType.MINING))))
                        .then(Commands.literal("combat")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> removeExperience(ctx, SkillType.COMBAT))))))
                
                // 金币相关
                .then(Commands.literal("money")
                    .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes(PlayerDataCommand::addMoney)))
                    .then(Commands.literal("remove")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes(PlayerDataCommand::removeMoney)))
                    .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                            .executes(PlayerDataCommand::setMoney))))
                
                // 能量相关
                .then(Commands.literal("energy")
                    .then(Commands.literal("add")
                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                            .executes(PlayerDataCommand::addEnergy)))
                    .then(Commands.literal("consume")
                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                            .executes(PlayerDataCommand::consumeEnergy)))
                    .then(Commands.literal("cure")
                        .executes(PlayerDataCommand::cureExhaustion)))
                
                // 生命值相关
                .then(Commands.literal("health")
                    .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes(PlayerDataCommand::setHealth)))
                    .then(Commands.literal("setmax")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(100))
                            .executes(PlayerDataCommand::setMaxHealth))))

                // 今日运气（daily luck）
                .then(Commands.literal("lucky")
                    .then(Commands.literal("get")
                        .executes(PlayerDataCommand::getLucky))
                    .then(Commands.literal("set")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                            .executes(PlayerDataCommand::setLucky))))
                
                // Recipe commands
                .then(Commands.literal("recipe")
                    .then(Commands.literal("unlock")
                        .then(Commands.argument("recipe_id", StringArgumentType.string())
                            .suggests((ctx, builder) -> {
                                java.util.List<String> suggestions = new java.util.ArrayList<>(com.stardew.craft.item.ModItems.COOKING_DISHES.keySet());
                                suggestions.add("all");
                                return net.minecraft.commands.SharedSuggestionProvider.suggest(suggestions, builder);
                            })
                            .executes(PlayerDataCommand::unlockRecipe)))
                    .then(Commands.literal("lock")
                        .then(Commands.argument("recipe_id", StringArgumentType.string())
                            .suggests((ctx, builder) -> {
                                java.util.List<String> suggestions = new java.util.ArrayList<>(com.stardew.craft.item.ModItems.COOKING_DISHES.keySet());
                                suggestions.add("all");
                                return net.minecraft.commands.SharedSuggestionProvider.suggest(suggestions, builder);
                            })
                            .executes(PlayerDataCommand::lockRecipe))))
                
                // 重置数据
                .then(Commands.literal("reset")
                    .executes(PlayerDataCommand::resetPlayerData))
            )
        );
    }

    private static int unlockRecipe(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        String recipeId = StringArgumentType.getString(ctx, "recipe_id");
        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        boolean changed = false;
        if ("all".equalsIgnoreCase(recipeId)) {
            for (String key : com.stardew.craft.item.ModItems.COOKING_DISHES.keySet()) {
                if (data.unlockRecipe(key)) {
                    changed = true;
                }
            }
        } else {
            changed = data.unlockRecipe(recipeId);
        }
        
        if (changed) {
            syncPlayerData(player, data);
            ctx.getSource().sendSuccess(() -> Component.literal("Unlocked recipe(s): " + recipeId), true);
            return 1;
        } else {
            ctx.getSource().sendFailure(Component.literal("Recipe(s) already unlocked or invalid: " + recipeId));
            return 0;
        }
    }

    private static int lockRecipe(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        String recipeId = StringArgumentType.getString(ctx, "recipe_id");
        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        boolean changed = false;
        if ("all".equalsIgnoreCase(recipeId)) {
            for (String key : com.stardew.craft.item.ModItems.COOKING_DISHES.keySet()) {
                if (data.lockRecipe(key)) {
                    changed = true;
                }
            }
        } else {
            changed = data.lockRecipe(recipeId);
        }
        
        if (changed) {
            syncPlayerData(player, data);
            ctx.getSource().sendSuccess(() -> Component.literal("Locked recipe(s): " + recipeId), true);
            return 1;
        } else {
            ctx.getSource().sendFailure(Component.literal("Recipe(s) not unlocked or invalid: " + recipeId));
            return 0;
        }
    }

    private static int getLucky(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        StardewTimeManager timeManager = StardewTimeManager.get();
        double luck = PlayerStardewDataAPI.getDailyLuck(player);

        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.lucky.info",
            luck,
            timeManager.getCurrentYear(),
            timeManager.getCurrentSeason(),
            timeManager.getCurrentDay()
        ), false);

        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.lucky.range",
            LUCK_MIN,
            LUCK_MAX
        ), false);

        return 1;
    }

    @SuppressWarnings("null")
    private static int setLucky(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        double value = DoubleArgumentType.getDouble(context, "value");
        if (value < LUCK_MIN || value > LUCK_MAX) {
            context.getSource().sendFailure(Component.translatable(
                "stardewcraft.command.lucky.out_of_range",
                LUCK_MIN,
                LUCK_MAX
            ));
            return 0;
        }

        StardewTimeManager timeManager = StardewTimeManager.get();
        PlayerStardewDataAPI.setDailyLuckForToday(player, value);

        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.lucky.set",
            value,
            timeManager.getCurrentYear(),
            timeManager.getCurrentSeason(),
            timeManager.getCurrentDay()
        ), false);

        return 1;
    }
    
    /**
     * 显示玩家信息
     */
    private static int showPlayerInfo(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        
        context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.player.info"), false);
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.health.info", data.getHealth(), data.getMaxHealth()), false);
        context.getSource().sendSuccess(() -> Component.translatable(
            data.isExhausted() ? "stardewcraft.command.energy.info_exhausted" : "stardewcraft.command.energy.info",
            data.getEnergy(), data.getMaxEnergy()), false);
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.money.info", data.getMoney()), false);
        
        context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.skill.header"), false);
        for (SkillType skill : SkillType.values()) {
            int level = data.getSkillLevel(skill);
            int exp = data.getSkillExperience(skill);
            int nextExp = data.getExpForNextLevel(skill);
            float progress = data.getLevelProgress(skill);
            
            context.getSource().sendSuccess(() -> Component.translatable(
                "stardewcraft.command.skill.detail", 
                skill.getDisplayName(), level, exp, nextExp, progress * 100), false);
        }
        
        if (!data.getProfessions().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.profession.header"), false);
            for (int profId : data.getProfessions()) {
                ProfessionType prof = ProfessionType.fromId(profId);
                if (prof != null) {
                    context.getSource().sendSuccess(() -> Component.translatable(
                        "stardewcraft.command.profession.detail", prof.getDisplayName()), false);
                }
            }
        }
        
        return 1;
    }
    
    /**
     * 添加经验
     */
    private static int addExperience(CommandContext<CommandSourceStack> context, SkillType skill) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        boolean leveledUp = PlayerStardewDataAPI.addExperience(player, skill, amount);
        
        int newLevel = PlayerStardewDataAPI.getSkillLevel(player, skill);
        int newExp = PlayerStardewDataAPI.getSkillExperience(player, skill);
        
        if (leveledUp) {
            context.getSource().sendSuccess(() -> Component.translatable(
                "stardewcraft.command.exp.levelup", skill.getDisplayName(), newLevel), true);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable(
                "stardewcraft.command.exp.added", amount, skill.getDisplayName(), newExp), false);
        }
        
        return 1;
    }
    
    /**
     * 设置经验
     */
    private static int setExperience(CommandContext<CommandSourceStack> context, SkillType skill) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerStardewDataAPI.setExperience(player, skill, amount);
        
        int newLevel = PlayerStardewDataAPI.getSkillLevel(player, skill);
        
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.exp.set", skill.getDisplayName(), amount, newLevel), false);
        
        return 1;
    }
    
    /**
     * 移除经验
     */
    private static int removeExperience(CommandContext<CommandSourceStack> context, SkillType skill) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerStardewDataAPI.removeExperience(player, skill, amount);
        
        int newLevel = PlayerStardewDataAPI.getSkillLevel(player, skill);
        int newExp = PlayerStardewDataAPI.getSkillExperience(player, skill);
        
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.exp.removed", amount, skill.getDisplayName(), newExp, newLevel), false);
        
        return 1;
    }
    
    /**
     * 添加金币
     */
    private static int addMoney(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerStardewDataAPI.addMoney(player, amount);
        
        int newMoney = PlayerStardewDataAPI.getMoney(player);
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.money.added", amount, newMoney), false);
        
        // 同步到客户端
        syncPlayerData(player, PlayerDataManager.getPlayerData(player));
        
        return 1;
    }
    
    /**
     * 移除金币
     */
    @SuppressWarnings("null")
    private static int removeMoney(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        boolean success = PlayerStardewDataAPI.removeMoney(player, amount);
        
        if (success) {
            int newMoney = PlayerStardewDataAPI.getMoney(player);
            context.getSource().sendSuccess(() -> Component.translatable(
                "stardewcraft.command.money.removed", amount, newMoney), false);
            
            // 同步到客户端
            syncPlayerData(player, PlayerDataManager.getPlayerData(player));
        } else {
            context.getSource().sendFailure(Component.translatable("stardewcraft.command.money.insufficient"));
        }
        
        return success ? 1 : 0;
    }
    
    /**
     * 设置金币
     */
    private static int setMoney(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerStardewDataAPI.setMoney(player, amount);
        
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.money.set", amount), false);
        
        return 1;
    }
    
    /**
     * 添加能量
     */
    private static int addEnergy(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        float amount = FloatArgumentType.getFloat(context, "amount");
        PlayerStardewDataAPI.restoreEnergy(player, amount);
        
        float newEnergy = PlayerStardewDataAPI.getEnergy(player);
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.energy.added", amount, newEnergy), false);
        
        return 1;
    }
    
    /**
     * 消耗能量
     */
    private static int consumeEnergy(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        float amount = FloatArgumentType.getFloat(context, "amount");
        PlayerStardewDataAPI.consumeEnergy(player, amount);
        
        float newEnergy = PlayerStardewDataAPI.getEnergy(player);
        boolean exhausted = PlayerStardewDataAPI.isExhausted(player);
        
        if (exhausted) {
            context.getSource().sendSuccess(() -> Component.translatable(
                "stardewcraft.command.energy.consumed_exhausted", amount, newEnergy), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable(
                "stardewcraft.command.energy.consumed", amount, newEnergy), false);
        }
        
        return 1;
    }
    
    /**
     * 治愈疲惫
     */
    private static int cureExhaustion(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerStardewDataAPI.cureExhaustion(player);
        context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.energy.cured"), false);
        
        return 1;
    }
    
    /**
     * 设置生命值
     */
    private static int setHealth(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerStardewDataAPI.setHealth(player, amount);
        
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.health.set", amount), false);
        
        return 1;
    }
    
    /**
     * 设置最大生命值
     */
    private static int setMaxHealth(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerStardewDataAPI.setMaxHealth(player, amount);
        
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.health.setmax", amount), false);
        
        return 1;
    }
    
    /**
     * 重置玩家数据
     */
    private static int resetPlayerData(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.get().removePlayerData(player.getUUID());
        PlayerDataManager.get().getOrCreateData(player.getUUID());
        
        context.getSource().sendSuccess(() -> Component.literal(
            "§e玩家数据已重置！"), false);
        
        return 1;
    }
}
