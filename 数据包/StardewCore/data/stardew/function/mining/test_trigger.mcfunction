# 测试 mining 右键触发
# 使用方法：/function stardew:mining/test_trigger

# 1. 检查计分板是否存在
execute unless score @s sd_right_click matches 0.. run tellraw @s {"text":"❌ sd_right_click 计分板未初始化！","color":"red"}
execute if score @s sd_right_click matches 0.. run tellraw @s [{"text":"✓ sd_right_click = ","color":"green"},{"score":{"name":"@s","objective":"sd_right_click"},"color":"yellow"}]

# 2. 检查手持物品
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute if score @s sd_temp matches 9101..9131 run tellraw @s [{"text":"✓ 手持 mining 工具，CMD = ","color":"green"},{"score":{"name":"@s","objective":"sd_temp"},"color":"yellow"}]
execute unless score @s sd_temp matches 9101..9131 run tellraw @s {"text":"❌ 未手持 mining 工具 (CMD 9101-9131)","color":"red"}

# 3. 模拟右键触发
tellraw @s {"text":"→ 手动触发 spawn_stone...","color":"aqua"}
function stardew:mining/spawn_stone
