# process_eggplant.mcfunction - CMD: 3404,3405,3406
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3404]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3404] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3405]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3405] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3406]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3406] 1
data modify storage stardew:keg processing set value {output_cmd:130,type:28,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
