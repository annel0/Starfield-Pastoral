# data/stardew/functions/fishing/lose_fight.mcfunction
# 执行者: 玩家

# 1. 清理状态
tag @s remove is_fighting_fish
scoreboard players set @s sd_fishing_tick 0
scoreboard players set @s sd_bite_time 0
scoreboard players set @s sd_fish_type 0
scoreboard players set @s sd_fish_shake 0
scoreboard players set @s sd_fish_phase 0
scoreboard players set @s sd_fish_pull_time 0
# [修复] 重置Shift检测状态
scoreboard players set @s sd_sneak_last 0
scoreboard players set @s sd_is_sneaking 0
scoreboard players set @s sd_fish_ready 0
scoreboard players set @s sd_bite_window 0

# 2. 隐藏bossbar
bossbar set stardew:fishing visible false

# 3. 失败音效 - 使用多层音效营造失败感
playsound minecraft:entity.villager.no player @s ~ ~ ~ 1.0 0.8
playsound minecraft:block.glass.break player @s ~ ~ ~ 0.7 0.5
playsound minecraft:entity.item.break player @s ~ ~ ~ 0.8 1.0

# 4. 提示
title @s title {"text":"线断了...","color":"dark_red","bold":true}
title @s subtitle {"text":"鱼跑掉了！","color":"red"}

# 5. 销毁鱼钩
execute as @e[type=fishing_bobber,distance=..32,limit=1] run kill @s