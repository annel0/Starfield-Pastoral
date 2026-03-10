# 格挡成功减伤

# 获取格挡等级（从主手武器）
execute store result score #block_level sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_block_level
execute unless score #block_level sd_temp matches 1.. run scoreboard players set #block_level sd_temp 1

# 保存原始伤害用于显示
scoreboard players operation #original_damage sd_temp = #damage_taken sd_temp

# 根据格挡等级减伤
# 等级 I: 50%减伤 (伤害 /= 2)
# 等级 II: 60%减伤 (伤害 *= 0.4)
# 等级 III: 70%减伤 (伤害 *= 0.3)
execute if score #block_level sd_temp matches 1 run scoreboard players operation #damage_taken sd_temp /= #2 sd_const
execute if score #block_level sd_temp matches 2 run scoreboard players operation #damage_taken sd_temp *= #2 sd_const
execute if score #block_level sd_temp matches 2 run scoreboard players operation #damage_taken sd_temp /= #5 sd_const
execute if score #block_level sd_temp matches 3.. run scoreboard players operation #damage_taken sd_temp *= #3 sd_const
execute if score #block_level sd_temp matches 3.. run scoreboard players operation #damage_taken sd_temp /= #10 sd_const

# 格挡成功反馈
particle minecraft:crit ~ ~1 ~ 0.5 0.5 0.5 0.1 30
particle minecraft:cloud ~ ~1 ~ 0.3 0.3 0.3 0.05 10
playsound minecraft:item.shield.block player @a ~ ~ ~ 1 1.5

# 根据等级显示不同的提示
execute if score #block_level sd_temp matches 1 run title @s subtitle [{"text":"🛡 格挡! ","color":"green","bold":true},{"text":"-50% 伤害 (","color":"gray"},{"score":{"name":"#original_damage","objective":"sd_temp"},"color":"white"},{"text":" → ","color":"gray"},{"score":{"name":"#damage_taken","objective":"sd_temp"},"color":"yellow"},{"text":")","color":"gray"}]
execute if score #block_level sd_temp matches 2 run title @s subtitle [{"text":"🛡 格挡! ","color":"green","bold":true},{"text":"-60% 伤害 (","color":"gray"},{"score":{"name":"#original_damage","objective":"sd_temp"},"color":"white"},{"text":" → ","color":"gray"},{"score":{"name":"#damage_taken","objective":"sd_temp"},"color":"yellow"},{"text":")","color":"gray"}]
execute if score #block_level sd_temp matches 3.. run title @s subtitle [{"text":"🛡 格挡! ","color":"green","bold":true},{"text":"-70% 伤害 (","color":"gray"},{"score":{"name":"#original_damage","objective":"sd_temp"},"color":"white"},{"text":" → ","color":"gray"},{"score":{"name":"#damage_taken","objective":"sd_temp"},"color":"yellow"},{"text":")","color":"gray"}]
title @s times 0 40 10
title @s title {"text":""}
