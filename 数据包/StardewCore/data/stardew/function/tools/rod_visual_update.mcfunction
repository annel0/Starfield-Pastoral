# data/stardew/functions/tools/rod_visual_update.mcfunction
# [执行者: 玩家]
# 作用：根据鱼竿 NBT，动态更新其 Lore 和名称

# 0. 检查：必须手持的是我们的鱼竿 (CMD 401-404)
execute store result score @s sd_const run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute unless score @s sd_const matches 401..404 run say ""

# 1. 初始化 NBT 数据
scoreboard players set @s sd_const 0
execute store result score @s sd_const run data get entity @s SelectedItem.components."minecraft:custom_data".min_level
scoreboard players set @s sd_config 0
execute store result score @s sd_config run data get entity @s SelectedItem.components."minecraft:custom_data".fish_power

# 2. 基础 Lore (Lv要求 + 钓力 + 分隔线)
data modify entity @s SelectedItem.components."minecraft:lore" set value ['{"text":"📊 🎣 钓鱼等级要求: ","color":"white","italic":false,"extra":[{"text":"","score":{"name":"@s","objective":"sd_const"},"color":"green","bold":true}]}','{"text":"💪 基础钓力: ","color":"white","italic":false,"extra":[{"text":"","score":{"name":"@s","objective":"sd_config"},"color":"aqua","bold":true}]}','{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"无","color":"gray","bold":false}]}','{"text":""}']

# 3. 检查渔具状态 (tackle_id)
# 默认状态
data modify storage stardew:ui TackleStatus set value '{"text":"[渔具槽] ","color":"gray","italic":false,"extra":[{"text":"未装备","color":"red"}]}'

# 先将 tackle_id 读入分数
scoreboard players set @s sd_temp 0
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".tackle_id

# 如果装备了声呐浮标 (5001)
execute if score @s sd_temp matches 5001 run data modify storage stardew:ui TackleStatus set value '{"text":"[渔具槽] ","color":"gray","italic":false,"extra":[{"text":"声呐浮标 (Lv.6)","color":"blue"}]}'

# 如果装备了优质浮标 (5002)
execute if score @s sd_temp matches 5002 run data modify storage stardew:ui TackleStatus set value '{"text":"[渔具槽] ","color":"gray","italic":false,"extra":[{"text":"优质浮标 (Lv.7)","color":"yellow"}]}'

# 4. 插入渔具 Lore
data modify entity @s SelectedItem.components."minecraft:lore" insert 2 from storage stardew:ui TackleStatus

# 5. [DEBUG] 插入 NBT 原始值 (只有在调试模式开启时)
execute if entity @s[tag=sd_debug_mode] run data modify entity @s SelectedItem.components."minecraft:lore" append value '{"text":"| ----- DEBUG ----- |","color":"dark_gray"}'
execute if entity @s[tag=sd_debug_mode] run data modify entity @s SelectedItem.components."minecraft:lore" append value '{"text":"TACKLE_CMD: ","color":"dark_gray","extra":[{"nbt":"SelectedItem.components.\\"minecraft:custom_data\\".tackle_id","entity":"@s","color":"gray"}]}'

# 6. 清理临时分数
scoreboard players set @s sd_const 0
scoreboard players set @s sd_config 0
scoreboard players set @s sd_temp 0