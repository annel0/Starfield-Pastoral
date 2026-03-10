# process_parsnip.mcfunction - CMD: 1504,1505,1506
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1504]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1504] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1505]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1505] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1506]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1506] 1
data modify storage stardew:keg processing set value {output_cmd:124,type:18,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
