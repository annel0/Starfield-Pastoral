# data/stardew/function/utility/sprinkler/place_sprinkler.mcfunction
# 在射线击中的位置放置洒水器实用设施
# 执行位置：射线击中的方块表面
# CMD映射: 物品3005-3007 → 视觉104-106

# 0. 检测物品CMD并设置类型
execute store result score #sprinkler_cmd sd_temp run data get entity @a[tag=sd_placing_sprinkler,limit=1] SelectedItem.components."minecraft:custom_model_data"

# CMD 3005 = 洒水器(type 1, 上下左右4格)
execute if score #sprinkler_cmd sd_temp matches 3005 run scoreboard players set #sprinkler_type sd_temp 1
execute if score #sprinkler_cmd sd_temp matches 3005 run scoreboard players set #visual_cmd sd_temp 104

# CMD 3006 = 优质洒水器(type 2, 周围8格)
execute if score #sprinkler_cmd sd_temp matches 3006 run scoreboard players set #sprinkler_type sd_temp 2
execute if score #sprinkler_cmd sd_temp matches 3006 run scoreboard players set #visual_cmd sd_temp 105

# CMD 3007 = 钻石洒水器(type 3, 5x5共24格)
execute if score #sprinkler_cmd sd_temp matches 3007 run scoreboard players set #sprinkler_type sd_temp 3
execute if score #sprinkler_cmd sd_temp matches 3007 run scoreboard players set #visual_cmd sd_temp 106

# 1. 检查是否已有设施(防止重复放置)
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_utility,tag=sd_sprinkler,distance=..0.5] as @a[tag=sd_placing_sprinkler,limit=1] run tellraw @s {"text":"这里已经有设施了","color":"red"}
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_utility,tag=sd_sprinkler,distance=..0.5] as @a[tag=sd_placing_sprinkler,limit=1] run tag @s remove sd_placing_sprinkler
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_utility,tag=sd_sprinkler,distance=..0.5] run return 1

# 2. 根据CMD召唤视觉实体 (oak_log with CMD 104-106, 位置上移0.625格, 缩放1.0倍)
execute store result storage stardew:temp sprinkler.cmd int 1 run scoreboard players get #visual_cmd sd_temp
execute align xyz positioned ~0.5 ~1.625 ~0.5 run function stardew:utility/sprinkler/summon_visual_macro with storage stardew:temp sprinkler

# 3. 放置屏障方块 (真实碰撞箱)
execute align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:barrier

# 4. 召唤交互实体 (稍微大一点以便右键)
execute align xyz positioned ~0.5 ~1 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["sd_utility","sd_sprinkler","sd_sprinkler_interaction","init_utility"],width:1.1f,height:1.1f,response:1b}

# 5. 给交互实体添加自定义数据
execute as @e[tag=init_utility,tag=sd_sprinkler_interaction,distance=..2] run data modify entity @s Tags append value "sd_utility_interaction"

# 6. 初始化计分板数据 (洒水器不需要state/timer,只需要type)
execute as @e[tag=init_utility,tag=sd_sprinkler_interaction,distance=..2] run scoreboard players operation @s sd_sprinkler_type = #sprinkler_type sd_temp
execute as @e[tag=init_utility,tag=sd_sprinkler_interaction,distance=..2] run scoreboard players set @s sd_utility_active 0

# 7. 播放放置音效和粒子效果(洒水器用机械音效和水粒子)
execute as @e[tag=init_utility,distance=..2,limit=1] at @s run playsound minecraft:block.iron_trapdoor.close block @a ~ ~ ~ 1 1.2
execute align xyz positioned ~0.5 ~1.625 ~0.5 run particle minecraft:splash ~ ~ ~ 0.3 0.3 0.3 0 20

# 8. 消耗物品(非创造模式)
execute if entity @e[tag=init_utility,distance=..2] as @a[tag=sd_placing_sprinkler,limit=1,gamemode=!creative,gamemode=!spectator] run item modify entity @s weapon.mainhand stardew:consume_one

# 8.5 初始化视觉实体的高亮系统
execute as @e[tag=init_utility,type=item_display,distance=..2] run function stardew:utility/init_highlight

# 9. 初始化完成
tag @e[tag=init_utility] remove init_utility

# 10. 清除标记
tag @a[tag=sd_placing_sprinkler] remove sd_placing_sprinkler
