# process_bok_choy.mcfunction - CMD: 3304,3305,3306
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3304]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3304] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3305]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3305] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3306]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3306] 1
data modify storage stardew:keg processing set value {output_cmd:134,type:24,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
