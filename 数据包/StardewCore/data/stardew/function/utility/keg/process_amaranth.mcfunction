# process_amaranth.mcfunction - CMD: 3604,3605,3606
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3604]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3604] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3605]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3605] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3606]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3606] 1
data modify storage stardew:keg processing set value {output_cmd:135,type:23,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
