# data/stardew/functions/tools/scythe.mcfunction
# [执行者: 玩家]

# 0. 检查冷却时间（镰刀是右键使用，所以粒子要显示在玩家位置）
execute if score @s sd_scythe_cd matches 1.. run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.5 0.5
execute if score @s sd_scythe_cd matches 1.. at @s run particle minecraft:angry_villager ~ ~1 ~ 0.1 0.1 0.1 0 1 force @a
execute if score @s sd_scythe_cd matches 1.. run return 0

# 0.5 能量检查（最低需要1点能量）
execute if score @s sd_energy matches ..0 run function stardew:energy/warn_depleted
execute if score @s sd_energy matches ..0 run return 0

# 0.6 初始化收割计数器（用于判断是否成功收割）
scoreboard players set @s sd_temp 0
scoreboard players set @s sd_grass_harvested 0

playsound minecraft:entity.player.attack.sweep player @s ~ ~ ~ 1 1

# 华丽的收割特效（使用冷却结束的特效）
execute at @s run particle minecraft:sweep_attack ~ ~1 ~ 0.5 0.3 0.5 0.1 8 force @a
execute at @s run particle minecraft:crit ~ ~1 ~ 0.8 0.3 0.8 0.2 15 force @a

# 1. 钻石镰刀 (CMD 104) - 7x7 (半径 4.5)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":104}}}] at @s run execute as @e[type=marker,tag=sd_crop,distance=..4.5] at @s run function stardew:farming/harvest_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":104}}}] at @s run execute as @e[type=interaction,tag=weed_hitbox,distance=..4.5] at @s run function stardew:weeds/break_weed
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":104}}}] at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..4.5] at @s run function stardew:grass/break_grass_with_scythe

# 2. 金镰刀 (CMD 103) - 5x5 (半径 3.5)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":103}}}] at @s run execute as @e[type=marker,tag=sd_crop,distance=..3.5] at @s run function stardew:farming/harvest_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":103}}}] at @s run execute as @e[type=interaction,tag=weed_hitbox,distance=..3.5] at @s run function stardew:weeds/break_weed
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":103}}}] at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..3.5] at @s run function stardew:grass/break_grass_with_scythe

# 3. 铁镰刀 (CMD 102) - 3x3 (半径 2.5)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":102}}}] at @s run execute as @e[type=marker,tag=sd_crop,distance=..2.5] at @s run function stardew:farming/harvest_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":102}}}] at @s run execute as @e[type=interaction,tag=weed_hitbox,distance=..2.5] at @s run function stardew:weeds/break_weed
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":102}}}] at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..2.5] at @s run function stardew:grass/break_grass_with_scythe

# 4. 铜镰刀 (CMD 101) / 默认 - 1x1 (半径 1.5)
# 逻辑：如果是铜镰刀(101) 或者 没有任何已知CMD的胡萝卜钓竿
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":101}}}] at @s run execute as @e[type=marker,tag=sd_crop,distance=..1.5] at @s run function stardew:farming/harvest_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":101}}}] at @s run execute as @e[type=interaction,tag=weed_hitbox,distance=..1.5] at @s run function stardew:weeds/break_weed
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":101}}}] at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..1.5] at @s run function stardew:grass/break_grass_with_scythe

# 默认处理 (排除法，针对无CMD的情况)
execute unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":101}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":102}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":103}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":104}}}] at @s run execute as @e[type=marker,tag=sd_crop,distance=..1.5] at @s run function stardew:farming/harvest_router
execute unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":101}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":102}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":103}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":104}}}] at @s run execute as @e[type=interaction,tag=weed_hitbox,distance=..1.5] at @s run function stardew:weeds/break_weed
execute unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":101}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":102}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":103}}}] unless entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":104}}}] at @s run execute as @e[type=interaction,tag=grass_hitbox,distance=..1.5] at @s run function stardew:grass/break_grass_with_scythe

# 4.5 能量消耗：如果成功收割了作物（sd_temp > 0），消耗1点能量
execute if score @s sd_temp matches 1.. run scoreboard players set #energy_cost sd_temp 1
execute if score @s sd_temp matches 1.. run function stardew:energy/consume

# 4.6 计算干草收获（只有收割了草时才计算）

execute if score @s sd_grass_harvested matches 1.. run function stardew:grass/calculate_hay_from_harvest

# 4.7 草收获能量消耗：如果收割了草，消耗1点能量
execute if score @s sd_grass_harvested matches 1.. run scoreboard players set #energy_cost sd_temp 1
execute if score @s sd_grass_harvested matches 1.. run function stardew:energy/consume

# 5. 重置草收割计数器
scoreboard players set @s sd_grass_harvested 0

# 6. 设置冷却时间（1秒 = 20 ticks）
scoreboard players set @s sd_scythe_cd 20
bossbar set stardew:scythe_cooldown max 20
playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.3 0.5