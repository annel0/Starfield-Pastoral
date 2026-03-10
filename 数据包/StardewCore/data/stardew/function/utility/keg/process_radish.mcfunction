# process_radish.mcfunction - CMD: 2304,2305,2306
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2304]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2304] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2305]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2305] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2306]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2306] 1
data modify storage stardew:keg processing set value {output_cmd:126,type:25,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
