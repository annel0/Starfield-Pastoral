# process_wheat.mcfunction - CMD: 1104,1105,1106
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1104]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1104] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1105]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1105] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1106]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1106] 1
data modify storage stardew:keg processing set value {output_cmd:109,type:11,time:2400}
function stardew:utility/keg/start_process with storage stardew:keg processing
