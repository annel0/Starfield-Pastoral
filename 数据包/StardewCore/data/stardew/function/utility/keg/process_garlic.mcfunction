# process_garlic.mcfunction - CMD: 1404,1405,1406
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1404]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1404] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1405]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1405] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1406]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1406] 1
data modify storage stardew:keg processing set value {output_cmd:123,type:21,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
