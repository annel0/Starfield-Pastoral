# 镰刀大范围收割草
# 根据镰刀等级决定收割范围，并尝试添加干草到筒仓

# 检测镰刀类型并设置范围
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 初始化收割计数器
scoreboard players set grass_harvest_count sd_temp 0

# 初始化干草计数器
scoreboard players set hay_gained_this_harvest sd_temp 0

# 根据镰刀类型收割不同范围的草
execute if score @s sd_temp matches 104 at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..4.5] at @s run function stardew:grass/harvest_single_grass
execute if score @s sd_temp matches 103 at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..3.5] at @s run function stardew:grass/harvest_single_grass
execute if score @s sd_temp matches 102 at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..2.5] at @s run function stardew:grass/harvest_single_grass
execute if score @s sd_temp matches 101 at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..1.5] at @s run function stardew:grass/harvest_single_grass

# 播放音效和特效
playsound minecraft:entity.player.attack.sweep player @s ~ ~ ~ 1 1
execute at @s run particle minecraft:sweep_attack ~ ~1 ~ 0.5 0.3 0.5 0.1 8 force @a

# 显示收割结果
execute if score grass_harvest_count sd_temp matches 1.. run tellraw @s [{"text":"收割了 ","color":"green"},{"score":{"name":"grass_harvest_count","objective":"sd_temp"},"color":"yellow"},{"text":" 个草","color":"green"}]

# 显示干草获得情况
execute if score hay_gained_this_harvest sd_temp matches 1.. run tellraw @s [{"text":"获得了 ","color":"green"},{"score":{"name":"hay_gained_this_harvest","objective":"sd_temp"},"color":"yellow"},{"text":" 个干草，筒仓: ","color":"green"},{"score":{"name":"@s","objective":"sd_hay_stored"},"color":"yellow"},{"text":"/","color":"gray"},{"score":{"name":"@s","objective":"sd_hay_capacity"},"color":"yellow"}]

# 设置冷却
scoreboard players set @s sd_scythe_cd 20