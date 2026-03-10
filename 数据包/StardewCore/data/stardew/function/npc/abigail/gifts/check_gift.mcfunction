# Check gift item
# @s = player

execute unless data entity @s SelectedItem run tellraw @s {"text":"Need item","color":"red"}
execute unless data entity @s SelectedItem run return 0

execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_item run tellraw @s {"text":"Not interested","color":"gray"}
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_item run return 0

execute store result score #gift_cmd stardew.temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"

function stardew:npc/abigail/gifts/determine_level