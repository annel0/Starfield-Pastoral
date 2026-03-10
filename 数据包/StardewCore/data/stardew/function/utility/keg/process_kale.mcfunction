# process_kale.mcfunction - CMD: 1804,1805,1806
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1804]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1804] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1805]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1805] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1806]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=1806] 1
data modify storage stardew:keg processing set value {output_cmd:132,type:20,time:3600}
function stardew:utility/keg/start_process with storage stardew:keg processing
