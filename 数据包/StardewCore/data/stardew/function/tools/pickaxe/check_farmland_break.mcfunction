# data/stardew/function/tools/pickaxe/check_farmland_break.mcfunction
# 镐子右键破坏耕地

# 检查冷却时间
execute if score @s sd_pickaxe_cd matches 1.. run return 0

# 清除可能的标记
tag @s remove sd_hit_farmland

# 进行射线检测
function stardew:tools/pickaxe/pickaxe_farmland_raycast

# 只有真的击中了耕地才设置冷却
execute if entity @s[tag=sd_hit_farmland] store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 201 run scoreboard players set @s sd_pickaxe_cd 10
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 201 run bossbar set stardew:pickaxe_cooldown max 10
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 202 run scoreboard players set @s sd_pickaxe_cd 7
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 202 run bossbar set stardew:pickaxe_cooldown max 7
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 203 run scoreboard players set @s sd_pickaxe_cd 5
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 203 run bossbar set stardew:pickaxe_cooldown max 5
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 204 run scoreboard players set @s sd_pickaxe_cd 2
execute if entity @s[tag=sd_hit_farmland] if score @s sd_temp matches 204 run bossbar set stardew:pickaxe_cooldown max 2

# 清除标记
tag @s remove sd_hit_farmland
