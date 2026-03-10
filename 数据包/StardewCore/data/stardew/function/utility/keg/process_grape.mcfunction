# process_grape.mcfunction - CMD: 4004,4005,4006
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4004]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4004] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4005]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4005] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4006]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4006] 1
data modify storage stardew:keg processing set value {output_cmd:106,type:4,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
