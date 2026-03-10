# process_coffee_bean.mcfunction - CMD: 2104,2105,2106
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2104]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2104] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2105]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2105] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2106]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2106] 1
data modify storage stardew:keg processing set value {output_cmd:138,type:13,time:1200}
function stardew:utility/keg/start_process with storage stardew:keg processing
