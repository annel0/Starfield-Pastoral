# process_melon.mcfunction - CMD: 3104,3105,3106
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3104]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3104] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3105]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3105] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3106]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3106] 1
data modify storage stardew:keg processing set value {output_cmd:104,type:9,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
