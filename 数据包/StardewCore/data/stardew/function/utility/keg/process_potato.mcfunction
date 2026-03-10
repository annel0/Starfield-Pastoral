# process_potato.mcfunction - CMD: 1604,1605,1606
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1604]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1604] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1605]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1605] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1606]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1606] 1
data modify storage stardew:keg processing set value {output_cmd:125,type:16,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
