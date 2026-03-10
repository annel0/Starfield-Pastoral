# 剑类武器 - 格挡

# 检查是否在冷却中
execute if score @s sd_block_cooldown matches 1.. run return 0

# 获取格挡等级
execute store result score #block_level sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_block_level
execute unless score #block_level sd_temp matches 1.. run scoreboard players set #block_level sd_temp 1

# 格挡持续时间（3秒 = 60 ticks）
scoreboard players set @s sd_blocking 60
tag @s add sd_blocking

# 格挡冷却（从武器读取，默认120 ticks = 6秒）
execute store result score @s sd_block_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_block_cooldown matches 1.. run scoreboard players set @s sd_block_cooldown 120
execute store result score #cooldown_max sd_temp run scoreboard players get @s sd_block_cooldown
execute store result storage minecraft:stardew temp.cooldown int 1 run scoreboard players get #cooldown_max sd_temp
function stardew:combat/weapon/set_block_cooldown_max with storage minecraft:stardew temp

# 视觉效果
particle minecraft:enchanted_hit ~ ~1 ~ 0.5 0.5 0.5 0.1 20
particle minecraft:crit ~ ~1 ~ 0.3 0.5 0.3 0.1 15

# 音效
playsound minecraft:item.shield.block player @a ~ ~ ~ 1 1.2

# 提示（根据等级显示）
execute if score #block_level sd_temp matches 1 run title @s subtitle [{"text":"🛡 格挡 I","color":"aqua","bold":true},{"text":" - 50%减伤","color":"gray"}]
execute if score #block_level sd_temp matches 2 run title @s subtitle [{"text":"🛡 格挡 II","color":"aqua","bold":true},{"text":" - 60%减伤","color":"gray"}]
execute if score #block_level sd_temp matches 3.. run title @s subtitle [{"text":"🛡 格挡 III","color":"aqua","bold":true},{"text":" - 70%减伤","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}
