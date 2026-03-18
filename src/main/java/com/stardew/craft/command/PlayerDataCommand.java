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

                .then(Commands.literal("unlock-source")
                    .then(Commands.literal("apply")
                        .then(Commands.argument("source_id", StringArgumentType.string())
                            .suggests((ctx, builder) ->
                                net.minecraft.commands.SharedSuggestionProvider.suggest(
                                    UnlockSourceData.getSourceIds(),
                                    builder
                                ))
                            .executes(PlayerDataCommand::applyUnlockSource))))

                // 职业相关（调试 + 原版分支选择流程）
                .then(Commands.literal("profession")
                    .then(Commands.literal("list")
                        .executes(PlayerDataCommand::listProfessions))
                    .then(Commands.literal("pending")
                        .executes(PlayerDataCommand::showPendingProfessions))
                    .then(Commands.literal("choose")
                        .then(Commands.argument("profession", StringArgumentType.word())
                            .suggests((ctx, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(getProfessionSuggestions(), builder))
                            .executes(PlayerDataCommand::choosePendingProfession)))
                    .then(Commands.literal("grant")
                        .then(Commands.argument("profession", StringArgumentType.word())
                            .suggests((ctx, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(getProfessionSuggestions(), builder))
                            .executes(PlayerDataCommand::grantProfession)))
                    .then(Commands.literal("revoke")
                        .then(Commands.argument("profession", StringArgumentType.word())
                            .suggests((ctx, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(getProfessionSuggestions(), builder))
                            .executes(PlayerDataCommand::revokeProfession)))
                    .then(Commands.literal("clear")
                        .executes(PlayerDataCommand::clearProfessions))
                    .then(Commands.literal("repair")
                        .executes(PlayerDataCommand::repairProfessionChoices)))
                
                // 重置数据
                .then(Commands.literal("reset")
                    .executes(PlayerDataCommand::resetPlayerData))
            )
        );
    }

    @SuppressWarnings("null")
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

    @SuppressWarnings("null")
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

    @SuppressWarnings("null")
    private static int applyUnlockSource(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        String sourceId = StringArgumentType.getString(ctx, "source_id");
        boolean changed = PlayerStardewDataAPI.applyUnlockSource(player, sourceId);
        if (changed) {
            ctx.getSource().sendSuccess(() -> Component.literal("Applied unlock source: " + sourceId), true);
            return 1;
        }

        ctx.getSource().sendFailure(Component.literal("No unlock applied (unknown source or already unlocked): " + sourceId));
        return 0;
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

        if (data.hasPendingProfessionChoices()) {
            context.getSource().sendSuccess(() -> Component.literal("待选职业："), false);
            int index = 1;
            for (PlayerStardewData.ProfessionChoicePrompt prompt : data.getPendingProfessionChoices()) {
                SkillType skill = prompt.skill();
                int level = prompt.level();
                String options = getChoiceOptionsText(skill, level, data);
                int currentIndex = index;
                context.getSource().sendSuccess(() -> Component.literal(
                    "  " + currentIndex + ") " + skill.getDisplayName() + " Lv." + level + " -> " + options
                ), false);
                index++;
            }
        }
        
        return 1;
    }

    private static java.util.List<String> getProfessionSuggestions() {
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        for (ProfessionType profession : ProfessionType.values()) {
            suggestions.add(profession.getName());
        }
        return suggestions;
    }

    private static ProfessionType parseProfession(CommandContext<CommandSourceStack> context) {
        String input = StringArgumentType.getString(context, "profession");
        for (ProfessionType profession : ProfessionType.values()) {
            if (profession.getName().equalsIgnoreCase(input)) {
                return profession;
            }
        }
        return null;
    }

    private static String getChoiceOptionsText(SkillType skill, int level, PlayerStardewData data) {
        if (level == 5) {
            ProfessionType[] options = ProfessionType.getLevel5Options(skill);
            return options[0].getName() + " | " + options[1].getName();
        }
        if (level == 10) {
            ProfessionType level5 = null;
            for (int professionId : data.getProfessions()) {
                ProfessionType profession = ProfessionType.fromId(professionId);
                if (profession != null && profession.getSkillType() == skill && profession.getRequiredLevel() == 5) {
                    level5 = profession;
                    break;
                }
            }
            if (level5 != null) {
                ProfessionType[] options = ProfessionType.getLevel10Options(skill, level5);
                return options[0].getName() + " | " + options[1].getName();
            }
        }
        return "(无可用选项)";
    }

    private static int listProfessions(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        if (data.getProfessions().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("当前没有已选职业。"), false);
            return 1;
        }

        context.getSource().sendSuccess(() -> Component.literal("已选职业："), false);
        for (int professionId : data.getProfessions()) {
            ProfessionType profession = ProfessionType.fromId(professionId);
            if (profession != null) {
                context.getSource().sendSuccess(() -> Component.literal(
                    "- " + profession.getName() + " (" + profession.getSkillType().getName() + " Lv." + profession.getRequiredLevel() + ")"
                ), false);
            }
        }
        return 1;
    }

    private static int showPendingProfessions(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        if (!data.hasPendingProfessionChoices()) {
            context.getSource().sendSuccess(() -> Component.literal("当前没有待选职业。"), false);
            return 1;
        }

        context.getSource().sendSuccess(() -> Component.literal("待选职业队列："), false);
        int index = 1;
        for (PlayerStardewData.ProfessionChoicePrompt prompt : data.getPendingProfessionChoices()) {
            SkillType skill = prompt.skill();
            int level = prompt.level();
            int currentIndex = index;
            String options = getChoiceOptionsText(skill, level, data);
            context.getSource().sendSuccess(() -> Component.literal(
                "" + currentIndex + ") " + skill.getName() + " Lv." + level + " -> " + options
            ), false);
            index++;
        }
        return 1;
    }

    private static int choosePendingProfession(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        ProfessionType profession = parseProfession(context);
        if (profession == null) {
            sendFailure(context.getSource(), "未知职业名称。");
            return 0;
        }

        boolean success = PlayerStardewDataAPI.choosePendingProfession(player, profession);
        if (!success) {
            sendFailure(context.getSource(), "职业选择失败：请先查看 /stardew player profession pending 的队列与可选项。");
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("已选择职业：" + profession.getName()), true);
        return 1;
    }

    private static int grantProfession(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        ProfessionType profession = parseProfession(context);
        if (profession == null) {
            sendFailure(context.getSource(), "未知职业名称。");
            return 0;
        }

        boolean changed = PlayerStardewDataAPI.addProfession(player, profession);
        if (!changed) {
            sendFailure(context.getSource(), "玩家已拥有该职业。");
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("已授予职业：" + profession.getName()), true);
        return 1;
    }

    private static int revokeProfession(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        ProfessionType profession = parseProfession(context);
        if (profession == null) {
            sendFailure(context.getSource(), "未知职业名称。");
            return 0;
        }

        boolean changed = PlayerStardewDataAPI.removeProfession(player, profession);
        if (!changed) {
            sendFailure(context.getSource(), "玩家未拥有该职业。");
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("已移除职业：" + profession.getName()), true);
        return 1;
    }

    private static int clearProfessions(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        boolean changed = PlayerStardewDataAPI.clearProfessions(player);
        if (!changed) {
            sendFailure(context.getSource(), "当前没有可清空的职业。");
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("已清空职业，并按当前技能等级重建待选队列。"), true);
        return 1;
    }

    private static int repairProfessionChoices(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerStardewDataAPI.repairMissingProfessionChoices(player);
        context.getSource().sendSuccess(() -> Component.literal("已按当前等级修复遗漏的职业待选项。"), false);
        return 1;
    }

    @SuppressWarnings("null")
    private static void sendFailure(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal(message));
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
            context.getSource().sendSuccess(() -> Component.literal(
                "经验已增加并达到升级阈值，等级将在夜间结算时生效。当前已生效等级: " + newLevel), true);
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
