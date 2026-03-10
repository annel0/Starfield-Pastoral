# stardew:weeds/check_scythe_interaction.mcfunction
# 检测右键杂草的玩家是否拿着镰刀
# 执行者: 玩家 (@s)

# 检测镰刀 (CMD 101-104)
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 如果拿着镰刀，触发镰刀收割逻辑
execute if score @s sd_temp matches 101..104 run function stardew:tools/scythe
