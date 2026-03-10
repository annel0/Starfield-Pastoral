# process_corn.mcfunction - CMD: 2904,2905,2906
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2904]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2904] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2905]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2905] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2906]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2906] 1
data modify storage stardew:keg processing set value {output_cmd:128,type:15,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
