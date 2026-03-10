# data/stardew/functions/fishing/miss_bite.mcfunction
# [执行者: 玩家]

# 1. 提示
playsound minecraft:entity.item.break player @s ~ ~ ~ 1 0.8
title @s times 0 20 10
title @s subtitle {"text":"反应太慢了...","color":"gray"}
title @s title {"text":"❌ 鱼跑了","color":"red","bold":true}

# 2. 清理状态
scoreboard players set @s sd_fish_ready 0
scoreboard players set @s sd_bite_window 0
scoreboard players set @s sd_fishing_tick 0
scoreboard players set @s sd_bite_time 0
scoreboard players set @s sd_fish_type 0
# [修复] 重置Shift检测状态
scoreboard players set @s sd_sneak_last 0
scoreboard players set @s sd_is_sneaking 0

# 3. 强制收杆
execute as @e[type=fishing_bobber,distance=..32,limit=1] run kill @s