# process_cauliflower.mcfunction - CMD: 2204,2205,2206
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2204]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2204] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2205]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2205] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2206]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2206] 1
data modify storage stardew:keg processing set value {output_cmd:121,type:17,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
