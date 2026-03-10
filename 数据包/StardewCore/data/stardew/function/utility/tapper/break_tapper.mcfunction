# data/stardew/function/utility/tapper/break_tapper.mcfunction
# 拆除提取器 - 当玩家用镐子左键提取器时
# 执行者: 树interaction实体 (@s)

# 1. 检查树是否有提取器
execute unless score @s sd_tapper_state matches 1.. run return 0

# 2. 找到对应的视觉实体并标记
execute as @e[type=item_display,tag=sd_tapper_visual,distance=..2,limit=1] at @s run tag @s add sd_breaking_tapper

# 3. 在视觉实体位置掉落提取器物品
execute as @e[tag=sd_breaking_tapper,type=item_display,limit=1] at @s run loot spawn ~ ~ ~ loot stardew:items/utility/tapper

# 4. 获取视觉实体的ID，删除有相同ID的产物和文本
execute as @e[tag=sd_breaking_tapper,limit=1] run scoreboard players operation #breaking_id sd_tapper_id = @s sd_tapper_id
execute as @e[type=item_display,tag=sd_tapper_product] if score @s sd_tapper_id = #breaking_id sd_tapper_id run kill @s
execute as @e[type=text_display,tag=sd_tapper_time] if score @s sd_tapper_id = #breaking_id sd_tapper_id run kill @s

# 5. 播放破坏音效和粒子效果
execute as @e[tag=sd_breaking_tapper,limit=1] at @s run playsound minecraft:block.wood.break block @a ~ ~ ~ 1 0.9
execute as @e[tag=sd_breaking_tapper,limit=1] at @s run particle minecraft:block{block_state:"minecraft:oak_log"} ~ ~0.5 ~ 0.2 0.2 0.2 0 20

# 6. 删除视觉实体
kill @e[tag=sd_breaking_tapper]

# 7. 重置树的提取器状态
tag @s remove sd_has_tapper
scoreboard players set @s sd_tapper_state 0
scoreboard players set @s sd_tapper_type 0
scoreboard players set @s sd_tapper_timer 0
scoreboard players set @s sd_tapper_max_time 0
scoreboard players set @s sd_utility_active 0
