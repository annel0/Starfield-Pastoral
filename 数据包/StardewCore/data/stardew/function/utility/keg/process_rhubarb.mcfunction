# process_rhubarb.mcfunction - CMD: 1124,1125,1126
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1124]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1124] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1125]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1125] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1126]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1126] 1
data modify storage stardew:keg processing set value {output_cmd:101,type:8,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
