# data/stardew/function/farming/xp/animal_care.mcfunction
# 动物照顾经验系统 - 每次照顾动物给予 5 XP
# 执行者: 玩家 (@s)

# 给予 5 XP (官方值)
scoreboard players add @s sd_farming_xp 5

# 视觉反馈 (显示经验获得)
title @s actionbar [{"text":"[农耕] ","color":"green","bold":true},{"text":"+5 XP ","color":"yellow"},{"text":"(照顾动物)","color":"gray"}]

# 音效反馈
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.5 1.5
