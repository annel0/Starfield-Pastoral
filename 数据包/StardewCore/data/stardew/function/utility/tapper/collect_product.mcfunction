# data/stardew/function/utility/tapper/collect_product.mcfunction
# 收取提取器产物 - 玩家右键已完成的提取器
# 执行者: 树interaction实体 (@s)

# 1. 找到对应的视觉实体
execute as @e[type=item_display,tag=sd_tapper_visual,distance=..2,limit=1] run tag @s add sd_collecting_tapper

# 2. 根据类型给予产物（给玩家）
execute if score @s sd_tapper_type matches 1 run loot give @a[tag=sd_collecting_tapper_player,limit=1] loot stardew:items/resource/oak_resin
execute if score @s sd_tapper_type matches 2 run loot give @a[tag=sd_collecting_tapper_player,limit=1] loot stardew:items/resource/maple_syrup
execute if score @s sd_tapper_type matches 3 run loot give @a[tag=sd_collecting_tapper_player,limit=1] loot stardew:items/resource/pine_tar
execute if score @s sd_tapper_type matches 4 run loot give @a[tag=sd_collecting_tapper_player,limit=1] loot stardew:items/resource/sap

# 3. 重新开始新一轮提取（保持提取器在树上）
scoreboard players set @s sd_tapper_state 1
scoreboard players set @s sd_tapper_timer 0
scoreboard players operation @s sd_tapper_last_time = Global sd_time
scoreboard players set @s sd_utility_active 1

# 4. 获取视觉实体的ID
execute as @e[tag=sd_collecting_tapper] run scoreboard players operation #current_id sd_tapper_id = @s sd_tapper_id

# 5. 更新文本显示为新的计时
execute as @e[type=text_display,tag=sd_tapper_time] if score @s sd_tapper_id = #current_id sd_tapper_id run data merge entity @s {text:'{"text":"计算中...","color":"yellow"}',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]}}

# 6. 播放音效
execute at @s run playsound minecraft:entity.item.pickup player @a ~ ~ ~ 1 1

# 7. 清除标记
tag @e[tag=sd_collecting_tapper] remove sd_collecting_tapper
tag @a[tag=sd_collecting_tapper_player] remove sd_collecting_tapper_player
