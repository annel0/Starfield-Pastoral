# process_strawberry.mcfunction - CMD: 1304,1305,1306
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1304]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1304] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1305]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1305] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1306]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1306] 1
data modify storage stardew:keg processing set value {output_cmd:100,type:1,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
