# data/stardew/functions/tools/debug_hud.mcfunction
# [执行者: 玩家 (拥有 sd_debug_mode 标签)]

# 1. 拼接显示文本
# 格式：Tick:123 | Max:80 | ID:41020 | Diff:10 | State:Fight

# 鱼钩状态图标
data modify storage stardew:ui DebugText set value [{"text":"🎣 ","color":"aqua"}]
execute unless entity @e[type=fishing_bobber,distance=..32] run data modify storage stardew:ui DebugText append value {"text":"[无钩] ","color":"red"}
execute if entity @e[type=fishing_bobber,distance=..32] run data modify storage stardew:ui DebugText append value {"text":"[有钩] ","color":"green"}

# 计时器 / 预定时间
data modify storage stardew:ui DebugText append value {"text":" Tick:","color":"gray"}
data modify storage stardew:ui DebugText append value {"score":{"name":"@s","objective":"sd_fishing_tick"},"color":"white"}
data modify storage stardew:ui DebugText append value {"text":"/","color":"gray"}
data modify storage stardew:ui DebugText append value {"score":{"name":"@s","objective":"sd_bite_time"},"color":"yellow"}

# 鱼种 / 区域
data modify storage stardew:ui DebugText append value {"text":" | ID:","color":"gray"}
data modify storage stardew:ui DebugText append value {"score":{"name":"@s","objective":"sd_fish_type"},"color":"aqua"}
data modify storage stardew:ui DebugText append value {"text":" | Area:","color":"gray"}
data modify storage stardew:ui DebugText append value {"score":{"name":"@s","objective":"sd_fish_region"},"color":"blue"}

# [新增] 难度显示 (Diff)
data modify storage stardew:ui DebugText append value {"text":" | Diff:","color":"gray"}
data modify storage stardew:ui DebugText append value {"score":{"name":"@s","objective":"sd_final_difficulty"},"color":"light_purple"}

# 状态
data modify storage stardew:ui DebugText append value {"text":" | ","color":"dark_gray"}
execute if score @s sd_fish_ready matches 1 run data modify storage stardew:ui DebugText append value {"text":"咬钩!","color":"red","bold":true}
execute if entity @s[tag=is_fighting_fish] run data modify storage stardew:ui DebugText append value {"text":"战斗中","color":"gold","bold":true}
execute if score @s sd_fish_ready matches 0 unless entity @s[tag=is_fighting_fish] run data modify storage stardew:ui DebugText append value {"text":"等待...","color":"gray"}

# 进度条 (如果在战斗)
execute if entity @s[tag=is_fighting_fish] run data modify storage stardew:ui DebugText append value {"text":" | Progress:","color":"green"}
execute if entity @s[tag=is_fighting_fish] run data modify storage stardew:ui DebugText append value {"score":{"name":"@s","objective":"sd_fish_progress"},"color":"white"}

# 2. 显示
title @s actionbar {"nbt":"DebugText","storage":"stardew:ui","interpret":true}