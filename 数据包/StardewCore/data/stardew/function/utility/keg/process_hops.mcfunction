# process_hops.mcfunction - CMD: 3004,3005,3006
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3004]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3004] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3005]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3005] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3006]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3006] 1
data modify storage stardew:keg processing set value {output_cmd:110,type:12,time:2400}
function stardew:utility/keg/start_process with storage stardew:keg processing
