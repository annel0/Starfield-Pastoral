# stardew:grass/on_grass_attack.mcfunction
# 玩家攻击草时调用
# 执行者: 玩家 (@s)
# 验证工具类型，只有剑/斧/镐可以破坏草

# 标记当前正在处理的草
tag @e[type=interaction,tag=grass_hitbox,distance=..6,limit=1,sort=nearest] add sd_current_grass_target

# 检测原版工具
# 剑类
execute if items entity @s weapon.mainhand #minecraft:swords run execute as @e[tag=sd_current_grass_target] at @s run function stardew:grass/break_grass
execute if items entity @s weapon.mainhand #minecraft:swords run tag @e[tag=sd_current_grass_target] remove sd_current_grass_target
execute if items entity @s weapon.mainhand #minecraft:swords run return 1

# 斧类
execute if items entity @s weapon.mainhand #minecraft:axes run execute as @e[tag=sd_current_grass_target] at @s run function stardew:grass/break_grass
execute if items entity @s weapon.mainhand #minecraft:axes run tag @e[tag=sd_current_grass_target] remove sd_current_grass_target
execute if items entity @s weapon.mainhand #minecraft:axes run return 1

# 镐类
execute if items entity @s weapon.mainhand #minecraft:pickaxes run execute as @e[tag=sd_current_grass_target] at @s run function stardew:grass/break_grass
execute if items entity @s weapon.mainhand #minecraft:pickaxes run tag @e[tag=sd_current_grass_target] remove sd_current_grass_target
execute if items entity @s weapon.mainhand #minecraft:pickaxes run return 1

# 检测自定义工具 (镰刀等)
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 镰刀 (CMD 101-104) - 也可以单个收割
execute if score @s sd_temp matches 101..104 run execute as @e[tag=sd_current_grass_target] at @s run function stardew:grass/break_grass_with_scythe
execute if score @s sd_temp matches 101..104 run tag @e[tag=sd_current_grass_target] remove sd_current_grass_target
execute if score @s sd_temp matches 101..104 run return 1

# 清除标记（如果没有匹配的工具）
tag @e[tag=sd_current_grass_target] remove sd_current_grass_target