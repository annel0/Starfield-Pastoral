# data/stardew/functions/fishing/rod_action.mcfunction
# [执行者: 玩家]

# 0. 检查是否为我们的鱼竿
tag @s remove is_stardew_rod
execute if entity @s[nbt={SelectedItem:{id:"minecraft:fishing_rod",components:{"minecraft:custom_model_data":401}}}] run tag @s add is_stardew_rod
execute if entity @s[nbt={SelectedItem:{id:"minecraft:fishing_rod",components:{"minecraft:custom_model_data":402}}}] run tag @s add is_stardew_rod
execute if entity @s[nbt={SelectedItem:{id:"minecraft:fishing_rod",components:{"minecraft:custom_model_data":403}}}] run tag @s add is_stardew_rod
execute if entity @s[nbt={SelectedItem:{id:"minecraft:fishing_rod",components:{"minecraft:custom_model_data":404}}}] run tag @s add is_stardew_rod

# 1. 如果不是我们的鱼竿，终止函数
execute unless entity @s[tag=is_stardew_rod] run return 0

# 2. 如果当前正在战斗，就不要拦截右键 (让原版逻辑收杆)
execute if entity @s[tag=is_fighting_fish] run return 0

# 3. 检查玩家等级
execute store result score @s sd_const run data get entity @s SelectedItem.components."minecraft:custom_data".min_level

# [修复 Bug 1 & 2] 等级不足：提示，强制收杆，并退出
execute if score @s sd_fishing_lvl < @s sd_const run tellraw @s {"text":"[钓鱼] 你的等级不足以使用这个鱼竿 (需要 Lv.","color":"red","extra":[{"score":{"name":"@s","objective":"sd_const"},"color":"yellow"},{"text":")"}]}
execute if score @s sd_fishing_lvl < @s sd_const at @s run kill @e[type=fishing_bobber,distance=..5,sort=nearest,limit=1]
execute if score @s sd_fishing_lvl < @s sd_const run tag @s add sd_level_failed
execute if score @s sd_fishing_lvl < @s sd_const run schedule function stardew:fishing/kill_illegal_bobber 2t
execute if score @s sd_fishing_lvl < @s sd_const run return 0

# 4. 抛竿时：初始化 sd_age
schedule function stardew:fishing/init_bobber_age 1t