# data/stardew/function/utility/furnace/collect_product.mcfunction
# 收取熔炉产物 - 玩家右键已完成的熔炉
# 执行者: 玩家 (@s)
# 前提: 附近有一个标记为 sd_interacting_furnace 的熔炉

# 1. 找到被标记的熔炉
execute as @e[type=interaction,tag=sd_interacting_furnace,distance=..5,limit=1] run tag @s add sd_collecting_furnace
execute as @e[type=item_display,tag=sd_furnace_visual,distance=..5,limit=1,sort=nearest] run tag @s add sd_collecting_visual

# 2. 根据类型给予产物（给玩家）
execute if entity @e[tag=sd_collecting_furnace,scores={sd_furnace_type=1}] run loot give @s loot stardew:items/resource/copper_bar
execute if entity @e[tag=sd_collecting_furnace,scores={sd_furnace_type=2}] run loot give @s loot stardew:items/resource/iron_bar
execute if entity @e[tag=sd_collecting_furnace,scores={sd_furnace_type=3}] run loot give @s loot stardew:items/resource/gold_bar
execute if entity @e[tag=sd_collecting_furnace,scores={sd_furnace_type=4}] run loot give @s loot stardew:items/resource/diamond_bar
execute if entity @e[tag=sd_collecting_furnace,scores={sd_furnace_type=5}] run loot give @s loot stardew:items/resource/refined_quartz

# 3. 重置熔炉状态
scoreboard players set @e[tag=sd_collecting_furnace] sd_furnace_state 0
scoreboard players set @e[tag=sd_collecting_furnace] sd_furnace_type 0
scoreboard players set @e[tag=sd_collecting_furnace] sd_furnace_timer 0
scoreboard players set @e[tag=sd_collecting_furnace] sd_furnace_max_time 0
scoreboard players reset @e[tag=sd_collecting_furnace] sd_anim_tick
scoreboard players reset @e[tag=sd_collecting_furnace] sd_anim_phase

# 4. 改变视觉实体回初始状态 (CMD 101)，保持旋转
execute as @e[tag=sd_collecting_visual] run data merge entity @s {item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":101}}}
execute as @e[tag=sd_collecting_visual] run function stardew:utility/apply_rotation

# 5. 获取视觉实体的ID，删除有相同ID的产物和文本
execute as @e[tag=sd_collecting_visual] run scoreboard players operation #current_id sd_furnace_id = @s sd_furnace_id
execute as @e[type=item_display,tag=sd_furnace_product] if score @s sd_furnace_id = #current_id sd_furnace_id run kill @s
execute as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #current_id sd_furnace_id run kill @s

# 6. 删除光源方块（基于视觉实体位置，向上1格对齐）
execute as @e[tag=sd_collecting_visual] at @s align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:air

# 7. 播放音效
execute as @e[tag=sd_collecting_furnace] at @s run playsound minecraft:entity.item.pickup player @a ~ ~ ~ 1 1

# 8. 清除标记
tag @e[tag=sd_collecting_furnace] remove sd_collecting_furnace
tag @e[tag=sd_collecting_visual] remove sd_collecting_visual
