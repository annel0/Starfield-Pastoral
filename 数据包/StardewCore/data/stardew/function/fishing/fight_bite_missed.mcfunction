# data/stardew/functions/fishing/fight_bite_missed.mcfunction
# 执行者: 玩家
# 情况：鱼咬钩了，但你在 1 秒内没有按 Shift

# 清理状态（回到完全没在钓鱼的状态）
scoreboard players set @s sd_fish_bite_state 0
scoreboard players set @s sd_fishing_tick 0
scoreboard players set @s sd_bite_time 0
scoreboard players set @s sd_fish_type 0
scoreboard players set @s sd_bite_react 0
execute as @e[type=fishing_bobber,tag=sd_bite_hook,distance=..32,limit=1] run tag @s remove sd_bite_hook


# 提示
title @s title {"text":"鱼跑掉了","color":"dark_red","bold":true}
title @s subtitle {"text":"你没有及时收杆。","color":"red"}

# 自动收杆：杀掉鱼钩
execute as @e[type=fishing_bobber,distance=..32,limit=1] run kill @s
