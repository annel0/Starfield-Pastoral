# process_artichoke.mcfunction - CMD: 4104,4105,4106
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4104]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4104] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4105]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4105] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4106]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=4106] 1
data modify storage stardew:keg processing set value {output_cmd:131,type:27,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
