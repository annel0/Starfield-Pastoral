# process_starfruit.mcfunction - CMD: 3204,3205,3206
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3204]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3204] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3205]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3205] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3206]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3206] 1
data modify storage stardew:keg processing set value {output_cmd:112,type:7,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
