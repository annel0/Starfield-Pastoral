# process_ancient_fruit.mcfunction - CMD: 4604,4605,4606
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4604]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4604] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4605]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4605] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4606]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4606] 1
data modify storage stardew:keg processing set value {output_cmd:102,type:5,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
