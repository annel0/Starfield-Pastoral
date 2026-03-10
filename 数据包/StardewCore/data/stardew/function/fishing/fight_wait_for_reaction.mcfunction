# data/stardew/functions/fishing/fight_wait_for_reaction.mcfunction
# 执行者: 玩家
# 状态：鱼已经咬钩，等待玩家按住 Shift 决定是否进入战斗

# 1. 咬钩反应时间 -1 tick
scoreboard players remove @s sd_bite_react 1

# 2. 玩家按住 Shift → 进入正式战斗
execute if score @s sd_is_sneaking matches 1 run function stardew:fishing/fight_enter_battle
execute if entity @s[tag=is_fighting_fish] run return 0

# 3. 计时耗尽 → 鱼跑掉
execute if score @s sd_bite_react matches ..0 run function stardew:fishing/fight_bite_missed
