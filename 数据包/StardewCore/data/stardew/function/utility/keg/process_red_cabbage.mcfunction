# process_red_cabbage.mcfunction - CMD: 2404,2405,2406
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2404]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2404] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2405]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2405] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2406]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2406] 1
data modify storage stardew:keg processing set value {output_cmd:127,type:22,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
