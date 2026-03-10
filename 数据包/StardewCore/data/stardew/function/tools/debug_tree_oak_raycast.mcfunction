# data/stardew/function/tools/debug_tree_oak_raycast.mcfunction
# Debug橡树生成射线检测

# 1. 检查是否为合适的地面
execute unless block ~ ~ ~ #stardew:tillable run scoreboard players add @s sd_ray_steps 1
execute unless block ~ ~ ~ #stardew:tillable if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:tools/debug_tree_oak_raycast
execute unless block ~ ~ ~ #stardew:tillable run return 0

execute unless block ~ ~1 ~ minecraft:air run scoreboard players add @s sd_ray_steps 1 
execute unless block ~ ~1 ~ minecraft:air if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:tools/debug_tree_oak_raycast
execute unless block ~ ~1 ~ minecraft:air run return 0

# 2. 防挤检测 - 只在找到合适位置时才进行
execute align xyz positioned ~0.5 ~1.5 ~0.5 if entity @e[tag=sd_tree,distance=..2.0] run tellraw @s {"text":"这里太挤了，树需要更多空间！","color":"red"}
execute align xyz positioned ~0.5 ~1.5 ~0.5 if entity @e[tag=sd_tree,distance=..2.0] run return 1

execute align xyz positioned ~0.5 ~2.5 ~0.5 if entity @e[tag=sd_tree,distance=..0.5] run tellraw @s {"text":"这里已经有树了！","color":"red"}
execute align xyz positioned ~0.5 ~2.5 ~0.5 if entity @e[tag=sd_tree,distance=..0.5] run return 1

# 3. 生成成熟橡树 - 在与种树系统相同的高度生成
execute align xyz positioned ~0.5 ~1.5 ~0.5 run function stardew:tree/spawn_oak
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"成功生成橡树","color":"green"}]
return 1