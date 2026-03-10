# stardew:weeds/on_weed_attack.mcfunction
# 玩家攻击杂草时调用
# 执行者: 玩家 (@s)
# 验证工具类型，只有剑/斧/镐可以破坏杂草

# 标记当前正在处理的杂草
tag @e[type=interaction,tag=weed_hitbox,distance=..6,limit=1,sort=nearest] add sd_current_weed_target

# 检测原版工具
# 剑类
execute if items entity @s weapon.mainhand #minecraft:swords run execute as @e[tag=sd_current_weed_target] at @s run function stardew:weeds/break_weed
execute if items entity @s weapon.mainhand #minecraft:swords run tag @e[tag=sd_current_weed_target] remove sd_current_weed_target
execute if items entity @s weapon.mainhand #minecraft:swords run return 1

# 斧类
execute if items entity @s weapon.mainhand #minecraft:axes run execute as @e[tag=sd_current_weed_target] at @s run function stardew:weeds/break_weed
execute if items entity @s weapon.mainhand #minecraft:axes run tag @e[tag=sd_current_weed_target] remove sd_current_weed_target
execute if items entity @s weapon.mainhand #minecraft:axes run return 1

# 镐类
execute if items entity @s weapon.mainhand #minecraft:pickaxes run execute as @e[tag=sd_current_weed_target] at @s run function stardew:weeds/break_weed
execute if items entity @s weapon.mainhand #minecraft:pickaxes run tag @e[tag=sd_current_weed_target] remove sd_current_weed_target
execute if items entity @s weapon.mainhand #minecraft:pickaxes run return 1

# 检测自定义工具（通过custom_data.tool_type）
# 自定义镐子 (CMD 201-204)
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute if score @s sd_temp matches 201..204 run execute as @e[tag=sd_current_weed_target] at @s run function stardew:weeds/break_weed
execute if score @s sd_temp matches 201..204 run tag @e[tag=sd_current_weed_target] remove sd_current_weed_target
execute if score @s sd_temp matches 201..204 run return 1

# 自定义斧子 (CMD 401-404)
execute if score @s sd_temp matches 401..404 run execute as @e[tag=sd_current_weed_target] at @s run function stardew:weeds/break_weed
execute if score @s sd_temp matches 401..404 run tag @e[tag=sd_current_weed_target] remove sd_current_weed_target
execute if score @s sd_temp matches 401..404 run return 1

# 清除标记（如果不是有效工具）
tag @e[tag=sd_current_weed_target] remove sd_current_weed_target
