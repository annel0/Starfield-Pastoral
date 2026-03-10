# data/stardew/functions/fishing/fight_bite_start.mcfunction
# 执行者: 玩家

# 进入“咬钩反应阶段”
scoreboard players set @s sd_fish_bite_state 1
# 给玩家一点明显提示：有鱼咬钩了！
playsound minecraft:entity.fishing_bobber.splash player @s ~ ~ ~ 0.8 1.0
particle minecraft:splash ~ ~ ~ 0.3 0.1 0.3 0.1 10

# 标记当前鱼钩为“咬钩中”
execute as @e[type=fishing_bobber,distance=..32,sort=nearest,limit=1] run tag @s add sd_bite_hook

# 给玩家大约 1 秒反应时间（20 tick）
# 用单独的计分板记录咬钩反应时间，避免和战斗计时混用
scoreboard players set @s sd_bite_react 20

# 让最近的鱼钩往下沉一点 + 粒子 + 声音
execute as @e[type=fishing_bobber,distance=..32,sort=nearest,limit=1] run tp @s ~ ~-0.2 ~
execute as @e[type=fishing_bobber,distance=..32,sort=nearest,limit=1] at @s run particle minecraft:splash ~ ~ ~ 0.1 0.05 0.1 0.1 15
execute as @e[type=fishing_bobber,distance=..32,sort=nearest,limit=1] at @s run playsound minecraft:entity.fishing_bobber.splash player @s ~ ~ ~ 0.8 1.0

tellraw @s ["",{"text":"[钓鱼] ","color":"aqua"},{"text":"鱼上钩了！按住 ","color":"yellow"},{"keybind":"key.sneak","color":"gold","bold":true},{"text":" 收紧鱼线！","color":"yellow"}]
