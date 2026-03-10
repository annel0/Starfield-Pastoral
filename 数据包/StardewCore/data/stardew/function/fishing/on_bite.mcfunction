# data/stardew/functions/fishing/on_bite.mcfunction
# [执行者: 玩家]

# 1. 切换状态
scoreboard players set @s sd_fish_ready 1
scoreboard players set @s sd_bite_window 40
scoreboard players set @s sd_fishing_tick 0

# 2. 视觉反馈
execute as @e[type=fishing_bobber,distance=..32,limit=1,sort=nearest] run tp @s ~ ~-0.4 ~
# 水花粒子
execute at @e[type=fishing_bobber,distance=..32,limit=1,sort=nearest] run particle minecraft:splash ~ ~ ~ 0.3 0.1 0.3 0.1 20
execute at @e[type=fishing_bobber,distance=..32,limit=1,sort=nearest] run particle minecraft:bubble ~ ~ ~ 0.2 0.1 0.2 0.05 10

playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1.0 2.0
playsound minecraft:entity.fishing_bobber.splash player @s ~ ~ ~ 1.0 1.0

# 3. 使用 Title 显示，防止被 Actionbar 覆盖
title @s times 0 10 5
title @s subtitle {"text":"按住 Shift 收杆！","color":"yellow"}
title @s title {"text":"❗ 咬钩 ❗","color":"red","bold":true}