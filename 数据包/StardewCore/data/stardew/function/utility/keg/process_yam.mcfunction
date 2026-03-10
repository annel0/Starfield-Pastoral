# process_yam.mcfunction - CMD: 3504,3505,3506
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3504]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3504] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3505]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3505] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3506]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=3506] 1
data modify storage stardew:keg processing set value {output_cmd:136,type:26,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
