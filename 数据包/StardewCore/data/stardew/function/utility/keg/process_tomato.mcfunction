# process_tomato.mcfunction - CMD: 1204,1205,1206
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1204]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1204] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1205]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1205] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1206]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1206] 1
data modify storage stardew:keg processing set value {output_cmd:122,type:14,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
