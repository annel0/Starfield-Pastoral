# process_cranberry.mcfunction - CMD: 3904,3905,3906
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3904]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3904] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3905]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3905] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3906]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3906] 1
data modify storage stardew:keg processing set value {output_cmd:105,type:3,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
