# process_pumpkin.mcfunction - CMD: 4204,4205,4206
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4204]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4204] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4205]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4205] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4206]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4206] 1
data modify storage stardew:keg processing set value {output_cmd:120,type:10,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
