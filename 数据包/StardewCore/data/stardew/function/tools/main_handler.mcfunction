# data/stardew/functions/tools/main_handler.mcfunction

# A. Debug
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":9001}}}] at @s run function stardew:tools/debug_wand
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":9002}}}] at @s run function stardew:tools/debug_time_wand
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":9003}}}] at @s run function stardew:tools/debug_fishing_status
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":9004}}}] at @s run function stardew:tools/debug_weather_toggle

# A2. Debug Tree Spawn Tools (CMD 2510-2513)
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":2510}}}] at @s run function stardew:tools/debug_spawn_oak
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":2511}}}] at @s run function stardew:tools/debug_spawn_maple
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":2512}}}] at @s run function stardew:tools/debug_spawn_pine
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":2513}}}] at @s run function stardew:tools/debug_spawn_mahogany

# B. 所有作物种子 (CMD 2100-5400) - 使用统一检测
# 特殊处理：草籽 (CMD 2601) 不需要耕地
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2601}}}] at @s run function stardew:grass/plant_raycast

# 其他种子使用统一检测
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute if score @s sd_temp matches 2100..5400 unless score @s sd_temp matches 2601 at @s run function stardew:farming/generic_raycast

# C. 水壶 (CMD 301-304)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":301}}}] at @s run function stardew:tools/watering_can/main_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":302}}}] at @s run function stardew:tools/watering_can/main_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":303}}}] at @s run function stardew:tools/watering_can/main_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":304}}}] at @s run function stardew:tools/watering_can/main_router

# D. 锄头 (CMD 501-504)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":501}}}] at @s run function stardew:tools/hoe/main_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":502}}}] at @s run function stardew:tools/hoe/main_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":503}}}] at @s run function stardew:tools/hoe/main_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":504}}}] at @s run function stardew:tools/hoe/main_router

# D2. 镐子破坏耕地 (CMD 201-204) - 右键
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":201}}}] at @s run function stardew:tools/pickaxe/check_farmland_break
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":202}}}] at @s run function stardew:tools/pickaxe/check_farmland_break
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":203}}}] at @s run function stardew:tools/pickaxe/check_farmland_break
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":204}}}] at @s run function stardew:tools/pickaxe/check_farmland_break

# E. 镰刀 (CMD 101-104)
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":101}}}] at @s run function stardew:tools/scythe
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":102}}}] at @s run function stardew:tools/scythe
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":103}}}] at @s run function stardew:tools/scythe
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":104}}}] at @s run function stardew:tools/scythe

# F. 树木种子 (CMD 2501-2504)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2501}}}] at @s run function stardew:tree/planting/tree_raycast
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2502}}}] at @s run function stardew:tree/planting/tree_raycast
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2503}}}] at @s run function stardew:tree/planting/tree_raycast
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2504}}}] at @s run function stardew:tree/planting/tree_raycast

# F2. 实用设施 (CMD 3001-3999)
# 熔炉 (CMD 3001)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":3001}}}] at @s run function stardew:utility/furnace/place_raycast
# 小桶 (CMD 3002)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":3002}}}] at @s run function stardew:utility/keg/place_raycast
# 箱子 (CMD 3004)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":3004}}}] at @s run function stardew:utility/chest/place_raycast
# 洒水器(CMD 3005-3007)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":3005}}}] at @s run function stardew:utility/sprinkler/place_raycast
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":3006}}}] at @s run function stardew:utility/sprinkler/place_raycast
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":3007}}}] at @s run function stardew:utility/sprinkler/place_raycast

# G. 宝箱系统 (40510, 40520, 40530)
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":40510}}}] at @s run function stardew:tools/open_treasure_common
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":40520}}}] at @s run function stardew:tools/open_treasure_rare
execute if entity @s[nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_model_data":40530}}}] at @s run function stardew:tools/open_treasure_epic

# =========================================================
# G2. 动物工具 (挤奶桶 8101, 剪刀 8100)
# =========================================================
# 已移至 animal/interact/on_interaction_clicked.mcfunction 统一处理
# 玩家右键interaction实体时会自动检测手持物品并执行对应操作

# =========================================================
# H. Mining Debug 工具 (CMD 9101-9131)
# =========================================================
# 使用统一检测 - 石头、矿石、宝石生成工具
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute if score @s sd_temp matches 9101..9131 at @s run function stardew:mining/spawn_stone

# =========================================================
# I. 肥料系统 (CMD 4001-4010)
# =========================================================
# 检测手持肥料物品并施肥
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_data~{item_type:"fertilizer"}] at @s run function stardew:farming/fertilizer/apply

# =========================================================
# J. 渔具装配/卸载 (CMD 5001-5999)
# =========================================================

# [装备渔具] 主手鱼竿 + 副手渔具 + 潜行右键渔具
execute if items entity @s weapon.mainhand fishing_rod if items entity @s weapon.offhand carrot_on_a_stick run function stardew:tools/tackle_attach_action