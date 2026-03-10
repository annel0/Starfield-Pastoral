# process_green_bean.mcfunction - CMD: 2004,2005,2006
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2004]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2004] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2005]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2005] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2006]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2006] 1
data modify storage stardew:keg processing set value {output_cmd:133,type:19,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
