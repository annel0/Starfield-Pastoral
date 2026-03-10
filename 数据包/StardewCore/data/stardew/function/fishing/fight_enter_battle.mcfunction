# data/stardew/functions/fishing/fight_enter_battle.mcfunction
# 执行者: 玩家
# 条件：鱼已咬钩，玩家在反应时间内按下 Shift

# 清除咬钩阶段标记
scoreboard players set @s sd_fish_bite_state 0
execute as @e[type=fishing_bobber,tag=sd_bite_hook,distance=..32,limit=1] run tag @s remove sd_bite_hook


# 进入现有战斗逻辑
function stardew:fishing/start_fight